package com.wind.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wind.google.photos.PhotoService;
import com.wind.google.photos.PhotosProcessor;
import com.wind.google.photos.VideoDto;
import com.wind.google.utube.YoutubeUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PhotoSyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final SheetService sheetService;
    private final PhotoService photoService;
    private final YoutubeUploader youtubeUploader;
    private final int LIMIT_YOUTUBE_UPLOAD_QUOTAS;

    @Inject
    public PhotoSyncService(SheetService sheetService,
                            PhotosProcessor photoService,
                            YoutubeUploader youtubeUploader,
                            @Named("number_videos_synced_utube") int limitYoutubeUploadQuotas) {
        this.sheetService = sheetService;
        this.photoService = photoService;
        this.youtubeUploader = youtubeUploader;
        this.LIMIT_YOUTUBE_UPLOAD_QUOTAS = limitYoutubeUploadQuotas;
    }

    public void pullVideoToday() throws IOException {
        List<VideoDto> videoDtos = photoService.getVideos(LocalDate.now());
        for (VideoDto video : videoDtos) {
            sheetService.insertVideo(video);
        }
    }

    public void pushVideoYoutube() throws IOException {
        Map<Integer, String> videoData = sheetService.pullData(LIMIT_YOUTUBE_UPLOAD_QUOTAS);
        videoData.forEach((rowId, videoID) -> {
            VideoDto videoDto = photoService.getVideo(videoID);
            String videoLink = youtubeUploader.upload(videoDto);
            sheetService.updateStatus(rowId, videoLink);
        });
    }
}
