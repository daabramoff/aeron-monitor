package io.aeron.monitor.ext;

import io.aeron.monitor.DriverAccess;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/*
 * Defines Plug-in.
 *
 * Plug-in is an optional task running in a separate thread within a thread pool,
 */
@ApiModel(description = "Plugin")
public interface Plugin extends Runnable {

    /**
     * Method called ones before the plug-in is dispatched to the thread pool.
     *
     * @param args    Raw application arguments: exactly how the were passed to the
     *                application
     * @param drivers Drivers to process
     */
    void init(String[] args, Map<String, DriverAccess> drivers);

    /**
     * Method called ones before the application thread pool is stopped.
     */
    void shutdown();

    /**
     * Name of the plug-in to be shown in the UI.
     */
    @ApiModelProperty("Plugin name")
    String getName();

    /**
     * Version of the plug-in to be shown in the UI.
     */
    @ApiModelProperty("Plugin version")
    String getVersion();
}
