package com.wind.controller;

import com.google.inject.Inject;
import com.wind.service.SheetService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record BotLoggingController(SheetService service) {

    private static final Logger logger = LoggerFactory.getLogger(SyncController.class.getName());

    @Inject
    public BotLoggingController {
    }

    public void createLog(Context context) {
        service.updateTradingLog();
    }
}
