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
import java.util.Calendar;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;


/**
 * Connection to a table of series.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
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
        return "SELECT \"product\", \"directory\", \"extension\", \"format\""
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
        final String directory = results.getString(2);
        final String extension = results.getString(3);
        final String formatID  = results.getString(4);
        final FormatEntry format = formats.getEntry(formatID);
        try {
            return new SeriesEntry(identifier, product, transaction.database.root, new URI(directory), extension, format);
        } catch (URISyntaxException e) {
            throw new IllegalRecordException(e, results, 2, identifier);
        }
    }

    /**
     * Returns the identifier for a series having the specified properties. If no
     * matching record is found, then a new one is created and added to the database.
     *
     * @param  directory  the path relative to the root directory, or the base URL.
     * @param  extension  the extension to add to filenames, or {@code null} or empty if none.
     * @param  driver     driver (data store name) of the format for the series considered.
     * @param  bands      the sample dimensions of the data to be added.
     * @return the identifier of a matching entry (never {@code null}).
     * @throws SQLException if an error occurred while reading from or writing to the database.
     */
    final int findOrInsert(final String product, final String directory, final String extension, final String driver,
            final List<SampleDimension> bands) throws SQLException, DataStoreException
    {
        final String format = formats.findOrInsert(driver, bands, product);
        boolean insert = false;
        do {
            final PreparedStatement statement;
            if (!insert) {
                statement = prepareStatement("SELECT \"identifier\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE "
                        + "\"product\"=? AND \"directory\"=? AND \"extension\" IS NOT DISTINCT FROM ? AND \"format\"=?");
            } else {
                statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"product\", \"directory\", \"extension\", \"format\") VALUES (?,?,?,?)", "identifier");
            }
            statement.setString(1, product);
            statement.setString(2, directory);
            if (extension != null && !extension.isEmpty()) {
                statement.setString(3, extension);
            } else {
                statement.setNull(3, Types.VARCHAR);
            }
            statement.setString(4, format);
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
     *
     * @todo current query does not use the "endTime" column of index. We may be able to achieve better
     *       performance by querying each series separately (they should not overlap anyway) and merge
     *       them together in Java code. TODO: check with PostgreSQL "EXPLAIN" on a larger data set.
     */
    final double[] listAllDates(final String product) throws SQLException {
        int count = 0;
        double[] timestamps = new double[100];
        final PreparedStatement statement = prepareStatement(
                "SELECT DISTINCT \"startTime\" + (\"endTime\" - \"startTime\")/2 AS \"time\" " +
                "FROM " + SCHEMA + ".\"" + GridCoverageTable.TABLE + "\" INNER JOIN " + SCHEMA + ".\"" + TABLE + "\" " +
                "ON (\"series\" = \"" + TABLE + "\".\"identifier\") WHERE \"product\"=? ORDER BY \"time\"");
        statement.setString(1, product);
        try (ResultSet results = statement.executeQuery()) {
            final Calendar calendar = newCalendar();
            final DefaultTemporalCRS axis = transaction.database.temporalCRS;
            while (results.next()) {
                final Timestamp stamp = results.getTimestamp(1, calendar);
                if (count >= timestamps.length) {
                    timestamps = Arrays.copyOf(timestamps, count*2);
                }
                timestamps[count++] = axis.toValue(stamp);
            }
        }
        return ArraysExt.resize(timestamps, count);
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
