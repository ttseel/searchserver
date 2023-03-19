package go.kb.searchserver;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotBootTest {
    @Test
    public void testStringToLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String strDate = "20230101";

        System.out.println(LocalDate.parse(strDate, formatter));
    }
}
