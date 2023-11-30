package com.wind.service;

import com.google.inject.Inject;
import com.wind.photos.PhotosProcessor;
import com.wind.photos.VideoDto;
import com.wind.utube.YoutubeUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class PhotoSyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final DataService dataService;
    private final PhotosProcessor photosProcessor;
    private final YoutubeUploader youtubeUploader;

    private final int LIMIT_YOUTUBE_UPLOAD_QUOTAS = 3;

    @Inject
    public PhotoSyncService(DataService dataService,
                            PhotosProcessor photosProcessor,
                            YoutubeUploader youtubeUploader) {
        this.dataService = dataService;
        this.photosProcessor = photosProcessor;
        this.youtubeUploader = youtubeUploader;
    }

    public void pushVideoYoutube() throws IOException {
        Map<Integer, String> videoData = dataService.pullData(LIMIT_YOUTUBE_UPLOAD_QUOTAS);
        videoData.forEach((rowId, videoID) -> {
            VideoDto videoDto = photosProcessor.getVideo(videoID);
            String videoLink = youtubeUploader.upload(videoDto);
            dataService.updateStatus(rowId, videoLink);
        });
    }
}
