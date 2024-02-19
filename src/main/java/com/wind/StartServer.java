package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wind.controller.*;
import com.wind.module.ConfigModule;
import com.wind.module.GoogleAPIMaterial;
import com.wind.module.ServiceModule;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;

import static io.javalin.apibuilder.ApiBuilder.*;

public class StartServer {

    public static void main(String[] args) {
        // Initialize module
        Injector injector = Guice.createInjector(new GoogleAPIMaterial(), new ConfigModule(), new ServiceModule());
        // Lookup the controllers and default config parameters
        UserController userController = injector.getInstance(UserController.class);
        String API_ACCESS_KEY = injector.getInstance(Key.get(String.class, Names.named("api_access_key")));

        // Build the web server
        int CFG_PORT = injector.getInstance(Key.get(Integer.class, Names.named("web_server_port")));
        var app = Javalin.create().get("/", context -> context.json("Hello, is it me you're looking for")).start(CFG_PORT);

        app.beforeMatched(context -> {
            if (context.path().startsWith("/private") && !AuthorizationHandler.canAccessAPI(context, API_ACCESS_KEY)) {
                throw new UnauthorizedResponse("Required ACCESS_KEY to access this API");
            }
        });

        // Public API
        app.get("/data/{collection}/{key}", ctx -> injector.getInstance(DataController.class).findData(ctx));
        app.get("/login", userController::login);
        app.get("/logout", userController::logout);
        app.get("/Callback", userController::callback);

        // Private API required access key
        app.unsafeConfig().router.apiBuilder(() -> path("private", () -> {
            get("video", ctx -> injector.getInstance(PhotosController.class).findMedia(ctx));
            get("sync/photos", ctx -> injector.getInstance(SyncController.class).syncLatestVideo(ctx));
            get("sync/utube", ctx -> injector.getInstance(SyncController.class).syncVideo2Youtube(ctx));
            post("sheets/log", ctx -> injector.getInstance(BotLoggingController.class).createLog(ctx));
        }));
    }
}

