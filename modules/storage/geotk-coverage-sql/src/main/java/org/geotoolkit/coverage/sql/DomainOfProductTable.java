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

import java.util.Calendar;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.geometry.Envelope2D;

import org.geotoolkit.display.shape.DoubleDimension2D;


/**
 * Connection to a table of domain of products. For internal use by {@link ProductTable} only.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class DomainOfProductTable extends Table {
    /**
     * A null domain.
     */
    private static final DomainOfProductEntry NULL = new DomainOfProductEntry(null, null, null, null);

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
     * Never returns {@code null} but may return a domain containing null elements.
     */
    public DomainOfProductEntry query(final String product) throws SQLException {
        DomainOfProductEntry entry = NULL;
        final PreparedStatement statement = prepareStatement("SELECT \"startTime\", \"endTime\","
                + " \"west\", \"east\", \"south\", \"north\", \"xResolution\", \"yResolution\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"product\" = ?");
        statement.setString(1, product);
        final Calendar calendar = newCalendar();
        try (ResultSet results = statement.executeQuery()) {
            if (results.next()) {
                Timestamp startTime   = results.getTimestamp(1, calendar);
                Timestamp endTime     = results.getTimestamp(2, calendar);
                double    west        = results.getDouble(3); if (results.wasNull()) west  = Longitude.MIN_VALUE;
                double    east        = results.getDouble(4); if (results.wasNull()) east  = Longitude.MAX_VALUE;
                double    south       = results.getDouble(5); if (results.wasNull()) south = Latitude .MIN_VALUE;
                double    north       = results.getDouble(6); if (results.wasNull()) north = Latitude .MAX_VALUE;
                double    xResolution = results.getDouble(7); if (results.wasNull()) xResolution = Double.NaN;
                double    yResolution = results.getDouble(8); if (results.wasNull()) yResolution = Double.NaN;
                Envelope2D bbox = new Envelope2D(transaction.database.extentCRS, west, south, east-west, north-south);
                entry = new DomainOfProductEntry(toInstant(startTime), toInstant(endTime), bbox,
                        (xResolution>0 || yResolution>0) ? new DoubleDimension2D(xResolution, yResolution) : null);
            }
        }
        return entry;
    }
}
