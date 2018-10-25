package io.aeron.monitor.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Util {

    public static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        }
    };

    private Util() {}
}
