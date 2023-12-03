package com.wind;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.wind.photos.PhotoService;
import com.wind.photos.PhotosProcessor;
import util.CustomGoogleCodeReceive;

public class ServiceModule extends AbstractModule {

    @Provides
    PhotoService providePhotoService(PhotosProcessor photosProcessor) {
        return photosProcessor;
    }

    @Provides @Singleton
    VerificationCodeReceiver verificationCodeReceiver(CustomGoogleCodeReceive customGoogleCodeReceive){
        return customGoogleCodeReceive;
    }

}
