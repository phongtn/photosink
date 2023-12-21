package com.wind.module;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            Properties property = new Properties();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            property.load(classLoader.getResourceAsStream("application.properties"));
            property.forEach((key, value) -> {
                String strValue = value.toString();
                if (strValue.startsWith("$")) {
                    String envValue = this.getEnvValue(strValue);
                    property.replace(key, envValue);
                }
            });
            Names.bindProperties(binder(), property);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEnvValue(String envConfigKey) {
        // Define the regular expression pattern
        String regex = "\\$\\{|}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(envConfigKey);
        String envKey = matcher.replaceAll("");
        return Optional.ofNullable(System.getenv().get(envKey)).orElse("");
    }
}
