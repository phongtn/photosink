import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wind.google.photos.PhotoService;
import com.wind.google.photos.VideoDto;
import com.wind.module.ConfigModule;
import com.wind.module.GoogleAPIMaterial;
import com.wind.module.ServiceModule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StreamUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GooglePhotosTest {

    private static final Logger logger = LoggerFactory.getLogger(GooglePhotosTest.class.getName());

    private static PhotoService photoService;

    @BeforeClass
    public static void setup() {
        Injector injector = Guice.createInjector(new GoogleAPIMaterial(), new ConfigModule(), new ServiceModule());
        photoService = injector.getInstance(PhotoService.class);
    }

    @Test
    public void testDownloadMultiVideoFile() {
        List<String> videoIDs = List.of("AAUJWKB2h8zXzqeR3fvsAEX1_IYsljgc8gmRJlqK9_Jq8wx5DqMb2w-yF3SWUT3oh5_YSKsqhvZoprmlBb3Q4tE3jSQ-vcOUbw",
                "AAUJWKASb5DvpRfi0doHpZ9IEv680SicF2e_imnxnVbpM0pHmX5BheE5bqp5OdcTJBndXsitmr7hKP7DtO9ZsuqRBj0IvQkbqw");
        videoIDs.forEach(videoID -> {
            VideoDto videoDto = photoService.getVideo(videoID);
            this.testReadBinaryVideo(videoDto);
//            this.testReadBinaryVideo(videoDto);
        });
    }

    public void testReadBinaryVideo(VideoDto videoDto) {
        String urlDownload = videoDto.getUrlDownload();
        logger.info("\nname: {} \nurl {}", videoDto.getName(), urlDownload);
        String FILE_NAME = videoDto.getName();
        long start = System.currentTimeMillis();
        try (BufferedInputStream in = new BufferedInputStream(new URL(urlDownload).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            long totalBytes = 0;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                if (totalBytes == 0) {
                    logger.info("Time to get first bytes: {}", System.currentTimeMillis() - start);
                }
                totalBytes += bytesRead;
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            logger.info("{} total size: {}", FILE_NAME, StreamUtil.humanReadableByteCountBin(totalBytes));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


}
