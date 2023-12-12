package com.wind;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Semaphore;

@Singleton
public class CustomGoogleCodeReceive implements VerificationCodeReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    final Semaphore waitUnlessSignaled = new Semaphore(0 /* initially zero permit */);

    private String code;

    public CustomGoogleCodeReceive() {
        logger.info("Initialize google oauth code handler");
    }

    @Override
    public String getRedirectUri() throws IOException {
//        return "https://ptube-gmzeotoo4q-uc.a.run.app/Callback";
        return "http://localhost:8080/Callback";
    }

    @Override
    public String waitForCode() throws IOException {
        waitUnlessSignaled.acquireUninterruptibly();
//        if (error != null) {
//            throw new IOException("User authorization failed (" + error + ")");
//        }
        return code;
    }

    public void setCode(String host) {
        this.code = host;
        waitUnlessSignaled.release();
    }

    @Override
    public void stop() throws IOException {
        waitUnlessSignaled.release();
        logger.info("stop server");
    }
}
