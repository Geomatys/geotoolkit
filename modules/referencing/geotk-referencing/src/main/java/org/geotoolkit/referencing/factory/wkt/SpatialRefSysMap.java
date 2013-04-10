/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.factory.wkt;

import java.util.Map;
import java.util.Set;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.naming.DefaultNameSpace;
import org.apache.sis.util.collection.BackingStoreException;

import static org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory.*;


/**
 * A {@link java.util.Map} view over a {@code "spatial_ref_sys"} table in a PostGIS database.
 * If a {@link SQLException} is thrown, then it is wrapped in a {@link BackingStoreException}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.5)
 * @module
 */
final class SpatialRefSysMap extends AbstractMap<String,String> {
    /**
     * Connection to the database, or {@code null} if none.
     */
    final Connection connection;

    /**
     * The schema of the CRS table, or {@code null} if none.
     */
    private final String schema;

    /**
     * The prepared statement for fetching the primary key, or {@code null} if not yet created.
     */
    private transient PreparedStatement selectPK;

    /**
     * The prepared statement for selecting an object, or {@code null} if not yet created.
     */
    private transient PreparedStatement select;

    /**
     * Creates a map. This constructor auto-detects the schema where
     * the {@code "spatial_ref_sys"} table is declared.
     *
     * @param connection The connection to the database.
     * @throws SQLException If an error occurred while fetching metadata from the database.
     */
    public SpatialRefSysMap(final Connection connection) throws SQLException {
        this.connection = connection;
        String schema;
        try (ResultSet result = connection.getMetaData().getTables(null, null, TABLE, new String[] {"TABLE"})) {
            schema = null;
            if (result.next()) {
                schema = result.getString("TABLE_SCHEM");
            }
        }
        this.schema = schema;
    }

    /**
     * Appends the {@code "FROM"} clause to the specified SQL statement.
     */
    private StringBuilder appendFrom(final StringBuilder sql) {
        sql.append(" FROM ");
        if (schema != null) {
            sql.append(schema).append('.');
        }
        return sql.append(TABLE);
    }

    /**
     * Returns the authority names found in the database. Keys are authority names,
     * and values are whatever the authority codes match primary keys or not.
     *
     * @return All authority names found in the database.
     * @throws SQLException if an access to the database failed.
     */
    Map<String,Boolean> getAuthorityNames() throws SQLException {
        final StringBuilder sql = new StringBuilder("SELECT ").append(AUTHORITY_COLUMN)
                .append(", SUM(CASE WHEN ").append(CODE_COLUMN).append('=').append(PRIMARY_KEY)
                .append(" THEN 1 ELSE 0 END) AS np, COUNT(").append(AUTHORITY_COLUMN).append(") AS n");
        appendFrom(sql)
                .append(" GROUP BY ").append(AUTHORITY_COLUMN)
                .append(" ORDER BY np DESC, n DESC");
        final Map<String,Boolean> authorities = new LinkedHashMap<>();
        try (Statement stmt = connection.createStatement();
             ResultSet results = stmt.executeQuery(sql.toString()))
        {
            while (results.next()) {
                final String name = results.getString(1); // May be null.
                final int    np   = results.getInt   (2);
                final int    n    = results.getInt   (3);
                authorities.put(name, np == n);
            }
        }
        return authorities;
    }

    /**
     * Returns the authority codes defined in the database for the given type.
     *
     * @param category The type of objects to search for (typically <code>{@linkplain
     *                 org.opengis.referencing.crs.CoordinateReferenceSystem}.class</code>).
     * @return The set of available codes.
     * @throws SQLException if an error occurred while querying the database.
     */
    Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> category) throws SQLException {
        final StringBuilder sql = new StringBuilder("SELECT CASE WHEN ")
                .append(CODE_COLUMN).append('=').append(PRIMARY_KEY).append(" THEN ")
                .append(PRIMARY_KEY).append("::text ELSE ").append(AUTHORITY_COLUMN)
                .append(" || '").append(DefaultNameSpace.DEFAULT_SEPARATOR).append("' || ")
                .append(CODE_COLUMN).append(" END AS code");
        appendFrom(sql);
        final String type = WKTFormat.getNameOf(category);
        if (type != null) {
            sql.append(" WHERE srtext ILIKE '").append(type).append("%'");
        }
        sql.append(" ORDER BY ").append(PRIMARY_KEY);
        final Set<String> codes = new LinkedHashSet<>();
        try (Statement stmt = connection.createStatement();
             ResultSet results = stmt.executeQuery(sql.toString()))
        {
            while (results.next()) {
                codes.add(results.getString(1));
            }
        }
        return codes;
    }

    /**
     * Returns the primary key for the specified authority code. This method searches for a row
     * with the given authority in the <cite>authority name</cite> column and the given integer
     * code in the <cite>authority SRID</cite> column. If such row is found, the value of its
     * <cite>SRID</cite> column is returned. Otherwise this method returns {@code null}.
     *
     * @param  code The authority code, for formatting an error message if needed.
     * @param  authority The authority part of the above code.
     * @param  srid The integer code part of the above code.
     * @return The primary key for the supplied code, or {@code null} if it has not been found.
     * @throws SQLException if an error occurred while querying the database.
     */
    Integer getPrimaryKey(final String code, final String authority, final int srid) throws SQLException {
        if (selectPK == null) {
            final StringBuilder sql = new StringBuilder("SELECT ").append(PRIMARY_KEY);
            appendFrom(sql).append(" WHERE ").append(AUTHORITY_COLUMN).append("=?")
                    .append(" AND ").append(CODE_COLUMN).append("=?");
            selectPK = connection.prepareStatement(sql.toString());
        }
        selectPK.setString(1, authority);
        selectPK.setInt   (2, srid);
        return singleton(selectPK, Integer.class, code);
    }

    /**
     * Returns the value in the specified statement. This method ensures that the result set
     * contains only one value.
     *
     * @param  statement The statement to execute.
     * @param  type The type of the value to fetch.
     * @param  code The authority code, for formatting an error message if needed.
     * @return The singleton value found, or {@code null} if none.
     * @throws SQLException if an error occurred while querying the database.
     */
    private static <T> T singleton(final PreparedStatement statement, final Class<T> type, final String code)
            throws SQLException
    {
        T value = null;
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final Object candidate;
                if (Integer.class.isAssignableFrom(type)) {
                    candidate = results.getInt(1);
                } else {
                    candidate = results.getString(1);
                }
                if (!results.wasNull()) {
                    if (value != null && !candidate.equals(value)) {
                        throw new SQLIntegrityConstraintViolationException(
                                Errors.format(Errors.Keys.DUPLICATED_VALUES_FOR_KEY_$1, code));
                    }
                    value = type.cast(candidate);
                }
            }
        }
        return value;
    }

    /**
     * Returns the WKT for the given code.
     *
     * @param  code The code of the CRS object to query, as an {@link Integer}.
     * @return The Well Known Text (WKT) for the given code, or {@code null} if none.
     * @throws BackingStoreException if an error occurred while querying the database.
     */
    @Override
    public String get(final Object key) throws BackingStoreException {
        final int srid = (Integer) key;
        try {
            if (select == null) {
                final StringBuilder sql = new StringBuilder("SELECT ").append(WKT_COLUMN);
                appendFrom(sql).append(" WHERE ").append(PRIMARY_KEY).append("=?");
                select = connection.prepareStatement(sql.toString());
            }
            select.setInt(1, srid);
            return singleton(select, String.class, key.toString());
        } catch (SQLException exception) {
            throw new BackingStoreException(exception);
        }
    }

    /**
     * Returns {@code true} if the database contains the given code.
     *
     * @param  code The code of the CRS object to query, as an {@link Integer}.
     * @throws BackingStoreException if an error occurred while querying the database.
     */
    @Override
    public boolean containsKey(final Object key) throws BackingStoreException {
        return get(key) != null;
    }

    /**
     * Returns {@code false} in all cases. This is a violation of the {@link Map} contract, but we
     * do that because this method is invoked by {@link WKTParsingAuthorityFactory#availability()}
     * and we want to avoid querying the database at this stage.
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Returns a view over the (key, wkt) pair in the database. We do not support this
     * operation at this stage (it is not needed by {@link WKTParsingAuthorityFactory}).
     */
    @Override
    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     */
    void dispose() throws SQLException {
        if (select != null) {
            select.close();
            select = null;
        }
        if (selectPK != null) {
            selectPK.close();
            selectPK = null;
        }
        connection.close();
    }
}
