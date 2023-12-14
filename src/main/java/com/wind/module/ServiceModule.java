package com.wind.module;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wind.GoogleAuthorizationUtil;
import com.wind.photos.PhotoService;
import com.wind.photos.PhotosProcessor;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ServiceModule extends AbstractModule {

    @Provides
    PhotoService providePhotoService(PhotosProcessor photosProcessor) {
        return photosProcessor;
    }

    @Provides @Singleton
    AuthorizationCodeFlow authorizationCodeFlow(GoogleAuthorizationUtil googleAuthorizationUtil) {
        try {
            return googleAuthorizationUtil.initializeFlow();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
