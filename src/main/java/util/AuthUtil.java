package util;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.name.Named;
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

    private GoogleClientSecrets.Details clientSecretsDetail;
    public final JsonFactory JSON_FACTORY;
    public final HttpTransport httpTransport;
    private final String APPLICATION_NAME;
    private final AuthorizationCodeFlow authorizationCodeFlow;


    @Inject
    public AuthUtil(JsonFactory jsonFactory, HttpTransport httpTransport,
                    @Named("application_name") String applicationName,
                    AuthorizationCodeFlow authorizationCodeFlow) {
        JSON_FACTORY = jsonFactory;
        this.httpTransport = httpTransport;
        APPLICATION_NAME = applicationName;
        this.authorizationCodeFlow = authorizationCodeFlow;
    }

    public YouTube initYouTubeClient(){
        Credential credential = this.loadCredential();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    public PhotosLibraryClient initPhotoClient() throws IOException {
        UserCredentials userCredentials = this.loadUserCredential();
        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(userCredentials);
        PhotosLibrarySettings settings = PhotosLibrarySettings.newBuilder()
                .setCredentialsProvider(credentialsProvider).build();
        return PhotosLibraryClient.initialize(settings);
    }

    public Sheets initSheetService() {
        UserCredentials userCredentials = this.loadUserCredential();
        return new Sheets.Builder(httpTransport, GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(userCredentials))
                .setApplicationName(APPLICATION_NAME).build();
    }

    public UserCredentials loadUserCredential() {
        Credential credential = this.loadCredential();
        return UserCredentials.newBuilder()
                .setClientId(clientSecretsDetail.getClientId())
                .setClientSecret(clientSecretsDetail.getClientSecret())
                .setRefreshToken(credential.getRefreshToken()).build();
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    public Credential loadCredential()  {
        try {
            // Load client secrets.
            InputStream is = Objects.requireNonNull(AuthUtil.class.getResourceAsStream("/client_secrets.json"));
            Reader clientSecretReader = new InputStreamReader(is);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);
            clientSecretsDetail = clientSecrets.getDetails();
            return authorizationCodeFlow.loadCredential("userId");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
