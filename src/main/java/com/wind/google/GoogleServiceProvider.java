package com.wind.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.youtube.YouTube;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.*;

public class GoogleServiceProvider {

    private final GoogleClientSecrets.Details clientSecretsDetail;
    public final HttpTransport httpTransport;
    private final String APPLICATION_NAME;
    private final AuthorizationCodeFlow authorizationCodeFlow;
    private final String userId;


    @Inject
    public GoogleServiceProvider(@Named("application_name") String applicationName,
                                 @Named("default_user_id") String userId,
                                 HttpTransport httpTransport,
                                 GoogleClientSecrets googleClientSecrets,
                                 AuthorizationCodeFlow authorizationCodeFlow) {
        this.clientSecretsDetail = googleClientSecrets.getDetails();
        this.httpTransport = httpTransport;
        this.APPLICATION_NAME = applicationName;
        this.authorizationCodeFlow = authorizationCodeFlow;
        this.userId = userId;
    }

    public YouTube initYouTubeClient() {
        Credential credential = this.loadCredential();
        return new YouTube.Builder(httpTransport, GsonFactory.getDefaultInstance(), credential)
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
    public Credential loadCredential() {
        try {
            return authorizationCodeFlow.loadCredential(userId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
