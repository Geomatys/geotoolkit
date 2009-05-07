

package org.geotoolkit.filter;

import org.geotoolkit.filter.expression.*;
import com.vividsolutions.jts.geom.Geometry;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLiteral<T> extends DefaultExpression implements Literal{

    private final T value;

    public DefaultLiteral(T value) {
        super(getType(value));
        this.value = value;
    }

    @Override
    public T evaluate(Object feature) {
        return value;
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value == null ? "NULL" : value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultLiteral<T> other = (DefaultLiteral<T>) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    private static ExpressionType getType(Object literal){
        if (literal instanceof Double) {
            return ExpressionType.LITERAL_DOUBLE;
        } else if (literal instanceof Integer) {
            return ExpressionType.LITERAL_INTEGER;
        } else if (literal instanceof Long) {
            return ExpressionType.LITERAL_LONG;
        } else if (literal instanceof String) {
            return ExpressionType.LITERAL_STRING;
        } else if (literal instanceof Geometry) {
            return ExpressionType.LITERAL_GEOMETRY;
        } else {
            return ExpressionType.LITERAL_UNDECLARED;
        }
    }

}
