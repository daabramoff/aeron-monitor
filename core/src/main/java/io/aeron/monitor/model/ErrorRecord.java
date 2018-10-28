package io.aeron.monitor.model;

import static io.aeron.monitor.model.Util.DATE_FORMAT;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.text.DateFormat;
import java.util.Date;

@ApiModel(description = "Error record")
public class ErrorRecord {

    @ApiModelProperty("Observation count")
    private final long observationCount;

    @ApiModelProperty("First observation timestamp")
    private final long firstObservationTimestamp;

    @ApiModelProperty("First observation time")
    private final String firstObservationTime;

    @ApiModelProperty("Last observation timestamp")
    private final long lastObservationTimestamp;

    @ApiModelProperty("Last observation time")
    private final String lastObservationTime;

    @ApiModelProperty("Encoded exception")
    private final String encodedException;

    /**
     * Constructs new instance.
     *
     * @param observationCount          observation count
     * @param firstObservationTimestamp first observation timestamp
     * @param lastObservationTimestamp  last observation timestamp
     * @param encodedException          encoded exception
     */
    public ErrorRecord(
            final int observationCount,
            final long firstObservationTimestamp,
            final long lastObservationTimestamp,
            final String encodedException) {
        this.observationCount = observationCount;
        this.firstObservationTimestamp = firstObservationTimestamp;
        this.lastObservationTimestamp = lastObservationTimestamp;
        this.encodedException = encodedException;

        final DateFormat df = DATE_FORMAT.get();
        firstObservationTime = df.format(new Date(firstObservationTimestamp));
        lastObservationTime = df.format(new Date(lastObservationTimestamp));
    }

    public long getObservationCount() {
        return observationCount;
    }

    public long getFirstObservationTimestamp() {
        return firstObservationTimestamp;
    }

    public String getFirstObservationTime() {
        return firstObservationTime;
    }

    public long getLastObservationTimestamp() {
        return lastObservationTimestamp;
    }

    public String getLastObservationTime() {
        return lastObservationTime;
    }

    public String getEncodedException() {
        return encodedException;
    }

    @Override
    public String toString() {
        return "ErrorRecord ["
                + "observationCount=" + observationCount
                + ", firstObservationTimestamp=" + firstObservationTimestamp
                + ", firstObservationTime=" + firstObservationTime
                + ", lastObservationTimestamp=" + lastObservationTimestamp
                + ", lastObservationTime=" + lastObservationTime
                + ", encodedException=" + encodedException
                + "]";
    }
}
