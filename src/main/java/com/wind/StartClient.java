package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.wind.photos.PhotosProcessor;
import com.wind.photos.VideoDto;
import com.wind.service.DataService;
import com.wind.service.PhotoSyncService;
import com.wind.sheet.SheetPersistence;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartClient {

    /**
     * <a href="https://javalin.io/documentation#requestloggerconfig">The java lightweight framework.</a>
     * <a href="https://micronaut.io/">...</a>
     * <a href="https://github.com/google/guice/wiki/GettingStarted">Google Guice</a>
     *
     */
    private static Logger logger = LoggerFactory.getLogger(StartClient.class.getName());

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(
                new GoogleAPIMaterial(),
                new ConfigModule());
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(7070);

        app.get("/sync", ctx -> {
            PhotoSyncService syncService = injector.getInstance(PhotoSyncService.class);
            syncService.pushVideoYoutube();
        });
    }
}

