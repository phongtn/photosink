package com.wind;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wind.module.ConfigModule;
import com.wind.module.GoogleAPIMaterial;
import com.wind.module.ServiceModule;
import com.wind.google.photos.PhotoService;
import com.wind.google.photos.VideoDto;
import com.wind.google.firestore.FirestoreRepository;
import com.wind.service.PhotoSyncService;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.javalin.Javalin.create;

public class StartServer {

    private static final Logger logger = LoggerFactory.getLogger(StartServer.class.getName());

    static final String userId = "userId";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GoogleAPIMaterial(), new ConfigModule(), new ServiceModule());
        String redirectURI = injector.getInstance(Key.get(String.class, Names.named("url_redirect")));

        var app = create(cfg -> cfg.routing.contextPath = "/").start(8080);
        app.get("/", ctx -> ctx.json("Hello, is it me you're looking for"));
        app.get("/data/{collection}/{key}", ctx -> {
            FirestoreRepository fsService = injector.getInstance(FirestoreRepository.class);
            Map<String, Object> data = fsService.readData(ctx.pathParam("collection"), ctx.pathParam("key"));
            ctx.json(data);
        });
        app.get("/login", ctx -> {
            AuthorizationCodeFlow flow = injector.getInstance(AuthorizationCodeFlow.class);
            Credential credential = flow.loadCredential(userId);
            if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() > 60)) {
                ctx.json("Found the credential valid").status(HttpStatus.OK);
            } else {
                AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
                logger.info("Sign in URL{}", authorizationUrl.build());
                ctx.redirect(authorizationUrl.build(), HttpStatus.MOVED_PERMANENTLY);
            }
        });
        app.get("/logout", ctx -> {
            AuthorizationCodeFlow flow = injector.getInstance(AuthorizationCodeFlow.class);
            flow.getCredentialDataStore().delete(userId);
            ctx.result("logout");
        });
        app.get("/Callback", ctx -> {
            String code = ctx.queryParam("code");
            AuthorizationCodeFlow flow = injector.getInstance(AuthorizationCodeFlow.class);
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            // store credential and return it
            flow.createAndStoreCredential(response, userId);
            ctx.json("login success").status(HttpStatus.OK);
        });
        app.get("/video", ctx -> {
            PhotoService photoService = injector.getInstance(PhotoService.class);
            int year = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("y")));
            int month = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("m")));
            int day = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("d")));
            List<VideoDto> video = photoService.getVideos(LocalDate.of(year, month, day));
            ctx.json(video).status(HttpStatus.OK);
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

