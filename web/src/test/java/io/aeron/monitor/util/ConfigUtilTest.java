package io.aeron.monitor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.aeron.monitor.util.ConfigUtil;
import io.aeron.monitoring.DriverDefinition;

class ConfigUtilTest {

    @ParameterizedTest
    @CsvSource({
        "'first:/dev/shm/abc_-1.tgz', 1", 
        "'first:/dev/shm/abc_-1.tgz,second:/dev/shm/xyz', 2",
        "'first:/dev/shm/abc_-1.tgz,second:/dev/shm/xyz,third:/dev/shm/foo', 3"
    })
    void shouldParseDriverList(final String arg, final int count) {
        final List<DriverDefinition> driverList = ConfigUtil.parseDriverList(arg);
        assertEquals(count, driverList.size());

        assertEquals("first", driverList.get(0).getName());
        assertEquals("/dev/shm/abc_-1.tgz", driverList.get(0).getDir());

        if (driverList.size() == 2) {
            assertEquals("second", driverList.get(1).getName());
            assertEquals("/dev/shm/xyz", driverList.get(1).getDir());
        }

        if (driverList.size() == 3) {
            assertEquals("third", driverList.get(2).getName());
            assertEquals("/dev/shm/foo", driverList.get(2).getDir());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "  " })
    void shouldNotParseEmptyDriverList(final String arg) {
        final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
            ConfigUtil.parseDriverList(arg);
        });
        assertEquals("Driver list must not be empty", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "first:/dev/shm/abc,second:/dev/shm\\xyz",
        "first:/dev/shm/abc second:/dev/shm/xyz"
    })
    void shouldNotParseInvalidDriverList(final String arg) {
        final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
            ConfigUtil.parseDriverList(arg);
        });
        assertEquals("Illegal driver list syntax", ex.getMessage());
    }
}
