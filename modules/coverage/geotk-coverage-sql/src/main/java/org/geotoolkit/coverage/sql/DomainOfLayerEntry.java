/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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

import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.sql.table.Entry;


/**
 * The spatio-temporal domain of a layer. For internal use by {@link LayerEntry} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class DomainOfLayerEntry extends Entry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2371725033886216666L;

    /**
     * The time range, or {@code null} if none.
     */
    final DateRange timeRange;

    /**
     * The geographic bounding box, or {@code null} if none.
     */
    final GeographicBoundingBox bbox;

    /**
     * The resolution, or {@code null} if none.
     */
    final Dimension2D resolution;

    /**
     * Creates a new entry with the specified values, which are <strong>not</strong> cloned.
     */
    DomainOfLayerEntry(final String name, final DateRange timeRange,
                       final GeographicBoundingBox bbox, final Dimension2D resolution,
                       final String remarks)
    {
        super(name, remarks);
        this.timeRange  = timeRange;
        this.bbox       = bbox;
        this.resolution = resolution;
    }

    /**
     * Compares this layer with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final DomainOfLayerEntry that = (DomainOfLayerEntry) object;
            return Utilities.equals(this.timeRange,  that.timeRange ) &&
                   Utilities.equals(this.bbox,       that.bbox)       &&
                   Utilities.equals(this.resolution, that.resolution);
        }
        return false;
    }
}
