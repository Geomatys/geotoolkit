/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.io.Serializable;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.StringUtilities;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.expression.Expression;

/**
 * Immutable "is between" filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPropertyIsBetween implements PropertyIsBetween,Serializable{

    private final Expression candidate;
    private final Expression lower;
    private final Expression upper;

    public DefaultPropertyIsBetween(final Expression candidate, final Expression lower, final Expression upper) {
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
    public boolean evaluate(final Object feature) {
        Object value = candidate.evaluate(feature);
        if (value == null) {
            return false;
        }

        if(!(value instanceof Comparable)){
            //object class is not comparable
            return false;
        }

        Class<?> valueClass = value.getClass();

        if(Number.class.isAssignableFrom(valueClass)){
            //we better compare with double to avoid truncations
            valueClass = Double.class;
        }

        final Comparable test = (Comparable) Converters.convert(value, valueClass);
        final Comparable down = (Comparable) lower.evaluate(feature,valueClass);
        final Comparable up = (Comparable) upper.evaluate(feature,valueClass);

        if(down == null || up == null){
            //we could not obtain 3 same class objects to compare.
            return false;
        }

        return down.compareTo(test) < 0 && up.compareTo(test) >= 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPropertyIsBetween other = (DefaultPropertyIsBetween) obj;
        if (this.candidate != other.candidate && (this.candidate == null || !this.candidate.equals(other.candidate))) {
            return false;
        }
        if (this.lower != other.lower && (this.lower == null || !this.lower.equals(other.lower))) {
            return false;
        }
        if (this.upper != other.upper && (this.upper == null || !this.upper.equals(other.upper))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.candidate != null ? this.candidate.hashCode() : 0);
        hash = 97 * hash + (this.lower != null ? this.lower.hashCode() : 0);
        hash = 97 * hash + (this.upper != null ? this.upper.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Between\n");
        sb.append(StringUtilities.toStringTree("Exp : "+ candidate,"Lower : "+lower, "Upper : "+upper));
        return sb.toString();
    }

}
