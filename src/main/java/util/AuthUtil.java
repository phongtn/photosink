package util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.youtube.YouTube;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

public class AuthUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    public final JsonFactory JSON_FACTORY;
    private GoogleClientSecrets.Details clientSecretsDetail;
    private final List<String> REQUIRED_SCOPES;
    {
        REQUIRED_SCOPES = ImmutableList.of(
                "https://www.googleapis.com/auth/photoslibrary.readonly",
                "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
                "https://www.googleapis.com/auth/photoslibrary.appendonly",
                "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata",
                "https://www.googleapis.com/auth/youtube.upload");
    }

    private final Credential credential;

    @Inject
    public AuthUtil(JsonFactory jsonFactory) {
        JSON_FACTORY = jsonFactory;
        try {
            String CLIENT_DATA_STORE = "PhotoSync";
            credential = authorize(REQUIRED_SCOPES);
            logger.info("Build the client credential success");
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Issues with initialize the credentials");
            throw new RuntimeException(e);
        }
    }

    public YouTube initYouTubeClient() {
        logger.info("Access Token {}", credential.getAccessToken());
        logger.info("Refresh Token {}", credential.getRefreshToken());
        logger.info("Expired Second {}", credential.getExpiresInSeconds());
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).
                setApplicationName("photos-utube-sync").build();
    }

    public PhotosLibraryClient initPhotoClient() throws IOException {
        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientSecretsDetail.getClientId())
                .setClientSecret(clientSecretsDetail.getClientSecret())
                .setRefreshToken(credential.getRefreshToken())
                .build();
        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(userCredentials);
        PhotosLibrarySettings settings = PhotosLibrarySettings.newBuilder().
                setCredentialsProvider(credentialsProvider).build();
        return PhotosLibraryClient.initialize(settings);
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run YouTube upload.
     */
    public Credential authorize(List<String> scopes) throws
            IOException, GeneralSecurityException {

        // Load client secrets.
        InputStream is = Objects.requireNonNull(AuthUtil.class.getResourceAsStream("/client_secrets.json"));
        Reader clientSecretReader = new InputStreamReader(is);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
        clientSecretsDetail = clientSecrets.getDetails();

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
                clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            logger.error("Client secret file not correctly formatted");
            System.exit(1);
        }

        /*
          This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
          This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
         */
        File credentialsDirectory = new File(System.getProperty("user.home") + "/.oauth-credentials");
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(credentialsDirectory);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, clientSecrets, scopes).
                setDataStoreFactory(fileDataStoreFactory).
                setAccessType("offline").build();

        // Build the local server and bind it to port 8080
        int LOCAL_PORT_BINDING = 8080;
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(LOCAL_PORT_BINDING).build();
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }
}
