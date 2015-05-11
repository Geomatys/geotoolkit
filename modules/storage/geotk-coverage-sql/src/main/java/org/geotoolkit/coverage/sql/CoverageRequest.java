/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Date;
import java.util.Objects;
import org.geotoolkit.util.Utilities;


/**
 * A request for a coverage in some spatio-temporal extent.
 * This is used as key in cache.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
final class CoverageRequest {
    /**
     * The name of the requested layer.
     */
    private final String layer;

    /**
     * The start time and end time, in milliseconds since January 1st 1970.
     */
    private final long startTime, endTime;

    /**
     * The PostGIS SRID of the horizontal CRS.
     */
    private final int srid;

    /**
     * Creates a new instance.
     *
     * @param layer     The requested layer.
     * @param startTime The start time, or {@code null} if none.
     * @param endTime   The end time, or {@code null} if none.
     * @param srid      The PostGIS SRID of the horizontal CRS.
     */
    public CoverageRequest(final Layer layer, final Date startTime, final Date endTime, final int srid) {
        this.layer     = layer.getName();
        this.startTime = (startTime != null) ? startTime.getTime() : Long.MIN_VALUE;
        this.endTime   =   (endTime != null) ?   endTime.getTime() : Long.MAX_VALUE;
        this.srid      = srid;
    }

    /**
     * Returns a hash code value for this request.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(startTime, Utilities.hash(endTime, layer.hashCode() + srid));
    }

    /**
     * Compares this request with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof CoverageRequest) {
            final CoverageRequest that = (CoverageRequest) object;
            return Objects.equals(this.layer, that.layer) &&
                   this.startTime == that.startTime &&
                   this.endTime   == that.endTime &&
                   this.srid      == that.srid;
        }
        return false;
    }

    /**
     * Returns a string representation of this object for debugging purpose.
     */
    @Override
    public String toString() {
        return "CoverageRequest[" + layer + ']';
    }
}
