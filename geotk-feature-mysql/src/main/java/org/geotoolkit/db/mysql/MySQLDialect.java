/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.db.mysql;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Version;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.JDBCFeatureStoreUtilities;
import org.geotoolkit.db.dialect.AbstractSQLDialect;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.capability.ArithmeticOperators;
import org.geotoolkit.filter.capability.ComparisonOperators;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.Functions;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.Operator;
import org.geotoolkit.filter.capability.DefaultScalarCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.SpatialOperator;
import org.geotoolkit.filter.capability.SpatialOperators;
import org.geotoolkit.filter.capability.DefaultTemporalCapabilities;
import org.geotoolkit.filter.capability.TemporalOperators;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.geotoolkit.filter.capability.FunctionName;
import org.geotoolkit.filter.capability.TemporalOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class MySQLDialect extends AbstractSQLDialect {

    private static final Map<Integer,Class> TYPE_TO_CLASS = new HashMap<>();
    private static final Map<String,Class> TYPENAME_TO_CLASS = new HashMap<>();
    private static final Map<Class,String> CLASS_TO_TYPENAME = new HashMap<>();
    private static final Map<String, String> TYPE_TO_ST_TYPE_MAP = new HashMap<>();
    private static final Set<String> IGNORE_TABLES = new HashSet<>();

    private static final DefaultFilterCapabilities FILTER_CAPABILITIES;
    private static final String TABLE_ESCAPE = "`";

    static {

        //fill base types
        TYPE_TO_CLASS.put(Types.VARCHAR,        String.class);
        TYPE_TO_CLASS.put(Types.CHAR,           String.class);
        TYPE_TO_CLASS.put(Types.LONGVARCHAR,    String.class);
        TYPE_TO_CLASS.put(Types.NVARCHAR,       String.class);
        TYPE_TO_CLASS.put(Types.NCHAR,          String.class);
        TYPE_TO_CLASS.put(Types.BIT,            Boolean.class);
        TYPE_TO_CLASS.put(Types.BOOLEAN,        Boolean.class);
        TYPE_TO_CLASS.put(Types.TINYINT,        Short.class);
        TYPE_TO_CLASS.put(Types.SMALLINT,       Short.class);
        TYPE_TO_CLASS.put(Types.INTEGER,        Integer.class);
        TYPE_TO_CLASS.put(Types.BIGINT,         Long.class);
        TYPE_TO_CLASS.put(Types.REAL,           Float.class);
        TYPE_TO_CLASS.put(Types.FLOAT,          Double.class);
        TYPE_TO_CLASS.put(Types.DOUBLE,         Double.class);
        TYPE_TO_CLASS.put(Types.DECIMAL,        BigDecimal.class);
        TYPE_TO_CLASS.put(Types.NUMERIC,        BigDecimal.class);
        TYPE_TO_CLASS.put(Types.DATE,           Date.class);
        TYPE_TO_CLASS.put(Types.TIME,           Time.class);
        TYPE_TO_CLASS.put(Types.TIMESTAMP,      Timestamp.class);
        TYPE_TO_CLASS.put(Types.BLOB,           byte[].class);
        TYPE_TO_CLASS.put(Types.BINARY,         byte[].class);
        TYPE_TO_CLASS.put(Types.CLOB,           String.class);
        TYPE_TO_CLASS.put(Types.VARBINARY,      byte[].class);
        //TYPE_TO_CLASS.put(Types.ARRAY,          Array.class);//mysql do not support array type

        //BINARY,BLOB,LONGBLOB,MEDIUMBLOB,TINYBLOB,VARBINARY(10)
        //DATE,DATETIME,TIME,TIMESTAMP,YEAR
        //CURVE,GEOMETRY,LINE,LINEARRING,LINESTRING,POINT,POLYGON,SURFACE
        //GEOMETRYCOLLECTION,MULTICURVE,MULTILINESTRING,MULTIPOINT,MULTIPOLYGON,MULTISURFACE
        //BIGINT,DECIMAL,DOUBLE,FLOAT,INT,MEDIUMINT,SMALLINT,TINYINT
        //CHAR,VARCHAR(100),LONGTEXT,MEDIUMTEXT,TEXT,TINYTEXT
        //BIT
        //ENUM('1','2')
        //SET('1','2')
        TYPENAME_TO_CLASS.put("binary",         byte[].class);
        TYPENAME_TO_CLASS.put("blob",           byte[].class);
        TYPENAME_TO_CLASS.put("longblob",       byte[].class);
        TYPENAME_TO_CLASS.put("mediumblob",     byte[].class);
        TYPENAME_TO_CLASS.put("tinyblob",       byte[].class);
        TYPENAME_TO_CLASS.put("varbinary",  byte[].class);
        TYPENAME_TO_CLASS.put("date",           Date.class);
        TYPENAME_TO_CLASS.put("datetime",       Date.class);
        TYPENAME_TO_CLASS.put("time",           Date.class);
        TYPENAME_TO_CLASS.put("timestamp",      Timestamp.class);
        TYPENAME_TO_CLASS.put("year",           Date.class);
        TYPENAME_TO_CLASS.put("curve",              Geometry.class);
        TYPENAME_TO_CLASS.put("geometry",           Geometry.class);
        TYPENAME_TO_CLASS.put("geometrycollecion", GeometryCollection.class);
        TYPENAME_TO_CLASS.put("line",               LineString.class);
        TYPENAME_TO_CLASS.put("linearring",         LineString.class);
        TYPENAME_TO_CLASS.put("linestring",         LineString.class);
        TYPENAME_TO_CLASS.put("multicurve",         GeometryCollection.class);
        TYPENAME_TO_CLASS.put("multilinestring",    MultiLineString.class);
        TYPENAME_TO_CLASS.put("multipoint",         MultiPoint.class);
        TYPENAME_TO_CLASS.put("multipolygon",       MultiPolygon.class);
        TYPENAME_TO_CLASS.put("multisurface",       GeometryCollection.class);
        TYPENAME_TO_CLASS.put("point",              Point.class);
        TYPENAME_TO_CLASS.put("polygon",            Polygon.class);
        TYPENAME_TO_CLASS.put("surface",            Geometry.class);
        TYPENAME_TO_CLASS.put("bigint",     BigInteger.class);
        TYPENAME_TO_CLASS.put("decimal",    BigDecimal.class);
        TYPENAME_TO_CLASS.put("double",     Double.class);
        TYPENAME_TO_CLASS.put("float",      Float.class);
        TYPENAME_TO_CLASS.put("int",        Integer.class);
        TYPENAME_TO_CLASS.put("mediumint",  Integer.class);
        TYPENAME_TO_CLASS.put("smallint",   Short.class);
        TYPENAME_TO_CLASS.put("tinyint",    Short.class);
        TYPENAME_TO_CLASS.put("char",           Character.class);
        TYPENAME_TO_CLASS.put("varchar",   String.class);
        TYPENAME_TO_CLASS.put("longtext",       String.class);
        TYPENAME_TO_CLASS.put("mediumtext",     String.class);
        TYPENAME_TO_CLASS.put("text",           String.class);
        TYPENAME_TO_CLASS.put("tinytext",       String.class);
        TYPENAME_TO_CLASS.put("bit",        Boolean.class);
        TYPENAME_TO_CLASS.put("enum",   String[].class);
        TYPENAME_TO_CLASS.put("set",    String[].class);

        CLASS_TO_TYPENAME.put(String.class, "varchar");
        CLASS_TO_TYPENAME.put(Boolean.class, "bool");
        CLASS_TO_TYPENAME.put(boolean.class, "bool");
        CLASS_TO_TYPENAME.put(Byte.class, "tinyint");
        CLASS_TO_TYPENAME.put(byte.class, "tinyint");
        CLASS_TO_TYPENAME.put(Short.class, "smallint");
        CLASS_TO_TYPENAME.put(short.class, "smallint");
        CLASS_TO_TYPENAME.put(Integer.class, "int");
        CLASS_TO_TYPENAME.put(int.class, "int");
        CLASS_TO_TYPENAME.put(Long.class, "bigint");
        CLASS_TO_TYPENAME.put(long.class, "bigint");
        CLASS_TO_TYPENAME.put(BigInteger.class, "bigint");
        CLASS_TO_TYPENAME.put(Float.class, "float");
        CLASS_TO_TYPENAME.put(float.class, "float");
        CLASS_TO_TYPENAME.put(Double.class, "double");
        CLASS_TO_TYPENAME.put(double.class, "double");
        CLASS_TO_TYPENAME.put(BigDecimal.class, "decimal");
        CLASS_TO_TYPENAME.put(Date.class, "date");
        CLASS_TO_TYPENAME.put(Time.class, "time");
        CLASS_TO_TYPENAME.put(java.util.Date.class, "timestamp");
        CLASS_TO_TYPENAME.put(Timestamp.class, "timestamp");
        CLASS_TO_TYPENAME.put(byte[].class, "blob");


        //filter capabilities
        final String version = null;
        //ID capabilities, support : EID, FID
        final DefaultIdCapabilities idCapa = new DefaultIdCapabilities(true, true);
        //Spatial capabilities
        final GeometryOperand[] geometryOperands = new GeometryOperand[] {
            GeometryOperand.Point,
            GeometryOperand.LineString,
            GeometryOperand.Polygon,
            GeometryOperand.Envelope
        };
        final SpatialOperator[] spatialOperatrs = new SpatialOperator[] {
            new SpatialOperator("BBOX"      , geometryOperands),
            new SpatialOperator("Beyond"    , geometryOperands),
            new SpatialOperator("Contains"  , geometryOperands),
            new SpatialOperator("Crosses"   , geometryOperands),
            new SpatialOperator("Disjoint"  , geometryOperands),
            new SpatialOperator("DWithin"   , geometryOperands),
            new SpatialOperator("Equals"    , geometryOperands),
            new SpatialOperator("Intersects", geometryOperands),
            new SpatialOperator("Overlaps"  , geometryOperands),
            new SpatialOperator("Touches"   , geometryOperands),
            new SpatialOperator("Within"    , geometryOperands)
        };
        final SpatialOperators spatialOperators = new SpatialOperators(spatialOperatrs);
        final DefaultSpatialCapabilities spatialCapa = new DefaultSpatialCapabilities(geometryOperands, spatialOperators);

        //scalar capabilities
        //support : AND, OR, NOT
        final boolean logical = true;
        //support : =, <>, <, <=, >, >=, LIKE, BEETWEN, NULL
        final Operator[] comparaisonOps = new Operator[] {
            new Operator("PropertyIsEqualTo"),
            new Operator("PropertyIsNotEqualTo"),
            new Operator("PropertyIsLessThan"),
            new Operator("PropertyIsLessThanOrEqualTo"),
            new Operator("PropertyIsGreaterThan"),
            new Operator("PropertyIsGreaterThanOrEqualTo"),
            new Operator("PropertyIsLike"),
            new Operator("PropertyIsBetween"),
            new Operator("PropertyIsNull")
        };
        final ComparisonOperators comparisonOperators = new ComparisonOperators(comparaisonOps);
        //support : +, -, *, /
        final boolean arithmeticSimple = true;
        //support various functions
        final FunctionName[] functionNames = new FunctionName[0];
        final Functions functions = new Functions(functionNames);
        final ArithmeticOperators arithmeticOperators = new ArithmeticOperators(arithmeticSimple, functions);
        final DefaultScalarCapabilities scalarCapa = new DefaultScalarCapabilities(logical, comparisonOperators, arithmeticOperators);

        //temporal capabilities
        final TemporalOperatorName[] temporalOperands = new TemporalOperatorName[0];
        final TemporalOperator[] temporalOperatrs = new TemporalOperator[0];
        final TemporalOperators temporalOperators = new TemporalOperators(temporalOperatrs);
        final DefaultTemporalCapabilities temporalCapa = new DefaultTemporalCapabilities(temporalOperands, temporalOperators);

        FILTER_CAPABILITIES = new DefaultFilterCapabilities(version, idCapa, spatialCapa, scalarCapa, temporalCapa);
    }

    private final DefaultJDBCFeatureStore featurestore;

    //cache
    private Version version = null;

    public MySQLDialect(DefaultJDBCFeatureStore store) {
        this.featurestore = store;
    }

    @Override
    public boolean supportGlobalMetadata() {
        return false;
    }

    @Override
    public Version getVersion(String schema) {
        if(version != null){
            return version;
        }

        Connection cx = null;
        Statement statement = null;
        ResultSet result = null;
        try {
            cx = featurestore.getDataSource().getConnection();
            statement = cx.createStatement();
            result = statement.executeQuery("SELECT version();");

            if (result.next()) {
                version = new Version(result.getString(1));
            }else{
                version = new Version("0.0.0");
            }
        } catch(SQLException ex){
            featurestore.getLogger().log(Level.WARNING, ex.getMessage(),ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(featurestore.getLogger(),cx,statement,result);
        }

        return version;
    }

    @Override
    public DefaultFilterCapabilities getFilterCapabilities() {
        return FILTER_CAPABILITIES;
    }

    @Override
    public FilterToSQL getFilterToSQL(FeatureType featureType) {
        try{
            PrimaryKey pk = null;
            if(featureType!=null){
                pk = featurestore.getDatabaseModel().getPrimaryKey(featureType.getName().toString());
            }
            return new MySQLFilterToSQL(this,
                featureType, pk,
                getVersion(featurestore.getDatabaseSchema()));
        }catch(DataStoreException ex){
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    @Override
    public String getTableEscape() {
        return TABLE_ESCAPE;
    }

    @Override
    public boolean ignoreTable(String name) {
        name = name.toLowerCase();
        return IGNORE_TABLES.contains(name.toLowerCase());
    }

    @Override
    public Class getJavaType(int sqlType, String sqlTypeName) {

        Class c = null;
        sqlTypeName = sqlTypeName.toLowerCase();

        if(sqlType == Types.ARRAY){
            //special case for array types
            if(sqlTypeName.startsWith("_")){
                sqlTypeName = sqlTypeName.substring(1);
            }
            c = TYPENAME_TO_CLASS.get(sqlTypeName);
            if(c==null) c = TYPENAME_TO_CLASS.get(sqlTypeName.toUpperCase());

            if(c == null){
                c = Object.class;
            }
            c = Array.newInstance(c, 0).getClass();
        }else{
            c = TYPENAME_TO_CLASS.get(sqlTypeName);
            if(c==null) c = TYPENAME_TO_CLASS.get(sqlTypeName.toUpperCase());

            if(c == null){
                //try relying on base type.
                c = TYPE_TO_CLASS.get(sqlType);
            }
        }
        if(c == null){
            featurestore.getLogger().log(Level.INFO, "No definied mapping for type : {0} {1}", new Object[]{sqlType, sqlTypeName});
            c = Object.class;
        }
        return c;
    }

    @Override
    public String getSQLType(Class javaType) throws SQLException {
        String sqlName = CLASS_TO_TYPENAME.get(javaType);
        if(sqlName == null) throw new SQLException("No database mapping for type "+ javaType);
        return sqlName;
    }

    @Override
    public String getColumnSequence(Connection cx, String schemaName, String tableName, String columnName) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeFilter(Filter filter, FeatureType type) {
        final FilterToSQL fts = getFilterToSQL(type);
        final StringBuilder sb = new StringBuilder();
        fts.visit(filter, sb);
        return sb.toString();
    }

    @Override
    public void encodeColumnType(StringBuilder sql, String sqlTypeName, Integer length) {
        if(length == null){
            sql.append(sqlTypeName);
        }else{
            sql.append(sqlTypeName);
            sql.append('(').append(length).append(')');
        }
    }

    @Override
    public void encodeLimitOffset(StringBuilder sql, Integer limit, int offset) {
        if (limit > 0 && limit < Integer.MAX_VALUE) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        } else if (offset > 0) {
            sql.append(" OFFSET ").append(offset);
        }
    }

    @Override
    public void encodeValue(StringBuilder sql, Object value, Class type) {
        //turn the value into a literal and use FilterToSQL to encode it
        final Literal literal = featurestore.getFilterFactory().literal(value);
        getFilterToSQL(null).visit(literal, sql);
    }

    @Override
    public void encodePrimaryKey(StringBuilder sql, Class binding, String sqlType) {
        if(Integer.class.isAssignableFrom(binding) || Short.class.isAssignableFrom(binding)){
            sql.append(" SERIAL ");
        }else if(Long.class.isAssignableFrom(binding)){
            sql.append(" BIGSERIAL ");
        }else{
             sql.append(' ').append(sqlType).append(' ');
        }
        sql.append("PRIMARY KEY");
    }

    @Override
    public Object nextValue(ColumnMetaModel column, Connection cx) throws SQLException, DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object decodeAttributeValue(AttributeType descriptor, ResultSet rs, int i) throws SQLException {
        final Class binding = descriptor.getValueClass();
        return rs.getObject(i);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Geometry types, not supported yet ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Integer getGeometrySRID(String schemaName, String tableName, String columnName, Map metas, Connection cx) throws SQLException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public CoordinateReferenceSystem createCRS(int srid, Connection cx) throws SQLException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void encodeGeometryColumn(StringBuilder sql, AttributeType gatt, int srid, Hints hints) {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void encodeGeometryValue(StringBuilder sql, Geometry value, int srid) throws DataStoreException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public Geometry decodeGeometryValue(AttributeType descriptor, ResultSet rs, String column) throws IOException, SQLException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public Geometry decodeGeometryValue(AttributeType descriptor, ResultSet rs, int column) throws IOException, SQLException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void decodeGeometryColumnType(SingleAttributeTypeBuilder atb, Connection cx, ResultSet rs, int columnIndex, boolean customquery) throws SQLException {
        throw new UnsupportedOperationException("Geometry types not supported in MySQL.");
    }

    @Override
    public void encodeCoverageValue(StringBuilder sql, GridCoverage value) throws DataStoreException {
        throw new UnsupportedOperationException("Coverage types not supported in MySQL.");
    }

    @Override
    public GridCoverage decodeCoverageValue(AttributeType descriptor, ResultSet rs, String column) throws IOException, SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GridCoverage decodeCoverageValue(AttributeType descriptor, ResultSet rs, int column) throws IOException, SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
