package io.aeron.monitor.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "System counter")
public class Stream {

    @ApiModelProperty("Session ID")
    private final int sessionId;

    @ApiModelProperty("Stream ID")
    private final int streamId;

    @ApiModelProperty("Channel ID")
    private final String channel;

    /**
     * Constructs new instance.
     *
     * @param sessionId session ID
     * @param streamId  stream ID
     * @param channel   channel
     */
    public Stream(final int sessionId, final int streamId, final String channel) {
        this.sessionId = sessionId;
        this.streamId = streamId;
        this.channel = channel;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + sessionId;
        result = prime * result + streamId;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        final Stream other = (Stream) obj;
        return this.sessionId == other.sessionId && this.streamId == other.streamId
                && this.channel.equals(other.channel);
    }

    @Override
    public String toString() {
        return "Stream ["
                + "sessionId=" + sessionId
                + ", streamId=" + streamId
                + ", channel=" + channel
                + "]";
    }
}
