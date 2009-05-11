

package org.geotoolkit.filter.binarycomparison;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.expression.Expression;


public class DefaultPropertyIsGreaterThan extends AbstractBinaryComparisonOperator<Expression,Expression> implements PropertyIsGreaterThan{

    public DefaultPropertyIsGreaterThan(Expression left, Expression right, boolean match) {
        super(left,right,match);
    }

    @Override
    public boolean evaluate(Object object) {
        Object objleft = left.evaluate(object);

        if(!(objleft instanceof Comparable)){
            return false;
        }

        Object objright = right.evaluate(object,objleft.getClass());

        if(objright == null){
            return false;
        }

        return ((Comparable)objleft).compareTo(objright) > 0;
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
