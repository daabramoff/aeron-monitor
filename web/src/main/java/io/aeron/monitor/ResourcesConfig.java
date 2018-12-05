package io.aeron.monitor;

import io.aeron.CommonContext;
import io.aeron.monitor.DriverAccess;
import io.aeron.monitor.util.ConfigUtil;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ResourcesConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesConfig.class);

    private static final String DEFAULT_DRIVER_LIST = Const.DEFAULT_DRIVER_NAME + ':'
            + CommonContext.AERON_DIR_PROP_DEFAULT;

    @Value("${aeron.drivers}")
    private String driverList;

    /**
     * Provides the singleton which accesses the observed drivers.
     *
     * @return {@link Map} where key is the driver's name and value is the driver's
     *         accessor
     */
    @Bean(Const.BEAN_NAME_DRIVERS)
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Map<String, DriverAccess> getDrivers() {
        final String list = !driverList.trim().isEmpty() ? driverList : DEFAULT_DRIVER_LIST;
        return Collections.unmodifiableMap(ConfigUtil.parseDriverList(list)
                .stream().map(d -> {
                    LOG.info("{}", d);
                    return new DriverAccess(d.getName(), d.getDir());
                }).collect(Collectors.toMap(DriverAccess::getName, Function.identity())));
    }
}
