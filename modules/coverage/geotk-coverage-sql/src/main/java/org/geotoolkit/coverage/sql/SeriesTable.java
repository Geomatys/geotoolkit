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

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.SQLNonTransientException;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.ConfigurationKey;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.DuplicatedRecordException;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Connection to a table of series. This connection is used internally by the
 * {@linkplain LayerTable layer table}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class SeriesTable extends SingletonTable<SeriesEntry> {
    /**
     * The format table, created when first needed.
     */
    private transient FormatTable formats;

    /**
     * The layer for which we want the series.
     */
    private String layer;

    /**
     * Creates a series table.
     *
     * @param database Connection to the database.
     */
    public SeriesTable(final Database database) {
        this(new SeriesQuery(database));
    }

    /**
     * Creates a series table using the specified query.
     */
    private SeriesTable(final SeriesQuery query) {
        super(query, query.byIdentifier);
    }

    /**
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private SeriesTable(final SeriesTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected SeriesTable clone() {
        return new SeriesTable(this);
    }

    /**
     * Returns the layer for the series to be returned by {@link #getEntries() getEntries()}.
     * The default value is {@code null}, which means that no filtering should be performed.
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the layer for the series to be returned. Next call to {@link #getEntries()}
     * will filters the series in order to return only the ones in this layer.
     */
    public void setLayer(final String layer) {
        if (!Utilities.equals(layer, this.layer)) {
            this.layer = layer;
            fireStateChanged("layer");
        }
    }

    /**
     * Returns the {@link FormatTable} instance, creating it if needed.
     */
    private FormatTable getFormatTable() throws CatalogException {
        FormatTable table = formats;
        if (table == null) {
            formats = table = getDatabase().getTable(FormatTable.class);
        }
        return table;
    }

    /**
     * Invoked automatically for a newly created statement or when this table
     * changed its state. The current implementation setups the SQL parameter
     * for the {@linkplain #getLayer currently selected layer}.
     */
    @Override
    protected void configure(final LocalCache lc, final QueryType type, final PreparedStatement statement)
            throws SQLException
    {
        super.configure(lc, type, statement);
        final SeriesQuery query = (SeriesQuery) super.query;
        final int index = query.byLayer.indexOf(type);
        if (index != 0) {
            final String layer = getLayer();
            if (layer == null) {
                throw new CatalogException(errors().getString(Errors.Keys.NO_PARAMETER_1, "layer"));
            }
            statement.setString(index, layer);
        }
    }

    /**
     * Creates a series from the current row in the specified result set.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  results The result set to read.
     * @param  identifier The identifier of the series to create.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    protected SeriesEntry createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws SQLException
    {
        final SeriesQuery query     = (SeriesQuery) super.query;
        final String  formatID      = results.getString(indexOf(query.format));
        final String  pathname      = results.getString(indexOf(query.pathname));
        final String  extension     = results.getString(indexOf(query.extension));
        final String  remarks       = results.getString(indexOf(query.comments));
        final String  rootDirectory = getProperty(ConfigurationKey.ROOT_DIRECTORY);
        final String  rootURL       = getProperty(ConfigurationKey.ROOT_URL);
        final FormatEntry format = getFormatTable().getEntry(formatID);
        return new SeriesEntry((Integer) identifier, (rootDirectory != null) ? rootDirectory : rootURL,
                               pathname, extension, format, remarks);
    }

    /**
     * Returns all series as (<var>identifier</var>, <var>series</var>) pairs.
     */
    public Map<Integer,SeriesEntry> getEntriesMap() throws SQLException {
        final Set<SeriesEntry> entries = getEntries();
        final Map<Integer,SeriesEntry> map = new HashMap<Integer,SeriesEntry>(XCollections.hashMapCapacity(entries.size()));
        for (final SeriesEntry entry : entries) {
            final Integer identifier = entry.getIdentifier();
            if (map.put(identifier, entry) != null) {
                throw new DuplicatedRecordException(errors().getString(Errors.Keys.DUPLICATED_RECORD_1, identifier));
            }
        }
        return map;
    }

    /**
     * Returns the identifier for a series having the specified properties.
     * If no matching record is found, then this method returns {@code null}.
     * <p>
     * The {@link #setLayer(LayerEntry)} method must be invoked before this one.
     *
     * @param  path      The path relative to the root directory, or the base URL.
     * @param  extension The extension to add to filenames.
     * @param  format    The format for the series considered.
     * @return The identifier of a matching entry, or {@code null} if none.
     * @throws SQLException if an error occurred while reading from the database.
     */
    Integer find(final String path, final String extension, final String format) throws SQLException {
        ensureNonNull("path",      path);
        ensureNonNull("extension", extension);
        ensureNonNull("format",    format);
        Integer id = null;
        final SeriesQuery query = (SeriesQuery) super.query;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.LIST);
            final PreparedStatement statement = ce.statement;
            final int idIndex = indexOf(query.identifier);
            final int pnIndex = indexOf(query.pathname);
            final int exIndex = indexOf(query.extension);
            final int ftIndex = indexOf(query.format);
            final ResultSet results = statement.executeQuery();
            while (results.next()) {
                final int nextID = results.getInt(idIndex);
                String value = results.getString(pnIndex);
                if (value == null || !comparePaths(value, path)) {
                    continue;
                }
                value = results.getString(exIndex);
                if (value == null || !value.equals(extension)) {
                    continue;
                }
                value = results.getString(ftIndex);
                if (value == null || !value.equals(format)) {
                    continue;
                }
                if (id != null && id.intValue() != nextID) {
                    // Could happen if there is insufficient conditions in the WHERE clause.
                    log("find", errors().getLogRecord(Level.WARNING, Errors.Keys.DUPLICATED_RECORD_1, id));
                    continue;
                }
                id = nextID;
            }
            results.close();
            release(lc, ce);
        }
        return id;
    }

    /**
     * Returns the identifier for a series having the specified properties. If no
     * matching record is found, then a new one is created and added to the database.
     * <p>
     * The {@link #setLayer(LayerEntry)} method must be invoked before this one.
     *
     * @param  path      The path relative to the root directory, or the base URL.
     * @param  extension The extension to add to filenames.
     * @param  format    The format for the series considered.
     * @return The identifier of a matching entry (never {@code null}).
     * @throws SQLException if an error occurred while reading from or writing to the database.
     */
    int findOrCreate(final String path, final String extension, final String format) throws SQLException {
        Integer id;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            boolean success = false;
            transactionBegin(lc);
            try {
                id = find(path, extension, format);
                if (id == null) {
                    /*
                     * No match found. Adds a new record in the database.
                     */
                    final SeriesQuery query = (SeriesQuery) super.query;
                    final LocalCache.Stmt ce = getStatement(lc, QueryType.INSERT);
                    final PreparedStatement statement = ce.statement;
                    statement.setString(indexOf(query.layer),     getLayer());
                    statement.setString(indexOf(query.pathname),  trimRoot(path));
                    statement.setString(indexOf(query.extension), extension);
                    statement.setString(indexOf(query.format),    format);
                    success = updateSingleton(statement);
                    /*
                     * Get the identifier of the entry that we just generated.
                     */
                    final ResultSet keys = statement.getGeneratedKeys();
                    while (keys.next()) {
                        id = keys.getInt(query.identifier.name);
                        if (!keys.wasNull()) break;
                        id = null; // Should never reach this point, but I'm paranoiac.
                    }
                    keys.close();
                    release(lc, ce);
                }
            } finally {
                transactionEnd(lc, success);
            }
        }
        if (id == null) {
            // Should never occur, but I'm paranoiac.
            throw new SQLNonTransientException();
        }
        return id;
    }

    /**
     * Returns {@code true} if the given paths are equals or equivalent. The two paths can
     * be relative or absolute, or only one path can be relative and the other one absolute.
     *
     * @param candidate The first path to compare. Can be relative or absolute.
     * @param path The second path to compare. Can be relative or absolute.
     * @return {@code true} if the two paths reference the same file.
     */
    private boolean comparePaths(final String candidate, final String path) {
        if (candidate.equals(path)) {
            return true;
        }
        File candidateFile = new File(candidate);
        File pathFile = new File(path);
        if (candidateFile.equals(pathFile)) {
            return true;
        }
        if (candidateFile.isAbsolute() && !pathFile.isAbsolute()) {
            return compareRelativeAndAbsolutePaths(pathFile, candidateFile);
        }
        if (!candidateFile.isAbsolute() && pathFile.isAbsolute()) {
            return compareRelativeAndAbsolutePaths(candidateFile, pathFile);
        }
        /*
         * If the above failed, tries to compare absolute path.
         */
        final String root = getProperty(ConfigurationKey.ROOT_DIRECTORY);
        if (root != null) {
            if (!candidateFile.isAbsolute()) {
                candidateFile = new File(root, candidateFile.getPath());
            }
            if (!pathFile.isAbsolute()) {
                pathFile = new File(root, pathFile.getPath());
            }
        }
        File cf = null; // Used for error message only.
        try {
            candidateFile = (cf = candidateFile).getCanonicalFile();
            pathFile = (cf = pathFile).getCanonicalFile();
        } catch (IOException exeption) {
            // Logs with a FINE level rather than WARNING because this exception may be normal.
            final LogRecord record = errors().getLogRecord(Level.FINE, Errors.Keys.NOT_A_DIRECTORY_1, cf);
            record.setThrown(exeption);
            log("comparePaths", record);
            return false;
        }
        return candidateFile.equals(pathFile);
    }

    /**
     * Returns {@code true} if the given absolute path ends with the given relative path.
     *
     * @param relative The relative path.
     * @param absolute The absolute path.
     * @return {@code true} if the absolute path ends with the relative path.
     */
    private static boolean compareRelativeAndAbsolutePaths(File relative, File absolute) {
        assert !relative.isAbsolute() : relative;
        assert  absolute.isAbsolute() : absolute;
        do {
            if (!relative.getName().equals(absolute.getName())) {
                return false;
            }
            absolute = absolute.getParentFile();
            if (absolute == null) {
                return false;
            }
        } while ((relative = relative.getParentFile()) != null);
        return true;
    }

    /**
     * Trims the root directory (if any) from the given path.
     */
    private String trimRoot(String path) {
        String root = getProperty(ConfigurationKey.ROOT_DIRECTORY);
        if (root != null) {
            final File pathFile = new File(path);
            if (pathFile.isAbsolute()) {
                final File rootFile = new File(root);
                if (rootFile.isAbsolute()) {
                    path = pathFile.getPath(); // For making sure that we use the right name separator.
                    root = rootFile.getPath();
                    if (path.startsWith(root)) {
                        path = path.substring(root.length());
                        if (path.startsWith(File.separator)) {
                            path = path.substring(File.separator.length());
                        }
                    }
                }
            }
        }
        return path.replace(File.separatorChar, '/').trim();
    }
}
