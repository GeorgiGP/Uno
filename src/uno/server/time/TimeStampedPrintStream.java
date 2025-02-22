package uno.server.time;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStampedPrintStream extends PrintStream {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");

    public TimeStampedPrintStream(PrintStream out) {
        super(out);
    }

    @Override
    public void println(String message) {
        String timeStamp = LocalDateTime.now().format(FORMATTER);
        super.println(timeStamp + " - " + message);
    }

    @Override
    public void println(Object obj) {
        println(String.valueOf(obj));
    }
}