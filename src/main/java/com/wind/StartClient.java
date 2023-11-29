package com.wind;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.wind.photos.PhotosProcessor;
import com.wind.photos.VideoDto;
import com.wind.sheet.SheetPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartClient {

    /**
     * <a href="https://javalin.io/documentation#requestloggerconfig">The java lightweight framework.</a>
     * <a href="https://quarkus.io/about/">...</a>
     * <a href="https://micronaut.io/">...</a>
     * <a href="https://github.com/google/guice/wiki/GettingStarted">Google Guice</a>
     */
    private static Logger logger = LoggerFactory.getLogger(StartClient.class.getName());

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(
                new GoogleAPIMaterial(),
                new ConfigModule());
        PhotosProcessor photosProcessor = injector.getInstance(PhotosProcessor.class);
        SheetPersistence sheetPersistence = injector.getInstance(SheetPersistence.class);

        List<VideoDto> videoDtoList = photosProcessor.filterVideo();
//        String range = "Sheet1!A1:C1";
//        String valueInputOption = "RAW";
//        List<Object> content = List.of("Title1", "URL1", "Status1   ");
        List<List<Object>> value = new ArrayList<>(videoDtoList.size());
        videoDtoList.forEach(video -> value.add(List.of(
                video.getId(),
                video.getName(),
                video.getProductUrl(),
                video.getDateCreate().toString())));

//        sheetPersistence.updateValues("A1", valueInputOption, value);
        sheetPersistence.appendValues("A1", value);
    }
}

