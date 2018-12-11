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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import org.apache.sis.measure.Units;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.resources.Errors;


/**
 * Connection to a table of grid sample dimensions.
 * This table creates instances of {@link SampleDimension} for a given format.
 * Sample dimensions are one of the components needed for creation of {@code GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class SampleDimensionTable extends Table {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "SampleDimensions";

    /**
     * The {@linkplain Category categories} table, created only when first needed.
     *
     * @see #getCategoryTable()
     */
    private transient CategoryTable categories;

    /**
     * Creates a sample dimension table.
     */
    SampleDimensionTable(final Transaction transaction) {
        super(transaction);
    }

    /**
     * Returns the {@link CategoryTable} instance, creating it if needed.
     */
    private CategoryTable getCategoryTable() {
        CategoryTable table = categories;
        if (table == null) {
            categories = table = new CategoryTable(transaction);
        }
        return table;
    }

    /**
     * Returns the sample dimensions for the given format. If no sample dimensions are specified,
     * return {@code null} (not an empty list). We are not allowed to return an empty list because
     * our raster I/O framework interprets that as "no bands", as opposed to "unknown bands".
     *
     * @param  format  the format name.
     * @return an entry containing the sample dimensions for the given format, or {@code null} if none.
     * @throws SQLException if an error occurred while reading the database.
     */
    public SampleDimensionEntries query(final String format) throws SQLException, CatalogException {
        String[]  names = new String [8];
        Unit<?>[] units = new Unit<?>[8];
        boolean[] packs = new boolean[8];
        String[]  mdDim = new String [8];       // TODO: replace by a data structure.
        boolean hasMetadata = false;
        int numSampleDimensions = 0;
        final PreparedStatement statement = prepareStatement("SELECT "
                + "\"band\", \"identifier\", \"units\", \"isPacked\", \"metadata\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"format\" = ? ORDER BY \"band\"");
        statement.setString(1, format);
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final int    band = results.getInt    (1);          // First band is 1.
                final String name = results.getString (2);
                String unitSymbol = results.getString (3);
                boolean  isPacked = results.getBoolean(4);
                String   metadata = results.getString (5);
                hasMetadata      |= !results.wasNull();
                Unit<?> unit = null;
                if (unitSymbol != null) {
                    unitSymbol = unitSymbol.trim();
                    if (unitSymbol.isEmpty()) {
                        unit = Units.UNITY;
                    } else {
                        try {
                            unit = transaction.database.unitFormat.parse(unitSymbol);
                        } catch (ParserException e) {
                            throw new IllegalRecordException(e, results, 3, name);
                        }
                    }
                }
                if (numSampleDimensions >= names.length) {
                    names = Arrays.copyOf(names, names.length*2);
                    units = Arrays.copyOf(units, units.length*2);
                    packs = Arrays.copyOf(packs, packs.length*2);
                    mdDim = Arrays.copyOf(mdDim, mdDim.length*2);
                }
                names[numSampleDimensions] = name;
                units[numSampleDimensions] = unit;
                packs[numSampleDimensions] = isPacked;
                mdDim[numSampleDimensions] = metadata;
                if (band != ++numSampleDimensions) {
                    throw new IllegalRecordException(errors().getString(
                            Errors.Keys.NonConsecutiveBands_2, numSampleDimensions, band),
                            results, 2, format);
                }
            }
        }
        /*
         * At this point, we have successfully read every SampleDimension rows.
         * Now read the categories, provided that there is at least one sample
         * dimension.
         */
        if (numSampleDimensions == 0) {
            return null;
        }
        final SampleDimensionEntries entries = getCategoryTable().query(format, units);
        try {
            entries.complete(transaction.database.nameFactory, names, packs, hasMetadata ? mdDim : null, numSampleDimensions);
        } catch (IllegalArgumentException exception) {
            throw new IllegalRecordException(exception, null, 0, format);
        }
        return entries;
    }

    /**
     * Adds the given sample dimensions to the database.
     *
     * @param  format  the newly created format for which to write the sample dimensions.
     * @param  bands   the sample dimensions to add.
     * @throws SQLException if an error occurred while writing to the database.
     */
    public void insert(final String format, final List<SampleDimension> bands) throws SQLException, IllegalUpdateException {
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"format\", \"band\", \"identifier\", \"units\", \"isPacked\") VALUES (?,?,?,?,?)");
        statement.setString(1, format);
        final List<List<Category>> categories = new ArrayList<>(bands.size());
        boolean areAllEmpty = true;
        int bandNumber = 0;
        for (SampleDimension band : bands) {
            final boolean isPacked = (band == (band = band.forConvertedValues(false)));
            statement.setInt(2, ++bandNumber);
            statement.setString(3, String.valueOf(band.getName()));
            final Optional<Unit<?>> unit = band.getUnits();
            if (unit.isPresent()) {
                statement.setString(4, transaction.database.unitFormat.format(unit.get()));
            } else {
                statement.setNull(4, Types.VARCHAR);
            }
            statement.setBoolean(5, isPacked);
            final int count = statement.executeUpdate();
            if (count != 1) {
                throw new IllegalUpdateException(transaction.database.locale, count);
            }
            final List<Category> bandCategories = band.getCategories();
            areAllEmpty &= bandCategories.isEmpty();
            categories.add(bandCategories);
        }
        if (!areAllEmpty) {
            getCategoryTable().insert(format, categories);
        }
    }

    /**
     * Closes the prepared statements created by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        final CategoryTable t = categories;
        if (t != null) {
            categories = null;
            t.close();
        }
    }
}
