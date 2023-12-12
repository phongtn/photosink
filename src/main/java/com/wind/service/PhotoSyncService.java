package com.wind.service;

import com.google.inject.Inject;
import com.wind.photos.PhotoService;
import com.wind.photos.PhotosProcessor;
import com.wind.photos.VideoDto;
import com.wind.utube.YoutubeUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PhotoSyncService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final DataService dataService;
    private final PhotoService photoService;
    private final YoutubeUploader youtubeUploader;

    private final int LIMIT_YOUTUBE_UPLOAD_QUOTAS = 1;

    @Inject
    public PhotoSyncService(DataService dataService,
                            PhotosProcessor photoService,
                            YoutubeUploader youtubeUploader) {
        this.dataService = dataService;
        this.photoService = photoService;
        this.youtubeUploader = youtubeUploader;
    }

    public void pullVideoToday() throws IOException {
        List<VideoDto> videoDtos = photoService.getVideos(LocalDate.now());
        for (VideoDto video : videoDtos) {
            dataService.insertVideo(video);
        }
    }

    public void pushVideoYoutube() throws IOException {
        Map<Integer, String> videoData = dataService.pullData(LIMIT_YOUTUBE_UPLOAD_QUOTAS);
        videoData.forEach((rowId, videoID) -> {
            VideoDto videoDto = photoService.getVideo(videoID);
            String videoLink = youtubeUploader.upload(videoDto);
            dataService.updateStatus(rowId, videoLink);
//            dataService.updateStatus(rowId, "http://testing");
        });
    }
}
