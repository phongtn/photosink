package com.wind.photos;

import java.time.LocalDate;
import java.util.List;

public interface PhotoService {

    List<VideoDto> getAllVideo();

    List<VideoDto> getVideos(LocalDate date);

    VideoDto getVideo(String videoId);
}
