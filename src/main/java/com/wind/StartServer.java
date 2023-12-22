package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wind.controller.*;
import com.wind.module.ConfigModule;
import com.wind.module.GoogleAPIMaterial;
import com.wind.module.ServiceModule;
import io.javalin.http.HttpStatus;

import static io.javalin.Javalin.create;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class StartServer {

    public static void main(String[] args) {
        // Initialize module
        Injector injector = Guice.createInjector(
                new GoogleAPIMaterial(),
                new ConfigModule(),
                new ServiceModule());
        // Lookup the controllers
        UserController userController = injector.getInstance(UserController.class);
        String API_ACCESS_KEY = injector.getInstance(Key.get(String.class, Names.named("api_access_key")));

        // Build the web server
        int CFG_PORT = injector.getInstance(Key.get(Integer.class, Names.named("web_server_port")));
        var app = create(cfg -> cfg.routing.contextPath = "/").start(CFG_PORT);

        // Private API needed authenticate with API KEY
        app.cfg.accessManager((handler, context, set) -> {
            if (context.path().startsWith("/private") &&
                    !AuthorizationUtil.canAccessAPI(context, API_ACCESS_KEY)) {
                context.status(HttpStatus.UNAUTHORIZED).result("unauthorized");
            } else
                handler.handle(context);
        });

        // Public API
        app.get("/", ctx -> ctx.json("Hello, is it me you're looking for"));
        app.get("/data/{collection}/{key}", ctx -> injector.getInstance(DataController.class).findData(ctx));
        app.get("/login", userController::login);
        app.get("/logout", userController::logout);
        app.get("/Callback", userController::callback);

        // Private API required access key
        app.routes(() -> path("private", () -> {
            get("video", ctx -> injector.getInstance(PhotosController.class).findMedia(ctx));
            get("sync/photos", ctx -> injector.getInstance(SyncController.class).syncLatestVideo(ctx));
            get("sync/utube", ctx -> injector.getInstance(SyncController.class).syncVideo2Youtube(ctx));
        }));


    }
}

