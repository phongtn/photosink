package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.photos.PhotosProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StartClient {

    /**
     * <a href="https://javalin.io/documentation#requestloggerconfig">The java lightweight framework.</a>
     * <a href="https://quarkus.io/about/">...</a>
     * <a href="https://micronaut.io/">...</a>
     * <a href="https://github.com/google/guice/wiki/GettingStarted">Google Guice</a>
     */
    private static Logger logger = LoggerFactory.getLogger(StartClient.class.getName());

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BaseModule());
        PhotosProcessor photosProcessor = injector.getInstance(PhotosProcessor.class);
//        photosProcessor.listAlbum();
        photosProcessor.syncVideoToYoutube();
    }
}

