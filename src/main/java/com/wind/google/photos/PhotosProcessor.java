package com.wind.google.photos;

import com.google.api.gax.rpc.ApiException;
import com.google.inject.Inject;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.*;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.library.v1.util.NewMediaItemFactory;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import com.google.photos.types.proto.VideoProcessingStatus;
import com.google.rpc.Code;
import com.google.rpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AuthUtil;
import util.DateTimeUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PhotosProcessor implements PhotoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final PhotosLibraryClient photosLibraryClient;

    @Inject
    public PhotosProcessor(AuthUtil authUtil) throws IOException {
        photosLibraryClient = authUtil.initPhotoClient();
    }

    @Override
    public VideoDto getVideo(String videoID) {
        MediaItem mediaItem = photosLibraryClient.getMediaItem(videoID);
        if (mediaItem != null) {
            MediaMetadata metadata = mediaItem.getMediaMetadata();
            String fileName = mediaItem.getFilename();
            String baseUrlDownload = mediaItem.getBaseUrl() + "=dv";
            VideoDto videoDto = new VideoDto(fileName, baseUrlDownload, videoID);
            videoDto.setMimeType(mediaItem.getMimeType());
            return videoDto;
        }
        photosLibraryClient.close();
        return null;
    }

    @Override
    public List<VideoDto> getAllVideo() {
        MediaTypeFilter mediaType = MediaTypeFilter.newBuilder().
                addMediaTypes(MediaTypeFilter.MediaType.VIDEO).build();
        Filters filters = Filters.newBuilder()
                .setMediaTypeFilter(mediaType)
                .build();
        return this.filterVideo(filters);
    }

    @Override
    public List<VideoDto> getVideos(LocalDate date) {
        LocalDate requestDate = Optional.ofNullable(date).orElse(DateTimeUtil.now());
        DateFilter dateFilter = DateFilter.newBuilder()
                .addDates(DateTimeUtil.toGoogleDate(requestDate))
                .build();

        MediaTypeFilter mediaType = MediaTypeFilter.newBuilder().
                addMediaTypes(MediaTypeFilter.MediaType.VIDEO).build();

        Filters filters = Filters.newBuilder()
                .setDateFilter(dateFilter)
                .setMediaTypeFilter(mediaType)
                .build();
        return this.filterVideo(filters);
    }

    /**
     * <a href="https://developers.google.com/photos/library/guides/apply-filters">...</a>
     */
    private List<VideoDto> filterVideo(Filters filters) {
        List<VideoDto> videoDTOs = new ArrayList<>();

        InternalPhotosLibraryClient.SearchMediaItemsPagedResponse searchResponse =
                photosLibraryClient.searchMediaItems(filters);

        for (MediaItem mediaItem : searchResponse.iterateAll()) {
            MediaMetadata metadata = mediaItem.getMediaMetadata();
            String fileName = mediaItem.getFilename();
            VideoProcessingStatus status = metadata.getVideo().getStatus();
            LocalDateTime dateCreate = DateTimeUtil.protoTimeStamp2DateTime(metadata.getCreationTime());

            if (VideoProcessingStatus.READY.equals(status)) {
                String mimeType = mediaItem.getMimeType();
                String baseUrlDownload = mediaItem.getBaseUrl() + "=dv";

                VideoDto videoDto = new VideoDto(fileName, baseUrlDownload, mediaItem.getId());
                videoDto.setMimeType(mimeType);
                videoDto.setDateCreate(dateCreate);
                videoDto.setProductUrl(mediaItem.getProductUrl());
                videoDTOs.add(videoDto);
            } else logger.warn("Video {} is not ready. Current status: {}", fileName, status);
        }
        logger.info("we found {} videos on filter {}", videoDTOs.size(), filters.getContentFilter());
        photosLibraryClient.close();
        return videoDTOs;
    }

    private String uploadItem(String pathToFile) {
        File fileUpload = new File(pathToFile);
        String uploadToken = this.generateTokenUpload(fileUpload);
        try {
            // Create a NewMediaItem with the following components:
            // - uploadToken obtained from the previous upload request
            // - filename that will be shown to the user in Google Photos
            // - description that will be shown to the user in Google Photos
            NewMediaItem newMediaItem = NewMediaItemFactory.createNewMediaItem(uploadToken, fileUpload.getName(), "sample file");
            List<NewMediaItem> newItems = List.of(newMediaItem);

            BatchCreateMediaItemsResponse response = photosLibraryClient.batchCreateMediaItems(newItems);
            for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
                Status status = itemsResponse.getStatus();
                if (status.getCode() == Code.OK_VALUE) {
                    // The item is successfully created in the user's library
                    MediaItem createdItem = itemsResponse.getMediaItem();
                    logger.info("Item created {}", createdItem);
                    return createdItem.getId();
                } else {
                    // The item could not be created. Check the status and try again
                    logger.error("Can't create media {}", itemsResponse.getStatus().getMessage());
                }
            }
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String generateTokenUpload(File f) {
        // Open the file and automatically close it after upload
        try (RandomAccessFile file = new RandomAccessFile(f, "r")) {
            // Create a new upload request
            UploadMediaItemRequest uploadRequest = UploadMediaItemRequest.newBuilder()
                    // The media type (e.g. "image/png")
                    .setMimeType(Files.probeContentType(f.toPath())).setDataFile(file).build();
            UploadMediaItemResponse uploadResponse = photosLibraryClient.uploadMediaItem(uploadRequest);
            if (uploadResponse.getError().isPresent()) {
                UploadMediaItemResponse.Error error = uploadResponse.getError().get();
                logger.warn("can't get upload token {}", error);
            } else {
                // If the upload is successful, get the uploadToken
                return uploadResponse.getUploadToken().orElse(null);
            }
        } catch (ApiException | IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void addMediaItemToAlbum(String albumId, List<String> mediaItemIds) {
        try {
            Objects.requireNonNull(albumId, "The Album ID can't null");
            // Add all given media items to the album
            BatchAddMediaItemsToAlbumResponse response = photosLibraryClient.batchAddMediaItemsToAlbum(albumId, mediaItemIds);
            logger.info("move result {}", response.toString());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Currently Google API doesn't support delete action via API.
     * We will be created a default album to add the video into this album.
     * Then we can manually delete these items using Google Photos application or browser.
     */
    public Album getVideoAlbum() {
//        GetAlbumRequest getAlbumRequest = GetAlbumRequest.newBuilder().set
        return photosLibraryClient.createAlbum("utube-moved");
    }
}
