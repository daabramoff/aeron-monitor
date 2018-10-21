package io.aeron.monitor;

import static io.aeron.driver.status.PublisherPos.PUBLISHER_POS_TYPE_ID;
import static io.aeron.driver.status.ReceiverPos.RECEIVER_POS_TYPE_ID;
import static io.aeron.driver.status.SubscriberPos.SUBSCRIBER_POSITION_TYPE_ID;
import static io.aeron.driver.status.SystemCounterDescriptor.SYSTEM_COUNTER_TYPE_ID;

import io.aeron.driver.status.SystemCounterDescriptor;
import io.aeron.monitor.model.Connection;
import io.aeron.monitor.model.Counter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    public static List<Connection> getPublications(final DriverAccess driver) {
        return getConnections(driver,
                typeId -> typeId == RECEIVER_POS_TYPE_ID || typeId == SUBSCRIBER_POSITION_TYPE_ID);
    }
    
    public static List<Connection> getSubscriptions(final DriverAccess driver) {
        return getConnections(driver, typeId -> typeId == PUBLISHER_POS_TYPE_ID);
    }

    private static List<Connection> getConnections(final DriverAccess driver,
            final Predicate<Integer> cond) {
        final Set<Connection> set = new HashSet<>();
        driver.reconnectIfInactive();
        driver.getCountersReader()
                .ifPresent(r -> r.forEach((counterId, typeId, keyBuffer, label) -> {
                    if (cond.test(typeId)) {
                        set.add(new Connection(label));
                    }
                }));
        final List<Connection> res = new ArrayList<>(set);
        Collections.sort(res, (lhs, rhs) -> lhs.getId() - rhs.getId());
        return res;
    }
}
