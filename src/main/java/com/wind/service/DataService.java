package com.wind.service;

import com.google.inject.Inject;
import com.wind.StartClient;
import com.wind.sheet.SheetPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DataService {

    private final SheetPersistence sheetPersistence;
    private static Logger logger = LoggerFactory.getLogger(DataService.class.getName());

    @Inject
    public DataService(SheetPersistence sheetPersistence) {
        this.sheetPersistence = sheetPersistence;
    }

    public void pullData() {
        try {
            List<List<Object>> rows = sheetPersistence.readValueRange("A2:D").get(0).getValues();
            logger.info("Data size {}", rows.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
