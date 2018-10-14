package io.aeron.monitoring;

public class DriverDefinition {

    private final String name;
    private final String dir;

    public DriverDefinition(final String name, final String dir) {
        this.name = name;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return "DriverDefinition [name=" + name + ", dir=" + dir + "]";
    }
}
