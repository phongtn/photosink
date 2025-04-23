import org.junit.Test;
import util.StreamUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamUtilTest {

    @Test
    public void testHumanReadableByteCountBin() {
        // Test bytes
        assertThat(StreamUtil.humanReadableByteCountBin(500), equalTo("500 B"));
        
        // Test kilobytes
        assertThat(StreamUtil.humanReadableByteCountBin(1024), equalTo("1.0 KiB"));
        assertThat(StreamUtil.humanReadableByteCountBin(2048), equalTo("2.0 KiB"));
        
        // Test megabytes
        assertThat(StreamUtil.humanReadableByteCountBin(1048576), equalTo("1.0 MiB"));
        
        // Test gigabytes
        assertThat(StreamUtil.humanReadableByteCountBin(1073741824), equalTo("1.0 GiB"));
        
        // Test negative values
        assertThat(StreamUtil.humanReadableByteCountBin(-1024), equalTo("-1.0 KiB"));
    }
}