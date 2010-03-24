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

import java.util.Map;
import java.util.Arrays;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.resources.Errors;

import org.geotoolkit.internal.sql.table.Table;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.IllegalRecordException;


/**
 * Connection to a table of {@linkplain GridSampleDimension sample dimensions}. This table creates
 * instances of {@link GridSampleDimension} for a given format. Sample dimensions are one of the
 * components needed for creation of {@link GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class SampleDimensionTable extends Table {
    /**
     * Connection to the {@linkplain Category categories} table.
     * Will be created only when first needed.
     * <p>
     * This field doesn't need to be declared {@code volatile} because it is not used
     * outside this {@code SampleDimensionTable}, so it is not expected to be accessed
     * by other threads.
     */
    private transient CategoryTable categories;

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
     * Returns the sample dimensions for the given format.
     *
     * @param  format The format name.
     * @return The sample dimensions for the given format.
     * @throws SQLException if an error occured while reading the database.
     */
    public GridSampleDimension[] getSampleDimensions(final String format) throws SQLException {
        final SampleDimensionQuery query = (SampleDimensionQuery) super.query;
        String[]  names = new String [8];
        Unit<?>[] units = new Unit<?>[8];
        int numSampleDimensions = 0;
        UnitFormat unitFormat = null;
        synchronized (getLock()) {
            final LocalCache.Stmt ce = getStatement(QueryType.LIST);
            final PreparedStatement statement = ce.statement;
            statement.setString(indexOf(query.byFormat), format);
            final int bandIndex = indexOf(query.band);
            final int nameIndex = indexOf(query.name);
            final int unitIndex = indexOf(query.units);
            final ResultSet results = statement.executeQuery();
            while (results.next()) {
                final String name = results.getString(nameIndex);
                final int    band = results.getInt   (bandIndex); // First band is 1.
                String unitSymbol = results.getString(unitIndex);
                Unit<?> unit = null;
                if (unitSymbol != null) {
                    unitSymbol = unitSymbol.trim();
                    if (unitSymbol.length() == 0) {
                        unit = Unit.ONE;
                    } else {
                        if (unitFormat == null) {
                            unitFormat = UnitFormat.getInstance();
                        }
                        try {
                            unit = (Unit<?>) unitFormat.parseObject(unitSymbol);
                        } catch (ParseException e) {
                            // The constructor of this exception will close the ResultSet.
                            final IllegalRecordException ex = new IllegalRecordException(errors().getString(
                                    Errors.Keys.UNPARSABLE_STRING_$2, "unit(" + unitSymbol + ')',
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
                            Errors.Keys.NON_CONSECUTIVE_BANDS_$2, numSampleDimensions, band),
                            this, results, bandIndex, format);
                }
            }
            results.close();
            ce.release();
        }
        /*
         * At this point, we have successfully read every SampleDimension rows.
         * Now read the categories, provided that there is at least one sample
         * dimension.
         */
        final GridSampleDimension[] sampleDimensions = new GridSampleDimension[numSampleDimensions];
        if (numSampleDimensions != 0) {
            if (categories == null) {
                categories = getDatabase().getTable(CategoryTable.class);
            }
            final Map<Integer,Category[]> cat = categories.getCategories(format);
            for (int i=0; i<numSampleDimensions; i++) {
                try {
                    sampleDimensions[i] = new GridSampleDimension(names[i], cat.remove(i+1), units[i]);
                } catch (IllegalArgumentException exception) {
                    throw new IllegalRecordException(exception, categories, null, 0, format);
                }
            }
        }
        return sampleDimensions;
    }
}
