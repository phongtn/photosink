package com.wind.sheet;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.inject.name.Named;
import com.wind.StartClient;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AuthUtil;

import java.io.IOException;
import java.util.List;

public class SheetPersistence {

    private static final Logger logger = LoggerFactory.getLogger(StartClient.class.getName());

    private final AuthUtil authUtil;
    private final String googleSheetID;
    private final Sheets sheetsService;

    @Inject
    public SheetPersistence(AuthUtil authUtil, @Named("gg_sheet_id") String googleSheetID) {
        this.authUtil = authUtil;
        this.sheetsService = authUtil.initSheetService();
        this.googleSheetID = googleSheetID;
    }

    /**
     * Create a new spreadsheet.
     *
     * @param title - the name of the sheet to be created.
     * @throws IOException - if credentials file not found.
     */
    public Spreadsheet createSpreadsheet(String title) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(new SpreadsheetProperties().setTitle(title));
        spreadsheet = sheetsService.spreadsheets()
                .create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        return spreadsheet;
    }

    public Spreadsheet getSheet() throws IOException {
        Sheets service = authUtil.initSheetService();
        return service.spreadsheets().get(googleSheetID).execute();
    }

    /**
     * Appends values to a spreadsheet.
     *
     * @param range  - Range of cells of the spreadsheet.
     * @param values - list of rows of values to input.
     * @throws IOException - if credentials file not found.
     *                     The INSERT_ROWS option means that we want the data to be added to a new row,
     *                     and not replace any existing data after the table
     */
    public void appendValues(String range,
                             List<List<Object>> values) throws IOException {

        AppendValuesResponse result = null;
        try {
            ValueRange body = new ValueRange().setValues(values);
            result = sheetsService.spreadsheets().values()
                    .append(googleSheetID, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
            logger.debug("{} cells appended.", result.getUpdates().getUpdatedCells());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                logger.error("Spreadsheet not found with id {}", googleSheetID);
            } else
                throw e;
        }
    }

    /**
     * <a href="https://developers.google.com/sheets/api/samples/writing">Basic writing</a>
     * Sets values in a range of a spreadsheet.
     *
     * @param range            - Range of cells of the spreadsheet.
     * @param values           - List of rows of values to input.
     * @return spreadsheet with updated values
     * @throws IOException - if credentials file not found.
     */
    public UpdateValuesResponse updateValues(String range,
                                             List<List<Object>> values) throws IOException {
        UpdateValuesResponse result = null;
        try {
            ValueRange body = new ValueRange().setValues(values);
            result = sheetsService.spreadsheets().values()
                    .update(googleSheetID, range, body)
                    .setValueInputOption("RAW")
                    .execute();
            logger.info("{} cells updated.", result.getUpdatedCells());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                logger.error("Spreadsheet not found with id {}", googleSheetID);
            } else {
                throw e;
            }
        }
        return result;
    }

    /**
     * @param fromCell start cell position
     * @param toCell   end cell position
     * @return cell value
     */
    public BatchGetValuesResponse readValueRange(String fromCell, String toCell) throws IOException {
        List<String> ranges = List.of(fromCell, toCell);
        return sheetsService.spreadsheets().values()
                .batchGet(googleSheetID)
                .setRanges(ranges)
                .execute();
    }
}
