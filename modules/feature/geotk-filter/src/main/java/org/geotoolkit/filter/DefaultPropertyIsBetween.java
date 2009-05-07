

package org.geotoolkit.filter;

import org.geotoolkit.util.converter.ConverterRegistry;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;

public class DefaultPropertyIsBetween implements PropertyIsBetween{

    private final Expression candidate;
    private final Expression lower;
    private final Expression upper;

    public DefaultPropertyIsBetween(Expression candidate, Expression lower, Expression upper) {
        if(candidate == null || lower == null || upper == null){
            throw new NullPointerException("Expressions can not be null");
        }

        this.candidate = candidate;
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public Expression getExpression() {
        return candidate;
    }

    @Override
    public Expression getLowerBoundary() {
        return lower;
    }

    @Override
    public Expression getUpperBoundary() {
        return upper;
    }

    @Override
    public boolean evaluate(Object feature) {
        Object value = candidate.evaluate(feature);
        if (value == null) {
            return false;
        }

        if(!(value instanceof Comparable)){
            //object class is not comparable
            return false;
        }

        final Class<?> valueClass = value.getClass();
        final Comparable test = (Comparable) value;
        final Comparable down = (Comparable) lower.evaluate(feature,valueClass);
        final Comparable up = (Comparable) upper.evaluate(feature,valueClass);

        if(down == null || up == null){
            //we could not obtain 3 same class objects to compare.
            return false;
        }

        return down.compareTo(test) <= 0 && up.compareTo(test) >= 0;
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
