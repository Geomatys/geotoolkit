

package org.geotoolkit.filter.binaryexpression;

import org.geotoolkit.filter.expression.AbstractExpression;

import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Expression;

public abstract class AbstractBinaryExpression<E extends Expression,F extends Expression> extends AbstractExpression implements BinaryExpression{

    protected final E left;
    protected final F right;

    protected AbstractBinaryExpression(E left, F right){
        if(left == null || right == null){
            throw new NullPointerException("Expresions can not be null");
        }

        this.left = left;
        this.right = right;
    }

    @Override
    public E getExpression1() {
        return left;
    }

    @Override
    public F getExpression2() {
        return right;
    }

}
