import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.ConfigModule;
import com.wind.GoogleAPIMaterial;
import com.wind.sheet.SheetPersistence;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GoogleSheetDataTest {

    private static Logger logger = LoggerFactory.getLogger(GoogleSheetDataTest.class.getName());

    private static SheetPersistence sheetServiceData;

    @BeforeClass
    public static void setup() {
        Injector injector = Guice.createInjector(new GoogleAPIMaterial(), new ConfigModule());
        sheetServiceData = injector.getInstance(SheetPersistence.class);
    }

    @Test
    public void testReadTitle() {
        try {
            BatchGetValuesResponse response = sheetServiceData.readValueRange("A1", "E1");
            ValueRange headTitle = response.getValueRanges().get(0);
            String cellValue = headTitle.getValues().get(0).get(0).toString();
            assertThat(cellValue, equalTo("ID"));
            ValueRange headStatus = response.getValueRanges().get(1);
            assertThat(headStatus.getValues().get(0).get(0).toString(), equalTo("Status"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateCellValue() {
        try {
            String ranges = "E2";
            String cellValue = "UPLOADED";
            // update the cell value
            List<List<Object>> bodyValue = new ArrayList<>();
            bodyValue.add(List.of(cellValue));
            UpdateValuesResponse updateResponse = sheetServiceData.updateValues(ranges, bodyValue);
            logger.info("The value update at {}:", updateResponse.getUpdatedRange());

            // Read the recent data updated
            BatchGetValuesResponse response = sheetServiceData.readValueRange(ranges, ranges);
            ValueRange headTitle = response.getValueRanges().get(0);
            String currentValue = headTitle.getValues().get(0).get(0).toString();
            logger.info("The current value at {} is {}", updateResponse.getUpdatedRange(), currentValue);
            assertThat(currentValue, equalTo(cellValue));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
