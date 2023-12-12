package com.wind.module;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            Properties property = new Properties();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            property.load(classLoader.getResourceAsStream("application.properties"));
            Names.bindProperties(binder(), property);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
