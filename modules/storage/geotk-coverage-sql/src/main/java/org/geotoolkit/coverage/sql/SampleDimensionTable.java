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

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.measure.Unit;
import javax.measure.format.ParserException;

import org.apache.sis.measure.Units;
import org.apache.sis.util.ArraysExt;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.resources.Errors;


/**
 * Connection to a table of grid sample dimensions.
 * This table creates instances of {@link GridSampleDimension} for a given format.
 * Sample dimensions are one of the components needed for creation of {@code GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class SampleDimensionTable extends Table {
    /**
     * The result of a query on a {@link SampleDimensionTable} object.
     *
     * @author Martin Desruisseaux (Geomatys)
     */
    static final class Entry {
        /**
         * The name of the color palette, or {@code null} if none. If more than one color
         * palettes are found, then the one for the largest range of values is used.
         *
         * <p>This is used for initializing the {@link FormatEntry#paletteName} attribute,
         * which is used by {@link GridCoverageLoader}. We retain only one palette name
         * because there is typically only one visible band in an index color model, so
         * {@code GridCoverageLoader} wants only one palette.</p>
         */
        final String paletteName;

        /**
         * The categories for each sample dimensions in a given format.
         * The keys are band numbers, where the first band is numbered 1.
         * Values are the categories for that band in arbitrary order.
         */
        private final Map<Integer,Category[]> categories;

        /**
         * The sample dimensions built from the {@link #categories} map. This field is initially
         * {@code null} and is initialized by {@link SampleDimensionTable#getSampleDimensions(String)}.
         */
        GridSampleDimension[] sampleDimensions;

        /**
         * Reference to an entry in the {@code metadata.SampleDimension} table, or {@code null} if none.
         * A non-null array may contain {@code null} elements.
         *
         * @todo stored but not yet used.
         */
        private String[] metadata;

        /**
         * Creates a new entry.
         */
        Entry(final Map<Integer,Category[]> categories, final String paletteName) {
            this.categories  = categories;
            this.paletteName = paletteName;
        }
    }

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
    public Entry query(final String format) throws SQLException, CatalogException {
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
        final GridSampleDimension[] sampleDimensions = new GridSampleDimension[numSampleDimensions];
        final CategoryTable categories = getCategoryTable();
        final Entry entry = categories.query(format);
        final Map<Integer,Category[]> cat = entry.categories;
        try {
            for (int i=0; i<numSampleDimensions; i++) {
                sampleDimensions[i] = new GridSampleDimension(names[i], cat.remove(i+1), units[i]).geophysics(!packs[i]);
            }
        } catch (IllegalArgumentException exception) {
            throw new IllegalRecordException(exception, null, 0, format);
        }
        entry.sampleDimensions = sampleDimensions;
        if (hasMetadata) {
            entry.metadata = ArraysExt.resize(mdDim, numSampleDimensions);
        }
        return entry;
    }

    /**
     * Adds the given sample dimensions to the database.
     *
     * @param  format  the newly created format for which to write the sample dimensions.
     * @param  bands   the sample dimensions to add.
     * @throws SQLException if an error occurred while writing to the database.
     */
    public void insert(final String format, final List<GridSampleDimension> bands) throws SQLException, IllegalUpdateException {
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"format\", \"band\", \"identifier\", \"units\", \"isPacked\")"
                + " VALUES (?,?,?,?,?,?,?,?,?)");
        statement.setString(1, format);
        final List<List<Category>> categories = new ArrayList<>(bands.size());
        boolean areAllEmpty = true;
        int bandNumber = 0;
        for (GridSampleDimension band : bands) {
            final boolean isPacked = (band == (band = band.geophysics(false)));
            statement.setInt(2, ++bandNumber);
            statement.setString(3, String.valueOf(band.getDescription()));
            final Unit<?> unit = band.getUnits();
            if (unit != null) {
                statement.setString(4, transaction.database.unitFormat.format(unit));
            } else {
                statement.setNull(4, Types.VARCHAR);
            }
            statement.setBoolean(5, isPacked);
            final int count = statement.executeUpdate();
            if (count != 1) {
                throw new IllegalUpdateException(transaction.database.locale, count);
            }
            List<Category> bandCategories = band.getCategories();
            if (bandCategories == null) {
                bandCategories = Collections.emptyList();
            } else if (areAllEmpty) {
                areAllEmpty = bandCategories.isEmpty();
            }
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
