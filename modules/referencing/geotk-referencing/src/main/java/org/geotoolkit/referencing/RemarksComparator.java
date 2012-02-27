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

import java.util.Comparator;
import java.io.Serializable;
import java.io.ObjectStreamException;
import org.opengis.referencing.IdentifiedObject;


/**
 * {@link IdentifiedObjects#REMARKS_COMPARATOR} implementation as a named class (rather than anonymous)
 * for more predictable serialization.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 1.2)
 * @module
 */
final class RemarksComparator implements Comparator<IdentifiedObject>, Serializable {
    /** For cross-version compatibility. */
    private static final long serialVersionUID = -6675419613224162715L;

    /** Compares the given identified objects for order. */
    @Override public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
        return IdentifiedObjects.doCompare(o1.getRemarks(), o2.getRemarks());
    }

    /** Canonicalizes to the singleton on deserialization. */
    protected Object readResolve() throws ObjectStreamException {
        return IdentifiedObjects.REMARKS_COMPARATOR;
    }
}
