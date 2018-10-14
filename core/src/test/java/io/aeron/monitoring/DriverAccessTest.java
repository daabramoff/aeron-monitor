package io.aeron.monitoring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.MediaDriver.Context;

@Disabled("Time consuming")
class DriverAccessTest {

    @Test
    void shouldConnectToMediaDriver() {
        final Context ctx = new MediaDriver.Context();
        final MediaDriver driver = MediaDriver.launchEmbedded(ctx);
        final String dir = driver.aeronDirectoryName();

        final DriverAccess driverAccess = new DriverAccess("test-name", dir);
        driverAccess.connect();

        assertTrue(driverAccess.isConnected());
        assertTrue(driverAccess.isActive());

        driver.close();
        ctx.deleteAeronDirectory();
        ctx.close();
    }

    @Test
    void shouldDetectInactiveDriver() throws InterruptedException {
        final Context ctx = new MediaDriver.Context();
        final MediaDriver driver = MediaDriver.launchEmbedded(ctx);
        final String dir = driver.aeronDirectoryName();

        final DriverAccess driverAccess = new DriverAccess("test-name", dir);
        driverAccess.connect();

        assertTrue(driverAccess.isActive());

        driver.close();

        TimeUnit.MILLISECONDS.sleep(ctx.driverTimeoutMs() + 1000);

        assertFalse(driverAccess.isActive());

        ctx.deleteAeronDirectory();
        ctx.close();
    }

    @Test
    void shouldDetectAcriveStateIfConnectedToExistingDir() throws InterruptedException {
        // create driver dir
        final Context ctx1 = new MediaDriver.Context();
        final MediaDriver driver1 = MediaDriver.launchEmbedded(ctx1);
        final String dir = driver1.aeronDirectoryName();
        driver1.close();
        TimeUnit.MILLISECONDS.sleep(ctx1.driverTimeoutMs() + 1000);
        ctx1.close();

        final DriverAccess driverAccess = new DriverAccess("test-name", dir);
        driverAccess.connect();

        // should not be active
        assertFalse(driverAccess.isActive());

        // launch new driver in the existing dir
        final Context ctx2 = new MediaDriver.Context();
        ctx2.aeronDirectoryName(dir);
        final MediaDriver driver2 = MediaDriver.launchEmbedded(ctx2);

        // should detect now
        assertTrue(driverAccess.isActive());

        driver2.close();
        ctx2.deleteAeronDirectory();

        ctx2.close();
    }
}
