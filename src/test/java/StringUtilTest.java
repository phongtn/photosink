import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringUtilTest {

    @Test
    public void testReplaceSymbol() {
        String envKeyCfg = "${API_ACCESS_KEY}";
        String regex = "\\$\\{|}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(envKeyCfg);
        String envKey = matcher.replaceAll("");
        assertThat(envKey, equalTo("API_ACCESS_KEY"));
    }
}
