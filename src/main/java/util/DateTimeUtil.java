package util;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {

    static final String ZONE_ID = "Asia/Ho_Chi_Minh";

    public static LocalDateTime protoTimeStamp2DateTime(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
                .atZone(ZoneId.of(ZONE_ID))
                .toLocalDateTime();
    }
}
