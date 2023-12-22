package com.wind.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public record UserController(AuthorizationCodeFlow authorizationCodeFlow,
                             @Named("url_redirect") String redirectURI,
                             @Named("default_user_id") String userId) {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    @Inject
    public UserController {
    }

    public void login(Context context) {
        try {
            Credential credential = authorizationCodeFlow.loadCredential(userId);
            if (this.isCredentialValid(credential)) {
                context.json("Found the credential valid").status(HttpStatus.OK);
            } else {
                AuthorizationCodeRequestUrl authorizationUrl = authorizationCodeFlow
                        .newAuthorizationUrl().setRedirectUri(redirectURI);
                context.redirect(authorizationUrl.build(), HttpStatus.MOVED_PERMANENTLY);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            context.result(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void logout(Context context) {
        try {
            authorizationCodeFlow.getCredentialDataStore().delete(userId);
            context.result("logout");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            context.result(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void callback(Context context) {
        try {
            String code = context.queryParam("code");
            TokenResponse response = authorizationCodeFlow
                    .newTokenRequest(code)
                    .setRedirectUri(redirectURI).execute();
            // store credential and return it
            authorizationCodeFlow.createAndStoreCredential(response, userId);
            context.json("login success").status(HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            context.result(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isCredentialValid(Credential credential) {
        return credential != null
                && (credential.getRefreshToken() != null
                || credential.getExpiresInSeconds() == null
                || credential.getExpiresInSeconds() > 60);
    }
}
