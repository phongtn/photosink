package com.wind.controller;

import io.javalin.http.Context;

public class AuthorizationUtil {

    /**
     * Just valid in case client include the API KEY
     * @return true if client is valid
     */
    public static boolean canAccessAPI(Context context, String configKey) {
        String headerAPIAccessKey = context.header("API_KEY");
        return configKey.equals(headerAPIAccessKey);
    }
}
