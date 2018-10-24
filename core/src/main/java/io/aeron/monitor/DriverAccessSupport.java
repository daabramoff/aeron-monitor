package io.aeron.monitor;

import static io.aeron.driver.status.PerImageIndicator.PER_IMAGE_TYPE_ID;
import static io.aeron.driver.status.PublisherLimit.PUBLISHER_LIMIT_TYPE_ID;
import static io.aeron.driver.status.PublisherPos.PUBLISHER_POS_TYPE_ID;
import static io.aeron.driver.status.ReceiverPos.RECEIVER_POS_TYPE_ID;
import static io.aeron.driver.status.SenderLimit.SENDER_LIMIT_TYPE_ID;
import static io.aeron.driver.status.StreamPositionCounter.CHANNEL_OFFSET;
import static io.aeron.driver.status.StreamPositionCounter.SESSION_ID_OFFSET;
import static io.aeron.driver.status.StreamPositionCounter.STREAM_ID_OFFSET;
import static io.aeron.driver.status.SystemCounterDescriptor.SYSTEM_COUNTER_TYPE_ID;

import io.aeron.driver.status.SystemCounterDescriptor;
import io.aeron.monitor.model.Counter;
import io.aeron.monitor.model.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DriverAccessSupport {

    private DriverAccessSupport() {
    }

    /**
     * Returns system counters for the driver.
     *
     * @param driver driver
     * @return system counters for the driver
     */
    public static List<Counter> getSystemCounters(final DriverAccess driver) {
        final List<Counter> res = new ArrayList<>();
        driver.reconnectIfInactive();
        driver.getCountersReader()
                .ifPresent(r -> r.forEach((counterId, typeId, keyBuffer, label) -> {
                    if (typeId == SYSTEM_COUNTER_TYPE_ID) {
                        final long value = r.getCounterValue(counterId);
                        final Counter c = new Counter(typeId,
                                SystemCounterDescriptor.get(counterId), label, value);
                        res.add(c);
                    }
                }));
        return res;
    }

    /**
     * Return streams associated with the driver.
     *
     * @param driver driver
     * @return streams
     */
    public static List<Stream> getStreams(final DriverAccess driver) {
        final Set<Stream> set = new HashSet<>();
        driver.reconnectIfInactive();
        driver.getCountersReader()
                .ifPresent(r -> r.forEach((counterId, typeId, keyBuffer, label) -> {
                    if ((typeId >= PUBLISHER_LIMIT_TYPE_ID && typeId <= RECEIVER_POS_TYPE_ID)
                            || typeId == SENDER_LIMIT_TYPE_ID || typeId == PER_IMAGE_TYPE_ID
                            || typeId == PUBLISHER_POS_TYPE_ID) {
                        set.add(new Stream(keyBuffer.getInt(SESSION_ID_OFFSET),
                                keyBuffer.getInt(STREAM_ID_OFFSET),
                                keyBuffer.getStringAscii(CHANNEL_OFFSET)));
                    }
                }));
        final List<Stream> res = new ArrayList<>(set);
        Collections.sort(res, (lhs, rhs) -> lhs.getSessionId() - rhs.getSessionId());
        return res;
    }
}
