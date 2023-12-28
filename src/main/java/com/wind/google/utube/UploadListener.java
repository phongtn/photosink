package com.wind.google.utube;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StreamUtil;

import java.io.IOException;

public class UploadListener implements MediaHttpUploaderProgressListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private long totalBytes;

    @Override
    public void progressChanged(MediaHttpUploader httpUploader) throws IOException {
        switch (httpUploader.getUploadState()) {
            case INITIATION_STARTED -> logger.info("Initiation Started");
            case INITIATION_COMPLETE -> logger.info("Initiation Completed");
            case MEDIA_IN_PROGRESS -> {
                totalBytes = totalBytes + httpUploader.getNumBytesUploaded();
                logger.info("Upload percentage: " + StreamUtil.humanReadableByteCountBin(httpUploader.getNumBytesUploaded()));
            }
            case MEDIA_COMPLETE -> logger.info("Upload Completed!. Video size: {}", StreamUtil.humanReadableByteCountBin(totalBytes));
            case NOT_STARTED -> logger.info("Upload Not Started!");
        }
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}
