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

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.apache.sis.measure.UnitFormat;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.resources.Errors;

import org.geotoolkit.internal.sql.table.Table;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.geotoolkit.internal.sql.table.IllegalUpdateException;


/**
 * Connection to a table of {@linkplain GridSampleDimension sample dimensions}. This table creates
 * instances of {@link GridSampleDimension} for a given format. Sample dimensions are one of the
 * components needed for creation of {@link GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class SampleDimensionTable extends Table {
    /**
     * The {@linkplain Category categories} table, created only when first needed.
     */
    private transient CategoryTable categories;

    /**
     * The unit format for parsing and formatting unit symbols.
     * Created only when first needed.
     */
    private transient UnitFormat unitFormat;

    /**
     * Creates a sample dimension table.
     *
     * @param database Connection to the database.
     */
    public SampleDimensionTable(final Database database) {
        super(new SampleDimensionQuery(database));
    }

    /**
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private SampleDimensionTable(final SampleDimensionTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected SampleDimensionTable clone() {
        return new SampleDimensionTable(this);
    }

    /**
     * Returns the {@link CategoryTable} instance, creating it if needed.
     */
    private CategoryTable getCategoryTable() throws CatalogException {
        CategoryTable table = categories;
        if (table == null) {
            categories = table = getDatabase().getTable(CategoryTable.class);
        }
        return table;
    }

    /**
     * Returns the unit format for parsing and formatting unit symbols.
     * Uses the France locale because it is the authoritative locale of BIPM.
     * For most languages, it doesn't make any change in the set of symbols.
     */
    private UnitFormat getUnitFormat() {
        if (unitFormat == null) {
            unitFormat = UnitFormat.getInstance(Locale.FRANCE);
        }
        return unitFormat;
    }

    /**
     * Returns the sample dimensions for the given format. If no sample dimensions are specified,
     * return {@code null} (not an empty list). We are not allowed to return an empty list because
     * our Image I/O framework interprets that as "no bands", as opposed to "unknown bands".
     *
     * @param  format The format name.
     * @return An entry containing the sample dimensions for the given format, or {@code null} if none.
     * @throws SQLException if an error occurred while reading the database.
     */
    public CategoryEntry getSampleDimensions(final String format) throws SQLException {
        final SampleDimensionQuery query = (SampleDimensionQuery) super.query;
        String[]  names = new String [8];
        Unit<?>[] units = new Unit<?>[8];
        int numSampleDimensions = 0;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.LIST);
            final PreparedStatement statement = ce.statement;
            statement.setString(indexOf(query.byFormat), format);
            final int bandIndex = indexOf(query.band);
            final int nameIndex = indexOf(query.name);
            final int unitIndex = indexOf(query.units);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final String name = results.getString(nameIndex);
                    final int    band = results.getInt   (bandIndex); // First band is 1.
                    String unitSymbol = results.getString(unitIndex);
                    Unit<?> unit = null;
                    if (unitSymbol != null) {
                        unitSymbol = unitSymbol.trim();
                        if (unitSymbol.isEmpty()) {
                            unit = Units.ONE;
                        } else {
                            try {
                                unit = (Unit<?>) getUnitFormat().parseObject(unitSymbol);
                            } catch (ParseException e) {
                                // The constructor of this exception will close the ResultSet.
                                final IllegalRecordException ex = new IllegalRecordException(errors().getString(
                                        Errors.Keys.UnparsableString_2, "unit(" + unitSymbol + ')',
                                        unitSymbol.substring(Math.max(0, e.getErrorOffset()))),
                                        this, results, unitIndex, name);
                                ex.initCause(e);
                                throw ex;
                            }
                        }
                    }
                    if (numSampleDimensions >= names.length) {
                        names = Arrays.copyOf(names, names.length*2);
                        units = Arrays.copyOf(units, units.length*2);
                    }
                    names[numSampleDimensions] = name;
                    units[numSampleDimensions] = unit;
                    if (band != ++numSampleDimensions) {
                        // The constructor of this exception will close the ResultSet.
                        throw new IllegalRecordException(errors().getString(
                                Errors.Keys.NonConsecutiveBands_2, numSampleDimensions, band),
                                this, results, bandIndex, format);
                    }
                }
            }
            release(lc, ce);
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
        final CategoryEntry entry = categories.getCategories(format);
        final Map<Integer,Category[]> cat = entry.categories;
        for (int i=0; i<numSampleDimensions; i++) {
            try {
                sampleDimensions[i] = new GridSampleDimension(names[i], cat.remove(i+1), units[i]);
            } catch (IllegalArgumentException exception) {
                throw new IllegalRecordException(exception, categories, null, 0, format);
            }
        }
        entry.sampleDimensions = sampleDimensions;
        return entry;
    }

    /**
     * Adds the given sample dimensions to the database.
     *
     * @param  format The newly created format for which to write the sample dimensions.
     * @param  bands The sample dimensions to add.
     * @throws SQLException if an error occurred while writing to the database.
     *
     * @since 3.13
     */
    public void addEntries(final String format, final List<GridSampleDimension> bands) throws SQLException {
        final SampleDimensionQuery query = (SampleDimensionQuery) super.query;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            boolean success = false;
            transactionBegin(lc);
            try {
                final LocalCache.Stmt ce = getStatement(lc, QueryType.INSERT);
                final PreparedStatement statement = ce.statement;
                statement.setString(indexOf(query.format), format);
                final int bandIndex = indexOf(query.band);
                final int nameIndex = indexOf(query.name);
                final int unitIndex = indexOf(query.units);
                final List<List<Category>> categories = new ArrayList<>(bands.size());
                boolean isEmpty = true;
                int bandNumber = 0;
                for (GridSampleDimension band : bands) {
                    band = band.geophysics(false);
                    statement.setInt(bandIndex, ++bandNumber);
                    statement.setString(nameIndex, String.valueOf(band.getDescription()));
                    final Unit<?> unit = band.getUnits();
                    if (unit != null) {
                        statement.setString(unitIndex, getUnitFormat().format(unit));
                    } else {
                        statement.setNull(unitIndex, Types.VARCHAR);
                    }
                    final int count = statement.executeUpdate();
                    if (count != 1) {
                        throw new IllegalUpdateException(getLocale(), count);
                    }
                    List<Category> bandCategories = band.getCategories();
                    if (bandCategories == null) {
                        bandCategories = Collections.emptyList();
                    } else if (isEmpty) {
                        isEmpty = bandCategories.isEmpty();
                    }
                    categories.add(bandCategories);
                }
                release(lc, ce);
                if (!isEmpty) {
                    getCategoryTable().addEntries(format, categories);
                }
                success = true;
            } finally {
                transactionEnd(lc, success);
            }
        }
    }
}
