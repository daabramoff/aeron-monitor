package io.aeron.monitor.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Connection {

    private static final Pattern PATTERN = Pattern
            .compile("(?:^[^:]+:)\\s+(\\d+)\\s+(?:[-]?\\d+)\\s+(\\d+)\\s+([^\\s]+).*");

    final int id;
    private final int streamId;
    private final String channel;

    /**
     * Constructs new instance.
     * 
     * @param label connection label from the correspondent counter
     */
    public Connection(final String label) {
        final Matcher matcher = PATTERN.matcher(label);
        matcher.find();
        id = Integer.parseInt(matcher.group(1));
        streamId = Integer.parseInt(matcher.group(2));
        channel = matcher.group(3);
    }

    public int getId() {
        return id;
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
        result = prime * result + streamId;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        final Connection other = (Connection) obj;
        if (id == other.id && streamId == other.streamId && channel.equals(other.channel)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Connection [id=" + id + ", streamId=" + streamId + ", channel=" + channel + "]";
    }
}