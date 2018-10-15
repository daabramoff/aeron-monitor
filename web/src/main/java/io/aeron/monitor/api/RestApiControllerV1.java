package io.aeron.monitor.api;

import io.aeron.monitor.Const;
import io.aeron.monitoring.DriverAccess;
import io.aeron.monitoring.DriverAccessSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping(value = "/api/v1", method = RequestMethod.GET)
public class RestApiControllerV1 {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestApiControllerV1.class);

    private static final String RESPONSE_KEY_ACTIVE = "active";
    private static final String RESPONSE_KEY_COUNTERS = "counters";
    private static final String RESPONSE_KEY_CNC_VERSION = "cncVersion";
    private static final String RESPONSE_KEY_DRIVER_NAME = "driverName";
    
    @Autowired
    @Qualifier(Const.BEAN_NAME_DRIVERS)
    private Map<String, DriverAccess> drivers;
    
    @RequestMapping("drivers")
    @ApiOperation("Returns counters related to the Media Driver entirely")
    public Set<String> getDrivers() {
        return drivers.keySet();
    }

    @RequestMapping("drivers/{name}")
    @ApiOperation("Returns driver information")
    public Map<String, Object> getDriver(
            @PathVariable("name") 
            @ApiParam("Driver name") 
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);

        if (!d.isPresent()) {
            return Collections.emptyMap();
        }

        final DriverAccess driver = d.get();
        final Map<String, Object> m = new HashMap<>();
        m.put(RESPONSE_KEY_DRIVER_NAME, driver.getName());
        m.put(RESPONSE_KEY_ACTIVE, driver.isActive());
        m.put(RESPONSE_KEY_CNC_VERSION, driver.getCncVersion());
        return m;
    }

    @RequestMapping("cnc/systemCounters/{name}")
    @ApiOperation("Returns system counters")
    public Map<String, Object> getCounters(
            @PathVariable("name") 
            @ApiParam("Driver name") 
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);

        if (!d.isPresent()) {
            return Collections.emptyMap();
        }

        final DriverAccess driver = d.get();
        final Map<String, Object> m = new HashMap<>();
        m.put(RESPONSE_KEY_DRIVER_NAME, driver.getName());
        m.put(RESPONSE_KEY_ACTIVE, driver.isActive());
        m.put(RESPONSE_KEY_COUNTERS, DriverAccessSupport.getSystemCounters(driver));
        return m;
    }

    @SuppressWarnings("unused")
    private Optional<DriverAccess> getConnectedDriver(final Optional<String> name) {
        return getConnectedDriver(name.orElse(Const.DEFAULT_DRIVER_NAME));
    }

    private Optional<DriverAccess> getConnectedDriver(final String name) {
        LOG.debug("Accessing driver: {}", name);
        final DriverAccess d = drivers.get(name);

        if (d == null) {
            LOG.warn("Driver not found: {}", name);
            return Optional.empty();
        }

        if (d.isConnected()) {
            LOG.debug("Connected driver found: {}", name);
            return Optional.of(d);
        }
        
        LOG.debug("Connecting to driver...");
        d.connect();
        if (d.isConnected()) {
            LOG.debug("Connected to driver: {}", d);
            return Optional.of(d);
        } else {
            LOG.debug("Failed to connect to driver: {}", d);
            return Optional.empty();
        }
    }
}
