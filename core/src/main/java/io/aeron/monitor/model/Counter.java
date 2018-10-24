package io.aeron.monitor.model;

import io.aeron.driver.status.SystemCounterDescriptor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "System counter")
public class Counter {

    @ApiModelProperty("Counter ID")
    private final int typeId;

    @ApiModelProperty("Counter descriptor")
    private final SystemCounterDescriptor descriptor;

    @ApiModelProperty("Counter label")
    private final String label;

    @ApiModelProperty("Counter value")
    private final long value;

    /**
     * Constructs new instance.
     * 
     * @param typeId     type of the counter
     * @param descriptor descriptor of the counter
     * @param label      the counter label
     * @param value      the counter value
     */
    public Counter(
            final int typeId,
            final SystemCounterDescriptor descriptor,
            final String label,
            final long value) {
        this.typeId = typeId;
        this.descriptor = descriptor;
        this.label = label;
        this.value = value;
    }

    public SystemCounterDescriptor getDescriptor() {
        return descriptor;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getLabel() {
        return label;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CounterValue ["
                + "typeId=" + typeId 
                + ", descriptor=" + descriptor 
                + ", label=" + label
                + ", value=" + value 
                + "]";
    }
}
