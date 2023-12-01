package com.wind;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wind.photos.PhotoService;
import com.wind.photos.PhotosProcessor;

public class ServiceModule extends AbstractModule {

    @Provides
    PhotoService providePhotoService(PhotosProcessor photosProcessor) {
        return photosProcessor;
    }

}
