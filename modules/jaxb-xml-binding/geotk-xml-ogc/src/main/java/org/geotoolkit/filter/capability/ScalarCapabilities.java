package org.geotoolkit.filter.capability;

public abstract class ScalarCapabilities {
    public abstract ComparisonOperators getComparisonOperators();

    public abstract boolean hasLogicalOperators();

    public abstract ArithmeticOperators getArithmeticOperators();
}
