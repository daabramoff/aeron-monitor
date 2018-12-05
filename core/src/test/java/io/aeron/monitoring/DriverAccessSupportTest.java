package io.aeron.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.aeron.Aeron;
import io.aeron.driver.MediaDriver;
import io.aeron.driver.MediaDriver.Context;
import io.aeron.monitor.DriverAccess;
import io.aeron.monitor.DriverAccessSupport;
import io.aeron.monitor.model.Stream;

class DriverAccessSupportTest {

    private static Context ctx;
    private static MediaDriver driver;

    @BeforeAll
    static void beforeAll() {
        ctx = new MediaDriver.Context();
        driver = MediaDriver.launchEmbedded(ctx);

    }

    @AfterAll
    static void afterAll() {
        driver.close();
        ctx.deleteAeronDirectory();
        ctx.close();
    }

    @ParameterizedTest
    @CsvSource({
        "'aeron:udp?endpoint=localhost:54325', 10, 1", 
        "'aeron:udp?endpoint=localhost:54326', 11, 2", 
        "'aeron:ipc',                          12, 3", 
    })
    void shouldReturnSubscriptionsAndPublications(final String channel, final int streamId,
            final int n) throws Exception {
        @SuppressWarnings("resource")
        final Aeron aeron = Aeron
                .connect(new Aeron.Context().aeronDirectoryName(driver.aeronDirectoryName()));

        aeron.addPublication(channel, streamId);
        aeron.addSubscription(channel, streamId);
        TimeUnit.MILLISECONDS.sleep(200);

        final DriverAccess driverAccess = new DriverAccess("test", driver.aeronDirectoryName());
        driverAccess.connect();

        final List<Stream> streams = DriverAccessSupport.getStreams(driverAccess);
        assertEquals(n, streams.size());
        assertEquals(1, streams.stream()
                .filter(c -> c.getChannel().equals(channel) && c.getStreamId() == streamId)
                .count());
    }
}
