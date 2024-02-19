package com.wind.controller;

import com.google.inject.Inject;
import com.wind.google.photos.PhotoService;
import com.wind.google.photos.VideoDto;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record PhotosController(PhotoService photoService) {
    @Inject
    public PhotosController {
    }

    public void findMedia(Context context) {
        int year = Integer.parseInt(Objects.requireNonNull(context.queryParam("y")));
        int month = Integer.parseInt(Objects.requireNonNull(context.queryParam("m")));
        int day = Integer.parseInt(Objects.requireNonNull(context.queryParam("d")));
        List<VideoDto> video = photoService.getVideos(LocalDate.of(year, month, day));
        context.json(video).status(HttpStatus.OK);
    }
}
