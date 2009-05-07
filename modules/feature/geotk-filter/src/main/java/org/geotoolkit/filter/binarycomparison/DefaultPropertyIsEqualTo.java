
package org.geotoolkit.filter.binarycomparison;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;


public class DefaultPropertyIsEqualTo extends AbstractPropertyEqual implements PropertyIsEqualTo{

    public DefaultPropertyIsEqualTo(Expression left, Expression right, boolean match) {
        super(left,right,match);
    }
    
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
