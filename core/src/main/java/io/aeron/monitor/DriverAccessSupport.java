package io.aeron.monitor;

import io.aeron.driver.status.SystemCounterDescriptor;
import io.aeron.monitor.model.Counter;

import java.util.ArrayList;
import java.util.List;

public final class DriverAccessSupport {

    private DriverAccessSupport() {
    }

    public static List<Counter> getSystemCounters(final DriverAccess driver) {
        final List<Counter> counters = new ArrayList<>();

        driver.reconnectIfInactive();

        driver.getCountersReader().ifPresent(r -> 
                r.forEach((counterId, typeId, keyBuffer, label) -> {
                    if (typeId == SystemCounterDescriptor.SYSTEM_COUNTER_TYPE_ID) {
                        final long value = r.getCounterValue(counterId);
                        final Counter c = new Counter(typeId,
                                SystemCounterDescriptor.get(counterId), label, value);
                        counters.add(c);
                    }
                }));
        return counters;
    }
}
