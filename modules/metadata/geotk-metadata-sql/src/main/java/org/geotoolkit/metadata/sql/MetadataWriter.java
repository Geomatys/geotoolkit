/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.metadata.sql;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.StringTokenizer;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.opengis.util.CodeList;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.KeyNamePolicy;
import org.geotoolkit.metadata.TypeValuePolicy;
import org.geotoolkit.metadata.NullValuePolicy;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.jdbc.DefaultDataSource;
import org.geotoolkit.internal.jdbc.IdentifierGenerator;
import org.geotoolkit.internal.jdbc.StatementEntry;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.resources.Errors;


/**
 * A connection to a metadata database with write capabilities. The database must have a
 * schema of the given name, which can be initially empty. Tables and columns are created
 * as needed when the {@link #add(Object)} method is invoked.
 * <p>
 * No more than one instance of {@code MetadataWriter} should be used for the same database.
 * However multiple instances of {@code MetadataSource} can be used concurrently with a single
 * {@code MetadataWriter} instance on the same database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
public class MetadataWriter extends MetadataSource {
    /**
     * The name of the column for code list.
     */
    private static final String CODE_COLUMN = "CODE";

    /**
     * Maximal length for the identifier. This applies also to code list values.
     */
    private static final int ID_MAX_LENGTH = 24;

    /**
     * Maximal length of values.
     */
    private static final int VALUE_MAX_LENGTH = 240;

    /**
     * Whatever the tables should contain a column for every attribute, or only for non-null
     * and non-empty attributes. The default is {@link NullValuePolicy#NON_EMPTY NON-EMPTY}.
     */
    private NullValuePolicy columnCreationPolicy = NullValuePolicy.NON_EMPTY;

    /**
     * The statements for checking collisions of primary keys.
     */
    private final IdentifierGenerator<String,StatementEntry> idCheck;

    /**
     * Creates a new metadata writer from the given JDBC URL. The URL must be conform to the
     * syntax expected by the {@link java.sql.Driver#connect Driver.connect(...)} method,
     * for example {@code "jdbc:postgresql://localhost/mydatabase"}.
     * <p>
     * This convenience method assumes that the metadata standard to be implemented is
     * {@linkplain MetadataStandard#ISO_19115 ISO 19115}.
     *
     * @param  url The URL to the JDBC database.
     * @param  schema The schema were metadata are expected to be found.
     * @throws SQLException If the connection to the given database can not be etablished.
     */
    public MetadataWriter(final String url, final String schema) throws SQLException {
        this(MetadataStandard.ISO_19115, new DefaultDataSource(url), schema);
    }

    /**
     * Creates a new metadata writer.
     *
     * @param  standard The metadata standard to implement.
     * @param  dataSource The source for getting a connection to the database.
     * @param  schema The schema were metadata are expected to be found.
     * @throws SQLException If the connection to the given database can not be etablished.
     */
    public MetadataWriter(final MetadataStandard standard, final DataSource dataSource, final String schema)
            throws SQLException
    {
        super(standard, dataSource, schema);
        idCheck = new IdentifierGenerator.Simple(statements, ID_COLUMN, buffer);
    }

    /**
     * Whatever the tables should contain a column for every attributes, or only for non-null
     * and non-empty attributes. The default is {@link NullValuePolicy#NON_EMPTY NON-EMPTY}, which
     * implies that new columns are added only when first needed.
     *
     * @return The current policy for column creation.
     */
    public NullValuePolicy getColumnCreationPolicy() {
        synchronized (statements) {
            return columnCreationPolicy;
        }
    }

    /**
     * Sets whatever columns should be created only for non-empty attributes, or for all
     * attributes. If this policy is set to {@link NullValuePolicy#ALL ALL}, then all columns
     * will be added in newly created tables even if their content is empty.
     *
     * @param policy The new policy for column creation.
     */
    public void setColumnCreationPolicy(final NullValuePolicy policy) {
        ensureNonNull("policy", policy);
        synchronized (statements) {
            columnCreationPolicy = policy;
        }
    }

    /**
     * Adds the given metadata object to the database, if it does not already exists.
     * If the database already contains a metadata equals to the given one, then the
     * database is left unchanged and the identifier of the existing metadata is returned.
     *
     * @param  metadata The metadata object to add.
     * @return The identifier (primary key) of the metadata just added, or the identifier
     *         of the existing metadata is one exists.
     * @throws SQLException If an exception occured while reading or writing the database.
     *         In such case, the database content is left unchanged (i.e. this method is a
     *         <cite>all or nothing</cite> operation).
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     */
    public String add(final Object metadata) throws ClassCastException, SQLException {
        String identifier = proxy(metadata);
        if (identifier == null) {
            synchronized (statements) {
                final Connection connection = statements.connection();
                final Statement stmt = connection.createStatement();
                connection.setAutoCommit(false);
                boolean success = false;
                try {
                    if (metadata instanceof CodeList<?>) {
                        identifier = addCode(stmt, (CodeList<?>) metadata);
                    } else {
                        identifier = add(stmt, metadata, new IdentityHashMap<Object,String>(), null);
                    }
                    stmt.close();
                    success = true;
                } finally {
                    if (success) {
                        connection.commit();
                    } else {
                        connection.rollback();
                    }
                    connection.setAutoCommit(true);
                }
            }
        }
        return identifier;
    }

    /**
     * Implementation of the {@link #add(Object)} method. This method invokes itself recursively,
     * and maintains a map of metadata inserted up to date in order to avoid infinite recursivity.
     *
     * @param  stmt     The statement to use for inserting data.
     * @param  metadata The metadata object to add.
     * @param  done     The metadata objects already added.
     * @param  parent   The identifier of the parent, or {@code null} if there is no parent.
     * @return The identifier (primary key) of the metadata just added.
     * @throws SQLException If an exception occured while reading or writing the database.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     */
    private String add(final Statement stmt, final Object metadata, final Map<Object,String> done,
            final String parent) throws ClassCastException, SQLException
    {
        assert Thread.holdsLock(statements);
        /*
         * Take a snapshot of the metadata content. We do that in order to protect ourself
         * against concurrent changes in the metadata object. This protection is needed
         * because we need to perform multiple passes on the same metadata.
         */
        final Map<String,Object> asMap = new LinkedHashMap<String,Object>();
        for (final Map.Entry<String,Object> entry : asMap(metadata).entrySet()) {
            asMap.put(entry.getKey(), extractFromCollection(entry.getValue()));
        }
        /*
         * Search the database for an existing metadata.
         */
        final Class<?> metadataType = metadata.getClass();
        final String table = getTableName(standard.getInterface(metadataType));
        final Set<String> columns = getColumns(table);
        String identifier = search(table, columns, asMap, stmt, buffer);
        if (identifier != null) {
            if (done.put(metadata, identifier) != null) {
                throw new AssertionError(metadata);
            }
            return identifier;
        }
        /*
         * Trim the null values or empty collections. We perform this operation only
         * after the check for existing entries, in order to take in account null values
         * when checking existing entries.
         */
        if (!NullValuePolicy.ALL.equals(columnCreationPolicy)) {
            for (final Iterator<Object> it=asMap.values().iterator(); it.hasNext();) {
                if (it.next() == null) {
                    it.remove();
                }
            }
        }
        /*
         * Process to the table creation if it doesn't already exists, and add missing columns
         * if there is any. If columns are added, we will keep trace of foreigner keys in this
         * process but will not create the constraints now because the foreigner tables may
         * not exist yet.
         */
        if (columns.isEmpty()) {
            stmt.executeUpdate(createTable(table, ID_COLUMN));
            columns.add(ID_COLUMN);
        }
        Map<String,Class<?>> foreigners = null;
        final Map<String,Class<?>> elementTypes = standard.asTypeMap(metadataType,
                TypeValuePolicy.ELEMENT_TYPE, KeyNamePolicy.UML_IDENTIFIER);
        for (final String column : asMap.keySet()) {
            if (!columns.contains(column)) {
                int maxLength = VALUE_MAX_LENGTH;
                Class<?> rt = elementTypes.get(column);
                if (CodeList.class.isAssignableFrom(rt) || standard.isMetadata(rt)) {
                    /*
                     * Found a foreigner key to an other metadata.
                     * Keep reference for creating a constraint later.
                     */
                    maxLength = ID_MAX_LENGTH;
                    if (foreigners == null) {
                        foreigners = new LinkedHashMap<String,Class<?>>();
                    }
                    if (foreigners.put(column, rt) != null) {
                        throw new AssertionError(column); // Should never happen.
                    }
                    rt = null; // For forcing VARCHAR type.
                }
                stmt.executeUpdate(buffer.createColumn(schema, table, column, rt, maxLength));
                columns.add(column);
            }
        }
        /*
         * Get the identifier for the new metadata. If no identifier is proposed, we will try
         * to recycle the identifier of the parent.  For example in ISO 19115, Contact (which
         * contains phone number, etc.) is associated only to ResponsibleParty. So it make
         * sense to use the ResponsibleParty ID for the contact info.
         */
        identifier = suggestIdentifier(metadata);
        if (identifier == null) {
            identifier = parent;
            if (identifier == null) {
                // Arbitrarily pickup the first non-metadata attribute.
                // Fallback on "unknown" if none are found.
                identifier = "unknown";
                for (final Object value : asMap.values()) {
                    if (value != null && !standard.isMetadata(value.getClass())) {
                        identifier = abbreviation(value.toString());
                        break;
                    }
                }
            }
        }
        /*
         * Check for key collision. We will add a suffix if there is one. Note that the
         * final identifier must be found before we put its value in the map, otherwise
         * cyclic references (if any) will use the wrong value.
         *
         * The loop removes at most 4 characters if the identifier is too long. After
         * that point, if the identifier still too long, we will let the database driver
         * produces its own SQLException.
         */
        for (int i=0; i<4; i++) {
            final int maxLength = ID_MAX_LENGTH - i;
            if (identifier.length() > maxLength) {
                identifier = identifier.substring(0, maxLength);
            }
            identifier = idCheck.identifier(schema, table, identifier);
            if (identifier.length() <= ID_MAX_LENGTH) {
                break;
            }
        }
        if (done.put(metadata, identifier) != null) {
            throw new AssertionError(metadata);
        }
        /*
         * Process all dependencies now. This block may invoke this method recursively.
         * Once a dependency has been added to the database, the value in the HashMap is
         * replaced by the identifier of the dependency we just added.
         */
        for (final Map.Entry<String,Object> entry : asMap.entrySet()) {
            Object value = entry.getValue();
            final Class<?> type = value.getClass();
            if (CodeList.class.isAssignableFrom(type)) {
                value = addCode(stmt, (CodeList<?>) value);
            } else if (standard.isMetadata(type)) {
                String dependency = proxy(value);
                if (dependency == null) {
                    dependency = done.get(value);
                    if (dependency == null) {
                        dependency = add(stmt, value, done, identifier);
                        assert done.get(value) == dependency;
                    }
                }
                value = dependency;
            }
            entry.setValue(value);
        }
        /*
         * Now that all dependencies have been inserted in the database,
         * we can setup the foreigner key constraints if there is any.
         */
        if (foreigners != null) {
            for (final Map.Entry<String,Class<?>> fk : foreigners.entrySet()) {
                Class<?> rt = fk.getValue();
                String pk = ID_COLUMN;
                final boolean code = CodeList.class.isAssignableFrom(rt);
                if (code) {
                    pk = CODE_COLUMN;
                } else {
                    rt = standard.getInterface(rt);
                }
                stmt.executeUpdate(buffer.createForeignKey(schema, table, fk.getKey(),
                        getTableName(rt), pk, !code)); // CASCADE if metadata, RESTRICT if CodeList.
            }
        }
        /*
         * Create the SQL statement which will insert the data.
         */
        buffer.clear().append("INSERT INTO ").appendIdentifier(schema, table).append(" (").append(ID_COLUMN);
        for (final String column : asMap.keySet()) {
            buffer.append(", ").appendIdentifier(column);
        }
        buffer.append(") VALUES (").appendValue(identifier);
        for (Object value : asMap.values()) {
            buffer.append(", ").appendValue(value);
        }
        String sql = buffer.append(')').toString();
        if (stmt.executeUpdate(sql) != 1) {
            throw new SQLException(Errors.format(Errors.Keys.DATABASE_UPDATE_FAILURE));
        }
        return identifier;
    }

    /**
     * Returns the SQL statement for creating the given table with the given primary key.
     * This method returns a string of the following form:
     *
     * {@preformat sql
     *     CREATE TABLE "schema"."table" (primaryKey VARCHAR(20) NOT NULL PRIMARY KEY)
     * }
     */
    private String createTable(final String table, final String primaryKey) {
        return buffer.clear().append("CREATE TABLE ").appendIdentifier(schema, table)
                .append(" (").append(primaryKey).append(" VARCHAR(").append(ID_MAX_LENGTH)
                .append(") NOT NULL PRIMARY KEY)").toString();
    }

    /**
     * Adds a code list if it is not already present. This is used only in order to ensure
     * foreigner key constraints in the database. The value if CodeList tables are not used
     * at parsing time.
     */
    private String addCode(final Statement stmt, final CodeList<?> code) throws SQLException {
        assert Thread.holdsLock(statements);
        final String table = getTableName(code.getClass());
        final Set<String> columns = getColumns(table);
        if (columns.isEmpty()) {
            stmt.executeUpdate(createTable(table, CODE_COLUMN));
            columns.add(CODE_COLUMN);
        }
        final String identifier = code.name();
        final ResultSet rs = stmt.executeQuery(buffer.clear().append("SELECT ").append(CODE_COLUMN)
                .append(" FROM ").appendIdentifier(schema, table).append(" WHERE ")
                .append(CODE_COLUMN).appendCondition(identifier).toString());
        final boolean exists = rs.next();
        rs.close();
        if (!exists) {
            final String sql = buffer.clear().append("INSERT INTO ").appendIdentifier(schema, table)
                    .append(" (").append(CODE_COLUMN).append(") VALUES (").appendValue(identifier)
                    .append(')').toString();
            if (stmt.executeUpdate(sql) != 1) {
                throw new SQLException(Errors.format(Errors.Keys.DATABASE_UPDATE_FAILURE));
            }
        }
        return identifier;
    }

    /**
     * Suggests an identifier (primary key) to be used for the given metadata.
     * This method is invoked automatically when a new metadata is about to be
     * inserted in the database. The default implementation uses heuristic rules
     * of a few "well known" metadata like {@link Identifier} and {@link Citation}.
     * Subclasses can override this method for implementing their own heuristic.
     * <p>
     * This method doesn't need to care about key collision. The caller will adds some
     * suffix if this is necessary for differentiation otherwise identical identifiers.
     *
     * @param  metadata The metadata for which to suggests an identifier.
     * @return The proposed identifier, or {@code null} if this method doesn't have any
     *         suggestion.
     * @throws SQLException If an access to some database was desired but failed.
     */
    protected String suggestIdentifier(final Object metadata) throws SQLException {
        if (metadata instanceof Identifier) {
            final Identifier id = (Identifier) metadata;
            final String authority = Citations.getIdentifier(id.getAuthority());
            final String code = id.getCode();
            return (authority != null) ? (authority + DefaultNameSpace.DEFAULT_SEPARATOR + code) : code;
        }
        if (metadata instanceof Citation) {
            return Citations.getIdentifier((Citation) metadata);
        }
        if (metadata instanceof ResponsibleParty){
            final ResponsibleParty rp = (ResponsibleParty) metadata;
            CharSequence name = rp.getIndividualName();
            if (name == null) {
                name = rp.getOrganisationName();
                if (name == null) {
                    name = rp.getPositionName();
                }
            }
            if (name != null) {
                return abbreviation(name.toString());
            }
        }
        return null;
    }

    /**
     * Returns an abreviation of the given identifier, if one is found.
     */
    private static String abbreviation(final String identifier) {
        final StringBuilder buffer = new StringBuilder();
        final StringTokenizer tokens = new StringTokenizer(identifier);
        while (tokens.hasMoreTokens()) {
            buffer.append(tokens.nextToken().charAt(0));
        }
        if (buffer.length() > 2) {
            return buffer.toString();
        }
        return identifier;
    }
}
