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
package org.geotoolkit.filter.function;

import java.util.List;
import org.geotoolkit.filter.AbstractExpression;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.StringUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.util.ScopedName;

/**
 * Immutable abstract function.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractFunction extends AbstractExpression {

    protected final String name;
    private final ScopedName scoped;
    protected final List<Expression<Object,?>> parameters;
    protected final Literal fallback;

    protected AbstractFunction(final String name, final Expression<Object,?>[] parameters, final Literal<Object,?> fallback) {
        ensureNonNull("name", name);
        this.name = name;
        this.parameters = UnmodifiableArrayList.wrap(parameters);
        this.fallback = fallback;
        scoped = createName(name);
    }

    protected AbstractFunction(final String name) {
        this(name, new Expression[0], null);
    }

    protected AbstractFunction(final String name, final Expression expression) {
        this(name, new Expression[] {expression}, null);
    }

    protected AbstractFunction(final String name, final Expression expr1, final Expression expr2) {
        this(name, new Expression[] {expr1, expr2}, null);
    }

    protected AbstractFunction(final String name, final Expression expr1, final Expression expr2, final Expression expr3) {
        this(name, new Expression[] {expr1, expr2, expr3}, null);
    }

    protected AbstractFunction(final String name, final Expression expr1, final Expression expr2, final Expression expr3, final Expression expr4) {
        this(name, new Expression[] {expr1, expr2, expr3, expr4}, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ScopedName getFunctionName() {
        return scoped;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Expression<Object,?>> getParameters() {
        return parameters;
    }

    protected final Geometry geometryValue(final Object feature) {
        return geometryValue(feature, 0);
    }

    protected final Geometry geometryValue(final Object feature, final int index) {
        final Object value = parameters.get(index).apply(feature);
        if (value instanceof Geometry) {
            return (Geometry) value;
        }
        throw new IllegalArgumentException("Filter Function problem for argument #" + index +
                " - expected type Geometry but got " + Classes.getClass(value));
    }

    protected final double doubleValue(final Object feature) {
        return doubleValue(feature, 0);
    }

    protected final double doubleValue(final Object feature, final int index) {
        final Object value = parameters.get(index).apply(feature);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new IllegalArgumentException("Filter Function problem for argument #" + index +
                " - expected type double but got " + Classes.getClass(value));
    }

    protected final String stringValue(final Object feature, final int index) {
        final Object value = parameters.get(index).apply(feature);
        return (value != null) ? value.toString() : null;
    }

    protected final String[] stringValues(final Object feature) {
        return stringValues(feature, parameters.size());
    }

    protected final String[] stringValues(final Object feature, final int length) {
        final String[] args = new String[length];
        for (int i=0 ; i<length ; i++) {
            args[i] = stringValue(feature, i);
        }
        return args;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
        hash = 89 * hash + (this.fallback != null ? this.fallback.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractFunction other = (AbstractFunction) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.parameters != other.parameters && (this.parameters == null || !this.parameters.equals(other.parameters))) {
            return false;
        }
        if (this.fallback != other.fallback && (this.fallback == null || !this.fallback.equals(other.fallback))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return StringUtilities.toStringTree(getFunctionName(), parameters);
    }
}
