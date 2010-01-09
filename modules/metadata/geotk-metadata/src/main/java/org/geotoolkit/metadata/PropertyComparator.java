/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.metadata;

import java.util.Comparator;
import java.lang.reflect.Method;

import org.opengis.annotation.UML;
import org.opengis.annotation.Obligation;

import org.geotoolkit.lang.Immutable;


/**
 * The comparator for sorting method order. This comparator put mandatory methods first,
 * which is necessary for reducing the risk of ambiguity in {@link PropertyTree#parse}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 2.4
 * @module
 */
@Immutable
final class PropertyComparator implements Comparator<Method> {
    /**
     * The singleton instance.
     */
    static final Comparator<Method> INSTANCE = new PropertyComparator();

    /**
     * Do not allow instantiation of this class, except for the singleton.
     */
    private PropertyComparator() {
    }

    /**
     * Compares the given methods for order.
     */
    @Override
    public int compare(final Method m1, final Method m2) {
        final UML a1 = m1.getAnnotation(UML.class);
        final UML a2 = m2.getAnnotation(UML.class);
        if (a1 != null) {
            if (a2 == null) return +1;       // Sort annotated elements first.
            int c = order(a1) - order(a2);   // Mandatory elements must be first.
            if (c == 0) {
                // Fallback on alphabetical order.
                c = a1.identifier().compareToIgnoreCase(a2.identifier());
            }
            return c;
        } else if (a2 != null) {
            return -1; // Sort annotated elements first.
        }
        // Fallback on alphabetical order.
        return m1.getName().compareToIgnoreCase(m2.getName());
    }

    /**
     * Returns a higher number for obligation which should be first.
     */
    private int order(final UML uml) {
        final Obligation obligation = uml.obligation();
        if (obligation != null) {
            switch (obligation) {
                case MANDATORY:   return 1;
                case CONDITIONAL: return 2;
                case OPTIONAL:    return 3;
                case FORBIDDEN:   return 4;
            }
        }
        return 5;
    }
}
