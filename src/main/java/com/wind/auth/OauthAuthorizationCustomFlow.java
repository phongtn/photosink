package com.wind.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Singleton
public class OauthAuthorizationCustomFlow {

    public final GoogleClientSecrets googleClientSecrets;
    public final HttpTransport HTTP_TRANSPORT;
    private final List<String> REQUIRED_SCOPES;
    private final FirestoreDataStoreFactory firestoreDataStoreFactory;
    {
        REQUIRED_SCOPES = ImmutableList.of(
                SheetsScopes.SPREADSHEETS,
                YouTubeScopes.YOUTUBE_UPLOAD,
                "https://www.googleapis.com/auth/photoslibrary.readonly",
                "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
                "https://www.googleapis.com/auth/photoslibrary.appendonly",
                "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata");
    }

    @Inject
    public OauthAuthorizationCustomFlow(GoogleClientSecrets googleClientSecrets, HttpTransport HTTP_TRANSPORT,
                                        FirestoreDataStoreFactory firestoreDataStoreFactory) {
        this.googleClientSecrets = googleClientSecrets;
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
        this.firestoreDataStoreFactory = firestoreDataStoreFactory;
    }

    public AuthorizationCodeFlow initializeFlow() throws
            IOException, GeneralSecurityException {

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                googleClientSecrets,
                REQUIRED_SCOPES).
                setDataStoreFactory(firestoreDataStoreFactory).
                setAccessType("offline").build();
    }
}
