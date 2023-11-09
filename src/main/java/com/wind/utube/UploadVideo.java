package com.wind.utube;

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


import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.wind.photos.VideoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <a href="https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/data/UploadThumbnail.java">...</a>
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 *
 * @author Jeremy Walker
 */
public class UploadVideo {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Define a global variable that specifies the MIME type of the video
     * being uploaded.
     */
    private static final String VIDEO_FILE_FORMAT = "video/*";

    private final YouTube youTube;

    public UploadVideo(YouTube youTube) {
        this.youTube = youTube;
    }

    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     */
    public void upload(VideoDto videoDto) {
        try {
            logger.info("{}: Start upload video {} .Time created: {}", Thread.currentThread().getId(), videoDto.getName(), new Date(videoDto.getCreateTime()));

            // Add extra information to the video before uploading.
            Video videoObjectDefiningMetadata = convertYoutubeVideo(videoDto);

            InputStream in = new URL(videoDto.getUrlDownload()).openStream();
            InputStreamContent mediaContent = new InputStreamContent(videoDto.getMimeType(), in);
//            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
//                    UploadVideo.class.getResourceAsStream("/sample-video.mp4"));

            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youTube.videos().insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

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

            MediaHttpUploaderProgressListener progressListener = httpUploader -> {
                switch (httpUploader.getUploadState()) {
                    case INITIATION_STARTED -> logger.info("Initiation Started");
                    case INITIATION_COMPLETE -> logger.info("Initiation Completed");
                    case MEDIA_IN_PROGRESS -> {
                        logger.info("Upload in progress");
                        logger.info("Upload percentage: " + httpUploader.getNumBytesUploaded());
                    }
                    case MEDIA_COMPLETE -> logger.info("Upload Completed!");
                    case NOT_STARTED -> logger.info("Upload Not Started!");
                }
            };
            uploader.setProgressListener(progressListener);

            // Call the API and upload the video.
            Video returnedVideo = videoInsert.execute();

            // Print data about the newly inserted video from the API response.
            logger.info("\n================== Returned Video ==================\n");
            logger.info("  - Id: " + returnedVideo.getId());
            logger.info("  - Title: " + returnedVideo.getSnippet().getTitle());
            logger.info("  - Tags: " + returnedVideo.getSnippet().getTags());
            logger.info("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            logger.info("  - Video Count: " + returnedVideo.getStatistics().getViewCount());

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError jsonError = e.getDetails();
            logger.error("GoogleJsonResponseException code: " + jsonError.getCode() + " : " + jsonError.getMessage(), e);
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        } catch (Throwable t) {
            logger.error("Throwable: " + t.getMessage(), t);
        }
    }

    private static Video convertYoutubeVideo(VideoDto videoDto) {
        Video videoObjectDefiningMetadata = new Video();

        // Set the video to be publicly visible. This is the default
        // setting. Other supporting settings are "unlisted" and "private."
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("private");
        videoObjectDefiningMetadata.setStatus(status);

        // Most of the video's metadata is set on the VideoSnippet object.
        VideoSnippet snippet = new VideoSnippet();

        // This code uses a Calendar instance to create a unique name and
        // description for test purposes so that you can easily upload
        // multiple files. You should remove this code from your project
        // and use your own standard names instead.
        Calendar cal = Calendar.getInstance();
        snippet.setTitle(videoDto.getName());
        snippet.setDescription("Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

        // Set the keyword tags that you want to associate with the video.
        List<String> tags = new ArrayList<>();
        tags.add("test");
        tags.add("java");
        snippet.setTags(tags);

        // Add the completed snippet object to the video resource.
        videoObjectDefiningMetadata.setSnippet(snippet);
        return videoObjectDefiningMetadata;
    }
}