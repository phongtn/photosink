package com.wind.google.utube;

/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.inject.Inject;
import com.wind.google.photos.VideoDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wind.google.GoogleServiceProvider;
import util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * <a href="https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/data/UploadThumbnail.java">...</a>
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 *
 * @author Jeremy Walker
 */
public class YouTubeUploader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Define a global variable that specifies the MIME type of the video
     * being uploaded.
     */
    private static final String VIDEO_FILE_FORMAT = "video/*";

    private final YouTube youTube;

    @Inject
    public YouTubeUploader(GoogleServiceProvider apiServiceProvider) {
        this.youTube = apiServiceProvider.initYouTubeClient();
    }

    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     */
    public String upload(VideoDto videoDto) throws IOException {
        logger.info("Start to sync video: {}", videoDto.getName());
        String videoLink = "https://www.youtube.com/watch?v=";
        // Add extra information to the video before uploading.
        Video videoObjectDefiningMetadata = convertYoutubeVideo(videoDto);
        InputStream in = new URL(videoDto.getUrlDownload()).openStream();
        InputStreamContent mediaContent = new InputStreamContent(videoDto.getMimeType(), in);

        // Insert the video. The command sends three arguments. The first
        // specifies which information the API request is setting and which
        // information the API response should return. The second argument
        // is the video resource that contains metadata about the new video.
        // The third argument is the actual video content.
        YouTube.Videos.Insert videoInsert = youTube.videos().insert(
                Collections.singletonList("snippet,statistics,status"),
                videoObjectDefiningMetadata, mediaContent);

        // Set the upload type and add an event listener.
        MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

        // Indicate whether direct media upload is enabled. A value of
        // "True" indicates that direct media upload is enabled and that
        // the entire media content will be uploaded in a single request.
        // A value of "False," which is the default, indicates that the
        // request will use the resumable media upload protocol, which
        // supports the ability to resume an upload operation after a
        // network interruption or other transmission failure, saving
        // time and bandwidth in the event of network failures.
        uploader.setDirectUploadEnabled(false);
        UploadListener uploadListener = new UploadListener();
        uploader.setProgressListener(uploadListener);

        // Call the API and upload the video.
        Video returnedVideo = videoInsert.execute();
        logger.info("The video {} synced to YouTube", returnedVideo.getSnippet().getTitle());
        return videoLink + returnedVideo.getId();
    }

    private static Video convertYoutubeVideo(VideoDto videoDto) {
        Video videoObjectDefiningMetadata = new Video();

        // Set the video to be publicly visible. This is the default
        // setting. Other supporting settings are "unlisted" and "private."
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("private");
        videoObjectDefiningMetadata.setStatus(status);

        VideoSnippet snippet = generateSnippet(videoDto);

        // Add the completed snippet object to the video resource.
        videoObjectDefiningMetadata.setSnippet(snippet);
        return videoObjectDefiningMetadata;
    }

    @NotNull
    private static VideoSnippet generateSnippet(VideoDto videoDto) {
        VideoSnippet snippet = new VideoSnippet();

        // This code uses a Calendar instance to create a unique name and
        // description for test purposes so that you can easily upload
        // multiple files. You should remove this code from your project
        // and use your own standard names instead.
        Calendar cal = Calendar.getInstance();
        snippet.setTitle(videoDto.getName());
        snippet.setDescription("This video upload from GooglePhotoSync service " + "on " + cal.getTime());

        // Set the keyword tags that you want to associate with the video.
        List<String> tags = new ArrayList<>();
        tags.add("google-photos");
        snippet.setTags(tags);
        return snippet;
    }
}