/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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

import java.util.Objects;
import java.awt.geom.Dimension2D;

import org.geotoolkit.util.DateRange;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.internal.sql.table.DefaultEntry;


/**
 * The spatio-temporal domain of a layer. For internal use by {@link LayerEntry} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class DomainOfLayerEntry extends DefaultEntry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2371725033886216666L;

    /**
     * A null domain.
     */
    static final DomainOfLayerEntry NULL = new DomainOfLayerEntry("NULL", null, null, null, null);

    /**
     * The time range, or {@code null} if none.
     */
    final DateRange timeRange;

    /**
     * The envelope in units of the database horizontal CRS, or {@code null} if none.
     */
    final Envelope2D bbox;

    /**
     * The resolution in units of the database horizontal CRS, or {@code null} if none.
     */
    final Dimension2D resolution;

    /**
     * Creates a new entry with the specified values, which are <strong>not</strong> cloned.
     */
    DomainOfLayerEntry(final Comparable<?> name, final DateRange timeRange,
                       final Envelope2D bbox, final Dimension2D resolution,
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
            return Objects.equals(this.timeRange,  that.timeRange ) &&
                   Objects.equals(this.bbox,       that.bbox)       &&
                   Objects.equals(this.resolution, that.resolution);
        }
        return false;
    }
}
