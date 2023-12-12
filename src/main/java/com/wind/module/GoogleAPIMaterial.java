package com.wind.module;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.inject.AbstractModule;
import util.AuthUtil;

/**
 * This configuration for build the Google Client
 */
public class GoogleAPIMaterial extends AbstractModule {

    @Override
    protected void configure() {
        bind(JsonFactory.class).toInstance(GsonFactory.getDefaultInstance());
        bind(HttpTransport.class).toInstance(new NetHttpTransport());
    }
}
