/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.sql;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.CodeList;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;

import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.internal.util.Citations;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.IdentifierGenerator;
import org.geotoolkit.internal.sql.StatementEntry;
import org.geotoolkit.naming.DefaultNameSpace;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Workaround;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;
import static org.apache.sis.metadata.KeyNamePolicy.UML_IDENTIFIER;
import static org.apache.sis.metadata.TypeValuePolicy.ELEMENT_TYPE;
import static org.apache.sis.metadata.TypeValuePolicy.DECLARING_INTERFACE;


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
 * @version 3.20
 *
 * @since 3.03
 * @module
 */
@ThreadSafe
public class MetadataWriter extends MetadataSource {
    /**
     * {@code true} if child tables inherit the index of their parent tables.
     * This feature is not yet supported in PostgreSQL.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-30">GEOTK-30</a>
     */
    @Workaround(library="PostgreSQL", version="9.1")
    private static final boolean INDEX_INHERITANCE_SUPPORTED = false;

    /**
     * The name of the column for code list.
     */
    private static final String CODE_COLUMN = "CODE";

    /**
     * Maximal length for the identifier. This applies also to code list values.
     */
    private int maximumIdentifierLength = 24;

    /**
     * Maximal length of values.
     */
    private int maximumValueLength = 1000;

    /**
     * Whatever the tables should contain a column for every attribute, or only for non-null
     * and non-empty attributes. The default is {@link ValueExistencePolicy#NON_EMPTY NON-EMPTY}.
     */
    private ValueExistencePolicy columnCreationPolicy = ValueExistencePolicy.NON_EMPTY;

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
     * @throws SQLException If the connection to the given database can not be established.
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
     * @throws SQLException If the connection to the given database can not be established.
     */
    public MetadataWriter(final MetadataStandard standard, final DataSource dataSource, final String schema)
            throws SQLException
    {
        super(standard, dataSource, schema);
        idCheck = new IdentifierGenerator.Simple(statements, ID_COLUMN, buffer);
    }

    /**
     * Whatever the tables should contain a column for every attributes, or only for non-null
     * and non-empty attributes. The default is {@link ValueExistencePolicy#NON_EMPTY NON-EMPTY}, which
     * implies that new columns are added only when first needed.
     *
     * @return The current policy for column creation.
     */
    public ValueExistencePolicy getColumnCreationPolicy() {
        synchronized (statements) {
            return columnCreationPolicy;
        }
    }

    /**
     * Sets whatever columns should be created only for non-empty attributes, or for all
     * attributes. If this policy is set to {@link ValueExistencePolicy#ALL ALL}, then all columns
     * will be added in newly created tables even if their content is empty.
     *
     * @param policy The new policy for column creation.
     */
    public void setColumnCreationPolicy(final ValueExistencePolicy policy) {
        ensureNonNull("policy", policy);
        synchronized (statements) {
            columnCreationPolicy = policy;
        }
    }

    /**
     * Returns the maximal number of characters allowed in text columns.
     * This is the parameter given to the {@code VARCHAR} type when creating new columns.
     * <p>
     * Attempts to insert a text longer than this limit will typically throws
     * a {@link SQLException}, but the exact behavior is database-dependent.
     *
     * @return The maximal number of characters allowed in text columns.
     *
     * @since 3.20
     */
    public int getMaximumValueLength() {
        synchronized (statements) {
            return maximumValueLength;
        }
    }

    /**
     * Sets the maximal number of characters allowed in text columns. Invoking this method
     * will affect only the new columns to be created, if any. This method has no effect on
     * the table columns that already exist in the database.
     *
     * @param length The maximal number of characters allowed in text columns.
     *
     * @since 3.20
     */
    public void setMaximumValueLength(final int length) {
        ensureStrictlyPositive("length", length);
        synchronized (statements) {
            maximumValueLength = length;
        }
    }

    /**
     * Returns the maximal number of characters allowed for primary keys.
     * This is the value given to the {@code VARCHAR} type when creating
     * new {@code "ID"} columns.
     * <p>
     * Primary keys are automatically generated by {@code MetadataWriter}.
     * Values longer than this maximal length are truncated, so there is
     * no need for a very high limit.
     *
     * @return The maximal number of characters allowed for primary keys.
     *
     * @since 3.20
     *
     * @see #suggestIdentifier(Object)
     */
    public int getMaximumIdentifierLength() {
        synchronized (statements) {
            return maximumIdentifierLength;
        }
    }

    /**
     * Sets the maximal number of characters allowed for primary keys. Invoking this method
     * will affect only the new columns to be created, if any. This method has no effect on
     * the table columns that already exist in the database.
     * <p>
     * Primary keys should be relatively short, for example no more than 20 characters. Those
     * keys are not visible to the end user, but may be used by the database administrator.
     *
     * @param length The maximal number of characters allowed for primary keys.
     *
     * @since 3.20
     *
     * @see #suggestIdentifier(Object)
     */
    public void setMaximumIdentifierLength(final int length) {
        ensureStrictlyPositive("length", length);
        synchronized (statements) {
            maximumIdentifierLength = length;
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
     * @throws SQLException If an exception occurred while reading or writing the database.
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
                connection.setAutoCommit(false);
                boolean success = false;
                try {
                    try (Statement stmt = connection.createStatement()) {
                        if (metadata instanceof CodeList<?>) {
                            identifier = addCode(stmt, (CodeList<?>) metadata);
                        } else {
                            identifier = add(stmt, metadata, new IdentityHashMap<Object,String>(), null);
                        }
                    }
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
     * @param  done     The metadata objects already added, mapped to their primary keys.
     * @param  parent   The primary key of the parent, or {@code null} if there is no parent.
     * @return The identifier (primary key) of the metadata just added.
     * @throws SQLException If an exception occurred while reading or writing the database.
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
        final Map<String,Object> asMap = new LinkedHashMap<>();
        for (final Map.Entry<String,Object> entry : asMap(metadata).entrySet()) {
            asMap.put(entry.getKey(), extractFromCollection(entry.getValue()));
        }
        /*
         * Search the database for an existing metadata.
         */
        final Class<?> implementationType = metadata.getClass();
        final Class<?> interfaceType = standard.getInterface(implementationType);
        final String table = getTableName(interfaceType);
        final Set<String> columns = getExistingColumns(table);
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
        if (columnCreationPolicy != ValueExistencePolicy.ALL) {
            for (final Iterator<Object> it=asMap.values().iterator(); it.hasNext();) {
                if (it.next() == null) {
                    it.remove();
                }
            }
        }
        /*
         * Process to the table creation if it doesn't already exists. If the table has parents,
         * they will be created first. The later will work only for database supporting table
         * inheritance, like PostgreSQL. For other kind of database engine, we can not store
         * metadata having parent interfaces.
         */
        createTable(stmt, interfaceType, table, columns);
        /*
         * Add missing columns if there is any. If columns are added, we will keep trace of
         * foreigner keys in this process but will not create the constraints now because the
         * foreigner tables may not exist yet. They will be created later by recursive calls
         * to this method a little bit below.
         */
        Map<String,Class<?>> colTypes = null, colTables = null;
        Map<String,FKey> foreigners = null;
        for (final String column : asMap.keySet()) {
            if (!columns.contains(column)) {
                if (colTypes == null) {
                    colTypes  = standard.asTypeMap(implementationType, UML_IDENTIFIER, ELEMENT_TYPE);
                    colTables = standard.asTypeMap(implementationType, UML_IDENTIFIER, DECLARING_INTERFACE);
                }
                /*
                 * We have found a column to add. Check if the column actually needs to be added
                 * to the parent table (if such parent exists). In most case, the answer is "no"
                 * and 'addTo' is equals to 'table'.
                 */
                String addTo = table;
                final Class<?> declaring = colTables.get(column);
                if (!interfaceType.isAssignableFrom(declaring)) {
                    addTo = getTableName(declaring);
                }
                /*
                 * Determine the column data type.
                 */
                int maxLength = maximumValueLength;
                Class<?> rt = colTypes.get(column);
                if (CodeList.class.isAssignableFrom(rt) || standard.isMetadata(rt)) {
                    /*
                     * Found a reference to an other metadata. Remind that
                     * column for creating a foreign key constraint later.
                     */
                    maxLength = maximumIdentifierLength;
                    if (foreigners == null) {
                        foreigners = new LinkedHashMap<>();
                    }
                    if (foreigners.put(column, new FKey(addTo, rt, null)) != null) {
                        throw new AssertionError(column); // Should never happen.
                    }
                    rt = null; // For forcing VARCHAR type.
                }
                stmt.executeUpdate(buffer.createColumn(schema, addTo, column, rt, maxLength));
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
         * First, we trim the identifier (primary key) to the maximal length. Then, the loop
         * removes at most 4 additional characters if the identifier is still too long. After
         * that point, if the identifier still too long, we will let the database driver
         * produces its own SQLException.
         */
        for (int i=0; i<4; i++) {
            final int maxLength = maximumIdentifierLength - i;
            if (identifier.length() > maxLength) {
                identifier = identifier.substring(0, maxLength);
            }
            identifier = idCheck.identifier(schema, table, identifier);
            if (identifier.length() <= maximumIdentifierLength) {
                break;
            }
        }
        if (done.put(metadata, identifier) != null) {
            throw new AssertionError(metadata);
        }
        /*
         * Process all dependencies now. This block may invoke this method recursively.
         * Once a dependency has been added to the database, the corresponding value in
         * the 'asMap' HashMap is replaced by the identifier of the dependency we just added.
         */
        Map<String,FKey> referencedTables = null;
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
                        assert done.get(value) == dependency; // NOSONAR: really identity comparison.
                        if (!INDEX_INHERITANCE_SUPPORTED) {
                            /*
                             * In a classical object-oriented model, the foreigner key constraints
                             * declared in the parent table would take in account the records in
                             * the child table and we would have nothing special to do. However
                             * PostgreSQL 9.1 does not yet inherit index. So if we detect that a
                             * column references some ecords in two different table, then we must
                             * suppress the foreigner key constraint.
                             */
                            final String column = entry.getKey();
                            final Class<?> targetType = standard.getInterface(value.getClass());
                            FKey fkey = null;
                            if (foreigners != null) {
                                fkey = foreigners.get(column);
                                if (fkey != null && !targetType.isAssignableFrom(fkey.tableType)) {
                                    // The foreigner key constraint does not yet exist, so we can
                                    // change the target table. Set the target to the child table.
                                    fkey.tableType = targetType;
                                }
                            }
                            if (fkey == null) {
                                // The foreigner key constraint may already exist. Get a list of all
                                // foreigner keys for the current table, then verify if the existing
                                // constraint references the right table.
                                if (referencedTables == null) {
                                    referencedTables = new HashMap<>();
                                    try (ResultSet rs = stmt.getConnection().getMetaData().getImportedKeys(CATALOG, schema, table)) {
                                        while (rs.next()) {
                                            if ((schema  == null || schema .equals(rs.getString("PKTABLE_SCHEM"))) &&
                                                (CATALOG == null || CATALOG.equals(rs.getString("PKTABLE_CAT"))))
                                            {
                                                referencedTables.put(rs.getString("FKCOLUMN_NAME"),
                                                            new FKey(rs.getString("PKTABLE_NAME"), null,
                                                                     rs.getString("FK_NAME")));
                                            }
                                        }
                                    }
                                }
                                fkey = referencedTables.remove(column);
                                if (fkey != null && !fkey.tableName.equals(getTableName(targetType))) {
                                    // The existing foreigner key constraint doesn't reference
                                    // the right table. We have no other choice than removing it...
                                    buffer.clear().append("ALTER TABLE ").appendIdentifier(schema, table)
                                            .append(" DROP CONSTRAINT ").appendIdentifier(fkey.keyName);
                                    stmt.executeUpdate(buffer.toString());
                                    final LogRecord record = Errors.getResources(null).getLogRecord(
                                            Level.WARNING, Errors.Keys.DROPPED_FOREIGNER_KEY_1,
                                            table + '.' + column + " ⇒ " + fkey.tableName + '.' + ID_COLUMN);
                                    record.setSourceMethodName("add");
                                    record.setSourceClassName(MetadataWriter.class.getName());
                                    Logging.getLogger(MetadataWriter.class).log(record);
                                }
                            }
                        }
                    }
                }
                value = dependency;
            }
            entry.setValue(value);
        }
        /*
         * Now that all dependencies have been inserted in the database, we can setup the
         * foreigner key constraints if there is any. Note that we deferred the foreigner
         * key creations not because of the missing rows, but because of missing tables
         * (since new tables may be created in the process of inserting dependencies).
         */
        if (foreigners != null) {
            for (final Map.Entry<String,FKey> entry : foreigners.entrySet()) {
                final FKey fkey = entry.getValue();
                Class<?> rt = fkey.tableType;
                String primaryKey = ID_COLUMN;
                final boolean isCodeList = CodeList.class.isAssignableFrom(rt);
                if (isCodeList) {
                    primaryKey = CODE_COLUMN;
                } else {
                    rt = standard.getInterface(rt);
                }
                final String column = entry.getKey();
                final String target = getTableName(rt);
                stmt.executeUpdate(buffer.createForeignKey(
                        schema, fkey.tableName, column, // Source (schema.table.column)
                        target, primaryKey,             // Target (table.column)
                        !isCodeList));                  // CASCADE if metadata, RESTRICT if CodeList.
                /*
                 * In a classical object-oriented model, the constraint would be inherited
                 * by child tables. However this is not yet supported as of PostgreSQL 9.1.
                 * If inheritance is not supported, then we have to repeat the constraint
                 * creation in child tables.
                 */
                if (!INDEX_INHERITANCE_SUPPORTED && !table.equals(fkey.tableName)) {
                    stmt.executeUpdate(buffer.createForeignKey(schema, table, column,
                            target, primaryKey, !isCodeList));
                }
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
     * Information about the source and the target of a foreigner key. This class stores only
     * the table names (indirectly in the case of {@link #tableType}, since the name is derived
     * from the type). The column name are known by other way: either as the map key in the
     * case of the source, or fixed to {@value MetadataWriter#ID_COLUMN} in the case of the
     * target.
     */
    private static final class FKey {
        final String tableName; // May be source or target, depending on the context.
            Class<?> tableType; // Always the target table.
        final String keyName;

        FKey(final String tableName, final Class<?> tableType, final String keyName) {
            this.tableName = tableName;
            this.tableType = tableType;
            this.keyName   = keyName;
        }
    }

    /**
     * Creates a table for the given type, if the table does not already exists. This method
     * may call itself recursively for creating parent tables, if they do not exist neither.
     *
     * @param  stmt    The statement to use for creating tables.
     * @param  type    The interface class.
     * @param  table   The name of the table (should be consistent with the type).
     * @param  columns The existing columns, as an empty set if the table does not exist yet.
     * @throws SQLException If an error occurred while creating the table.
     */
    private void createTable(final Statement stmt, final Class<?> type, final String table,
            final Set<String> columns) throws SQLException
    {
        if (columns.isEmpty()) {
            StringBuilder inherits = null;
            for (final Class<?> candidate : type.getInterfaces()) {
                if (standard.isMetadata(candidate)) {
                    final String parent = getTableName(candidate);
                    createTable(stmt, candidate, parent, getExistingColumns(parent));
                    if (inherits == null) {
                        buffer.clear().append("CREATE TABLE ").appendIdentifier(schema, table);
                        if (!INDEX_INHERITANCE_SUPPORTED) {
                            /*
                             * In a classical object-oriented model, the new child table would inherit
                             * the index from its parent table. However this not yet the case as of
                             * PostgreSQL 9.1. If the index is not inherited, then we have to repeat
                             * the primary key creation in every child tables.
                             */
                            buffer.append("(CONSTRAINT ").appendIdentifier(table + "_pkey")
                                  .append(" PRIMARY KEY (").append(ID_COLUMN).append(")) ");
                        }
                        inherits = new StringBuilder(buffer.append(" INHERITS (").toString());
                    } else {
                        inherits.append(", ");
                    }
                    inherits.append(buffer.clear().appendIdentifier(schema, parent));
                }
            }
            final String sql;
            if (inherits != null) {
                sql = inherits.append(')').toString();
            } else {
                sql = createTable(table, ID_COLUMN);
            }
            stmt.executeUpdate(sql);
            columns.add(ID_COLUMN);
        }
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
                .append(" (").append(primaryKey).append(" VARCHAR(").append(maximumIdentifierLength)
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
        final Set<String> columns = getExistingColumns(table);
        if (columns.isEmpty()) {
            stmt.executeUpdate(createTable(table, CODE_COLUMN));
            columns.add(CODE_COLUMN);
        }
        final String identifier = code.name();
        final String query = buffer.clear().append("SELECT ").append(CODE_COLUMN)
                .append(" FROM ").appendIdentifier(schema, table).append(" WHERE ")
                .append(CODE_COLUMN).appendCondition(identifier).toString();
        final boolean exists;
        try (ResultSet rs = stmt.executeQuery(query)) {
            exists = rs.next();
        }
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
     * suffix if this is necessary for differentiating otherwise identical identifiers.
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
     * Returns an abbreviation of the given identifier, if one is found.
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
