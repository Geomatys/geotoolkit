/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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

import java.awt.geom.Dimension2D;
import java.time.Instant;
import org.apache.sis.geometry.Envelope2D;


/**
 * The spatiotemporal domain of a product.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class DomainOfProductEntry {
    /**
     * The time range, or {@code null} if none.
     */
    final Instant startTime, endTime;

    /**
     * The envelope in units of the database horizontal CRS, or {@code null} if none.
     */
    final Envelope2D bbox;

    /**
     * The resolution in units of the database horizontal CRS, or {@code null} if none.
     */
    private final Dimension2D resolution;

    /**
     * Creates a new entry with the specified values, which are <strong>not</strong> cloned.
     */
    DomainOfProductEntry(final Instant startTime, final Instant endTime, final Envelope2D bbox, final Dimension2D resolution) {
        this.startTime  = startTime;
        this.endTime    = endTime;
        this.bbox       = bbox;
        this.resolution = resolution;
    }
}
