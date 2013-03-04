/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;
import java.util.List;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.opengis.referencing.operation.MathTransform1D;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
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
 * @version 3.15
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
     * @param  lc The {@link #getLocalCache()} value.
     * @param  results The result set to read.
     * @param  identifier The identifier of the format to create.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    protected FormatEntry createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws SQLException
    {
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
        return new FormatEntry((String) identifier, format, paletteName, sampleDimensions, viewType, comments);
    }

    /**
     * Custom configuration of a statement which is about to be executed. In the particular case
     * where the query type is {@code LIST}, this method configure the statement in order to search
     * for formats using the same image plugin.
     */
    @Override
    protected void configure(final LocalCache lc, final QueryType type, final PreparedStatement statement)
            throws SQLException
    {
        super.configure(lc, type, statement);
        switch (type) {
            case LIST: {
                if (imageFormats == null) {
                    imageFormats = CharSequences.EMPTY_ARRAY;
                }
                final Array array = statement.getConnection().createArrayOf("varchar", imageFormats);
                final FormatQuery query = (FormatQuery) super.query;
                statement.setArray(indexOf(query.byPlugin), array);
                //array.free(); TODO: Revisit after we upgrated the PostgreSQL driver.
                break;
            }
        }
    }

    /**
     * Returns the size of the given list, or 0 if null.
     */
    private static int size(final List<?> list) {
        return (list != null) ? list.size() : 0;
    }

    /**
     * Gets the range of sample values from the given category, with inclusive
     * bounds for consistency with the database definition.
     */
    private static NumberRange<?> getRange(final Category category) {
        NumberRange<?> range = category.geophysics(false).getRange();
        final Class<?> type = range.getElementType();
        if (Numbers.isInteger(type)) {
            if (!range.isMaxIncluded() || !range.isMinIncluded() || type != Integer.class) {
                range = new NumberRange<Integer>(Integer.class,
                        (int) Math.floor(range.getMinimum(true)), true,
                        (int) Math.ceil (range.getMaximum(true)), true);
            }
        }
        return range;
    }

    /**
     * If a format exists for the given codec and sample dimensions, return it.
     * Otherwise returns {@code null}.
     * <p>
     * This method ignores mismatches in the following properties, because they
     * do not affect the numerical values computed by the transfer function:
     * <p>
     * <ul>
     *   <li>Sample dimension names.</li>
     *   <li>Category names.</li>
     *   <li>Color palette (ignored because often encoded in the image format,
     *       in which case {@link #createEntry()} will ignore it anyway).</li>
     * </ul>
     *
     * @param  codecName  The name of the Image I/O plugin.
     * @param  bands      The sample dimensions to look for.
     * @return An existing format, or {@code null} if none.
     * @throws SQLException If an error occurred while querying the database.
     *
     * @since 3.13
     */
    public FormatEntry find(final String codecName, final List<GridSampleDimension> bands)
            throws SQLException
    {
        final int numBands = size(bands);
        setImageFormats(FormatEntry.getImageFormats(codecName));
next:   for (final FormatEntry candidate : getEntries()) {
            final List<GridSampleDimension> current = candidate.sampleDimensions;
            if (size(current) != numBands) {
                // Number of band don't match: look for an other format.
                continue next;
            }
            for (int i=0; i<numBands; i++) {
                final GridSampleDimension band1 = bands.get(i);
                final GridSampleDimension band2 = current.get(i);
                if (!Utilities.equals(band1.getUnits(), band2.getUnits())) {
                    // Units don't match for at least one band: look for an other format.
                    continue next;
                }
                final List<Category> categories1 = band1.getCategories();
                final List<Category> categories2 = band2.getCategories();
                final int numCategories = size(categories1);
                if (size(categories2) != numCategories) {
                    // Number of category don't match in at least one band: look for an other format.
                    continue next;
                }
                for (int j=0; j<numCategories; j++) {
                    Category category1 = categories1.get(j);
                    Category category2 = categories2.get(j);
                    /*
                     * Converts the two categories to non-geophysics categories.
                     * If we detect in this process that one category is geophysics
                     * while the other is not, consider that we don't have a match.
                     */
                    if ((category1 == (category1 = category1.geophysics(false))) !=
                        (category2 == (category2 = category2.geophysics(false))))
                    {
                        continue next;
                    }
                    /*
                     * Compares the sample value range (not the geophysics one) because
                     * the former is definitive in the database. However do not convert
                     * to geophysics categories when comparing the transforms,  because
                     * we want to differentiate "geophysics" views from the packed ones
                     * (the former have identity transforms).
                     */
                    if (!Utilities.equals(getRange(category1), getRange(category2)) ||
                        !Utilities.equals(category1.getSampleToGeophysics(),
                                          category2.getSampleToGeophysics()))
                    {
                        continue next;
                    }
                }
            }
            return candidate;
        }
        return null;
    }

    /**
     * Creates a new format for the given sample dimensions, or returns an existing one.
     * If a format for the given name already exists, then this method does nothing; it
     * does not check the image format and the bands. Otherwise a new format created with
     * the given image format and the bands.
     *
     * @param  name        The name of the new format, or {@code null} for a default one.
     * @param  imageFormat The name of the Image I/O plugin.
     * @param  bands       The sample dimensions to add to the database.
     * @return The format name.
     * @throws SQLException if an error occurred while writing to the database.
     *
     * @since 3.13
     */
    public String findOrCreate(String name, final String imageFormat, final List<GridSampleDimension> bands)
            throws SQLException
    {
        /*
         * Determine whatever the given bands are for geophysics or packed data.
         * If at least one band is not geophysics, we consider all of them as packed.
         */
        ViewType type = ViewType.PHOTOGRAPHIC;
check:  for (final GridSampleDimension band : bands) {
            final List<Category> categories = band.getCategories();
            if (categories != null) {
                for (final Category category : categories) {
                    final MathTransform1D tr = category.getSampleToGeophysics();
                    if (tr != null) {
                        if (tr.isIdentity()) {
                            type = ViewType.GEOPHYSICS;
                        } else {
                            type = ViewType.PACKED;
                            break check;
                        }
                    }
                }
            }
        }
        /*
         * Now process to the insertion in the database. Before doing the actual
         * insertion, we will check for existing entries inside the write lock.
         */
        final FormatQuery query = (FormatQuery) super.query;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            boolean success = false;
            transactionBegin(lc);
            try {
                /*
                 * Checks for existing entries.
                 */
                if (name == null) {
                    final FormatEntry candidate = find(imageFormat, bands);
                    if (candidate != null && candidate.viewType == type) {
                        return candidate.getIdentifier();
                    }
                    name = searchFreeIdentifier(lc, imageFormat);
                } else if (exists(name)) {
                    return name;
                }
                /*
                 * No existing entry fit. Adds the new entry.
                 */
                final LocalCache.Stmt ce = getStatement(lc, QueryType.INSERT);
                final PreparedStatement statement = ce.statement;
                statement.setString(indexOf(query.name),     name);
                statement.setString(indexOf(query.plugin),   imageFormat);
                statement.setString(indexOf(query.packMode), type.name().toLowerCase(Locale.ENGLISH));
                final boolean inserted = updateSingleton(statement);
                release(lc, ce);
                if (inserted) {
                    if (!bands.isEmpty()) {
                        getSampleDimensionTable().addEntries(name, bands);
                    }
                    success = true;
                }
            } finally {
                transactionEnd(lc, success);
            }
        }
        return name;
    }

    /**
     * Searches for a format name not already in use. If the given string is not in use, then
     * it is returned as-is. Otherwise this method appends a unused decimal number to the
     * specified name.
     *
     * @since 3.15
     */
    public String searchFreeIdentifier(final String base) throws SQLException {
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            return searchFreeIdentifier(lc, base);
        }
    }
}
