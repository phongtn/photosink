package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.photos.PhotoService;
import com.wind.photos.VideoDto;
import com.wind.service.PhotoSyncService;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AuthUtil;
import util.CustomGoogleCodeReceive;

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

        var app = create(cfg -> cfg.routing.contextPath = "/").start(8080);
        app.get("/", ctx -> {
            AuthUtil authUtil = injector.getInstance(AuthUtil.class);
            ctx.json("Hello, is it me you're looking for");
        });
        app.get("/video", ctx -> {
            PhotoService photoService = injector.getInstance(PhotoService.class);
            int year = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("y")));
            int month = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("m")));
            int day = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("d")));
            List<VideoDto> video = photoService.getVideos(LocalDate.of(year, month, day));
            ctx.json(video).status(HttpStatus.OK);
        });
        app.get("/Callback", ctx -> {
            String code = ctx.queryParam("code");
            CustomGoogleCodeReceive customGoogleCodeReceive = injector.getInstance(CustomGoogleCodeReceive.class);
            customGoogleCodeReceive.setHost(code);
            ctx.json("Received verification code. You may now close this window.").status(HttpStatus.OK);
        });
        app.get("/sync/photos", ctx -> {
            PhotoSyncService syncService = injector.getInstance(PhotoSyncService.class);
            syncService.pullVideoToday();
            ctx.result("Success").status(HttpStatus.OK);
        });
        app.get("/sync/utube", ctx -> {
            PhotoSyncService syncService = injector.getInstance(PhotoSyncService.class);
            syncService.pushVideoYoutube();
            ctx.result("Success").status(HttpStatus.OK);
        });

    }
}

