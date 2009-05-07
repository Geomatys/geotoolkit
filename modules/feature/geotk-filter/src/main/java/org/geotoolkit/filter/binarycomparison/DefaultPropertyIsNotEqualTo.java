

package org.geotoolkit.filter.binarycomparison;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.expression.Expression;

public class DefaultPropertyIsNotEqualTo extends AbstractPropertyEqual implements PropertyIsNotEqualTo{

    public DefaultPropertyIsNotEqualTo(Expression left, Expression right, boolean match) {
        super(left,right,match);
    }

    @Override
    public boolean evaluate(Object candidate) {
        return !super.evaluate(candidate);
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
