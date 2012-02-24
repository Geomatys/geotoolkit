/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Objects;
import net.jcip.annotations.Immutable;


/**
 * A (name, description) pair for a coordinate system axis.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@Immutable
final class AxisName {
    /**
     * The coordinate system axis name (never {@code null}).
     */
    final String name;

    /**
     * The coordinate system axis description, or {@code null} if none.
     */
    final String description;

    /**
     * Creates a new coordinate system axis name.
     */
    AxisName(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns a hash code for this object.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Compare this name with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof AxisName) {
            final AxisName that = (AxisName) object;
            return Objects.equals(this.name,        that.name) &&
                   Objects.equals(this.description, that.description);
        }
        return false;
    }

    /**
     * Returns a string representation of this object, for debugging purpose only.
     */
    @Override
    public String toString() {
        return name;
    }
}
