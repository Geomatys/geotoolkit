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

import java.util.Arrays;
import java.util.Calendar;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.util.ArraysExt;


/**
 * Connection to a table of series.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
final class SeriesTable extends CachedTable<Integer,SeriesEntry> {
    /**
     * Name of this table in the database.
     */
    static final String TABLE = "Series";

    /**
     * The table to use for fetching information about formats.
     */
    private final FormatTable formats;

    /**
     * Creates a series table.
     */
    SeriesTable(final Transaction transaction) {
        super(Target.SERIES, transaction);
        formats = new FormatTable(transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"product\", \"dataset\", \"directory\", \"extension\", \"format\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"identifier\" = ?";
    }

    /**
     * Creates a series from the current row in the specified result set.
     *
     * @param  results     the result set to read.
     * @param  identifier  the identifier of the series to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    SeriesEntry createEntry(final ResultSet results, final Integer identifier) throws SQLException, DataStoreException {
        final String product   = results.getString(1);
        final String dataset   = results.getString(2);
        final String directory = results.getString(3);
        final String extension = results.getString(4);
        final String formatID  = results.getString(5);
        final FormatEntry format = formats.getEntry(formatID);
        try {
            return new SeriesEntry(identifier, product, dataset, transaction.database.root, new URI(directory), extension, format);
        } catch (URISyntaxException e) {
            throw new IllegalRecordException(e, results, 2, identifier);
        }
    }

    /**
     * Returns the identifier for a series having the specified properties.
     * If no matching record is found, then a new one is created and added to the database.
     *
     * @param  product    identifier of the product to which the series belong.
     * @param  directory  the path relative to the root directory, or the base URL.
     * @param  extension  the extension to add to filenames, or {@code null} or empty if none.
     * @param  raster     information about the raster to be added.
     * @return the identifier of a matching entry (never {@code null}).
     * @throws SQLException if an error occurred while reading from or writing to the database.
     */
    final int findOrInsert(final String product, final String directory, final String extension, final NewRaster raster)
            throws SQLException, DataStoreException
    {
        final String format = formats.findOrInsert(product, raster.driver, SampleDimensionEntry.wrap(raster.bands), raster.suggestedID(product));
        boolean insert = false;
        do {
            final PreparedStatement statement;
            if (!insert) {
                statement = prepareStatement("SELECT \"identifier\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE "
                        + "\"product\"=? AND \"dataset\" IS NOT DISTINCT FROM ? AND "
                        + "\"directory\"=? AND \"extension\" IS NOT DISTINCT FROM ? AND \"format\"=?");
            } else {
                statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"product\", \"dataset\", \"directory\", \"extension\", \"format\") VALUES (?,?,?,?,?)", "identifier");
            }
            statement.setString(1, product);
            if (raster.dataset != null) {
                statement.setString(2, raster.dataset);
            } else {
                statement.setNull(2, Types.VARCHAR);
            }
            statement.setString(3, directory);
            if (extension != null && !extension.isEmpty()) {
                statement.setString(4, extension);
            } else {
                statement.setNull(4, Types.VARCHAR);
            }
            statement.setString(5, format);
            if (insert) {
                if (statement.executeUpdate() == 0) {
                    continue;                                           // Should never happen, but we are paranoiac.
                }
            }
            try (ResultSet results = insert ? statement.getGeneratedKeys() : statement.executeQuery()) {
                while (results.next()) {
                    final int identifier = results.getInt(1);
                    if (!results.wasNull()) return identifier;          // Should never be null, but we are paranoiac.
                }
            }
        } while ((insert = !insert) == true);
        throw new IllegalUpdateException("Can not add the series.");    // TODO: provide better error message.
    }

    /**
     * Lists the timestamps of all rasters in the given product.
     * This time returns <em>central</em> date of each raster.
     * It is caller's responsibility to convert from "pixel center" to "pixel corner" convention.
     * The returned time positions use {@link GridGeometryEntry#TEMPORAL_CRS}.
     */
    final double[] listAllDates(final String product, final GridGeometryTable gridGeometries)
            throws SQLException, TransformException, DataStoreException
    {
        final DefaultTemporalCRS crs = transaction.database.temporalCRS;
        final Calendar calendar = newCalendar();
        MathTransform1D timeOffsets = null;
        double[] times = new double[500];
        int count = 0;
        final PreparedStatement statement = prepareStatement(
                "SELECT \"grid\", \"startTime\", (\"startTime\" + (\"endTime\" - \"startTime\")/2) AS \"medianTime\"" +
                "FROM " + SCHEMA + ".\"" + GridCoverageTable.TABLE + "\" " +
                "INNER JOIN " + SCHEMA + ".\"" + TABLE + "\" ON (\"series\" = \"identifier\") " +
                "WHERE \"product\"=? AND \"startTime\" IS NOT NULL");

        statement.setString(1, product);
        try (ResultSet results = statement.executeQuery()) {
            int lastGrid = Integer.MIN_VALUE;
            int numToAdd = 0;
            while (results.next()) {
                final int gridId = results.getInt(1);
                if (gridId != lastGrid) {
                    lastGrid = gridId;
                    final AdditionalAxisEntry axis = gridGeometries.listTimeOffsets(gridId);
                    if (axis != null) {
                        timeOffsets = axis.gridToCRS;
                        numToAdd    = axis.count;
                    } else {
                        timeOffsets = null;
                        numToAdd    = 1;
                    }
                }
                if (count + numToAdd > times.length) {
                    times = Arrays.copyOf(times, Math.max(times.length * 2, count + numToAdd));
                }
                if (timeOffsets == null) {
                    final Timestamp time = results.getTimestamp(3, calendar);
                    times[count++] = crs.toValue(time);
                } else {
                    final Timestamp startTime = results.getTimestamp(2, calendar);
                    final double tMin = crs.toValue(startTime);
                    for (int i=0; i<numToAdd; i++) {
                        times[count++] = tMin + timeOffsets.transform(i + 0.5);
                    }
                }
            }
        }
        /*
         * Sorts, then removes duplicated values.
         */
        if (count == 0) {
            return ArraysExt.EMPTY_DOUBLE;
        }
        Arrays.parallelSort(times, 0, count);
        double previous = times[0];
        int n=1;
        for (int i=1; i<count; i++) {
            final double t = times[i];
            if (t != previous) {
                times[n++] = t;
                previous = t;
            }
        }
        return ArraysExt.resize(times, n);
    }

    /**
     * Returns what seems a commonly used format for the given product.
     * Current implementation checks only the number of occurrences in "Series" table;
     * we do not count the number of occurrences in "GridCoverages" table.
     */
    final FormatEntry getRepresentativeFormat(final String product) throws SQLException, DataStoreException {
        String identifier = null;
        final PreparedStatement statement = prepareStatement("SELECT \"format\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE "
                + "\"product\"=? GROUP BY \"format\" ORDER BY COUNT(*) DESC");
        statement.setString(1, product);
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                identifier = results.getString(1);
                if (!results.wasNull()) break;      // Paranoiac check.
            }
        }
        if (identifier != null) {
            return formats.getEntry(identifier);
        }
        return null;
    }

    /**
     * Closes the prepared statements created by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        formats.close();
    }
}
