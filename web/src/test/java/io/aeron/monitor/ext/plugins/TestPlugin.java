package io.aeron.monitor.ext.plugins;

import java.util.Map;

import io.aeron.monitor.DriverAccess;
import io.aeron.monitor.ext.Plugin;

public class TestPlugin implements Plugin {

    private volatile boolean initialized;
    private volatile boolean executed;
    private volatile boolean shutdown;

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void init(final String[] args, final Map<String, DriverAccess> drivers) {
        initialized = true;
    }

    @Override
    public void run() {
        executed = true;
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getVersion() {
        return this.getClass().getName();
    }
}
