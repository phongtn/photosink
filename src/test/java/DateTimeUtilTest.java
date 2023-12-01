import com.google.type.Date;
import org.junit.Test;
import util.DateTimeUtil;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class DateTimeUtilTest {

    @Test
    public void testLocalDateProto() {
        LocalDate localDate = LocalDate.now();
        Date protoDate =  DateTimeUtil.toGoogleDate(localDate);
        assertThat(protoDate.getDay(), equalTo(localDate.getDayOfMonth()));
        assertThat(protoDate.getMonth(), equalTo(localDate.getMonthValue()));
        assertThat(protoDate.getYear(), equalTo(localDate.getYear()));
    }
}
