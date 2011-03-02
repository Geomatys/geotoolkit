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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.jdbc.FilterToSQL;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.filter.capability.GeometryOperand;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
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
import org.geotoolkit.jdbc.JDBCDataStore;
import org.opengis.filter.expression.Literal;


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
public abstract class AbstractSQLDialect implements SQLDialect{
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
    protected AbstractSQLDialect(final JDBCDataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * sql type to java class mappings
     */
    protected final Map<Integer, Class> sqlTypeToClassMappings = new HashMap<Integer, Class>();
    /**
     * sql type name to java class mappings
     */
    protected final Map<String, Class> sqlTypeNameToClassMappings = new HashMap<String, Class>();
    /**
     * java class to sql type mappings;
     */
    protected final Map<Class, Integer> classToSqlTypeMappings = new HashMap<Class, Integer>();
    /**
     * sql type to sql type name overrides
     */
    protected final Map<Integer, String> sqlTypeToSqlTypeNameOverrides = new HashMap<Integer, String>();



    /**
     * {@inheritDoc }
     */
    @Override
    public final Map<Integer, Class> getSqlTypeToClassMappings() {
        return sqlTypeToClassMappings;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Map<String, Class> getSqlTypeNameToClassMappings() {
        return sqlTypeNameToClassMappings;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Map<Class, Integer> getClassToSqlTypeMappings() {
        return classToSqlTypeMappings;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Map<Integer, String> getSqlTypeToSqlTypeNameOverrides() {
        return sqlTypeToSqlTypeNameOverrides;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Class<?> getMapping(final int sqlType) {
        return getSqlTypeToClassMappings().get(sqlType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Class<?> getMapping(final String sqlTypeName) {
        return getSqlTypeNameToClassMappings().get(sqlTypeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final Integer getMapping(final Class<?> clazz) {
        Integer mapping = getClassToSqlTypeMappings().get(clazz);

        if (mapping == null) {

            //maybe a parent interface might be found
            for(Entry<Class,Integer> entry : classToSqlTypeMappings.entrySet()){
                if(entry.getKey().isAssignableFrom(clazz)){
                    mapping = entry.getValue();
                    //learn it for faster acces next time
                    //@todo not thread safe
                    classToSqlTypeMappings.put(clazz, mapping);
                    break;
                }
            }

            if(mapping == null){
                mapping = Types.OTHER;
                LOGGER.warning("No mapping for " + clazz.getName());
            }
        }
        
        return mapping;
    }



    ////////////////////////////////////////////////////////////////////////////
    // todo MUST CHECK ALL THOSES FOLLOWING ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeValue(final Object value, final Class type, final StringBuilder sql) {

        //turn the value into a literal and use FilterToSQL to encode it
        final Literal literal = dataStore.getFilterFactory().literal(value);
        final FilterToSQL filterToSQL = dataStore.createFilterToSQL(null);

        final StringWriter w = new StringWriter();
        filterToSQL.setWriter(w);
        filterToSQL.visit(literal,type);

        sql.append(w.getBuffer().toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FilterToSQL createFilterToSQL() {
        final FilterToSQL f2s = new FilterToSQL();
        f2s.setCapabilities(BASE_DBMS_CAPABILITIES);
        return f2s;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean includeTable(final String schemaName, final String tableName, final Connection cx)
            throws SQLException {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<?> getMapping(final ResultSet columnMetaData, final Connection cx) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
     * {@inheritDoc }
     */
    @Override
    public void encodeColumnName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeColumnType(final String sqlTypeName, final StringBuilder sql) {
        sql.append(sqlTypeName);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeColumnAlias(final String raw, final StringBuilder sql) {
        sql.append(" as ");
        encodeColumnName(raw, sql);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeTableAlias(final String raw, final StringBuilder sql) {
        sql.append(" as ");
        encodeColumnName(raw, sql);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeTableName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodeSchemaName(final String raw, final StringBuilder sql) {
        sql.append(ne()).append(raw).append(ne());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getGeometryTypeName(final Integer type) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getGeometrySRID(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
     * {@inheritDoc }
     */
    @Override
    public void encodeGeometryColumn(final GeometryDescriptor gatt, final int srid, final StringBuilder sql) {
        encodeColumnName(gatt.getLocalName(), sql);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final String column, final GeometryFactory factory, final Connection cx) throws IOException, SQLException;

    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
        final int column, final GeometryFactory factory, final Connection cx ) throws IOException, SQLException {

        final String columnName = rs.getMetaData().getColumnName( column );
        return decodeGeometryValue(descriptor, rs, columnName, factory, cx);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodePrimaryKey(final Class binding, final String sqlType, final StringBuilder sql) {
        sql.append( " INTEGER PRIMARY KEY" );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodePostColumnCreateTable(final AttributeDescriptor att, final StringBuilder sql) {

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void encodePostCreateTable(final String tableName, final StringBuilder sql) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void postCreateTable(final String schemaName, final SimpleFeatureType featureType,
                                final Connection cx) throws SQLException {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getNextAutoGeneratedValue(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSequenceForColumn(final String schemaName, final String tableName, final String columnName,
            final Connection cx) throws SQLException{
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getNextSequenceValue(final String schemaName, final String sequenceName, final Connection cx)
            throws SQLException {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isLimitOffsetSupported() {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void applyLimitOffset(final StringBuilder sql, final int limit, final int offset) {
        throw new UnsupportedOperationException("Override this method when isLimitOffsetSupported returns true");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Static //////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////


    /**
     * Registers the sql type name to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * This method register the basic type.
     * </p>
     */
    protected static final void initBaseSqlTypeNameToClassMappings(final Map<String, Class> mappings) {
        //TODO: do the normal types
    }

    /**
     * Registers the sql type to java type mappings that the dialect uses when
     * reading and writing objects to and from the database.
     * <p>
     * This method register the basic type.
     * </p>
     */
    protected static final void initBaseSqlTypeToClassMappings(final Map<Integer, Class> mappings) {
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
    }

    /**
     * Registers the java type to sql type mappings that the datastore uses when
     * reading and writing objects to and from the database.
     * * <p>
     * This method register the basic type.
     * </p>
     */
    protected static final void initBaseClassToSqlMappings(final Map<Class, Integer> mappings) {
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
    protected static final void initBaseSqlTypeToSqlTypeNameOverrides(final Map<Integer,String> overrides) {
    }

}
