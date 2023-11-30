package util;

import com.google.protobuf.Timestamp;
import com.google.type.Date;

import java.time.*;

public class DateTimeUtil {

    static final String ZONE_ID = "Asia/Ho_Chi_Minh";

    public static LocalDateTime protoTimeStamp2DateTime(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atZone(ZoneId.of(ZONE_ID))
                .toLocalDateTime();
    }

    protected static Timestamp toGoogleTimestampUTC(final LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.toEpochSecond(ZoneOffset.of(ZONE_ID)))
                .setNanos(localDateTime.getNano())
                .build();
    }

    protected static Date toGoogleDate(final LocalDate localDate) {
        return Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonth().getValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }
}
