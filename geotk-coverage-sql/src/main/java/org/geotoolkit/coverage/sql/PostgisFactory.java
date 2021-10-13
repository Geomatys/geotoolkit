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
package org.geotoolkit.coverage.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.sis.internal.referencing.WKTKeywords;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.apache.sis.util.logging.Logging;
import org.apache.sis.internal.simple.SimpleCitation;
import org.apache.sis.io.wkt.WKTDictionary;
import org.apache.sis.util.iso.DefaultNameSpace;
import org.apache.sis.io.TableAppender;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;


/**
 * An authority factory creating CRS from the {@value #TABLE} table in a spatial SQL database.
 * This class is called <code><u>Postgis</u>Factory</code> because of some assumptions more suitable to PostGIS,
 * like the default {@linkplain #getAuthority() authority} if none were explicitly defined.
 * But this class should be usable with other OGC compliant spatial database as well.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class PostgisFactory extends WKTDictionary implements CRSAuthorityFactory {
    /**
     * The standard name of the table containing CRS definitions, which is {@value}.
     */
    private static final String TABLE = "spatial_ref_sys";

    /**
     * The primary key column, which is {@value}.
     */
    private static final String PRIMARY_KEY = "srid";

    /**
     * The standard name ({@value}) of the column containing the authority names.
     */
    private static final String AUTHORITY_COLUMN = "auth_name";

    /**
     * The standard name ({@value}) of the column containing the authority codes.
     */
    private static final String CODE_COLUMN = "auth_srid";

    /**
     * The standard name ({@value}) of the column containing the WKT definitions.
     */
    private static final String WKT_COLUMN = "srtext";

    /**
     * Connection to the database.
     */
    private final Connection connection;

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
     * Authorities found in the database. Will be computed only when first needed.
     * Keys are authority names, and values are whatever the authority codes match
     * primary keys or not.
     */
    private transient Map<String,Boolean> authorityUsePK;

    public PostgisFactory(final Connection connection) throws SQLException {
        this(new SimpleCitation("PostGIS"), connection);
        parser.setConvention(Convention.WKT1_COMMON_UNITS);
    }

    /**
     * Creates a factory using the given connection.
     * This constructor auto-detects the schema where the {@code "spatial_ref_sys"} table is declared.
     * The connection is {@linkplain Connection#close() closed} when this factory is {@linkplain #dispose(boolean) disposed}.
     *
     * @todo Needs a mechanism for reducing the time the connection is kept open.
     *
     * @param  authority   the authority which is responsible for the maintenance of the primary keys.
     *                     Note that primary keys are not necessarily the same than authority codes.
     *                     The primary keys are stored in the {@value #PRIMARY_KEY} column, while the authority
     *                     codes are defined by the {@value #AUTHORITY_COLUMN} : {@value #CODE_COLUMN} tuples.
     * @param  connection  the connection to the database.
     * @throws SQLException if an error occurred while fetching metadata from the database.
     */
    public PostgisFactory(final Citation authority, final Connection connection) throws SQLException {
        super(authority);
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
     * Returns a description of the underlying backing store.
     */
    public String getBackingStoreDescription() throws FactoryException {
        final Citation   authority = getAuthority();
        final TableAppender  table = new TableAppender(" ");
        final Vocabulary resources = Vocabulary.getResources(null);
        CharSequence cs;
        if ((cs=authority.getEdition()) != null) {
            final String identifier = org.apache.sis.metadata.iso.citation.Citations.getIdentifier(authority);
            table.append(resources.getString(Vocabulary.Keys.VersionOf_1, identifier));
            table.append(':');
            table.nextColumn();
            table.append(cs.toString());
            table.nextLine();
        }
        try {
            String s;
            final DatabaseMetaData metadata = connection.getMetaData();
            if ((s=metadata.getDatabaseProductName()) != null) {
                table.append(resources.getLabel(Vocabulary.Keys.DatabaseEngine));
                table.nextColumn();
                table.append(s);
                if ((s = metadata.getDatabaseProductVersion()) != null) {
                    table.append(' ');
                    table.append(resources.getString(Vocabulary.Keys.Version_1, s));
                }
                table.nextLine();
            }
            if ((s = metadata.getURL()) != null) {
                table.append(resources.getLabel(Vocabulary.Keys.DatabaseUrl));
                table.nextColumn();
                table.append(s);
                table.nextLine();
            }
        } catch (SQLException exception) {
            throw databaseFailure(null, exception);
        }
        return table.toString();
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
     * and values are whether the authority codes match primary keys or not.
     *
     * @return all authority names found in the database.
     * @throws FactoryException if an access to the database failed.
     */
    private Map<String,Boolean> getAuthorityNames() throws FactoryException {
        assert Thread.holdsLock(this);
        if (authorityUsePK == null) try {
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
            authorityUsePK = authorities;
        } catch (SQLException exception) {
            throw databaseFailure(null, exception);
        }
        return authorityUsePK;
    }

    /**
     * Returns the authority codes defined in the database for the given type.
     *
     * @param  category  the type of objects to search for (typically <code>{@linkplain CoordinateReferenceSystem}.class</code>).
     * @return the set of available codes.
     * @throws FactoryException if an error occurred while querying the database.
     */
    @Override
    public synchronized Set<String> getAuthorityCodes(final Class<? extends IdentifiedObject> category)
            throws FactoryException
    {
        try {
            final StringBuilder sql = new StringBuilder("SELECT CASE WHEN ")
                    .append(CODE_COLUMN).append('=').append(PRIMARY_KEY).append(" THEN ")
                    .append(PRIMARY_KEY).append("::text ELSE ").append(AUTHORITY_COLUMN)
                    .append(" || '").append(DefaultNameSpace.DEFAULT_SEPARATOR).append("' || ")
                    .append(CODE_COLUMN).append(" END AS code");
            appendFrom(sql);
            final String[] type = WKTKeywords.forType(category);
            if (type != null) {
                sql.append(" WHERE srtext ILIKE '").append(type[0]).append("%'");
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
        } catch (SQLException exception) {
            throw databaseFailure(null, exception);
        }
    }

    /**
     * Returns the WKT for the given code.
     *
     * @param  identifier  the code of the CRS object to query.
     * @return the Well Known Text (WKT) for the given code, or {@code null} if none.
     * @throws FactoryException if an error occurred while querying the database.
     */
    @Override
    protected String fetchDefinition(final DefaultIdentifier identifier) throws FactoryException {
        final String code = identifier.getCode();
        final int srid = getPrimaryKey(identifier.getCodeSpace(), code);
        try {
            if (select == null) {
                final StringBuilder sql = new StringBuilder("SELECT ").append(WKT_COLUMN);
                appendFrom(sql).append(" WHERE ").append(PRIMARY_KEY).append("=?");
                select = connection.prepareStatement(sql.toString());
            }
            select.setInt(1, srid);
            return (String) singleton(select, false, code);
        } catch (SQLException exception) {
            throw new FactoryException(exception);
        }
    }

    /**
     * Returns the primary key for the specified authority code. If the supplied code contains an
     * <cite>authority</cite> part as in {@code "EPSG:4326"}, then this method searches for a row
     * with the given authority ({@code "EPSG"}) in the {@value #AUTHORITY_COLUMN} column and the
     * given integer code ({@code 4326}) in the {@value #CODE_COLUMN} column. If such row is found,
     * then the value of its {@value #PRIMARY_KEY} column is returned.
     * <p>
     * If the supplied code does not contain an <cite>authority</cite> part (e.g. {@code "4326"}),
     * then this method parses the code as an integer. This is consistent with common practice
     * where the spatial CRS table contains entries from a single authority with primary keys
     * identical to the authority codes. This is also consistent with the codes returned by the
     * {@link #getAuthorityCodes(Class)} method.
     *
     * @param  code  the authority code to convert to primary key value.
     * @return the primary key for the supplied code. There is no guarantee that this key exists
     *         (this method may or may not query the database).
     * @throws NoSuchAuthorityCodeException if a code can't be parsed as an integer or can't
     *         be found in the database.
     * @throws FactoryException if an error occurred while querying the database.
     *
     * @see #getPrimaryKeyAuthority()
     */
    private int getPrimaryKey(final String codespace, final String code)
            throws NoSuchAuthorityCodeException, FactoryException
    {
        int srid;
        try {
            srid = Integer.parseInt(code);
        } catch (NumberFormatException cause) {
            NoSuchAuthorityCodeException e = noSuchAuthorityCode(code);
            e.initCause(cause);
            throw e;
        }
        if (codespace == null || Boolean.TRUE.equals(getAuthorityNames().get(codespace))) {
            return srid;
        }
        final Integer c;
        try {
            if (selectPK == null) {
                final StringBuilder sql = new StringBuilder("SELECT ").append(PRIMARY_KEY);
                appendFrom(sql).append(" WHERE ").append(AUTHORITY_COLUMN).append("=?")
                        .append(" AND ").append(CODE_COLUMN).append("=?");
                selectPK = connection.prepareStatement(sql.toString());
            }
            selectPK.setString(1, codespace);
            selectPK.setInt   (2, srid);
            c = (Integer) singleton(selectPK, true, code);
        } catch (SQLException exception) {
            throw databaseFailure(code, exception);
        }
        if (c == null) {
            throw noSuchAuthorityCode(code);
        }
        return c;
    }

    /**
     * Returns the value in the specified statement.
     * This method ensures that the result set contains only one value.
     *
     * @param  statement  the statement to execute.
     * @param  isInteger  {@code true} for {@link Integer} values, otherwise {@link String} values.
     * @param  code       the authority code, for formatting an error message if needed.
     * @return the singleton value found, or {@code null} if none.
     * @throws SQLException if an error occurred while querying the database.
     */
    private static Object singleton(final PreparedStatement statement, final boolean isInteger, final String code)
            throws SQLException
    {
        Object value = null;
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                final Object candidate;
                if (isInteger) {
                    candidate = results.getInt(1);
                } else {
                    candidate = results.getString(1);
                }
                if (!results.wasNull()) {
                    if (value != null && !value.equals(candidate)) {
                        throw new SQLIntegrityConstraintViolationException(
                                Errors.format(Errors.Keys.DuplicatedValuesForKey_1, code));
                    }
                    value = candidate;
                }
            }
        }
        return value;
    }

    /**
     * Wraps an {@link Exception} into a {@link FactoryException}.
     * The given exception is typically a {@link java.sql.SQLException}.
     *
     * @param  code       the code of the object being created, or {@code null} if unknown.
     * @param  exception  the exception that occurred while querying the backing store.
     * @return A factory exception wrapping the given exception.
     */
    private static FactoryException databaseFailure(final String code, final Throwable exception) {
        String message = exception.getLocalizedMessage();
        if (code != null) {
            final String typeName = "IdentifiedObject";
            message = Errors.format(Errors.Keys.DatabaseFailure_2, typeName, code) + ": " + message;
        }
        return new FactoryException(message, exception);
    }

    /**
     * Creates an exception for an unknown authority code. This convenience method is provided
     * for implementation of {@code createXXX} methods.
     *
     * @param  code  the unknown authority code.
     * @return An exception initialized with an error message built from the specified information.
     */
    private NoSuchAuthorityCodeException noSuchAuthorityCode(final String code) {
        final Class<?> type = IdentifiedObject.class;
        final InternationalString authority = getAuthority().getTitle();
        return new NoSuchAuthorityCodeException(Errors.format(Errors.Keys.NoSuchAuthorityCode_3,
                   code, authority, type), authority.toString(), trimNamespace(code), code);
    }

    /**
     * Closes the JDBC connection used by this factory.
     */
    public synchronized void dispose() {
        try {
            if (select != null) {
                select.close();
                select = null;
            }
            if (selectPK != null) {
                selectPK.close();
                selectPK = null;
            }
            connection.close();
        } catch (SQLException exception) {
            Logging.unexpectedException(null, PostgisFactory.class, "dispose", exception);
        }
    }
}
