package com.wind.module;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wind.auth.OauthAuthorizationUtil;
import com.wind.photos.PhotoService;
import com.wind.photos.PhotosProcessor;
import util.AuthUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class ServiceModule extends AbstractModule {

    @Provides
    PhotoService providePhotoService(PhotosProcessor photosProcessor) {
        return photosProcessor;
    }

    @Provides
    Firestore provideFirestore() {
        InputStream is = Objects.requireNonNull(AuthUtil.class.getResourceAsStream("/service_account.json"));
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

    @Provides
    @Singleton
    AuthorizationCodeFlow authorizationCodeFlow(OauthAuthorizationUtil oauthAuthorizationUtil) {
        try {
            return oauthAuthorizationUtil.initializeFlow();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
