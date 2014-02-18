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

import java.util.Date;
import java.util.Calendar;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.display.shape.DoubleDimension2D;

import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.SpatialDatabase;


/**
 * Connection to a table of domain of layers. For internal use by {@link LayerTable} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class DomainOfLayerTable extends SingletonTable<DomainOfLayerEntry> {
    /**
     * Creates a domain of layer table.
     *
     * @param database Connection to the database.
     */
    public DomainOfLayerTable(final Database database) {
        this(new DomainOfLayerQuery(database));
    }

    /**
     * Constructs a new {@code DomainOfLayerTable} from the specified query.
     */
    private DomainOfLayerTable(final DomainOfLayerQuery query) {
        super(query, query.byLayer);
    }

    /**
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private DomainOfLayerTable(final DomainOfLayerTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected DomainOfLayerTable clone() {
        return new DomainOfLayerTable(this);
    }

    /**
     * Creates a layer from the current row in the specified result set.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  results The result set to read.
     * @param  identifier The name of the layer for the entry being read.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    protected DomainOfLayerEntry createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws SQLException
    {
        final DomainOfLayerQuery query = (DomainOfLayerQuery) super.query;
        final Calendar calendar = getCalendar(lc);
        Date   startTime   = results.getTimestamp(indexOf(query.startTime), calendar);
        Date   endTime     = results.getTimestamp(indexOf(query.endTime), calendar);
        double west        = results.getDouble(indexOf(query.west));  if (results.wasNull()) west  = Longitude.MIN_VALUE;
        double east        = results.getDouble(indexOf(query.east));  if (results.wasNull()) east  = Longitude.MAX_VALUE;
        double south       = results.getDouble(indexOf(query.south)); if (results.wasNull()) south = Latitude .MIN_VALUE;
        double north       = results.getDouble(indexOf(query.north)); if (results.wasNull()) north = Latitude .MAX_VALUE;
        final double xResolution = results.getDouble(indexOf(query.xResolution));
        final double yResolution = results.getDouble(indexOf(query.yResolution));
        // Replace java.sql.Timestamp by java.util.Date.
        if (startTime != null) {
            startTime = new Date(startTime.getTime());
        }
        if (endTime != null) {
            endTime = new Date(endTime.getTime());
        }
        final Envelope2D bbox = new Envelope2D(((SpatialDatabase) getDatabase()).horizontalCRS, west, south, east-west, north-south);
        return new DomainOfLayerEntry(identifier,
                (startTime != null || endTime != null) ? new DateRange(startTime, endTime) : null, bbox,
                (xResolution>0 || yResolution>0) ? new DoubleDimension2D(xResolution, yResolution) : null, null);
    }
}
