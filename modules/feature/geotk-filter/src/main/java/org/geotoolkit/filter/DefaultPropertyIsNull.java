

package org.geotoolkit.filter;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;

public class DefaultPropertyIsNull implements PropertyIsNull {

    private final Expression exp;

    public DefaultPropertyIsNull(Expression exp) {
        if(exp == null){
            throw new NullPointerException("Expression can not be null");
        }
        this.exp = exp;
    }

    @Override
    public Expression getExpression() {
        return exp;
    }

    @Override
    public boolean evaluate(Object object) {
        Object obj = exp.evaluate(object);
        return obj == null;
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
