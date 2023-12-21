package com.wind.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import util.AuthUtil;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@Singleton
public class OauthAuthorizationUtil {

    public final JsonFactory JSON_FACTORY;
    public final HttpTransport HTTP_TRANSPORT;
    private final List<String> REQUIRED_SCOPES;
    private final FirestoreDataStoreFactory firestoreDataStoreFactory;

    {
        REQUIRED_SCOPES = ImmutableList.of(
                SheetsScopes.SPREADSHEETS,
                YouTubeScopes.YOUTUBE_UPLOAD,
                "https://www.googleapis.com/auth/photoslibrary.readonly",
                "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
                "https://www.googleapis.com/auth/photoslibrary.appendonly",
                "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata");
    }

    @Inject
    public OauthAuthorizationUtil(JsonFactory JSON_FACTORY, HttpTransport HTTP_TRANSPORT,
                                  FirestoreDataStoreFactory firestoreDataStoreFactory) {
        this.JSON_FACTORY = JSON_FACTORY;
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
        this.firestoreDataStoreFactory = firestoreDataStoreFactory;
    }

    public AuthorizationCodeFlow initializeFlow() throws
            IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream is = Objects.requireNonNull(AuthUtil.class.getResourceAsStream("/client_secrets.json"));
        Reader clientSecretReader = new InputStreamReader(is);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, clientSecrets, REQUIRED_SCOPES).
                setDataStoreFactory(firestoreDataStoreFactory).
                setAccessType("offline").build();
    }

    @NotNull
    public static FileDataStoreFactory getFileDataStoreFactory(GoogleClientSecrets clientSecrets) throws IOException {
        if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
                clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            throw new RuntimeException("Client secret file not correctly formatted");
        }

        /*
          This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
          This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
         */
        File credentialsDirectory = new File("oauth-credentials");
        return new FileDataStoreFactory(credentialsDirectory);
    }
}
