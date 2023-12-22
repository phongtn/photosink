package com.wind.controller;

import com.google.inject.Inject;
import com.wind.service.PhotoSyncService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public record SyncController(PhotoSyncService photoSyncService) {

    private static final Logger logger = LoggerFactory.getLogger(SyncController.class.getName());

    @Inject
    public SyncController {
    }

    public void syncLatestVideo(Context context) {
        try {
            photoSyncService.pullVideoToday();
            context.status(HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            context.result(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void syncVideo2Youtube(Context context) {
        try {
            photoSyncService.pushVideoYoutube();
            context.result("Success").status(HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            context.result(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
