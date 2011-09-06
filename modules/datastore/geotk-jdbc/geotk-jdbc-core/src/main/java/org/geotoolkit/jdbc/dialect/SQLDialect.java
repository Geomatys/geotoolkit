/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc.dialect;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.sql.DatabaseMetaData;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotoolkit.data.jdbc.FilterToSQL;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeTypeBuilder;


/**
 * The driver used by JDBCDataStore to directly communicate with the database.
 * <p>
 * This class encapsulates all the database specific operations that JDBCDataStore
 * needs to function. It is implemented on a per-database basis.
 * </p>
 * <p>
 *  <h3>Type Mapping</h3>
 * One of the jobs of a dialect is to map sql types to java types and vice
 * versa. This abstract implementation provides default mappings for "primitive"
 * java types. The following mappings are provided. A '*' denotes that the
 * mapping is the default java to sql mapping as well.
 * <ul>
 *   <li>VARCHAR -> String *
 *   <li>CHAR -> String
 *   <li>LONGVARCHAR -> String
 *   <li>BIT -> Boolean
 *   <li>BOOLEAN -> Boolean *
 *   <li>SMALLINT -> Short *
 *   <li>TINYINT -> Short
 *   <li>INTEGER -> Integer *
 *   <li>BIGINT -> Long *
 *   <li>REAL -> Float *
 *   <li>DOUBLE -> Double *
 *   <li>FLOAT -> Double
 *   <li>NUMERIC -> BigDecimal *
 *   <li>DECIMAL -> BigDecimal
 *   <li>DATE -> java.sql.Date *
 *   <li>TIME -> java.sql.Time *
 *   <li>TIMESTAMP -> java.sql.Timestmap *
 * </ul>
 * Subclasses should <b>extend</b> (not override) the following methods to
 * configure the mappings:
 * <ul>
 *   <li>{@link #registerSqlTypeToClassMappings(Map)}
 *   <li>{@link #registerSqlTypeNameToClassMappings(Map)}
 *   <li>{@link #registerClassToSqlMappings(Map)}
 * </ul>
 * </p>
 * <p>
 *
 * </p>
 * <p>
 * This class is intended to be stateless, therefore subclasses should not
 * maintain any internal state. If for some reason a subclass must keep some
 * state around (not recommended), it must ensure that the state is accessed in
 * a thread safe manner.
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 *
 * @module pending
 */
public interface SQLDialect {

    /**
     * The sql type to java type mappings that the datastore uses when reading
     * and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerSqlTypeToClassMappings(java.util.Map)}
     * </p>
     * @return The mappings, never <code>null</code>.
     */
    Map<Integer, Class> getSqlTypeToClassMappings();

    /**
     * The sql type name to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerSqlTypeNameToClassMappings(Map)}
     * </p>
     *
     * @return The mappings, never <code>null<code>.
     */
    Map<String, Class> getSqlTypeNameToClassMappings();

    /**
     * The java type to sql type mappings that the datastore uses when reading
     * and writing objects to and from the database.
     * <p>
     * These mappings are derived from {@link SQLDialect#registerClassToSqlMappings(Map)}
     * </p>
     * @return The mappings, never <code>null</code>.
     */
    Map<Class, Integer> getClassToSqlTypeMappings();

    /**
     * Returns any overrides which map integer constants for database types (from {@link Types})
     * to database type names.
     * <p>
     * This method will return an empty map when there are no overrides.
     * </p>
     */
    Map<Integer, String> getSqlTypeToSqlTypeNameOverrides();

    /**
     * Returns the java type mapped to the specified sql type.
     * <p>
     * If there is no such type mapped to <tt>sqlType</tt>, <code>null</code>
     * is returned.
     * </p>
     * @param sqlType The integer constant for the sql type from {@link Types}.
     *
     * @return The mapped java class, or <code>null</code>. if no such mapping exists.
     */
    Class<?> getMapping(final int sqlType);

    /**
     * Returns the java type mapped to the specified sql type name.
     * <p>
     * If there is no such type mapped to <tt>sqlTypeName</tt>, <code>null</code>
     * is returned.
     * </p>
     * @param sqlTypeName The name of the sql type.
     *
     * @return The mapped java class, or <code>null</code>. if no such mapping exists.
     */
    Class<?> getMapping(final String sqlTypeName);

    /**
     * Returns the sql type mapped to the specified java type.
     * <p>
     * If there is no such type mapped to <tt>clazz</tt>, <code>Types.OTHER</code>
     * is returned.
     * </p>
     * @param clazz The java class.
     *
     * @return The mapped sql type from {@link Types}, Types.OTHER if no such
     * mapping exists.
     */
    Integer getMapping(final Class<?> clazz);

    /**
     * Determines the class mapping for a particular column of a table.
     * <p>
     * Implementing this method is optional. It is used to allow database to
     * perform custom type mappings based on various column metadata. It is called
     * before the mappings registered in {@link #registerSqlTypeToClassMappings(Map)}
     * and {@link #registerSqlTypeNameToClassMappings(Map)} are used to determine
     * the mapping. Subclasses should implement as needed, this default implementation
     * returns <code>null</code>.
     * </p>
     * <p>
     * The <tt>columnMetaData</tt> argument is provided from
     * {@link DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
     * </p>
     * @param columnMetaData The column metadata
     * @param cx The connection used to retrieve the metadata
     * @return The class mapped to the to column, or <code>null</code>.
     */
    void buildMapping(final AttributeTypeBuilder atb, final Connection cx,
            final String typeName, final int datatype, final String schemaName,
            final String tableName, final String columnName) throws SQLException;


    ////////////////////////////////////////////////////////////////////////////
    // todo MUST CHECK ALL THOSES FOLLOWING ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Determines if the specified table should be included in those published
     * by the datastore.
     * <p>
     * This method returns <code>true</code> if the table should be published as
     * a feature type, otherwise it returns <code>false</code>. Subclasses should
     * override this method, this default implementation returns <code>true<code>.
     * </p>
     * <p>
     * A database connection is provided to the dialect but it should not be closed.
     * However any statements objects or result sets that are instantiated from it
     * must be closed.
     * </p>
     * @param schemaName The schema of the table, might be <code>null</code>..
     * @param tableName The name of the table.
     * @param cx Database connection.
     *
     */
    boolean includeTable(final String schemaName, final String tableName, final Connection cx)
            throws SQLException;

    /**
     * Returns the string used to escape names.
     * <p>
     * This value is used to escape any name in a query. This includes columns,
     * tables, schemas, indexes, etc... If no escape is necessary this method
     * should return the empty string, and never return <code>null</code>.
     * </p>
     * <p>
     * This default implementation returns a single double quote ("), subclasses
     * must override to provide a different espcape.
     * </p>
     */
    String getNameEscape();

    /**
     * Encodes the name of a column in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    void encodeColumnName(final String raw, final StringBuilder sql);

    /**
     * Encodes the type of a column in an SQL CREATE TABLE statement.
     * <p>
     * The default implementation simply outputs the <tt>sqlTypeName</tt> argument
     * as is. Subclasses may override this method. Such cases might include:
     * <ul>
     *   <li>A type definition requires some parameter, ex: size of a varchar
     *   <li>The provided attribute (<tt>att</tt>) contains some additional
     *   restrictions that can be encoded in the type, ex: field length
     * </ul>
     * </p>
     * @param sqlTypeName
     * @param sql
     */
    void encodeColumnType(final String sqlTypeName, final StringBuilder sql);

    /**
     * Encodes the alias of a column in an sql query.
     * <p>
     * This default implementation uses the syntax: <pre>as "alias"</pre>.
     * Subclasses should override to provide a different syntax.
     * </p>
     */
    void encodeColumnAlias(final String raw, final StringBuilder sql);

    /**
     * Encodes the alias of a table in an sql query.
     * <p>
     * This default implementation uses the syntax: <pre>as "alias"</pre>.
     * Subclasses should override to provide a different syntax.
     * </p>
     */
    void encodeTableAlias(final String raw, final StringBuilder sql);

    /**
     * Encodes the name of a table in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    void encodeTableName(final String raw, final StringBuilder sql);

    /**
     * Encodes the name of a schema in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    void encodeSchemaName(final String raw, final StringBuilder sql);

    /**
     * Encodes a value in an sql statement.
     * <p>
     * Subclasses may wish to override or extend this method to handle specific
     * types. This default implementation does the following:
     * <ol>
     *   <li>The <tt>value</tt> is encoded via its {@link #toString()} representation.
     *   <li>If <tt>type</tt> is a character type (extends {@link CharSequence}),
     *   it is wrapped in single quotes (').
     * </ol>
     * </p>
     *
     */
    void encodeValue(final Object value, final Class type, final StringBuilder sql);

    /**
     * Encodes a geometry value in an sql statement.
     * <p>
     * An implementations should serialize <tt>value</tt> into some exchange
     * format which will then be transported to the underlying database. For
     * example, consider an implementation which converts a geometry into its
     * well known text representation:
     * <pre>
     *   <code>
     *   sql.append( "GeomFromText('" );
     *   sql.append( new WKTWriter().write( value ) );
     *   sql.append( ")" );
     *   </code>
     *  </pre>
     * </p>
     * <p>
     *  The <tt>srid</tt> parameter is the spatial reference system identifier
     *  of the geometry, or 0 if not known.
     * </p>
     */
    void encodeGeometryValue(final Geometry value, final int srid, final StringBuilder sql)
        throws IOException;

    /**
     * Creates the filter encoder to be used by the datastore when encoding
     * query predicates.
     * <p>
     * Sublcasses can override this method to return a subclass of {@link FilterToSQL}
     * if need be.
     * </p>
     */
    FilterToSQL createFilterToSQL();

    /**
     * Returns the name of a geometric type based on its integer constant.
     * <p>
     * The constant, <tt>type</tt>, is registered in {@link #registerSqlTypeNameToClassMappings(Map)}.
     * </p>
     * <p>
     * This default implementation returns <code>null</code>, subclasses should
     * override.
     * </p>
     */
    String getGeometryTypeName(final Integer type);

    /**
     * Returns the spatial reference system identifier (srid) for a particular
     * geometry column.
     * <p>
     * This method is given a direct connection to the database. The connection
     * must not be closed. However any statements or result sets instantiated
     * from the connection must be closed.
     * </p>
     * <p>
     * In the event that the srid cannot be determined, this method should return
     * <code>null</code>.
     * </p>
     * @param schemaName The database schema, could be <code>null</code>.
     * @param tableName The table, never <code>null</code>.
     * @param columnName The column name, never <code>null</code>
     * @param cx The database connection.
     */
    Integer getGeometrySRID(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException;

    /**
     * Turns the specified srid into a {@link CoordinateReferenceSystem}, or returns <code>null</code> if not possible.
     * <p>
     * The implementation might just use <code>CRS.decode("EPSG:" + srid)</code>, but most spatial databases will have
     * their own SRS database that can be queried as well.</p>
     * <p>As a rule of thumb you should override this method if your spatial database uses codes that are
     * not part of the EPSG standard database, of if for some reason you deem it preferable to use
     * your database definition instead of an official EPSG one.</p>
     * <p>Most overrides will try out to decode the official EPSG code first, and fall back on
     * the custom database definition otherwise</p>
     * @param srid
     * @return CoordinateReferenceSystem
     */
    CoordinateReferenceSystem createCRS(final int srid, final Connection cx) throws SQLException;

    /**
     * Encodes the spatial extent function of a geometry column in a SELECT statement.
     * <p>
     * This method must also be sure to properly encode the name of the column
     * with the {@link #encodeColumnName(String, StringBuilder)} function.
     * </p>
     * @param tableName
     */
    void encodeGeometryEnvelope(final String tableName, final String geometryColumn, final StringBuilder sql);

    /**
     * Decodes the result of a spatial extent function in a SELECT statement.
     * <p>
     * This method is given direct access to a result set. The <tt>column</tt>
     * parameter is the index into the result set which contains the spatial
     * extent value. The query for this value is build with the {@link #encodeGeometryEnvelope(String, String, StringBuilder)}
     * method.
     * </p>
     * <p>
     * This method must not read any other objects from the result set other then
     * the one referenced by <tt>column</tt>.
     * </p>
     * @param rs A result set
     * @param column Index into the result set which points at the spatial extent value.
     * @param cx The database connection.
     */
    Envelope decodeGeometryEnvelope(final ResultSet rs, final int column, final Connection cx)
        throws SQLException, IOException;

    /**
     * Encodes the name of a geometry column in a SELECT statement.
     * <p>
     * This method should wrap the column name in any functions that are used to
     * retrieve its value. For instance, often it is necessary to use the function
     * <code>asText</code>, or <code>asWKB</code> when fetching a geometry.
     * </p>
     * <p>
     * This method must also be sure to properly encode the name of the column
     * with the {@link #encodeColumnName(String, StringBuilder)} function.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     *   <code>
     *   sql.append( "asText(" );
     *   column( gatt.getLocalName(), sql );
     *   sql.append( ")" );
     *   </code>
     * </pre>
     * </p>
     * <p>
     * This default implementation simply uses the column name without any
     * wrapping function, subclasses must override.
     * </p>
     */
    void encodeGeometryColumn(final GeometryDescriptor gatt, final int srid, final StringBuilder sql,final Hints hints);

    /**
     * Decodes a geometry value from the result of a query.
     * <p>
     * This method is given direct access to a result set. The <tt>column</tt>
     * parameter is the index into the result set which contains the geometric
     * value.
     * </p>
     * <p>
     * An implementation should deserialize the value provided by the result
     * set into {@link Geometry} object. For example, consider an implementation
     * which deserializes from well known text:
     * <code>
     *   <pre>
     *   String wkt = rs.getString( column );
     *   if ( wkt == null ) {
     *     return null;
     *   }
     *   return new WKTReader(factory).read( wkt );
     *   </pre>
     * </code>
     * Note that implementations must handle <code>null</code> values.
     * </p>
     * <p>
     * The <tt>factory</tt> parameter should be used to instantiate any geometry
     * objects.
     * </p>
     */
    Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final String column, final GeometryFactory factory, final Connection cx) throws IOException, SQLException;

    /**
     * Decodes a geometry value from the result of a query specifying the column
     * as an index.
     * <p>
     * @see #decodeGeometryValue(org.opengis.feature.type.GeometryDescriptor, java.sql.ResultSet,
     * java.lang.String, com.vividsolutions.jts.geom.GeometryFactory, java.sql.Connection).
     * for a more in depth description.
     * </p>
     * @see #decodeGeometryValue(org.opengis.feature.type.GeometryDescriptor, java.sql.ResultSet,
     * java.lang.String, com.vividsolutions.jts.geom.GeometryFactory, java.sql.Connection).
     */
    Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final int column, final GeometryFactory factory, final Connection cx ) throws IOException, SQLException;

    /**
     * Encodes the primary key definition in a CREATE TABLE statement.
     */
    void encodePrimaryKey(Class binding, String sqlType, final StringBuilder sql);

    /**
     * Encodes anything post a column in a CREATE TABLE statement.
     * <p>
     * This is appended after the column name and type. Subclasses may choose to override
     * this method, the default implementation does nothing.
     * </p>
     * @param att The attribute corresponding to the column.
     */
    void encodePostColumnCreateTable(final AttributeDescriptor att, final StringBuilder sql);

    /**
     * Encodes anything post a CREATE TABLE statement.
     * <p>
     * This is appended to a CREATE TABLE statement after the column definitions.
     * This default implementation does nothing, subclasses should override as
     * need be.
     * </p>
     */
    void encodePostCreateTable(final String tableName, final StringBuilder sql);

    /**
     * Callback to execute any additional sql statements post a create table
     * statement.
     * <p>
     * This method should be implemented by subclasses that need to do some post
     * processing on the database after a table has been created. Examples might
     * include:
     * <ul>
     *   <li>Creating a sequence for a primary key
     *   <li>Registering geometry column metadata
     *   <li>Creating a spatial index
     * </ul>
     * </p>
     * <p>
     * A common case is creating an auto incrementing sequence for the primary
     * key of a table. It should be noted that all tables created through the
     * datastore use the column "fid" as the primary key.
     * </p>
     * <p>
     * A direct connection to the database is provided (<tt>cx</tt>). This
     * connection must not be closed, however any statements or result sets
     * instantiated from the connection must be closed.
     * </p>
     * @param schemaName The name of the schema, may be <code>null</code>.
     * @param featureType The feature type that has just been created on the database.
     * @param cx Database connection.
     *
     */
    void postCreateTable(final String schemaName, final SimpleFeatureType featureType,
                                final Connection cx) throws SQLException;

    /**
     * Obtains the next value of the primary key of a column.
     * <p>
     * Implementations should determine the next value of a column for which
     * values are automatically generated by the database.
     * </p>
     * <p>
     * This method is given a direct connection to the database, but this connection
     * should never be closed. However any statements or result sets instantiated
     * from the connection must be closed.
     * </p>
     * <p>
     * Implementations should handle the case where <tt>schemaName</tt> is <code>null</code>.
     * </p>
     * @param schemaName The schema name, this might be <code>null</code>.
     * @param tableName The name of the table.
     * @param columnName The column.
     * @param cx The database connection.
     *
     * @return The next value of the column, or <code>null</code>.
     */
    Object getNextAutoGeneratedValue(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException;

    /**
     * Determines the name of the sequence (if any) which is used to increment
     * generate values for a table column.
     * <p>
     * This method should return null if no such sequence exists.
     * </p>
     * <p>
     * This method is given a direct connection to the database, but this connection
     * should never be closed. However any statements or result sets instantiated
     * from the connection must be closed.
     * </p>
     * @param schemaName The schema name, this might be <code>null</code>.
     * @param tableName The table name.
     * @param columnName The column name.
     * @param cx The database connection.
     *
     */
    String getSequenceForColumn(final String schemaName, final String tableName, final String columnName,
            final Connection cx) throws SQLException;

    /**
     * Obtains the next value of a sequence, incrementing the sequence to the next state in the
     * process.
     * <p>
     * Implementations should determine the next value of a column for which
     * values are automatically generated by the database.
     * </p>
     * <p>
     * This method is given a direct connection to the database, but this connection
     * should never be closed. However any statements or result sets instantiated
     * from the connection must be closed.
     * </p>
     * <p>
     * Implementations should handle the case where <tt>schemaName</tt> is <code>null</code>.
     * </p>
     * @param schemaName The schema name, this might be <code>null</code>.
     * @param sequenceName The name of the sequence.
     * @param cx The database connection.
     *
     * @return The next value of the sequence, or <code>null</code>.
     */
    Object getNextSequenceValue(final String schemaName, final String sequenceName, final Connection cx)
            throws SQLException;

    /**
     * Returns true if this dialect can encode both {@linkplain Query#getStartIndex()}
     * and {@linkplain Query#getMaxFeatures()} into native SQL.
     * @return boolean
     */
    boolean isLimitOffsetSupported();

    /**
     * Alters the query provided so that limit and offset are natively dealt with. This might mean
     * simply appending some extra directive to the query, or wrapping it into a bigger one.
     * @param sql
     * @param limit
     * @param offset
     */
    void applyLimitOffset(final StringBuilder sql, final int limit, final int offset);
    
}
