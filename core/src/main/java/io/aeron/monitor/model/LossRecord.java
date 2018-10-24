package io.aeron.monitor.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApiModel(description = "Loss record")
public class LossRecord {

    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        }
    };

    @ApiModelProperty("Observation count")
    private final long observationCount;

    @ApiModelProperty("Total bytes lost")
    private final long totalBytesLost;

    @ApiModelProperty("First observation timestamp")
    private final long firstObservationTimestamp;

    @ApiModelProperty("Last observation timestamp")
    private final long lastObservationTimestamp;

    @ApiModelProperty("Session ID")
    private final int sessionId;

    @ApiModelProperty("Stream ID")
    private final int streamId;

    @ApiModelProperty("Channel ID")
    private final String channel;

    @ApiModelProperty("Source")
    private final String source;

    /**
     * Constructs new instance.
     *
     * @param observationCount          observation count
     * @param totalBytesLost            total bytes lost
     * @param firstObservationTimestamp first observation timestamp
     * @param lastObservationTimestamp  last observation timestamp
     * @param sessionId                 session id
     * @param streamId                  stream id
     * @param channel                   channel
     * @param source                    source
     */
    public LossRecord(
            final long observationCount,
            final long totalBytesLost,
            final long firstObservationTimestamp,
            final long lastObservationTimestamp, 
            final int sessionId,
            final int streamId,
            final String channel,
            final String source) {
        this.observationCount = observationCount;
        this.totalBytesLost = totalBytesLost;
        this.firstObservationTimestamp = firstObservationTimestamp;
        this.lastObservationTimestamp = lastObservationTimestamp;
        this.sessionId = sessionId;
        this.streamId = streamId;
        this.channel = channel;
        this.source = source;
    }

    public long getObservationCount() {
        return observationCount;
    }

    public long getTotalBytesLost() {
        return totalBytesLost;
    }

    public long getFirstObservationTimestamp() {
        return firstObservationTimestamp;
    }

    public String getFirstObservationTime() {
        return DATE_FORMAT.get().format(new Date(firstObservationTimestamp));
    }

    public long getLastObservationTimestamp() {
        return lastObservationTimestamp;
    }

    public String getLastObservationTime() {
        return DATE_FORMAT.get().format(new Date(lastObservationTimestamp));
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getStreamId() {
        return streamId;
    }

    public String getChannel() {
        return channel;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "LossRecord ["
                + "observationCount=" + observationCount
                + ", totalBytesLost=" + totalBytesLost
                + ", firstObservationTimestamp=" + firstObservationTimestamp
                + ", lastObservationTimestamp=" + lastObservationTimestamp
                + ", sessionId=" + sessionId
                + ", streamId=" + streamId
                + ", channel=" + channel
                + ", source=" + source
                + "]";
    }
}
