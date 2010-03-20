/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.IllegalRecordException;


/**
 * Connection to the table of image {@linkplain Format formats}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
@ThreadSafe(concurrent = true)
final class FormatTable extends SingletonTable<FormatEntry> {
    /**
     * Creates a format table.
     *
     * @param database Connection to the database.
     */
    public FormatTable(final Database database) {
        this(new FormatQuery(database));
    }

    /**
     * Constructs a new {@code FormatTable} from the specified query.
     */
    private FormatTable(final FormatQuery query) {
        super(query, query.byName);
    }

    /**
     * Creates a format from the current row in the specified result set.
     *
     * @param  results The result set to read.
     * @param  identifier The identifier of the format to create.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occured while reading the database.
     */
    @Override
    protected FormatEntry createEntry(final ResultSet results, final Comparable<?> identifier) throws SQLException {
        final FormatQuery query = (FormatQuery) super.query;
        final int encodingIndex = indexOf(query.encoding);
        final String format   = results.getString(indexOf(query.format));
        final String encoding = results.getString(encodingIndex);
        final boolean geophysics;
        final String type = String.valueOf(encoding).toLowerCase();
        if (type.equals("geophysics")) {
            geophysics = true;
        } else if (type.equals("packed") || type.equals("rendered") || type.equals("native")) {
            geophysics = false;
        } else {
            // Following constructor will close the ResultSet.
            throw new IllegalRecordException(errors().getString(
                    Errors.Keys.UNKNOWN_PARAMETER_$1, encoding),
                    this, results, encodingIndex, identifier);
        }
        return new FormatEntry(getDatabase(), identifier, format, geophysics);
    }
}
