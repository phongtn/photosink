package com.wind.photos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class VideoDto {

    private final String name;

    private String mimeType = "video/*";

    /**
     * This url to get bytes response
     */
    private final String urlDownload;

    /**
     * Url to open in Google Photos
     */
    private String productUrl;

    private final String id;

    private final long linkExpiredMillis;

    private LocalDateTime dateCreate;

    /**
     * @param name        video name
     * @param urlDownload Base URLs within the Google Photos Library API allow you to access the bytes of the media items. </br >.
     *                    They are valid for 60 minutes and require additional parameters as they cannot be used as is.
     * @param id          item id
     */
    public VideoDto(String name, String urlDownload, String id) {
        this.name = name;
        this.urlDownload = urlDownload;
        this.id = id;
        this.linkExpiredMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60);
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public long getExpiredDownloadLink() {
        return linkExpiredMillis;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    @Override
    public String toString() {
        return "VideoDto{" +
                "name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
//                ", urlDownload='" + urlDownload + '\'' +
//                ", productUrl='" + productUrl + '\'' +
                ", id='" + id + '\'' +
                ", linkExpiredMillis=" + linkExpiredMillis +
                ", dateCreate=" + dateCreate +
                '}';
    }
}
