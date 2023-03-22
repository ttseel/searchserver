package go.kb.searchserver.common.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    public static Optional<String> parseUrlFromString(String urlIncludedString) {
        Optional<String> parseResult = Optional.empty();
        String urlPattern = "((https?://)?(www\\.)?[\\w-]+(\\.[\\w-]+)+([^\\s]*))";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(urlIncludedString);

        if (matcher.find()) {
            parseResult = Optional.ofNullable(matcher.group());
        }
        return parseResult;
    }
}
