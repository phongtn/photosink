package com.wind.service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.wind.google.photos.VideoDto;
import com.wind.google.sheet.SheetPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DateTimeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetService {

    private final SheetPersistence sheetPersistence;
    private final int ROW_BEGIN_INDEX;
    private final String DATA_RANGES;
    private final String COL_LABEL_STATUS;
    private final String COL_LABEL_CREATED;
    private static final Logger logger = LoggerFactory.getLogger(SheetService.class.getName());

    @Inject
    public SheetService(SheetPersistence sheetPersistence,
                        @Named("row_begin") int rowBeginIndex,
                        @Named("data_ranges") String dataRanges,
                        @Named("column_status") String colLabelStatus,
                        @Named("column_created_at") String colLabelCreated) {
        this.sheetPersistence = sheetPersistence;
        ROW_BEGIN_INDEX = rowBeginIndex;
        DATA_RANGES = dataRanges;
        COL_LABEL_STATUS = colLabelStatus;
        COL_LABEL_CREATED = colLabelCreated;
    }

    /**
     * @param limit number of videos sync to YouTube
     * @return a map store the row index as key and video's ID as value
     * @throws IOException if data not found
     */
    public Map<Integer, String> pullData(int limit) throws IOException {
        int countVideoUploaded = 0;
        Map<Integer, String> videoIDs = new HashMap<>();
        List<List<Object>> rows = sheetPersistence.readValueRange(DATA_RANGES).get(0).getValues();
        for (int i = 0; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (row.size() <= 4) {
                int rowIndex = ROW_BEGIN_INDEX + i;
                videoIDs.put(rowIndex, row.get(0).toString());
            } else
                countVideoUploaded++;

            // valid the limit
            if (videoIDs.size() >= limit)
                break;
        }
        logger.info("\n\tTotal videos uploaded: {} video. \n\tNow we start to sync: {} videos.",
                countVideoUploaded, videoIDs.size());
        return videoIDs;
    }

    public void updateResult(int rowIndex, String status, String youtubeLink) {
        // e.g: A22:G
        String range = COL_LABEL_STATUS + rowIndex + ":" + COL_LABEL_CREATED;
        sheetPersistence.updateMultiValue(range, List.of(status, youtubeLink, DateTimeUtil.nowWithTime()));
        logger.info("The row index {} update succeeded.", rowIndex);
    }

    public void insertVideo(VideoDto videoDto) throws IOException {
        List<Object> row = List.of(
                videoDto.getId(),
                videoDto.getName(),
                videoDto.getProductUrl(),
                videoDto.getDateCreate().toString());
        ValueRange body = new ValueRange()
                .setValues(List.of(row));
        sheetPersistence.appendData(body);
    }
}
