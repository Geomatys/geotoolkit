/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.util.converter;

import java.io.Serializable;
import net.jcip.annotations.Immutable;


/**
 * A {@linkplain ClassFilter Class Filter} implementation accepting only classes that are
 * {@linkplain Class#isAssignableFrom assignable to} a given base class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
class BaseClassFilter implements ClassFilter, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8927565996519595329L;

    /**
     * The base class.
     */
    protected final Class<?> base;

    /**
     * The negative of this filter. Created only when first needed.
     */
    private transient ClassFilter negate;

    /**
     * Creates a new class filter for the given base.
     *
     * @param base The base class.
     */
    public BaseClassFilter(final Class<?> base) {
        this.base = base;
    }

    /**
     * Returns {@code true} if the given type is assignable to the {@linkplain #base} class.
     */
    @Override
    public boolean accepts(Class<?> type) {
        return base.isAssignableFrom(type);
    }

    /**
     * Returns a filter which is the negation of this filter. With the default implementation
     * of {@link #accepts} method, it is a filter accepting any class that are <strong>not</strong>
     * assignable to the {@linkplain #base} class.
     */
    @Override
    public synchronized ClassFilter negate() {
        if (negate == null) {
            negate = new Negate();
        }
        return negate;
    }

    /**
     * Implementation of {@link ClassFilter} which is the negation of the enclosing class.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     */
    @Immutable
    private final class Negate implements ClassFilter, Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 5873379460642760085L;

        /**
         * Returns {@code true} if the given type is <strong>not</strong>
         * acceptable according the enclosing class.
         */
        @Override
        public boolean accepts(final Class<?> type) {
            return !BaseClassFilter.this.accepts(type);
        }

        /**
         * Returns the original filter.
         */
        @Override
        public ClassFilter negate() {
            return BaseClassFilter.this;
        }
    }
}
