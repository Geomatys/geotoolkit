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

import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.IllegalRecordException;


/**
 * Connection to the table of image {@linkplain Format formats}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatTable extends SingletonTable<FormatEntry> {
    /**
     * Connection to the sample dimensions table.
     * Will be created only when first needed.
     * <p>
     * This field doesn't need to be declared {@code volatile} because it is not used
     * outside this {@code FormatTable}, so it is not expected to be accessed by other
     * threads.
     */
    private transient SampleDimensionTable sampleDimensions;

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
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private FormatTable(final FormatTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected FormatTable clone() {
        return new FormatTable(this);
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
        SampleDimensionTable table = sampleDimensions;
        if (table == null) {
            // Not a big deal if two instances are created concurrently.
            sampleDimensions = table = getDatabase().getTable(SampleDimensionTable.class);
        }
        final GridSampleDimension[] bands = table.getSampleDimensions(identifier.toString());
        return new FormatEntry(identifier, format, bands, geophysics);
    }
}
