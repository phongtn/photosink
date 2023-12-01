package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.photos.PhotoService;
import com.wind.photos.VideoDto;
import com.wind.service.PhotoSyncService;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static io.javalin.Javalin.create;

public class StartServer {

    private static final Logger logger = LoggerFactory.getLogger(StartServer.class.getName());

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(
                new GoogleAPIMaterial(),
                new ConfigModule(),
                new ServiceModule());
        PhotoSyncService syncService = injector.getInstance(PhotoSyncService.class);
        PhotoService photoService = injector.getInstance(PhotoService.class);

        var app = create(cfg -> cfg.routing.contextPath = "/ptube").start(8081);
        app.get("/", ctx -> ctx.json("Hello, is it me you're looking for"));
        app.get("/video", ctx -> {
            int year = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("y")));
            int month = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("m")));
            int day = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("d")));
            List<VideoDto> video = photoService.getVideos(LocalDate.of(year, month, day));
            ctx.json(video).status(HttpStatus.OK);
        });
        app.get("/sync/photos", ctx -> {
            syncService.pullVideoToday();
            ctx.result("Success").status(HttpStatus.OK);
        });
        app.get("/sync/utube", ctx -> {
            syncService.pushVideoYoutube();
            ctx.result("Success").status(HttpStatus.OK);
        });

    }
}

