package util;

import com.google.protobuf.Timestamp;
import com.google.type.Date;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final String ZONE_ID = "Asia/Ho_Chi_Minh";

    public static LocalDate now() {
        return LocalDate.now(ZoneId.of(ZONE_ID));
    }

    public static String nowWithTime() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(ZONE_ID));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(localDateTime);
    }

    public static LocalDateTime protoTimeStamp2DateTime(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atZone(ZoneId.of(ZONE_ID))
                .toLocalDateTime();
    }

    public static Timestamp toGoogleTimestampUTC(final LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.toEpochSecond(ZoneOffset.of(ZONE_ID)))
                .setNanos(localDateTime.getNano())
                .build();
    }

    public static Date toGoogleDate(final LocalDate localDate) {
        return Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonth().getValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }

}
