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

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.IllegalRecordException;


/**
 * Connection to the table of image {@linkplain Format formats}.
 * <p>
 * <b>NOTE:</b> The inherited {@link #getEntries()} method returns only the
 * entries using one of the formats listed to {@link #setImageFormats(String[]).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatTable extends SingletonTable<FormatEntry> {
    /**
     * The sample dimensions table, created when first needed.
     */
    private transient SampleDimensionTable sampleDimensions;

    /**
     * The last value given to {@link #setImageFormats(String[]).
     * Used in order to find similar formats with {@link #getEntries()}.
     */
    private String[] imageFormats;

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
     * Sets the image formats for the entries to be returned by {@link #getEntries()}.
     * The image formats array is typically provided by {@link FormatEntry#getImageFormats()}.
     *
     * @param formats The image formats. This method does not clone the provided array;
     *        do not modify!
     */
    public void setImageFormats(final String... formats) {
        imageFormats = formats;
        fireStateChanged("imageFormats");
    }

    /**
     * Returns the {@link SampleDimensionTable} instance, creating it if needed.
     */
    private SampleDimensionTable getSampleDimensionTable() throws CatalogException {
        SampleDimensionTable table = sampleDimensions;
        if (table == null) {
            sampleDimensions = table = getDatabase().getTable(SampleDimensionTable.class);
        }
        return table;
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
        final int encodingIndex = indexOf(query.packMode);
        final String format   = results.getString(indexOf(query.plugin));
        final String encoding = results.getString(encodingIndex);
        final String comments = results.getString(indexOf(query.comments));
        final ViewType viewType;
        final String type = String.valueOf(encoding).toLowerCase();
        if (type.equals("photographic")) { // TODO: use switch on Strings with Java 7.
            viewType = ViewType.PHOTOGRAPHIC;
        } else if (type.equals("geophysics")) {
            viewType = ViewType.GEOPHYSICS;
        } else if (type.equals("native")) {
            viewType = ViewType.NATIVE;
        } else if (type.equals("packed")) {
            viewType = ViewType.PACKED;
        } else {
            // Following constructor will close the ResultSet.
            throw new IllegalRecordException(errors().getString(
                    Errors.Keys.UNKNOWN_PARAMETER_$1, encoding),
                    this, results, encodingIndex, identifier);
        }
        final CategoryEntry entry = getSampleDimensionTable().getSampleDimensions(identifier.toString());
        GridSampleDimension[] sampleDimensions = null;
        String paletteName = null;
        if (entry != null) {
            sampleDimensions = entry.sampleDimensions;
            paletteName = entry.paletteName;
        }
        return new FormatEntry(identifier, format, paletteName, sampleDimensions, viewType, comments);
    }

    /**
     * Custom configuration of a statement which is about to be executed. In the particular case
     * where the query type is {@code LIST}, this method configure the statement in order to search
     * for formats using the same image plugin.
     */
    @Override
    protected void configure(final QueryType type, final PreparedStatement statement) throws SQLException {
        super.configure(type, statement);
        switch (type) {
            case LIST: {
                if (imageFormats == null) {
                    imageFormats = new String[0];
                }
                final Array array = statement.getConnection().createArrayOf("varchar", imageFormats);
                final FormatQuery query = (FormatQuery) super.query;
                statement.setArray(indexOf(query.byPlugin), array);
                //array.free(); TODO: Revisit after we upgrated the PostgreSQL driver.
                break;
            }
        }
    }
}
