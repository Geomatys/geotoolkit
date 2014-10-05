/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing;

import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;
import java.io.ObjectStreamException;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;


/**
 * {@link IdentifiedObjects#IDENTIFIER_COMPARATOR} implementation as a named class (rather than anonymous)
 * for more predictable serialization.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 1.2)
 * @module
 */
final class IdentifierComparator implements Comparator<IdentifiedObject>, Serializable {
    /** For cross-version compatibility. */
    private static final long serialVersionUID = -7315726806679993522L;

    /** Compares the given identified objects for order. */
    @Override public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
        Collection<Identifier> a1 = o1.getIdentifiers();
        Collection<Identifier> a2 = o2.getIdentifiers();
        if (a1 == null) a1 = Collections.emptySet();
        if (a2 == null) a2 = Collections.emptySet();
        final Iterator<Identifier> i1 = a1.iterator();
        final Iterator<Identifier> i2 = a2.iterator();
        boolean n1, n2;
        while ((n1 = i1.hasNext()) & (n2 = i2.hasNext())) {  // NOSONAR: Really '&', not '&&'
            final int c = IdentifiedObjects.doCompare(i1.next().getCode(), i2.next().getCode());
            if (c != 0) {
                return c;
            }
        }
        if (n1) return +1;
        if (n2) return -1;
        return 0;
    }

    /** Canonicalizes to the singleton on deserialization. */
    protected Object readResolve() throws ObjectStreamException {
        return IdentifiedObjects.IDENTIFIER_COMPARATOR;
    }
}
