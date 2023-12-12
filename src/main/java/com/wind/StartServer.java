package com.wind;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.module.ConfigModule;
import com.wind.module.GoogleAPIMaterial;
import com.wind.module.ServiceModule;
import com.wind.photos.PhotoService;
import com.wind.photos.VideoDto;
import com.wind.service.PhotoSyncService;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static io.javalin.Javalin.create;

public class StartServer {

    private static final Logger logger = LoggerFactory.getLogger(StartServer.class.getName());

    static final String userId = "userId";
    static final String redirectURI = "https://ptube-gmzeotoo4q-uc.a.run.app/Callback";
//    static final String redirectURI = "https://ptube-gmzeotoo4q-uc.a.run.app/Callback";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GoogleAPIMaterial(), new ConfigModule(), new ServiceModule());

        var app = create(cfg -> cfg.routing.contextPath = "/").start(8080);
        app.get("/", ctx -> ctx.json("Hello, is it me you're looking for"));
        app.get("/login", ctx -> {
            AuthorizationCodeFlow flow = injector.getInstance(AuthorizationCodeFlow.class);
            Credential credential = flow.loadCredential(userId);
            if (credential != null && (credential.getRefreshToken() != null
                    || credential.getExpiresInSeconds() == null
                    || credential.getExpiresInSeconds() > 60)) {
                ctx.json("Found the credential valid").status(HttpStatus.OK);
            } else {
                AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
                logger.info("Sign in URL{}", authorizationUrl.build());
                ctx.redirect(authorizationUrl.build(), HttpStatus.MOVED_PERMANENTLY);
            }
        });
        app.get("/logout", ctx -> {
            File credentialsDirectory = new File("oauth-credentials");
            FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(credentialsDirectory);
            fileDataStoreFactory.getDataStore("StoredCredential").clear();
            ctx.result("logout");
        });
        app.get("/Callback", ctx -> {
            String code = ctx.queryParam("code");
            // receive authorization code and exchange it for an access token
            AuthorizationCodeFlow flow = injector.getInstance(AuthorizationCodeFlow.class);
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            // store credential and return it
            Credential credential = flow.createAndStoreCredential(response, userId);
            logger.info("Access token {}", credential.getAccessToken());
            logger.info("Refresh token {}", credential.getRefreshToken());
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

