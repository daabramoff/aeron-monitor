package io.aeron.monitor.api;

import io.aeron.monitor.Const;
import io.aeron.monitor.DriverAccess;
import io.aeron.monitor.DriverAccessSupport;
import io.aeron.monitor.ext.Plugin;
import io.aeron.monitor.model.ErrorRecord;
import io.aeron.monitor.model.LossRecord;
import io.aeron.monitor.model.Stream;
import io.aeron.monitor.model.SysInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    @Qualifier(Const.BEAN_NAME_PLUGINS)
    private List<Plugin> plugins;

    /**
     * Returns system information.
     * 
     * @return system information
     */
    @RequestMapping(method = RequestMethod.GET, value = "sysInfo")
    @ApiOperation("Returns system information")
    public SysInfo getSysInfo() {
        return new SysInfo();
    }

    /**
     * Returns plug-in list.
     * 
     * @return the list of the plug-ins loaded at startup
     */
    @RequestMapping(method = RequestMethod.GET, value = "plugins")
    @ApiOperation("Returns plugin list")
    public ResponseEntity<List<Plugin>> getPlugins() {
        System.out.println(plugins);
        if (!plugins.isEmpty()) {
            return new ResponseEntity<>(plugins, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Returns driver list.
     * 
     * @return the list of the drivers
     */
    @RequestMapping(method = RequestMethod.GET, value = "drivers")
    @ApiOperation("Returns counters related to the Media Driver entirely")
    public Set<String> getDrivers() {
        return drivers.keySet();
    }

    /**
     * Returns driver information.
     *
     * @param name name of the driver
     * @return {@link Map} which contains the driver's name, CNC version and active
     *         status
     */
    @RequestMapping(method = RequestMethod.GET, value = "drivers/{name}")
    @ApiOperation("Returns driver information")
    public ResponseEntity<Map<String, Object>> getDriver(
            @PathVariable("name")
            @ApiParam("Driver name")
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);
        if (d.isPresent()) {
            final DriverAccess driver = d.get();
            final Map<String, Object> m = new HashMap<>();
            m.put(RESPONSE_KEY_DRIVER_NAME, driver.getName());
            m.put(RESPONSE_KEY_ACTIVE, driver.isActive());
            m.put(RESPONSE_KEY_CNC_VERSION, driver.getCncVersion());
            return new ResponseEntity<>(m, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Returns system counters for the driver.
     *
     * @param name name of the driver
     * @return {@link Map} which contains name of the driver, active status and
     *         {@link List} of the counters
     */
    @RequestMapping(method = RequestMethod.GET, value = "cnc/systemCounters/{name}")
    @ApiOperation("Returns system counters")
    public ResponseEntity<Map<String, Object>> getCounters(
            @PathVariable("name")
            @ApiParam("Driver name")
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);
        if (d.isPresent()) {
            final DriverAccess driver = d.get();
            final Map<String, Object> m = new HashMap<>();
            m.put(RESPONSE_KEY_DRIVER_NAME, driver.getName());
            m.put(RESPONSE_KEY_ACTIVE, driver.isActive());
            m.put(RESPONSE_KEY_COUNTERS, DriverAccessSupport.getSystemCounters(driver));
            return new ResponseEntity<>(m, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Returns streams associated with the driver.
     *
     * @param name name of the driver
     * @return {@link List} of streams
     */
    @RequestMapping(method = RequestMethod.GET, value = "cnc/streams/{name}")
    @ApiOperation("Returns publications")
    public ResponseEntity<List<Stream>> getStreams(
            @PathVariable("name")
            @ApiParam("Driver name")
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);
        return d.isPresent()
                ? new ResponseEntity<>(DriverAccessSupport.getStreams(d.get()), HttpStatus.OK)
                : new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    /**
     * Returns loss records for the driver.
     *
     * @param name name of the driver
     * @return {@link List} of loss records
     */
    @RequestMapping(method = RequestMethod.GET, value = "lossRecords/{name}")
    @ApiOperation("Returns loss records")
    public ResponseEntity<List<LossRecord>> getLossRecords(
            @PathVariable("name")
            @ApiParam("Driver name")
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);
        return d.isPresent()
                ? new ResponseEntity<>(DriverAccessSupport.getLossRecords(d.get()), HttpStatus.OK)
                : new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    /**
     * Returns error records for the driver.
     *
     * @param name name of the driver
     * @return {@link List} of error records
     */
    @RequestMapping(method = RequestMethod.GET, value = "errorRecords/{name}")
    @ApiOperation("Returns error records")
    public ResponseEntity<List<ErrorRecord>> getErrorRecords(
            @PathVariable("name")
            @ApiParam("Driver name")
            final String name) {
        final Optional<DriverAccess> d = getConnectedDriver(name);
        return d.isPresent()
                ? new ResponseEntity<>(DriverAccessSupport.getErrorRecords(d.get()), HttpStatus.OK)
                : new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
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
