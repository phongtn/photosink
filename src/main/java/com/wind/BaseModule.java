package com.wind;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.inject.AbstractModule;

public class BaseModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(JsonFactory.class).toInstance(GsonFactory.getDefaultInstance());
    }
}
