package org.geotoolkit.filter.capability;

import java.util.Collection;

public abstract class TemporalCapabilities {
    public abstract TemporalOperators getTemporalOperators();

    public abstract Collection<TemporalOperand> getTemporalOperands();
}
