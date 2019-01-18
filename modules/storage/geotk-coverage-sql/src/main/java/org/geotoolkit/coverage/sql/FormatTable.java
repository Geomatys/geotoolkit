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
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.sis.util.Numbers;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.MeasurementRange;


/**
 * Connection to the table of raster formats.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class FormatTable extends CachedTable<String,FormatEntry> {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "Formats";

    /**
     * Maximum number of formats for the same name. Current algorithm is very inefficient
     * for a large number of name collisions, so we are better to keep this limit small.
     */
    private static final int MAX_FORMATS = 100;

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
    FormatEntry createEntry(final ResultSet results, final String identifier) throws SQLException, CatalogException {
        final String  format   = results.getString(1);
        final String  metadata = results.getString(2);
        final SampleDimension[] categories = sampleDimensions.query(identifier);
        return new FormatEntry(format, UnmodifiableArrayList.wrap(categories), metadata);
    }

    /**
     * Returns the size of the given list, or 0 if null.
     */
    private static int size(final List<?> list) {
        return (list != null) ? list.size() : 0;
    }

    /**
     * Gets the range of sample values from the given category, with inclusive bounds for consistency
     * with the database definition. We use this method for comparing the sample value range (not the
     * real value range) because the former is definitive in the database.
     */
    private static NumberRange<?> getRange(final Category category) {
        NumberRange<?> range = category.getSampleRange();
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
    private String search(final String driver, final List<SampleDimension> bands) throws SQLException, DataStoreException {
        final int numBands = size(bands);
        try (PreparedStatement statement = getConnection().prepareStatement(
                "SELECT \"name\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"driver\" = ?"))
        {
            statement.setString(1, driver);
            try (final ResultSet results = statement.executeQuery()) {
next:           while (results.next()) {
                    final String name = results.getString(1);
                    final FormatEntry candidate = getEntry(name);                               // May use the cache.
                    final List<SampleDimension> current = candidate.sampleDimensions;
                    if (size(current) != numBands) {
                        // Number of band don't match: look for an other format.
                        continue;
                    }
                    for (int i=0; i<numBands; i++) {
                        SampleDimension band1 = bands.get(i);
                        SampleDimension band2 = current.get(i);
                        final boolean isReal1 = SampleDimensionTable.isReal(band1);
                        final boolean isReal2 = SampleDimensionTable.isReal(band2);
                        if (isReal1 != isReal2 || !Objects.equals(band1.getUnits(), band2.getUnits())) {
                            // Units don't match for at least one band: look for an other format.
                            continue next;
                        }
                        final SampleDimension supplied = band1;
                        band1 = band1.forConvertedValues(false);
                        band2 = band2.forConvertedValues(false);
                        List<Category> categories1 = band1.getCategories();
                        List<Category> categories2 = band2.getCategories();
                        if (isReal1 && band1 == supplied) {
                            final Optional<MeasurementRange<?>> range = band1.getMeasurementRange();
                            if (range.isPresent()) {
                                // Use the range that would be written in the database.
                                categories1 = SampleDimensionTable.defaultCategories(categories1, range.get());
                                // TODO: opportunistically replace the SampleDimension in the 'bands' list?
                            }
                        }
                        final int numCategories = size(categories1);
                        if (size(categories2) != numCategories) {
                            // Number of category don't match in at least one band: look for an other format.
                            continue next;
                        }
                        for (int j=0; j<numCategories; j++) {
                            final Category category1 = categories1.get(j);
                            final Category category2 = categories2.get(j);
                            if (!getRange(category1).equals(getRange(category2)) ||
                                !category1.getTransferFunction().equals(category2.getTransferFunction()))
                            {
                                continue next;
                            }
                        }
                    }
                    return name;
                }
            }
        }
        return null;
    }

    /**
     * Inserts a new format for the given sample dimensions, or returns an existing one.
     * If a format already exists, then this method returns its identifier.
     * Otherwise a new format is created with the given driver and the bands.
     *
     * @param  driver       the name of the data store to use.
     * @param  bands        the sample dimensions to add to the database.
     * @param  suggestedID  suggested name if a new format needs to be inserted.
     * @return the actual format name.
     * @throws SQLException if an error occurred while writing to the database.
     */
    final String findOrInsert(final String driver, final List<SampleDimension> bands, String suggestedID)
            throws SQLException, DataStoreException
    {
        String existing = search(driver, bands);
        if (existing != null) {
            return existing;
        }
        /*
         * Attempt to insert a new record may cause a name collision.
         * Following algorithm is inefficient, but should be okay if
         * there is few formats for the same product.
         */
        final PreparedStatement statement = prepareStatement("INSERT INTO " +
                SCHEMA + ".\"" + TABLE + "\" (\"name\",\"driver\") VALUES (?,?) ON CONFLICT (\"name\") DO NOTHING");
        statement.setString(2, driver);
        StringBuilder buffer = null;
        for (int n=2; ; n++) {
            statement.setString(1, suggestedID);
            if (statement.executeUpdate() != 0) {
                if (bands != null && !bands.isEmpty()) {
                    sampleDimensions.insert(suggestedID, bands);
                }
                return suggestedID;
            }
            if (n >= MAX_FORMATS) {
                throw new CatalogException("Rows already exist for all names up to \"" + suggestedID + "\".");
            }
            if (buffer == null) {
                buffer = new StringBuilder(suggestedID).append('-');
            }
            final int s = buffer.length();
            suggestedID = buffer.append(n).toString();
            buffer.setLength(s);
        }
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
