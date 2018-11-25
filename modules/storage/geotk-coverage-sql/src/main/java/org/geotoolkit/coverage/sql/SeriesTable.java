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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * Connection to a table of series.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
final class SeriesTable extends CachedTable<Integer, SeriesTable.Entry> {
    /**
     * A series of coverages sharing common characteristics in a {@link ProductEntry}.
     * A product often regroup all coverages in a single series, but in some cases a product may contain
     * more than one series. For example a <cite>Sea Surface Temperature</cite> (SST) product from
     * Nasa <cite>Pathfinder</cite> can be subdivised in two series:
     *
     * <ul>
     *   <li>Final release of historical data. Those data are often two years old.</li>
     *   <li>More recent but not yet definitive data.</li>
     * </ul>
     *
     * In most cases it is sufficient to work with {@link ProductEntry} as a whole without
     * the need to go down to the {@code SeriesTable.Entry}.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     */
    static final class Entry {
        /**
         * Identifier of this series.
         */
        final int identifier;

        /**
         * Identifier of the product to which this series belong.
         */
        final String product;

        /**
         * The directory which contains the data files for this series.
         */
        private final Path directory;

        /**
         * The extension to add to filenames, not including the dot character.
         */
        private final String extension;

        /**
         * The format of all coverages in this series.
         */
        final Format format;

        /**
         * Creates a new series entry.
         *
         * @param root       the root directory or URL, or {@code null} if none.
         * @param directory  the relative or absolute directory which contains the data files for this series.
         * @param extension  the extension to add to filenames, not including the dot character.
         * @param format     the format of all coverages in this series.
         */
        private Entry(final int identifier, final String product, final Path root, final URI directory, String extension, final Format format) {
            this.identifier = identifier;
            this.product    = product;
            this.extension  = (extension != null && !(extension = extension.trim()).isEmpty()) ? extension : null;
            this.format     = format;
            this.directory  = directory.isAbsolute() ? Paths.get(directory) : root.resolve(directory.toString());
        }

        /**
         * Returns the given filename as a {@link Path} in the directory of this series.
         *
         * @param  filename  the filename, not including the extension.
         * @return path to the file.
         */
        public Path path(String filename) {
            if (extension != null) {
                filename = filename + '.' + extension;
            }
            return directory.resolve(filename);
        }
    }

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
    Entry createEntry(final ResultSet results, final Integer identifier) throws SQLException, CatalogException {
        final String product   = results.getString(1);
        final String directory = results.getString(2);
        final String extension = results.getString(3);
        final String formatID  = results.getString(4);
        final Format format    = formats.getEntry(formatID);
        try {
            return new Entry(identifier, product, transaction.database.root, new URI(directory), extension, format);
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
    public int findOrInsert(final String product, final String directory, final String extension, final String driver,
            final List<GridSampleDimension> bands) throws SQLException, CatalogException
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
     * Closes the prepared statements created by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        formats.close();
    }
}
