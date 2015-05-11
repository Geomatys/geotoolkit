/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;

import org.opengis.annotation.UML;
import org.opengis.util.CodeList;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.sql.SQLBuilder;
import org.geotoolkit.internal.sql.StatementPool;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.StatementEntry;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.util.collection.WeakValueHashMap;
import org.apache.sis.util.Classes;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A connection to a metadata database in read-only mode. The database must have a schema
 * of the given name ({@code "metadata"} in the example below). Existing entries can be
 * obtained as in the example below:
 *
 * {@preformat java
 *   DataSource     source     = ... // This is database-specific.
 *   MetadataSource source     = new MetadataSource(MetadataStandard.ISO_19115, source, "metadata");
 *   Telephone      telephone  = source.get(Telephone.class, id);
 * }
 *
 * where {@code id} is the primary key value for the desired record in the {@code CI_Telephone} table.
 *
 * {@section Concurrency}
 * {@code MetadataSource} is thread-safe but is not concurrent, because JDBC connections can not
 * be assumed concurrent. If concurrency is desired, multiple instances of {@code MetadataSource}
 * can be created for the same {@link DataSource}. The {@link #MetadataSource(MetadataSource)}
 * convenience constructor can be used for this purpose.
 *
 * @author Touraïvane (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.1)
 * @module
 */
public class MetadataSource implements AutoCloseable {
    /**
     * The column name used for the identifiers. We do not quote this
     * identifier; we will let the database uses its own convention.
     */
    static final String ID_COLUMN = "ID";

    /**
     * The metadata standard to be stored in the database.
     */
    protected final MetadataStandard standard;

    /**
     * The catalog, set to {@code null} for now. This is defined as a constant in order to
     * make easier to spot the place where catalog would be used, if we want to use it in
     * a future version.
     */
    static final String CATALOG = null;

    /**
     * The schema where metadata are stored, or {@code null} if none.
     */
    final String schema;

    /**
     * The tables which have been queried or created up to date. Keys are table names
     * and values are the columns defined for that table.
     */
    private final Map<String, Set<String>> tables;

    /**
     * The prepared statements created is previous call to {@link #getValue}.
     * Those statements are encapsulated into {@link MetadataResult} objects.
     * The key-value pairs must be one of the following:
     * <p>
     * <ul>
     *   <li>{@link Class}  key with {@link MetadataResult} value</li>
     *   <li>{@link String} key with {@link StatementEntry} value</li>
     * </ul>
     * <p>
     * This object is also the lock on which every SQL query must be garded.
     * We use this object because SQL queries will typically involve usage of this map.
     */
    final StatementPool<Object,StatementEntry> statements;

    /**
     * The previously created objects. Used in order to share existing instances
     * for the same interface and primary key.
     */
    private final WeakValueHashMap<CacheKey,Object> cache;

    /**
     * A buffer used for constructing SQL statements. This buffer is keep for the
     * duration of this {@code MetadataSource} because it contains database metadata,
     * so by keeping this helper object we avoid fetching those metadata every time.
     */
    final SQLBuilder buffer;

    /**
     * The last converter used.
     */
    private transient volatile ObjectConverter<?,?> lastConverter;

    /**
     * The class loader to use for proxy creation.
     */
    private final ClassLoader loader;

    /**
     * Creates a new metadata source from the given JDBC URL. The URL must be conform to the
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
    public MetadataSource(final String url, final String schema) throws SQLException {
        this(MetadataStandard.ISO_19115, new DefaultDataSource(url), schema);
    }

    /**
     * Creates a new metadata source.
     *
     * @param  standard The metadata standard to implement.
     * @param  dataSource The source for getting a connection to the database.
     * @param  schema The schema were metadata are expected to be found, or {@code null} if none.
     * @throws SQLException If the connection to the given database can not be established.
     */
    public MetadataSource(final MetadataStandard standard, final DataSource dataSource, final String schema)
            throws SQLException
    {
        ensureNonNull("standard",   standard);
        ensureNonNull("dataSource", dataSource);
        this.standard = standard;
        this.schema = schema;
        statements  = new StatementPool<>(10, dataSource);
        tables      = new HashMap<>();
        cache       = new WeakValueHashMap<>(CacheKey.class);
        loader      = getClass().getClassLoader();
        synchronized (statements) {
            buffer = new SQLBuilder(statements.connection().getMetaData());
        }
    }

    /**
     * Creates a new metadata source with the same configuration than the given source.
     * The two sources will share the same data source but will use their own
     * {@linkplain Connection connection}.
     *
     * @param source The source from which to copy the configuration.
     */
    public MetadataSource(final MetadataSource source) {
        ensureNonNull("source", source);
        standard   = source.standard;
        schema     = source.schema;
        loader     = source.loader;
        buffer     = new SQLBuilder(source.buffer);
        tables     = new HashMap<>();
        cache      = new WeakValueHashMap<>(CacheKey.class);
        statements = new StatementPool<>(source.statements);
    }

    /**
     * If the given value is a collection, returns the first element in that collection
     * or {@code null} if empty.
     *
     * @param  value The value to inspect (can be {@code null}).
     * @return The given value, or its first element if the value is a collection,
     *         or {@code null} if the given value is null or an empty collection.
     */
    static Object extractFromCollection(Object value) {
        while (value instanceof Iterable<?>) {
            final Iterator<?> it = ((Iterable<?>) value).iterator();
            if (!it.hasNext()) {
                return null;
            }
            value = it.next();
        }
        return value;
    }

    /**
     * Returns the table name for the specified class.
     * This is usually the ISO 19115 name.
     */
    static String getTableName(final Class<?> type) {
        final UML annotation = type.getAnnotation(UML.class);
        if (annotation == null) {
            return type.getSimpleName();
        }
        final String name = annotation.identifier();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Returns the column name for the specified method.
     */
    private static String getColumnName(final Method method) {
        final UML annotation = method.getAnnotation(UML.class);
        if (annotation == null) {
            return method.getName();
        }
        final String name = annotation.identifier();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Returns a view of the given metadata as a map. This method returns always a map
     * using UML identifier and containing all entries including the null ones because
     * the {@code MetadataSource} implementation assumes so.
     *
     * @param  metadata The metadata object to view as a map.
     * @return A map view over the metadata object.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     */
    final Map<String,Object> asMap(final Object metadata) throws ClassCastException {
        return standard.asValueMap(metadata, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.ALL);
    }

    /**
     * If the given metadata is a proxy generated by this {@code MetadataSource}, returns the
     * identifier of that proxy. Such metadata don't need to be inserted again in the database.
     *
     * @param  metadata The metadata to test.
     * @return The identifier (primary key), or {@code null} if the given metadata is not a proxy.
     */
    final String proxy(final Object metadata) {
        return (metadata instanceof MetadataProxy) ? ((MetadataProxy) metadata).identifier(this) : null;
    }

    /**
     * Searches for the given metadata in the database. If such metadata is found, then its
     * identifier (primary key) is returned. Otherwise this method returns {@code null}.
     *
     * @param  metadata The metadata to search for.
     * @return The identifier of the given metadata, or {@code null} if none.
     * @throws SQLException If an error occurred while searching in the database.
     * @throws ClassCastException if the metadata object doesn't implement a metadata
     *         interface of the expected package.
     */
    public String search(final Object metadata) throws ClassCastException, SQLException {
        ensureNonNull("metadata", metadata);
        String identifier = proxy(metadata);
        if (identifier == null) {
            /*
             * Code lists don't need to be stored in the database. Some code list tables may
             * be present in the database in order to ensure foreigner key constraints, but
             * those tables are not used in anyway by the org.geotoolkit.metadata.sql package.
             */
            if (metadata instanceof CodeList<?>) {
                identifier = ((CodeList<?>) metadata).name();
            } else {
                final String table = getTableName(standard.getInterface(metadata.getClass()));
                final Map<String,Object> asMap = asMap(metadata);
                synchronized (statements) {
                    try (Statement stmt = statements.connection().createStatement()) {
                        identifier = search(table, null, asMap, stmt, buffer);
                    }
                }
            }
        }
        return identifier;
    }

    /**
     * Searches for the given metadata in the database. If such metadata is found, then its
     * identifier (primary key) is returned. Otherwise this method returns {@code null}.
     *
     * @param  table The table where to search.
     * @param  columns The table columns as given by {@link #getExistingColumns}, or {@code null}.
     * @param  metadata A map view of the metadata to search for.
     * @param  stmt The statement to use for executing the query.
     * @param  buffer An initially buffer for creating the SQL query.
     * @return The identifier of the given metadata, or {@code null} if none.
     * @throws SQLException If an error occurred while searching in the database.
     */
    final String search(final String table, Set<String> columns, final Map<String,Object> metadata,
            final Statement stmt, final SQLBuilder buffer) throws SQLException
    {
        assert Thread.holdsLock(statements);
        buffer.clear();
        for (final Map.Entry<String,Object> entry : metadata.entrySet()) {
            /*
             * Gets the value and the column where this value is stored. If the value
             * is non-null, then the column must exist otherwise the metadata will be
             * considered as not found.
             */
            Object value = extractFromCollection(entry.getValue());
            final String column = entry.getKey();
            if (columns == null) {
                columns = getExistingColumns(table);
            }
            if (!columns.contains(column)) {
                if (value != null) {
                    return null;  // The column was mandatory for the searched metadata.
                } else {
                    continue;     // Do not include a non-existent column in the SQL query.
                }
            }
            /*
             * Tests if the value is an other metadata, in which case we will invoke this
             * method recursively. Note that if a metadata dependency is not found, we can
             * stop the whole process immediately.
             */
            if (value instanceof CodeList<?>) {
                value = ((CodeList<?>) value).name();
            } else if (value != null) {
                String dependency = proxy(value);
                if (dependency != null) {
                    value = dependency;
                } else {
                    final Class<?> type = value.getClass();
                    if (standard.isMetadata(type)) {
                        dependency = search(getTableName(standard.getInterface(type)),
                                null, asMap(value), stmt, new SQLBuilder(buffer));
                        if (dependency == null) {
                            return null;  // Dependency not found.
                        }
                        value = dependency;
                    }
                }
            }
            /*
             * Builds the SQL statement with the resolved value.
             */
            if (buffer.isEmpty()) {
                buffer.append("SELECT ").append(ID_COLUMN).append(" FROM ")
                        .appendIdentifier(schema, table).append(" WHERE ");
            } else {
                buffer.append(" AND ");
            }
            buffer.appendIdentifier(column).appendCondition(value);
        }
        /*
         * The SQL statement is ready, with metadata dependency (if any) resolved. We can now
         * execute it. If more than one record is found, the identifier of the first one will
         * be retained but a warning will be logged.
         */
        String identifier = null;
        try (ResultSet rs = stmt.executeQuery(buffer.toString())) {
            while (rs.next()) {
                final String candidate = rs.getString(1);
                if (candidate != null) {
                    if (identifier == null) {
                        identifier = candidate;
                    } else if (!identifier.equals(candidate)) {
                        Logging.log(MetadataSource.class, "search", Errors.getResources(null).getLogRecord(
                                Level.WARNING, Errors.Keys.DUPLICATED_VALUES_FOR_KEY_1, candidate));
                    }
                }
            }
        }
        return identifier;
    }

    /**
     * Returns the set of all columns in a table, or an empty set if none (never {@code null}).
     * Because each table should have at least the {@value #ID_COLUMN} column, an empty set of
     * columns will be understood as meaning that the table doesn't exist.
     * <p>
     * This method returns a direct reference to the cached set. The returned set shall be
     * modified in-place if new columns are added in the database table.
     *
     * @param  table The name of the table for which to get the columns.
     * @return The set of columns, or an empty set if the table has not yet been created.
     * @throws SQLException If an error occurred while querying the database.
     */
    final Set<String> getExistingColumns(final String table) throws SQLException {
        assert Thread.holdsLock(statements);
        Set<String> columns = tables.get(table);
        if (columns == null) {
            columns = new HashSet<>();
            /*
             * Note: a null schema in the DatabaseMetadata. getExistingColumns(...) call means "do not
             * take schema in account" - it does not mean "no schema" (the later is specified
             * by an empty string). This match better what we want because if we do not specify
             * a schema in a SELECT statement, then the actual schema used depends on the search
             * path set in the database environment variables.
             */
            try (ResultSet rs = statements.connection().getMetaData().getColumns(CATALOG, schema, table, null)) {
                while (rs.next()) {
                    if (!columns.add(rs.getString("COLUMN_NAME"))) {
                        // Paranoiac check, but should never happen.
                        throw new SQLNonTransientException(table);
                    }
                }
            }
            tables.put(table, columns);
        }
        return columns;
    }

    /**
     * Returns an implementation of the specified metadata interface filled
     * with the data referenced by the specified identifier. Alternatively,
     * this method can also returns a {@link CodeList} element.
     *
     * @param  <T> The parameterized type of the {@code type} argument.
     * @param  type The interface to implement (e.g. {@link org.opengis.metadata.citation.Citation}),
     *         or the {@link CodeList}.
     * @param  identifier The identifier used in order to locate the record for the metadata entity
     *         to be created. This is usually the primary key of the record to search for.
     * @return An implementation of the required interface, or the code list element.
     * @throws SQLException if a SQL query failed.
     */
    public <T> T getEntry(final Class<T> type, final String identifier) throws SQLException {
        ensureNonNull("type", type);
        ensureNonNull("identifier", identifier);
        /*
         * IMPLEMENTATION NOTE: This method must not invoke any method which may access
         * 'statements'. It is not allowed to acquire the lock on 'statements' neither.
         */
        Object value;
        if (CodeList.class.isAssignableFrom(type)) {
            value = getCodeList(type, identifier);
        } else {
            final CacheKey key = new CacheKey(type, identifier);
            synchronized (cache) {
                value = cache.get(key);
                if (value == null) {
                    value = Proxy.newProxyInstance(loader, new Class<?>[] {type, MetadataProxy.class},
                            new MetadataHandler(identifier, this));
                    cache.put(key, value);
                }
            }
        }
        return type.cast(value);
    }

    /**
     * Returns the code of the given type and name. This method is defined for avoiding
     * the warning message when the actual class is unknown (it must have been checked
     * dynamically by the caller however).
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static CodeList<?> getCodeList(final Class<?> type, final String name) {
        return Types.forCodeName((Class) type, name, true);
    }

    /**
     * Returns an attribute from a table.
     *
     * @param  type       The interface class. This is mapped to the table name in the database.
     * @param  method     The method invoked. This is mapped to the column name in the database.
     * @param  identifier The primary key of the record to search for.
     * @return The value of the requested attribute.
     * @throws SQLException if the SQL query failed.
     */
    final Object getValue(final Class<?> type, final Method method, final String identifier) throws SQLException {
        final Class<?> valueType    = method.getReturnType();
        final boolean  isCollection = Collection.class.isAssignableFrom(valueType);
        final Class<?> elementType  = isCollection ? Classes.boundOfParameterizedProperty(method) : valueType;
        final boolean  isMetadata   = standard.isMetadata(elementType);
        final String   tableName    = getTableName(type);
        final String   columnName   = getColumnName(method);
        final boolean  isArray;
        Object value;
        synchronized (statements) {
            if (getExistingColumns(tableName).contains(columnName)) {
                /*
                 * Prepares the statement and executes the SQL query in this synchronized block.
                 * Note that the usage of 'result' must stay inside this synchronized block
                 * because we can not assume that JDBC connections are thread-safe.
                 */
                MetadataResult result = (MetadataResult) statements.remove(type);
                if (result == null) {
                    final String query = buffer.clear().append("SELECT * FROM ")
                            .appendIdentifier(schema, tableName).append(" WHERE ")
                            .append(ID_COLUMN).append("=?").toString();
                    result = new MetadataResult(type, statements.connection().prepareStatement(query));
                }
                value = result.getObject(identifier, columnName);
                isArray = (value instanceof java.sql.Array);
                if (isArray) {
                    final java.sql.Array array = (java.sql.Array) value;
                    value = array.getArray();
                    array.free();
                }
                if (statements.put(type, result) != null) {
                    throw new AssertionError(type);
                }
            } else {
                // Column does not exists.
                value = null;
                isArray = false;
            }
        }
        /*
         * If the value is an array and the return type is anything except an array of
         * primitive type, ensure that the value is converted in an array of type Object[].
         * In this process, resolve foreigner keys.
         */
        if (isArray && (isCollection || !elementType.isPrimitive())) {
            final Object[] values = new Object[Array.getLength(value)];
            for (int i=0; i<values.length; i++) {
                Object element = Array.get(value, i);
                if (element != null) {
                    if (isMetadata) {
                        element = getEntry(elementType, element.toString());
                    } else try {
                        element = convert(elementType, element);
                    } catch (UnconvertibleObjectException e) {
                        throw new MetadataException(Errors.format(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2,
                                columnName + '[' + i + ']', value), e);
                    }
                }
                values[i] = element;
            }
            value = values;
            if (isCollection) {
                Collection<Object> collection = Arrays.asList(values);
                if (SortedSet.class.isAssignableFrom(valueType)) {
                    collection = new TreeSet<>(collection);
                } else if (Set.class.isAssignableFrom(valueType)) {
                    collection = new LinkedHashSet<>(collection);
                }
                value = collection;
            }
        }
        /*
         * Now converts the value to its final type, including conversion of null
         * value to empty collections if the return value should be a collection.
         */
        if (value == null) {
            if (isCollection) {
                if (Set.class.isAssignableFrom(valueType)) {
                    return Collections.EMPTY_SET;
                }
                return Collections.EMPTY_LIST;
            }
        } else {
            if (isMetadata) {
                value = getEntry(elementType, value.toString());
            } else try {
                value = convert(elementType, value);
            } catch (UnconvertibleObjectException e) {
                throw new MetadataException(Errors.format(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, columnName, value), e);
            }
            if (isCollection) {
                if (Set.class.isAssignableFrom(valueType)) {
                    return Collections.singleton(value);
                }
                return Collections.singletonList(value);
            }
        }
        return value;
    }

    /**
     * Converts the specified non-metadata value into an object of the expected type.
     * The expected value is an instance of a class outside the metadata package, for
     * example {@link String}, {@link InternationalString}, {@link URI}, <i>etc.</i>
     *
     * @throws UnconvertibleObjectException If the value can not be converter.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private Object convert(final Class<?> targetType, Object value) throws UnconvertibleObjectException {
        final Class<?> sourceType = value.getClass();
        if (!targetType.isAssignableFrom(sourceType)) {
            ObjectConverter converter = lastConverter;
            if (converter == null || !converter.getSourceClass().equals(sourceType) ||
                    !targetType.equals(converter.getTargetClass()))
            {
                lastConverter = converter = ObjectConverters.find(sourceType, targetType);
            }
            value = converter.apply(value);
        }
        return value;
    }

    /**
     * Closes the database connection used by this object.
     *
     * @throws SQLException If an error occurred while closing the connection.
     */
    @Override
    public void close() throws SQLException {
        statements.close();
    }
}
