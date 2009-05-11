/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.filter;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;

/**
 * Immutable "is between" filter.
 *
 * @author Johann Sorel (Geomatys)
 */
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getExpression() {
        return candidate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLowerBoundary() {
        return lower;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getUpperBoundary() {
        return upper;
    }

    /**
     * {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
