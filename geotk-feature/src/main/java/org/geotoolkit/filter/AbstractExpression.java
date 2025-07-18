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
import java.util.List;
import jakarta.xml.bind.annotation.XmlTransient;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.iso.Names;
import org.opengis.filter.Expression;
import org.opengis.filter.InvalidFilterValueException;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;

/**
 * Override evaluate(Object,Class) by using the converters system.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class AbstractExpression implements Expression<Object,Object>, Serializable {

    private static final LocalName ns = Names.createLocalName(null, null, "geotk");

    protected static ScopedName createName(String name) {
        return Names.createScopedName(ns, null, name);
    }

    @Override
    public Class<Object> getResourceClass() {
        return Object.class;        // Actually undetermined.
    }

    /**
     * Use the converters utility class to convert the default result object
     * to the wished class.
     */
    public <T> T evaluate(final Object candidate, final Class<T> target) {
        final Object value = apply(candidate);
        if (target == null) {
            return (T) value; // TODO - unsafe cast!!!!
        }
        try {
            return ObjectConverters.convert(value, target);
        } catch (UnconvertibleObjectException ex) {
            return null;
        }
    }

    @Override
    public <N> Expression<Object,N> toValueType(Class<N> type) {
        // TODO: that is a brut-force workaround to overcome the lack of type-safety at this level
        return new Conversion<>(type);
    }

    /**
     * A fallback to allow expression result conversion.
     * Once all expression implementations are properly typed, this workaround should not be needed anymore.
     *
     * @param <V> Output value type
     */
    private class Conversion<V> implements Expression<Object, V> {

        private final Class<V> valueType;

        private Conversion(Class<V> valueType) {
            this.valueType = valueType;
        }

        @Override
        public ScopedName getFunctionName() {
            return AbstractExpression.this.getFunctionName();
        }

        @Override
        public Class<Object> getResourceClass() {
            return AbstractExpression.this.getResourceClass();
        }

        @Override
        public List<Expression<? super Object, ?>> getParameters() {
            return AbstractExpression.this.getParameters();
        }

        @Override
        public V apply(Object input) throws InvalidFilterValueException {
            final Object baseValue = AbstractExpression.this.apply(input);
            if (baseValue == null) return (V) baseValue;

            return ObjectConverters.convert(baseValue, valueType);
        }

        @Override
        public <N1> Expression<Object, N1> toValueType(Class<N1> type) {
            return new Conversion<>(type);
        }
    }
}
