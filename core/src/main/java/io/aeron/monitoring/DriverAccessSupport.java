package io.aeron.monitoring;

import io.aeron.driver.status.SystemCounterDescriptor;
import io.aeron.monitoring.model.Counter;

import java.util.ArrayList;
import java.util.List;

import org.agrona.concurrent.status.CountersReader;

public final class DriverAccessSupport {

    private DriverAccessSupport() {
    }

    public static List<Counter> getSystemCounters(final DriverAccess driver) {
        if (!driver.isActive()) {
            driver.reconnect();
        }

        final List<Counter> counters = new ArrayList<>();
        final CountersReader reader = driver.getCountersReader();

        reader.forEach((counterId, typeId, keyBuffer, label) -> {
            if (typeId == SystemCounterDescriptor.SYSTEM_COUNTER_TYPE_ID) {
                final long value = reader.getCounterValue(counterId);
                final Counter c = new Counter(typeId, SystemCounterDescriptor.get(counterId), label,
                        value);
                counters.add(c);
            }
        });
        return counters;
    }
}
