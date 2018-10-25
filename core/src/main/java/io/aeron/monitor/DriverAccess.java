package io.aeron.monitor;

import static io.aeron.CncFileDescriptor.cncVersionOffset;

import io.aeron.Aeron;
import io.aeron.CncFileDescriptor;
import io.aeron.CommonContext;
import io.aeron.driver.reports.LossReportUtil;

import java.io.File;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

import org.agrona.DirectBuffer;
import org.agrona.IoUtil;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.ManyToOneRingBuffer;
import org.agrona.concurrent.status.CountersReader;

public class DriverAccess {

    private final String name;
    private final String dir;

    private Consumer<Throwable> errorHandler;

    private MappedByteBuffer cncByteBuffer;
    private DirectBuffer cncMetaData;
    private ManyToOneRingBuffer toDriver;
    private CountersReader countersReader;
    private AtomicBuffer lossReportBuffer;
    private AtomicBuffer errorLogBuffer;

    private boolean connected;

    public DriverAccess(final String name, final String dir) {
        this.name = name;
        this.dir = dir;
    }

    public synchronized void setErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Checks if the driver is active.
     *
     * @return <code>true</code> if the driver is active otherwise
     *         <code>false</code>
     */
    public boolean isActive() {
        final Consumer<String> logger = s -> {
        };

        if (!connected) {
            return false;
        }

        if (CommonContext.isDriverActive(Aeron.Context.DEFAULT_DRIVER_TIMEOUT_MS, logger,
                cncByteBuffer)) {
            return true;
        }

        reconnect();
        return CommonContext.isDriverActive(Aeron.Context.DEFAULT_DRIVER_TIMEOUT_MS, logger,
                cncByteBuffer);
    }

    public int getCncVersion() {
        return connected ? cncMetaData.getInt(cncVersionOffset(0)) : 0;
    }

    public long getStartTimeStamp() {
        return connected ? CncFileDescriptor.startTimestamp(cncMetaData) : 0L;
    }

    public long getConsumerHeartbeatTime() {
        return connected ? toDriver.consumerHeartbeatTime() : 0L;
    }

    public Optional<CountersReader> getCountersReader() {
        return connected ? Optional.of(countersReader) : Optional.empty();
    }

    public Optional<AtomicBuffer> getLossReportBuffer() {
        return connected ? Optional.of(lossReportBuffer) : Optional.empty();
    }

    public Optional<AtomicBuffer> getErrorLogBuffer() {
        return connected ? Optional.of(errorLogBuffer) : Optional.empty();
    }

    /**
     * Connects to the driver.
     */
    public synchronized void connect() {
        if (connected) {
            return;
        }

        try {
            final File cncFile = new File(dir, CncFileDescriptor.CNC_FILE);
            final File lossReportFile = LossReportUtil.file(dir);

            cncByteBuffer = IoUtil.mapExistingFile(cncFile, "cnc");
            cncMetaData = CncFileDescriptor.createMetaDataBuffer(cncByteBuffer);

            toDriver = new ManyToOneRingBuffer(
                    CncFileDescriptor.createToDriverBuffer(cncByteBuffer, cncMetaData));

            countersReader = new CountersReader(
                    CncFileDescriptor.createCountersMetaDataBuffer(cncByteBuffer, cncMetaData),
                    CncFileDescriptor.createCountersValuesBuffer(cncByteBuffer, cncMetaData),
                    StandardCharsets.US_ASCII);

            lossReportBuffer = new UnsafeBuffer(
                    IoUtil.mapExistingFile(lossReportFile, "Loss Report"));

            errorLogBuffer = CncFileDescriptor.createErrorLogBuffer(cncByteBuffer, cncMetaData);

            connected = true;
        } catch (final Exception ex) {
            if (errorHandler != null) {
                errorHandler.accept(ex);
            }
        }
    }

    public synchronized void reconnect() {
        connected = false;
        connect();
    }

    /**
     * Reconnects to the driver if it is inactive.
     */
    public void reconnectIfInactive() {
        if (!isActive()) {
            reconnect();
        }
    }

    @Override
    public String toString() {
        return "DriverAccess [name=" + name + ", dir=" + dir + "]";
    }
}
