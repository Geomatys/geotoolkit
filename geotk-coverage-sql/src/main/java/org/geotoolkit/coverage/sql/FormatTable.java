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

import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.util.internal.shared.UnmodifiableArrayList;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.storage.DataStoreException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


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
     * TODO: reduce limit to 99 when real issue is solved.
     */
    private static final int MAX_FORMATS = 99999;
    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.coverage.sql");

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
        return "SELECT \"driver\", \"metadata\", \"approximate\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\" = ?";
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
        final String  format      = results.getString (1);
        final String  metadata    = results.getString (2);
        final boolean approximate = results.getBoolean(3);
        final SampleDimension[] categories = sampleDimensions.query(identifier);
        return new FormatEntry(format, UnmodifiableArrayList.wrap(categories), metadata, approximate);
    }

    /**
     * Returns the size of the given list, or 0 if null.
     */
    private static int size(final List<?> list) {
        return (list != null) ? list.size() : 0;
    }

    /**
     * Sorts the given categories by measurement ranges. This is required before to compare two category lists since they may
     * be in different order. This happen if a list describes packed categories (using integer values and a transfer function)
     * while the other list describes "real values" where no transfer function exist. The NaN values are last in "real values"
     * category lists while they are typically (but not necessarily) first in packed categories.
     */
    private static List<Category> sort(final List<Category> categories) {
        final Category[] array = categories.toArray(new Category[categories.size()]);
        Arrays.sort(array, (Category c1, Category c2) -> {
            Optional<MeasurementRange<?>> r1 = c1.getMeasurementRange();
            Optional<MeasurementRange<?>> r2 = c2.getMeasurementRange();
            final boolean p = r1.isPresent();
            if (p != r2.isPresent()) {
                return p ? -1 : +1;
            }
            final double v1 = (p ? r1.get() : c1.getSampleRange()).getMinDouble(true);
            final double v2 = (p ? r2.get() : c2.getSampleRange()).getMinDouble(true);
            int c = Double.compare(v1, v2);
            if (c == 0) {
                c = Long.compare(Double.doubleToRawLongBits(v1),
                                 Double.doubleToRawLongBits(v2));
            }
            return c;
        });
        return Arrays.asList(array);
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
     *   <li>Transfer function, ignored only if value in the {@code "approximate"} column is {@code true}
     *       and the product is non-null.</li>
     * </ul>
     *
     * If a product is specified, this method restricts the search in the formats specified for that product.
     * Only in such case this method may return the identifier of an approximate format.
     * This method always searches for an exact match if {@code product} is null.
     *
     * @param  product  the product for which to search a format, or {@code null} for not using that criterion.
     * @param  driver   the name of the data store plugin.
     * @param  bands    the sample dimensions to look for.
     * @return identifier of an existing format, or {@code null} if none.
     * @throws SQLException if an error occurred while querying the database.
     */
    private String search(final String product, final String driver, final List<SampleDimensionEntry> bands) throws SQLException, DataStoreException {
        final int numBands = size(bands);
        String fallback = null;                                                 // Value to return if we find no exact match.
        final String sql;
        if (product == null) {
            // Search in any products (exact matches only).
            sql = "SELECT \"name\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"driver\" = ? and \"approximate\" = FALSE";
        } else {
            // Restrict the search to formats associated to the given product.
            sql = "SELECT \"name\" FROM " + SCHEMA + ".\"" + TABLE + "\" INNER JOIN " + SCHEMA + ".\""
                + SeriesTable.TABLE + "\" ON \"" + SeriesTable.TABLE + "\".\"format\" = \"" + TABLE + "\".\"name\" "
                + "WHERE \"driver\" = ? AND \"" + SeriesTable.TABLE + "\".\"product\" = ?";
        }
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, driver);
            if (product != null) {
                statement.setString(2, product);
            }
            try (final ResultSet results = statement.executeQuery()) {
next:           while (results.next()) {
                    final String name = results.getString(1);
                    final FormatEntry candidate = getEntry(name);                               // May use the cache.
                    final List<SampleDimension> candidateBands = candidate.sampleDimensions;
                    if (size(candidateBands) != numBands) {
                        // Number of band don't match: look for another format.
                        continue;
                    }
                    final boolean ignoreTransferFunction = candidate.approximate;
                    boolean transferFunctionMatches = true;
                    for (int i=0; i<numBands; i++) {
                        final SampleDimensionEntry band1 = bands.get(i);
                        final SampleDimension      band2 = candidateBands.get(i);
                        if (band1.isReal != SampleDimensionEntry.isReal(band2) || !Objects.equals(band1.band.getUnits(), band2.getUnits())) {
                            // Units don't match for at least one band: look for another format.
                            continue next;
                        }
                        if (!band1.band.getName().tip().toString().equals(band2.getName().tip().toString())) {
                            // Since names are used for identifying netCDF variables, we require a match.
                            continue next;
                        }
                        List<Category> categories1 = band1.categories;
                        List<Category> categories2 = band2.getCategories();
                        final int numCategories = size(categories1);
                        if (size(categories2) != numCategories) {
                            // Number of category don't match in at least one band: look for another format.
                            continue next;
                        }
                        categories1 = sort(categories1);
                        categories2 = sort(categories2);
                        for (int j=0; j<numCategories; j++) {
                            final Category category1 = categories1.get(j);
                            final Category category2 = categories2.get(j);
                            if (category1.isQuantitative() != category2.isQuantitative()) {
                                continue next;
                            }
                            // Do not compare names. We allow user to rename categories in the database.
                            switch (TransferFunction.equals(category1, category2)) {
                                case TransferFunction.DIFFERENT: {
                                    continue next;
                                }
                                case TransferFunction.RANGE_EQUAL: {
                                    transferFunctionMatches = false;
                                    if (!ignoreTransferFunction) {
                                        continue next;
                                    }
                                    // Require at least the measurement ranges to intersect.
                                    final Optional<MeasurementRange<?>> mr1 = category1.getMeasurementRange();
                                    final Optional<MeasurementRange<?>> mr2 = category2.getMeasurementRange();
                                    if ((mr1.isPresent() != mr2.isPresent()) ||
                                        (mr1.isPresent() && !TransferFunction.intersect(mr1.get(), mr2.get())))
                                    {
                                        continue next;
                                    }
                                }
                            }
                        }
                    }
                    if (transferFunctionMatches) {
                        return name;
                    } else if (fallback == null) {
                        fallback = name;
                    }
                }
            }
        }
        return fallback;
    }

    /**
     * Inserts a new format for the given sample dimensions, or returns an existing one.
     * If a format already exists, then this method returns its identifier.
     * Otherwise a new format is created with the given driver and the bands.
     *
     * @param  product      the product for which to search a format, or {@code null} for not using that criterion.
     * @param  driver       the name of the data store to use.
     * @param  bands        the sample dimensions to add to the database.
     * @param  suggestedID  suggested name if a new format needs to be inserted.
     * @return the actual format name.
     * @throws SQLException if an error occurred while writing to the database.
     */
    final String findOrInsert(final String product, final String driver, final List<SampleDimensionEntry> bands, String suggestedID)
            throws SQLException, DataStoreException
    {
        String existing = search(product, driver, bands);       // Give precedence to format associated to the same product.
        if (existing != null) {
            return existing;
        }
        existing = search(null, driver, bands);                 // Accept format from any product if exact match.
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
                    if (sampleDimensions.insert(suggestedID, bands)) {
                        /*
                         * If the value we just inserted is approximate, set the boolean in "approximate" column.
                         * It should happen only once per product, because after we have declared an approximate
                         * format it should be used for next insertions of the same product.
                         */
                        try (PreparedStatement stmt = statement.getConnection().prepareStatement("UPDATE " +
                                SCHEMA + ".\"" + TABLE + "\" SET \"approximate\" = TRUE WHERE \"name\" = ?"))
                        {
                            stmt.setString(1, suggestedID);
                            stmt.executeUpdate();
                        }
                    }
                }
                return suggestedID;
            }

            if (n >= MAX_FORMATS) {
                throw new CatalogException("Rows already exist for all names up to \"" + suggestedID + "\".");
            } else if (n == 9000) {
                LOGGER.log(Level.WARNING, "Number of rows for format {0}: it's over 9000 ! (ง •̀_•́)ง", suggestedID);
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
