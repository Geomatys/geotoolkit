
package org.geotoolkit.filter.binarycomparison;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.expression.Expression;

public abstract class AbstractBinaryComparisonOperator<E extends Expression,F extends Expression> implements BinaryComparisonOperator{

    protected final E left;
    protected final F right;
    protected final boolean match;

    public AbstractBinaryComparisonOperator(E left, F right, boolean match) {
        if(left == null || right == null){
            throw new NullPointerException("Expressions can not be null");
        }
        this.left = left;
        this.right = right;
        this.match = match;
    }

    @Override
    public E getExpression1() {
        return left;
    }

    @Override
    public F getExpression2() {
        return right;
    }

    @Override
    public boolean isMatchingCase() {
        return match;
    }

}
