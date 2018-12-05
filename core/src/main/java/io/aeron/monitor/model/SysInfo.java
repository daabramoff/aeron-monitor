package io.aeron.monitor.model;

import static io.aeron.monitor.model.Util.DATE_FORMAT;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@ApiModel(description = "System information")
public class SysInfo {

    @ApiModelProperty("JVM name")
    private final String jvmName;

    @ApiModelProperty("JVM version")
    private final String jvmVersion;

    @ApiModelProperty("JVM vendor")
    private final String jvmVendor;

    @ApiModelProperty("JVM start timestamp")
    private final long jvmStartTimeStamp;

    @ApiModelProperty("JVM start time")
    private final String jvmStartTime;

    @ApiModelProperty("JVM process ID")
    private final int jvmPid;

    @ApiModelProperty("Host name")
    private final String hostName;

    /**
     * Constructs new instance.
     */
    public SysInfo() {
        final RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

        jvmName = mxBean.getVmName();
        jvmVersion = mxBean.getVmVersion();
        jvmVendor = mxBean.getVmVendor();
        jvmStartTimeStamp = mxBean.getStartTime();
        jvmStartTime = DATE_FORMAT.get().format(new Date(jvmStartTimeStamp));

        int pid = 0;
        try {
            final Field jvm = mxBean.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            final Object mgmt = jvm.get(mxBean);
            final Method pidMethod = mgmt.getClass().getDeclaredMethod("getProcessId");
            pidMethod.setAccessible(true);
            pid = (int) pidMethod.invoke(mgmt);
        } catch (final Exception ignore) {
            // NOOP
        }
        jvmPid = pid;

        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (final UnknownHostException ignore) {
            // NOOP
        }

        hostName = addr != null ? addr.getHostName() : null;
    }

    public String getJvmName() {
        return jvmName;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public String getJvmVendor() {
        return jvmVendor;
    }

    public long getJvmStartTimeStamp() {
        return jvmStartTimeStamp;
    }

    public String getJvmStartTime() {
        return jvmStartTime;
    }

    public int getJvmPid() {
        return jvmPid;
    }

    public String getHostName() {
        return hostName;
    }

    @Override
    public String toString() {
        return "SysInfo ["
                + "jvmName=" + jvmName
                + ", jvmVersion=" + jvmVersion
                + ", jvmVendor=" + jvmVendor
                + ", jvmStartTimeStamp=" + jvmStartTimeStamp
                + ", jvmStartTime=" + jvmStartTime
                + ", jvmPid=" + jvmPid
                + ", hostName=" + hostName
                + "]";
    }
}
