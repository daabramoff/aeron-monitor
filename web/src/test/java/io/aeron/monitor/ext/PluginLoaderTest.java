package io.aeron.monitor.ext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.aeron.monitor.ext.PluginLoader;
import io.aeron.monitor.ext.plugins.TestPlugin;
import io.aeron.monitoring.ext.Plugin;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PluginLoaderTest {

    @Autowired
    private PluginLoader pluginLoader;

    @Test
    public void shouldLoadExecuteAndShutdownPluginsFromClassPath() {
        final List<Plugin> plugins = pluginLoader.getPlugins();
        assertTrue(!plugins.isEmpty());

        Assertions.assertTimeout(Duration.ofSeconds(10), () -> {

            plugins.forEach(p -> {
                final TestPlugin tp = (TestPlugin) p;
                while (!tp.isInitialized() && !tp.isExecuted()) {
                    // NOOP
                }
            });

            pluginLoader.shutdown();

            plugins.forEach(p -> {
                final TestPlugin tp = (TestPlugin) p;
                while (!tp.isShutdown()) {
                    // NOOP
                }
            });

        });

    }
}
