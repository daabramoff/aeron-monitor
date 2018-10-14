package io.aeron.monitor.util;

import io.aeron.monitoring.DriverDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigUtil {

    private ConfigUtil() {
    }

    public static List<DriverDefinition> parseDriverList(final String arg) {
        if (arg == null || arg.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver list must not be empty");
        }

        if (!Pattern.compile("(\\w+):([/_\\-\\.a-z0-9]+)(,(\\w+):([/_\\-\\.a-z0-9]+))*")
                .matcher(arg).matches()) {
            throw new IllegalArgumentException("Illegal driver list syntax");
        }

        final List<DriverDefinition> result = new ArrayList<>();
        for (final Matcher matcher = Pattern.compile("(\\w+):([/_\\-\\.a-z0-9]+)")
                .matcher(arg); matcher.find();) {
            result.add(new DriverDefinition(matcher.group(1), matcher.group(2)));
        }
        return result;
    }
}
