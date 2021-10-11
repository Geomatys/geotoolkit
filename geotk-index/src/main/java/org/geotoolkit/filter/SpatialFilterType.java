/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public enum SpatialFilterType {

    CONTAINS,
    INTERSECTS,
    EQUALS,
    DISJOINT,
    BBOX,
    BEYOND, // not implemented in TreeX
    CROSSES, // not implemented in TreeX
    DWITHIN, // not implemented in TreeX
    WITHIN,
    TOUCHES,
    OVERLAPS;

    public boolean isDistance() {
        return this == DWITHIN || this == BEYOND;
    }
}
