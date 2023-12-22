package com.wind.module;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wind.auth.OauthAuthorizationCustomFlow;
import com.wind.google.photos.PhotoService;
import com.wind.google.photos.PhotosProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class ServiceModule extends AbstractModule {

    @Provides
    PhotoService providePhotoService(PhotosProcessor photosProcessor) {
        return photosProcessor;
    }

    @Provides
    @Singleton
    AuthorizationCodeFlow authorizationCodeFlow(OauthAuthorizationCustomFlow oauthAuthorizationCustomFlow) {
        try {
            return oauthAuthorizationCustomFlow.initializeFlow();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    Firestore provideFirestore() {
        InputStream is = Objects.requireNonNull(this.getClass().getResourceAsStream("/service_account.json"));
        try {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(is);
            FirestoreOptions firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId("wind-personal")
                            .setCredentials(googleCredentials)
                            .build();
            return firestoreOptions.getService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GoogleClientSecrets loadGoogleClientSecret() {
        try {
            // Load client secrets.
            InputStream is = Objects.requireNonNull(this.getClass().getResourceAsStream("/client_secrets.json"));
            Reader clientSecretReader = new InputStreamReader(is);
            return GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), clientSecretReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
