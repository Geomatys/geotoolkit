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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.DatabaseMetaData;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.filter.capability.GeometryOperand;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.filter.capability.DefaultArithmeticOperators;
import org.geotoolkit.filter.capability.DefaultComparisonOperators;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.DefaultFunctionName;
import org.geotoolkit.filter.capability.DefaultFunctions;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.DefaultOperator;
import org.geotoolkit.filter.capability.DefaultScalarCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialOperator;
import org.geotoolkit.filter.capability.DefaultSpatialOperators;


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
public abstract class SQLDialect {
    protected static final Logger LOGGER = Logging.getLogger(SQLDialect.class);

    /**
     * The basic filter capabilities all databases should have
     */
    public static final DefaultFilterCapabilities BASE_DBMS_CAPABILITIES;
    static {
        final DefaultIdCapabilities idCaps = new DefaultIdCapabilities(true, true);

        final DefaultOperator[] ops = new DefaultOperator[]{
            new DefaultOperator("and"),
            new DefaultOperator("or"),
            new DefaultOperator("not")
        };
        final DefaultComparisonOperators compOps = new DefaultComparisonOperators(ops);
        final String obj = "obj";
        final DefaultFunctionName[] functionNames = new DefaultFunctionName[] {
            new DefaultFunctionName("equals", Collections.singletonList(obj), 0),
            new DefaultFunctionName("greaterThan", Collections.singletonList(obj), 0),
            new DefaultFunctionName("greaterThanEqual", Collections.singletonList(obj), 0),
            new DefaultFunctionName("lessThan", Collections.singletonList(obj), 0),
            new DefaultFunctionName("lessThanEqual", Collections.singletonList(obj), 0),
            new DefaultFunctionName("notEquals", Collections.singletonList(obj), 0)
        };
        final DefaultFunctions functions = new DefaultFunctions(functionNames);
        final DefaultArithmeticOperators arithmOps = new DefaultArithmeticOperators(true, functions);
        final DefaultScalarCapabilities scalCaps = new DefaultScalarCapabilities(true, compOps, arithmOps);

        final DefaultSpatialOperator[] spatialOp = new DefaultSpatialOperator[] {
            new DefaultSpatialOperator("include", new GeometryOperand[] {
                GeometryOperand.Envelope, GeometryOperand.Polygon, GeometryOperand.Point
            })
        };
        final DefaultSpatialOperators spatialOps = new DefaultSpatialOperators(spatialOp);
        final GeometryOperand[] geomOperands = new GeometryOperand[] {
            GeometryOperand.Envelope, GeometryOperand.Polygon, GeometryOperand.Point
        };
        final DefaultSpatialCapabilities spatialCaps = new DefaultSpatialCapabilities(geomOperands, spatialOps);

        BASE_DBMS_CAPABILITIES = new DefaultFilterCapabilities(null, idCaps, spatialCaps, scalCaps);
    }

    /**
     * The datastore using the dialect
     */
    protected final JDBCDataStore dataStore;

    /**
     * Creates the dialect.
     * @param dataStore The dataStore using the dialect.
     */
    protected SQLDialect(final JDBCDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Initializes a newly created database connection.
     * <p>
     * Subclasses should override this method if there is some additional action
     * that needs to be taken when a new connection to the database is created. The
     * default implementation does nothing.
     * </p>
     * @param cx The new database connection.
     */
    public void initializeConnection(final Connection cx) throws SQLException {

    }
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
    public boolean includeTable(final String schemaName, final String tableName, final Connection cx)
            throws SQLException
    {
        return true;
    }

    /**
     * Registers the sql type name to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * Subclasses should extend (not override) this method to provide additional
     * mappings, or to override mappings provided by this implementation. This
     * implementation provides the following mappings:
     * </p>
     */
    public void registerSqlTypeNameToClassMappings(final Map<String, Class<?>> mappings) {
        //TODO: do the normal types
    }

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
    public Class<?> getMapping(final ResultSet columnMetaData, final Connection cx) throws SQLException {
        return null;
    }

    /**
     * Registers the sql type to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * Subclasses should extend (not override) this method to provide additional
     * mappings, or to override mappings provided by this implementation. This
     * implementation provides the following mappings:
     * </p>
     *
     */
    public void registerSqlTypeToClassMappings(final Map<Integer, Class<?>> mappings) {
        mappings.put(Types.VARCHAR, String.class);
        mappings.put(Types.CHAR, String.class);
        mappings.put(Types.LONGVARCHAR, String.class);

        mappings.put(Types.BIT, Boolean.class);
        mappings.put(Types.BOOLEAN, Boolean.class);

        mappings.put(Types.TINYINT, Short.class);
        mappings.put(Types.SMALLINT, Short.class);

        mappings.put(Types.INTEGER, Integer.class);
        mappings.put(Types.BIGINT, Long.class);

        mappings.put(Types.REAL, Float.class);
        mappings.put(Types.FLOAT, Double.class);
        mappings.put(Types.DOUBLE, Double.class);

        mappings.put(Types.DECIMAL, BigDecimal.class);
        mappings.put(Types.NUMERIC, BigDecimal.class);

        mappings.put(Types.DATE, Date.class);
        mappings.put(Types.TIME, Time.class);
        mappings.put(Types.TIMESTAMP, Timestamp.class);

        //subclasses should extend to provide additional
    }

    /**
     * Registers the java type to sql type mappings that the datastore uses when
     * reading and writing objects to and from the database.
     * * <p>
     * Subclasses should extend (not override) this method to provide additional
     * mappings, or to override mappings provided by this implementation. This
     * implementation provides the following mappings:
     * </p>
     */
    public void registerClassToSqlMappings(final Map<Class<?>, Integer> mappings) {
        mappings.put(String.class, Types.VARCHAR);

        mappings.put(Boolean.class, Types.BOOLEAN);

        mappings.put(Short.class, Types.SMALLINT);

        mappings.put(Integer.class, Types.INTEGER);
        mappings.put(Long.class, Types.BIGINT);

        mappings.put(Float.class, Types.REAL);
        mappings.put(Double.class, Types.DOUBLE);

        mappings.put(BigDecimal.class, Types.NUMERIC);

        mappings.put(Date.class, Types.DATE);
        mappings.put(Time.class, Types.TIME);
        mappings.put(java.util.Date.class, Types.TIMESTAMP);
        mappings.put(Timestamp.class, Types.TIMESTAMP);

        //subclasses should extend and provide additional
    }

    /**
     * Registers any overrides that should occur when mapping an integer sql type
     * value to an underlying sql type name.
     * <p>
     * The default implementation of this method does nothing. Subclasses should override
     * in cases where:
     * <ul>
     * <li>database type metadata does not provide enough information to properly map
     * <li>to support custom types (those not in {@link Types})
     * </ul>
     * </p>
     */
    public void registerSqlTypeToSqlTypeNameOverrides(final Map<Integer,String> overrides) {
    }

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
    public String getNameEscape() {
        return "\"";
    }

    /**
     * Quick accessor for {@link #getNameEscape()}.
     */
    protected final String ne() {
        return getNameEscape();
    }

    /**
     * Encodes the name of a column in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    public void encodeColumnName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

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
    public void encodeColumnType(final String sqlTypeName, final StringBuilder sql) {
        sql.append(sqlTypeName);
    }

    /**
     * Encodes the alias of a column in an sql query.
     * <p>
     * This default implementation uses the syntax: <pre>as "alias"</pre>.
     * Subclasses should override to provide a different syntax.
     * </p>
     */
    public void encodeColumnAlias(final String raw, final StringBuilder sql) {
        sql.append(" as ");
        encodeColumnName(raw, sql);
    }

    /**
     * Encodes the alias of a table in an sql query.
     * <p>
     * This default implementation uses the syntax: <pre>as "alias"</pre>.
     * Subclasses should override to provide a different syntax.
     * </p>
     */
    public void encodeTableAlias(final String raw, final StringBuilder sql) {
        sql.append(" as ");
        encodeColumnName(raw, sql);
    }

    /**
     * Encodes the name of a table in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    public void encodeTableName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

    /**
     * Encodes the name of a schema in an SQL statement.
     * <p>
     * This method wraps <tt>raw</tt> in the character provided by
     * {@link #getNameEscape()}. Subclasses usually dont override this method
     * and instead override {@link #getNameEscape()}.
     * </p>
     */
    public void encodeSchemaName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

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
    public String getGeometryTypeName(final Integer type) {
        return null;
    }

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
    public Integer getGeometrySRID(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException
    {
        return null;
    }

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
    public CoordinateReferenceSystem createCRS(final int srid, final Connection cx) throws SQLException {
        try {
            return CRS.decode("EPSG:" + srid);
        } catch(Exception e) {
            if(LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Could not decode " + srid + " using the built-in EPSG database", e);
            }
            return null;
        }
    }

    /**
     * Encodes the spatial extent function of a geometry column in a SELECT statement.
     * <p>
     * This method must also be sure to properly encode the name of the column
     * with the {@link #encodeColumnName(String, StringBuilder)} function.
     * </p>
     * @param tableName
     */
    public abstract void encodeGeometryEnvelope(final String tableName, final String geometryColumn, final StringBuilder sql);

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
    public abstract Envelope decodeGeometryEnvelope(final ResultSet rs, final int column, final Connection cx)
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
    public void encodeGeometryColumn(final GeometryDescriptor gatt, final int srid, final StringBuilder sql) {
        encodeColumnName(gatt.getLocalName(), sql);
    }

    /**
     * Encodes a generalized geometry using a DB provided SQL function if available
     * If not supported, subclasses should not implement
     * Only called if {@link HintsPending#GEOMETRY_GENERALIZATION is supported}
     *
     * Example:
     * </p>
     * <pre>
     *   <code>
     *   sql.append( "asText(generalize(" );
     *   column( gatt.getLocalName(), sql );
     *   sql.append( "," );
	 *   sql.append(distance);
     *   sql.append( "))" );
     *   </code>
     * </pre>
     * </p>
     * <p>
     *
     */

    public void encodeGeometryColumnGeneralized(final GeometryDescriptor gatt, final int srid,
                                                final StringBuilder sql, final Double distance)
    {
        throw new UnsupportedOperationException("Geometry generalization not supported");
    }


    /**
     *
     * Encodes a simplified geometry using a DB provided SQL function if available
     * If not supported, subclasses should not implement
     * Only called if {@link HintsPending#GEOMETRY_SIMPLIFICATION is supported}
     * @see #encodeGeometryColumnGeneralized(org.opengis.feature.type.GeometryDescriptor, int, java.lang.StringBuilder, java.lang.Double)
     */
    public void encodeGeometryColumnSimplified(final GeometryDescriptor gatt, final int srid,
                                               final StringBuilder sql, final Double distance)
    {
        throw new UnsupportedOperationException("Geometry simplification not supported");
    }


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
    public abstract Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
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
    public Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final int column, final GeometryFactory factory, final Connection cx ) throws IOException, SQLException {

        final String columnName = rs.getMetaData().getColumnName( column );
        return decodeGeometryValue(descriptor, rs, columnName, factory, cx);
    }

    /**
     * Encodes the primary key definition in a CREATE TABLE statement.
     * <p>
     * Subclasses should override this method if need be, the default implementation does the
     * following:
     * <pre>
     *   <code>
     *   encodeColumnName( column, sql );
     *   sql.append( " int PRIMARY KEY" );
     *   </code>
     * </pre>
     * </p>
     *
     */
    public void encodePrimaryKey(final String column, final StringBuilder sql) {
        encodeColumnName( column, sql );
        sql.append( " INTEGER PRIMARY KEY" );
    }

    /**
     * Encodes anything post a column in a CREATE TABLE statement.
     * <p>
     * This is appended after the column name and type. Subclasses may choose to override
     * this method, the default implementation does nothing.
     * </p>
     * @param att The attribute corresponding to the column.
     */
    public void encodePostColumnCreateTable(final AttributeDescriptor att, final StringBuilder sql) {

    }

    /**
     * Encodes anything post a CREATE TABLE statement.
     * <p>
     * This is appended to a CREATE TABLE statement after the column definitions.
     * This default implementation does nothing, subclasses should override as
     * need be.
     * </p>
     */
    public void encodePostCreateTable(final String tableName, final StringBuilder sql) {
    }

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
    public void postCreateTable(final String schemaName, final SimpleFeatureType featureType,
                                final Connection cx) throws SQLException
    {
    }

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
    public Object getNextAutoGeneratedValue(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException
    {
        return null;
    }

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
    public String getSequenceForColumn(final String schemaName, final String tableName, final String columnName,
            final Connection cx) throws SQLException
    {
        return null;
    }

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
    public Object getNextSequenceValue(final String schemaName, final String sequenceName, final Connection cx)
            throws SQLException
    {
        return null;
    }

    /**
     * Returns true if this dialect can encode both {@linkplain Query#getStartIndex()}
     * and {@linkplain Query#getMaxFeatures()} into native SQL.
     * @return boolean
     */
    public boolean isLimitOffsetSupported() {
        return false;
    }

    /**
     * Alters the query provided so that limit and offset are natively dealt with. This might mean
     * simply appending some extra directive to the query, or wrapping it into a bigger one.
     * @param sql
     * @param limit
     * @param offset
     */
    public void applyLimitOffset(final StringBuilder sql, final int limit, final int offset) {
        throw new UnsupportedOperationException("Override this method when isLimitOffsetSupported returns true");
    }
    /**
     * Add hints to the JDBC Feature Source. A subclass
     * can override
     *
     * possible hints (but not limited to)
     *
     * {@link HintsPending#GEOMETRY_GENERALIZATION}
     * {@link HintsPending#GEOMETRY_SIMPLIFICATION}
     *
     * @param hints
     */
    protected void addSupportedHints(final Set<Hints.Key> hints) {
    }

}
