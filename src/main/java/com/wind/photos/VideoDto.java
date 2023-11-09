package com.wind.photos;

public class VideoDto {

    private final String name;

    private String mimeType = "video/*";

    private final String urlDownload;

    private final String id;

    private final long createTime;

    public VideoDto(String name, String urlDownload, String id) {
        this.name = name;
        this.urlDownload = urlDownload;
        this.id = id;
        this.createTime = System.currentTimeMillis();
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

    public long getCreateTime() {
        return createTime;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "VideoDto{" +
                "name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", urlDownload='" + urlDownload + '\'' +
                ", id='" + id + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
