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

import java.util.Date;
import java.util.Calendar;
import java.awt.geom.Dimension2D;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.geometry.Envelope2D;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.display.shape.DoubleDimension2D;


/**
 * Connection to a table of domain of products. For internal use by {@link ProductTable} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 *
 * @todo rename DomainOfProducts
 */
final class DomainOfProductTable extends Table {
    /**
     * The spatio-temporal domain of a product. For internal use by {@link ProductEntry} only.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     */
    static final class Entry {
        /**
         * A null domain.
         */
        static final Entry NULL = new Entry(null, null, null);

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
        private final Dimension2D resolution;

        /**
         * Creates a new entry with the specified values, which are <strong>not</strong> cloned.
         */
        private Entry(final DateRange timeRange, final Envelope2D bbox, final Dimension2D resolution) {
            this.timeRange  = timeRange;
            this.bbox       = bbox;
            this.resolution = resolution;
        }
    }
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "DomainOfProducts";

    /**
     * Creates a domain of product table.
     */
    DomainOfProductTable(final Transaction transaction) {
        super(transaction);
    }

    /**
     * Returns the domain of the given product.
     */
    public Entry query(final String product) throws SQLException {
        Entry entry = null;
        final PreparedStatement statement = prepareStatement("SELECT \"startTime\", \"endTime\","
                + " \"west\", \"east\", \"south\", \"north\", \"xResolution\", \"yResolution\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"product\" = ?");
        final Calendar calendar = newCalendar();
        try (ResultSet results = statement.executeQuery()) {
            if (results.next()) {
                Date   startTime   = results.getTimestamp(1, calendar);
                Date   endTime     = results.getTimestamp(2, calendar);
                double west        = results.getDouble(3); if (results.wasNull()) west  = Longitude.MIN_VALUE;
                double east        = results.getDouble(4); if (results.wasNull()) east  = Longitude.MAX_VALUE;
                double south       = results.getDouble(5); if (results.wasNull()) south = Latitude .MIN_VALUE;
                double north       = results.getDouble(6); if (results.wasNull()) north = Latitude .MAX_VALUE;
                double xResolution = results.getDouble(7); if (results.wasNull()) xResolution = Double.NaN;
                double yResolution = results.getDouble(8); if (results.wasNull()) yResolution = Double.NaN;
                /*
                 * Replace java.sql.Timestamp by java.util.Date.
                 */
                if (startTime != null) startTime = new Date(startTime.getTime());
                if (endTime   != null) endTime   = new Date(endTime.getTime());
                Envelope2D bbox = new Envelope2D(transaction.database.extentCRS, west, south, east-west, north-south);
                entry = new Entry((startTime != null || endTime != null) ? new DateRange(startTime, endTime) : null, bbox,
                                  (xResolution>0 || yResolution>0) ? new DoubleDimension2D(xResolution, yResolution) : null);
            }
        }
        return entry;
    }
}
