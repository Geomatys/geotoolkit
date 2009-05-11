

package org.geotoolkit.filter.binaryexpression;

import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;

public class DefaultDivide extends AbstractBinaryExpression<Expression,Expression> implements Divide{

    public DefaultDivide(Expression left, Expression right) {
        super(left,right);
    }

    @Override
    public Object evaluate(Object object) {
        final Double val1 = left.evaluate(object, Double.class);
        final Double val2 = right.evaluate(object, Double.class);

        if(val1 == null || val2 == null){
            return null;
        }

        return val1 / val2;
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
