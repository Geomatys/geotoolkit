/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.List;
import java.util.Objects;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.Numbers;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * Connection to the table of raster formats.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class FormatTable extends CachedTable<String,Format> {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "Formats";

    /**
     * The sample dimensions table.
     */
    private final SampleDimensionTable sampleDimensions;

    /**
     * Creates a format table.
     */
    FormatTable(final Transaction transaction) {
        super(Target.FORMAT, transaction);
        sampleDimensions = new SampleDimensionTable(transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"driver\", \"metadata\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\" = ?";
    }

    /**
     * Creates a format from the current row in the specified result set.
     *
     * @param  results     the result set to read.
     * @param  identifier  the identifier of the format to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    Format createEntry(final ResultSet results, final String identifier) throws SQLException, CatalogException {
        final String  format     = results.getString (1);
        final String  metadata   = results.getString (3);
        SampleDimensionTable.Entry categories = sampleDimensions.query(identifier);
        final GridSampleDimension[] sampleDimensions;
        final String paletteName;
        if (categories != null) {
            sampleDimensions = categories.sampleDimensions;
            paletteName = categories.paletteName;
        } else {
            sampleDimensions = null;
            paletteName = null;
        }
        return new Format(format, paletteName, sampleDimensions, metadata);
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
                range = new NumberRange<>(Integer.class,
                        (int) Math.floor(range.getMinDouble(true)), true,
                        (int) Math.ceil (range.getMaxDouble(true)), true);
            }
        }
        return range;
    }

    /**
     * If a format exists for the given plugin and sample dimensions, returns its name.
     * Otherwise returns {@code null}.
     * This method ignores mismatches in the following properties, because they
     * do not affect the numerical values computed by the transfer function:
     *
     * <ul>
     *   <li>Sample dimension names and metadata.</li>
     *   <li>Category names.</li>
     *   <li>Color palette (ignored because often encoded in the image format,
     *       in which case {@link #createEntry()} will ignore it anyway).</li>
     * </ul>
     *
     * @param  driver  the name of the data store plugin.
     * @param  bands   the sample dimensions to look for.
     * @return identifier of an existing format, or {@code null} if none.
     * @throws SQLException if an error occurred while querying the database.
     */
    private String search(final String driver, final List<GridSampleDimension> bands) throws SQLException, CatalogException {
        final int numBands = size(bands);
        final PreparedStatement statement = prepareStatement("SELECT \"name\" FROM " + SCHEMA + ".\"" + TABLE + " WHERE \"driver\" = ?");
        statement.setString(1, driver);
        try (final ResultSet results = statement.executeQuery()) {
next:       while (results.next()) {
                final String name = results.getString(1);
                final Format candidate = getEntry(name);                               // May use the cache.
                final List<GridSampleDimension> current = candidate.sampleDimensions;
                if (size(current) != numBands) {
                    // Number of band don't match: look for an other format.
                    continue;
                }
                for (int i=0; i<numBands; i++) {
                    final GridSampleDimension band1 = bands.get(i);
                    final GridSampleDimension band2 = current.get(i);
                    if (!Objects.equals(band1.getUnits(), band2.getUnits())) {
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
                        if (!Objects.equals(getRange(category1), getRange(category2)) ||
                            !Objects.equals(category1.getSampleToGeophysics(),
                                            category2.getSampleToGeophysics()))
                        {
                            continue next;
                        }
                    }
                }
                return name;
            }
        }
        return null;
    }

    /**
     * Creates a new format for the given sample dimensions, or returns an existing one.
     * If a format already exists, then this method returns its identifier.
     * Otherwise a new format created with the given driver and the bands.
     *
     * @param  name    suggested name of the new format.
     * @param  driver  the name of the data store to use.
     * @param  bands   the sample dimensions to add to the database.
     * @return the format name.
     * @throws SQLException if an error occurred while writing to the database.
     */
    public String findOrCreate(final String name, final String driver, final List<GridSampleDimension> bands)
            throws SQLException, CatalogException
    {
        String existing = search(driver, bands);
        if (existing != null) {
            return existing;
        }
        /*
         * TODO:
         *   - infer a name automatically if 'name' is null.
         *   - verify if there is a name collision.
         */
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"name\", \"driver\") VALUES (?,?)");
        statement.setString(1, name);
        statement.setString(2, driver);
        if (statement.executeUpdate() != 0 && !bands.isEmpty()) {
            sampleDimensions.insert(name, bands);
        }
        return name;
    }

    /**
     * Closes the prepared statements created by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        sampleDimensions.close();
    }
}
