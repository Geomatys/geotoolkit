

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.ScalarCapabilities;

public class DefaultScalarCapabilities implements ScalarCapabilities {

    private final boolean logical;
    private final ComparisonOperators comparisons;
    private final ArithmeticOperators arithmetics;

    public DefaultScalarCapabilities(boolean logical, ComparisonOperators comparisons, ArithmeticOperators arithmetics) {
        this.logical = logical;
        this.comparisons = comparisons;
        this.arithmetics = arithmetics;
    }

    @Override
    public boolean hasLogicalOperators() {
        return logical;
    }

    @Override
    public ComparisonOperators getComparisonOperators() {
        return comparisons;
    }

    @Override
    public ArithmeticOperators getArithmeticOperators() {
        return arithmetics;
    }

}
