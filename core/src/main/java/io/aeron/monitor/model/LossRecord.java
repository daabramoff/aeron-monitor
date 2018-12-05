package io.aeron.monitor.model;

import static io.aeron.monitor.model.Util.DATE_FORMAT;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.text.DateFormat;
import java.util.Date;

@ApiModel(description = "Loss record")
public class LossRecord {

    @ApiModelProperty("Observation count")
    private final long observationCount;

    @ApiModelProperty("Total bytes lost")
    private final long totalBytesLost;

    @ApiModelProperty("First observation timestamp")
    private final long firstObservationTimestamp;

    @ApiModelProperty("First observation time")
    private final String firstObservationTime;

    @ApiModelProperty("Last observation timestamp")
    private final long lastObservationTimestamp;

    @ApiModelProperty("Last observation time")
    private final String lastObservationTime;

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

        final DateFormat df = DATE_FORMAT.get();
        firstObservationTime = df.format(new Date(firstObservationTimestamp));
        lastObservationTime = df.format(new Date(lastObservationTimestamp));
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
        return firstObservationTime;
    }

    public long getLastObservationTimestamp() {
        return lastObservationTimestamp;
    }

    public String getLastObservationTime() {
        return lastObservationTime;
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
                + ", firstObservationTime=" + firstObservationTime
                + ", lastObservationTimestamp=" + lastObservationTimestamp
                + ", lastObservationTime=" + lastObservationTime
                + ", sessionId=" + sessionId
                + ", streamId=" + streamId
                + ", channel=" + channel
                + ", source=" + source
                + "]";
    }
}
