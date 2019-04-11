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
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.MathTransform1D;
import org.apache.sis.measure.Units;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.math.MathFunctions;
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
     * Default range of values used if the sample dimensions does not define any transfer function.
     * This is the range of "packed values" stored as integers, together with an arbitrary transfer
     * function from integer values to real values computed by {@code defaultCategories(…)} method.
     *
     * @see #defaultCategories(List, MeasurementRange)
     */
    private static final int DEFAULT_RANGE = 10000;

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
    final SampleDimension[] query(final String format) throws SQLException, CatalogException {
        String[]  names = new String [8];
        Unit<?>[] units = new Unit<?>[8];
        boolean[] packs = new boolean[8];
        Integer[] backs = new Integer[8];
        int numSampleDimensions = 0;
        final PreparedStatement statement = prepareStatement("SELECT "
                + "\"band\", \"identifier\", \"units\", \"isPacked\", \"background\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"format\" = ? ORDER BY \"band\"");
        statement.setString(1, format);
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final int     band = results.getInt    (1);          // First band is 1.
                final String  name = results.getString (2);
                String  unitSymbol = results.getString (3);
                boolean   isPacked = results.getBoolean(4);
                Integer background = results.getInt    (5);
                if (results.wasNull()) background = null;
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
                    backs = Arrays.copyOf(backs, backs.length*2);
                }
                names[numSampleDimensions] = name;
                units[numSampleDimensions] = unit;
                packs[numSampleDimensions] = isPacked;
                backs[numSampleDimensions] = background;
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
        try {
            return getCategoryTable().query(format, names, units, packs, backs);
        } catch (IllegalArgumentException | FactoryException exception) {
            throw new IllegalRecordException(exception, null, 0, format);
        }
    }

    /**
     * Adds the given sample dimensions to the database.
     *
     * @param  format  the newly created format for which to write the sample dimensions.
     * @param  bands   the sample dimensions to add.
     * @throws SQLException if an error occurred while writing to the database.
     */
    final void insert(final String format, final List<SampleDimension> bands) throws SQLException, IllegalUpdateException {
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"format\", \"band\", \"identifier\", \"units\", \"isPacked\", \"background\") VALUES (?,?,?,?,?,?)");

        statement.setString(1, format);             // Constant for all bands.
        final List<List<Category>> categories = new ArrayList<>(bands.size());
        boolean areAllEmpty = true;
        int bandNumber = 0;
        for (final SampleDimension band : bands) {
            // Whether to consider the sample values as already converted.
            final boolean isReal = isReal(band);
            final SampleDimension packed = band.forConvertedValues(false);
            List<Category> bandCategories = packed.getCategories();
            if (isReal && band == packed) {
                /*
                 * If the SampleDimension describes values that are already converted and does not describe
                 * any way to encode the data as integer, we have to invent or own packaging here because
                 * this is the way we encode the information in the database.
                 */
                final Optional<MeasurementRange<?>> range = band.getMeasurementRange();
                if (range.isPresent()) {
                    bandCategories = defaultCategories(bandCategories, range.get());
                }
            }
            statement.setInt(2, ++bandNumber);
            statement.setString(3, String.valueOf(band.getName()));
            final Optional<Unit<?>> unit = band.getUnits();
            if (unit.isPresent()) {
                statement.setString(4, transaction.database.unitFormat.format(unit.get()));
            } else {
                statement.setNull(4, Types.VARCHAR);
            }
            statement.setBoolean(5, !isReal);
            final Optional<Number> background = packed.getBackground();
            if (background.isPresent()) {
                statement.setInt(6, background.get().intValue());
            } else {
                statement.setNull(6, Types.INTEGER);
            }
            final int count = statement.executeUpdate();
            if (count != 1) {
                throw new IllegalUpdateException(transaction.database.locale, count);
            }
            areAllEmpty &= bandCategories.isEmpty();
            categories.add(bandCategories);
        }
        if (!areAllEmpty) {
            getCategoryTable().insert(format, categories);
        }
    }

    /**
     * Returns {@code true} if the following sample dimension is considered packed or for real values.
     */
    static boolean isReal(final SampleDimension band) {
        return band.getTransferFunction().map(MathTransform1D::isIdentity).orElse(true);
    }

    /**
     * Creates a new list of categories for real values in the given range.  The returned categories use
     * an arbitrary packing. This method is invoked when the sample dimensions to insert in the database
     * declare only real values, without transfer function.  Since our database expect integer values to
     * be converted to real value using  transfer function, we have to invent our own. We insert rounded
     * numbers because the minimum and maximum values in that situation are often determined by scanning
     * the data, in which case every raster would produce different minimum and maximum. Since we want a
     * sample dimension that fit for all data of the same kind, we expand the value range for increasing
     * the chance that the categories would be reusable for other files of the same series.
     */
    private static List<Category> defaultCategories(final List<Category> original, final MeasurementRange<?> range) {
        final SampleDimension.Builder b = new SampleDimension.Builder();
        InternationalString name = null;
        int padValue = 0;
        for (final Category c : original) {
            if (!c.getTransferFunction().isPresent()) {
                b.addQualitative(c.getName(), padValue++);
            } else if (name == null) {
                name = c.getName();
            }
        }
        if (padValue == 0) {
            b.setBackground(null, padValue++);
        }
        double min = range.getMinDouble();
        double max = range.getMaxDouble();
        /*
         * The minimum and maximum values may have been extracted from the actual data, in which case
         * they are likely to differ for each files. But we need more stable values in order to avoid
         * generating new SampleDimension entries for every files of the same product. First we round
         * the extremum numbers in such a way that they differ by exactly one decimal digit.
         */
        final int exp = (int) Math.floor(Math.log10(max - min));
        final double magnitude = MathFunctions.pow10(Math.abs(exp));
        if (exp >= 0) {
            min = Math.floor(min / magnitude) * magnitude;
            max = Math.ceil (max / magnitude) * magnitude;
        } else {
            // Keep 'magnitude' as an integer for reducing rounding errors.
            min = Math.floor(min * magnitude) / magnitude;
            max = Math.ceil (max * magnitude) / magnitude;
        }
        /*
         * Compute a scale factor for this new range and round it in such a way that its only decimal digit
         * is 1, 2 or 5.  This may shrink or expand the range by a factor between 0.5714 and 1.4286 with an
         * average factor of 0.9764 (determined empirically).  To compensate, we multiply the initial scale
         * by 1.75 since 0.5714… × 1.75 ≈ 1. The result will be a range with a span between 1 and 2.5 times
         * the span of the original range.
         */
        double delta = max - min;
        double scale = delta / (DEFAULT_RANGE / 1.75);
        final int es = (int) Math.floor(Math.log10(scale));
        final double ms = MathFunctions.pow10(es);
        final double sr = scale / ms;
        if (sr >= 3.5) {
            scale = MathFunctions.pow10(es + 1);            // Equivalent to ms * 10 but more accurate.
            if (sr < 7.5) scale /= 2;                       // Equivalent to ms *  5 but more accurate.
        } else if (sr >= 1.5) {
            scale = ms * 2;
        } else {
            scale = ms;
        }
        /*
         * Distribute the extra space between the minimum and maximum and apply the same rounding than we did before
         * (only one decimal digit different between minimum and maximum). If the range is positive, distribute evenly.
         * If the range crosses zero, distribute with a proportion p tending to result in min = -max.
         *
         *     min - delta*p = -(max + delta*(1-p))
         *     delta*p = (min + max + delta) / 2
         */
        final boolean isPositive = (min >= 0);
        delta = scale * DEFAULT_RANGE - delta;
        if (isPositive || max < 0) {
            delta /= 2;
        } else {
            delta = Math.min((min + max + delta) / 2, delta);
        }
        min -= Math.max(delta, 0);
        if (exp >= 0) {
            min = Math.rint(min / magnitude) * magnitude;
        } else {
            min = Math.rint(min * magnitude) / magnitude;
        }
        if (isPositive && min < 0) min = 0;
        /*
         * We take the minimum value as the offset, assuming that above rounding introduced enough space below the
         * minimum value for encompassing all the data. This declared range is approximative anyway.
         */
        b.addQuantitative(name, padValue, DEFAULT_RANGE + 1, scale, min, range.unit());
        return b.build().getCategories();
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
