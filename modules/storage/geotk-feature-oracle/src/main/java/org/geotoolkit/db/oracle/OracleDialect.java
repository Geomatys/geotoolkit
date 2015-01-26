/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.db.oracle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKBReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Blob;
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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.Version;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.JDBCFeatureStoreUtilities;
import org.geotoolkit.db.dialect.AbstractSQLDialect;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.filter.capability.DefaultArithmeticOperators;
import org.geotoolkit.filter.capability.DefaultComparisonOperators;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.capability.DefaultFunctions;
import org.geotoolkit.filter.capability.DefaultIdCapabilities;
import org.geotoolkit.filter.capability.DefaultOperator;
import org.geotoolkit.filter.capability.DefaultScalarCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialCapabilities;
import org.geotoolkit.filter.capability.DefaultSpatialOperator;
import org.geotoolkit.filter.capability.DefaultSpatialOperators;
import org.geotoolkit.filter.capability.DefaultTemporalCapabilities;
import org.geotoolkit.filter.capability.DefaultTemporalOperators;
import org.geotoolkit.referencing.CRS;
import org.opengis.coverage.Coverage;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.capability.Functions;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.Operator;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperator;
import org.opengis.filter.capability.TemporalOperators;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OracleDialect extends AbstractSQLDialect{

    protected final Map<Integer, CoordinateReferenceSystem> CRS_CACHE = new HashMap<>();

    private static final Map<Integer,Class> TYPE_TO_CLASS = new HashMap<>();
    private static final Map<String,Class> TYPENAME_TO_CLASS = new HashMap<>();
    private static final Map<Class,String> CLASS_TO_TYPENAME = new HashMap<>();
    private static final Map<String, String> TYPE_TO_ST_TYPE_MAP = new HashMap<>();
    private static final Set<String> IGNORE_TABLES = new HashSet<>();

    private static final FilterCapabilities FILTER_CAPABILITIES;

    static {
        //fill base types
        TYPE_TO_CLASS.put(Types.VARCHAR,        String.class);
        TYPE_TO_CLASS.put(Types.CHAR,           String.class);
        TYPE_TO_CLASS.put(Types.LONGVARCHAR,   String.class);
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
        TYPE_TO_CLASS.put(Types.ARRAY,          Array.class);


//NAME IN CREATE QUERY          SQL TYPE     SQL TPE NAME
/*serial                            4           serial      */ TYPENAME_TO_CLASS.put("serial", Integer.class);
/*bigserial                         -5          bigserial   */ TYPENAME_TO_CLASS.put("bigserial", Long.class);
/*abstime                           1111        abstime     */ TYPENAME_TO_CLASS.put("abstime", Object.class);
/*aclitem                           1111        aclitem     */ TYPENAME_TO_CLASS.put("aclitem", Object.class);
/*bigint                            -5          int8        */ TYPENAME_TO_CLASS.put("int8", Long.class);
/*bit(1)                            -7          bit         */ TYPENAME_TO_CLASS.put("bit", Boolean.class);
/*bit varying                       1111        varbit      */ TYPENAME_TO_CLASS.put("varbit", Object.class);
/*boolean                           -7          bool        */ TYPENAME_TO_CLASS.put("bool", Boolean.class);
/*box                               1111        box         */ TYPENAME_TO_CLASS.put("box", Object.class);
/*byte                              -2          bytea       */ TYPENAME_TO_CLASS.put("bytea", Object.class);
/*char                              1           char        */ TYPENAME_TO_CLASS.put("char", String.class);
/*character(1)                      1           bpchar      */ TYPENAME_TO_CLASS.put("bpchar", String.class);
/*character varying                 12          varchar     */ TYPENAME_TO_CLASS.put("varchar", String.class);
/*cid                               1111        cid         */ TYPENAME_TO_CLASS.put("cid", Object.class);
/*cidr                              1111        cidr        */ TYPENAME_TO_CLASS.put("cidr", Object.class);
/*circle                            1111        circle      */ TYPENAME_TO_CLASS.put("circle", Object.class);
/*date                              91          date        */ TYPENAME_TO_CLASS.put("date", Date.class);
/*double precision                  8           float8      */ TYPENAME_TO_CLASS.put("float8", Double.class);
/*gtsvector                         1111        gtsvector   */ TYPENAME_TO_CLASS.put("gtsvector", Object.class);
/*inet                              1111        inet        */ TYPENAME_TO_CLASS.put("inet", Object.class);
/*int2vector                        1111        int2vector  */ TYPENAME_TO_CLASS.put("int2vector", Object.class);
/*integer                           4           int4        */ TYPENAME_TO_CLASS.put("int4", Integer.class);
/*interval                          1111        interval    */ TYPENAME_TO_CLASS.put("interval", Object.class);
/*line                              1111        line        */ TYPENAME_TO_CLASS.put("line", Object.class);
/*lseg                              1111        lseg        */ TYPENAME_TO_CLASS.put("lseg", Object.class);
/*macaddr                           1111        macaddr     */ TYPENAME_TO_CLASS.put("macaddr", Object.class);
/*money                             8           money       */ TYPENAME_TO_CLASS.put("money", Double.class);
/*name                              12          name        */ TYPENAME_TO_CLASS.put("name", String.class);
/*numeric                           2           numeric     */ TYPENAME_TO_CLASS.put("numeric", BigDecimal.class);
/*oid                               -5          oid         */ TYPENAME_TO_CLASS.put("oid", Long.class);
/*oidvector                         1111        oidvector   */ TYPENAME_TO_CLASS.put("oidvector", Object.class);
/*path                              1111        path        */ TYPENAME_TO_CLASS.put("path", Object.class);
/*pg_node_tree                      1111        pg_node_tree*/ TYPENAME_TO_CLASS.put("pg_node_tree", Object.class);
/*point                             1111        point       */ TYPENAME_TO_CLASS.put("point", Object.class);
/*polygon                           1111        polygon     */ TYPENAME_TO_CLASS.put("polygon", Object.class);
/*real                              7           float4      */ TYPENAME_TO_CLASS.put("float4", Float.class);
/*refcursor                         1111        refcursor   */ TYPENAME_TO_CLASS.put("refcursor", Object.class);
/*regclass                          1111        regclass    */ TYPENAME_TO_CLASS.put("regclass", Object.class);
/*regconfig                         1111        regconfig   */ TYPENAME_TO_CLASS.put("regconfig", Object.class);
/*regdictionary                     1111       regdictionary*/ TYPENAME_TO_CLASS.put("regdictionary", Object.class);
/*regoper                           1111        regoper     */ TYPENAME_TO_CLASS.put("regoper", Object.class);
/*regoperator                       1111        regoperator */ TYPENAME_TO_CLASS.put("regoperator", Object.class);
/*regproc                           1111        regproc     */ TYPENAME_TO_CLASS.put("regproc", Object.class);
/*regprocedure                      1111        regprocedure*/ TYPENAME_TO_CLASS.put("regprocedure", Object.class);
/*regtype                           1111        regtype     */ TYPENAME_TO_CLASS.put("regtype", Object.class);
/*reltime                           1111        reltime     */ TYPENAME_TO_CLASS.put("reltime", Object.class);
/*smallint                          8           int2        */ TYPENAME_TO_CLASS.put("int2", Short.class);
/*smgr                              1111        smgr        */ TYPENAME_TO_CLASS.put("smgr", Object.class);
/*text                              12          text        */ TYPENAME_TO_CLASS.put("text", String.class);
/*tid                               1111        tid         */ TYPENAME_TO_CLASS.put("tid", Object.class);
/*timestamp without time zone       93          timestamp   */ TYPENAME_TO_CLASS.put("timestamp", Timestamp.class);
/*timestamp with time zone          93          timestamptz */ TYPENAME_TO_CLASS.put("timestamptz", Timestamp.class);
/*time without time zone            92          time        */ TYPENAME_TO_CLASS.put("time", Time.class);
/*time with time zone               92          timetz      */ TYPENAME_TO_CLASS.put("timetz", Time.class);
/*tinterval                         1111        tinterval   */ TYPENAME_TO_CLASS.put("tinterval", Object.class);
/*tsquery                           1111        tsquery     */ TYPENAME_TO_CLASS.put("tsquery", Object.class);
/*tsvector                          1111        tsvector    */ TYPENAME_TO_CLASS.put("tsvector", Object.class);
/*txid_snapshot                     1111       txid_snapshot*/ TYPENAME_TO_CLASS.put("txid_snapshot", Object.class);
/*uuid                              1111        uuid        */ TYPENAME_TO_CLASS.put("uuid", Object.class);
/*xid                               1111        xid         */ TYPENAME_TO_CLASS.put("xid", Object.class);
/*xml                               2009        xml         */ TYPENAME_TO_CLASS.put("xml", String.class);
/*box2d                             1111        box2d       */ TYPENAME_TO_CLASS.put("box2d", Object.class);
/*box3d                             1111        box3d       */ TYPENAME_TO_CLASS.put("box3d", Object.class);
/*box3d_extent                      1111        box3d_extent*/ TYPENAME_TO_CLASS.put("box3d_extent", Object.class);
/*chip                              1111        chip        */ TYPENAME_TO_CLASS.put("chip", Object.class);
/*geography                         1111        geography   */ TYPENAME_TO_CLASS.put("geography", Object.class);
/*geometry_dump                     2002       geometry_dump*/ TYPENAME_TO_CLASS.put("geometry_dump", Object.class);
/*gidx                              1111        gidx        */ TYPENAME_TO_CLASS.put("gidx", Object.class);
/*pgis_abs                          1111        pgis_abs    */ TYPENAME_TO_CLASS.put("pgis_abs", Object.class);
/*spheroid                          1111        spheroid    */ TYPENAME_TO_CLASS.put("spheroid", Object.class);
        CLASS_TO_TYPENAME.put(String.class, "varchar");
        CLASS_TO_TYPENAME.put(Boolean.class, "bool");
        CLASS_TO_TYPENAME.put(boolean.class, "bool");
        CLASS_TO_TYPENAME.put(Byte.class, "smallint");
        CLASS_TO_TYPENAME.put(byte.class, "smallint");
        CLASS_TO_TYPENAME.put(Short.class, "int2");
        CLASS_TO_TYPENAME.put(short.class, "int2");
        CLASS_TO_TYPENAME.put(Integer.class, "int4");
        CLASS_TO_TYPENAME.put(int.class, "int4");
        CLASS_TO_TYPENAME.put(Long.class, "int8");
        CLASS_TO_TYPENAME.put(long.class, "int8");
        CLASS_TO_TYPENAME.put(Float.class, "float4");
        CLASS_TO_TYPENAME.put(float.class, "float4");
        CLASS_TO_TYPENAME.put(Double.class, "float8");
        CLASS_TO_TYPENAME.put(double.class, "float8");
        CLASS_TO_TYPENAME.put(BigDecimal.class, "");
        CLASS_TO_TYPENAME.put(Date.class, "date");
        CLASS_TO_TYPENAME.put(Time.class, "time");
        CLASS_TO_TYPENAME.put(java.util.Date.class, "timestamp");
        CLASS_TO_TYPENAME.put(Timestamp.class, "timestamp");
        CLASS_TO_TYPENAME.put(byte[].class, "blob");
        CLASS_TO_TYPENAME.put(Coverage.class, "raster");


        //H2GIS extension
        TYPENAME_TO_CLASS.put("GEOMETRY", Geometry.class);
        TYPENAME_TO_CLASS.put("GEOGRAPHY", Geometry.class);
        TYPENAME_TO_CLASS.put("POINT", Point.class);
        TYPENAME_TO_CLASS.put("POINTM", Point.class);
        TYPENAME_TO_CLASS.put("LINESTRING", LineString.class);
        TYPENAME_TO_CLASS.put("LINESTRINGM", LineString.class);
        TYPENAME_TO_CLASS.put("POLYGON", Polygon.class);
        TYPENAME_TO_CLASS.put("POLYGONM", Polygon.class);
        TYPENAME_TO_CLASS.put("MULTIPOINT", MultiPoint.class);
        TYPENAME_TO_CLASS.put("MULTIPOINTM", MultiPoint.class);
        TYPENAME_TO_CLASS.put("MULTILINESTRING", MultiLineString.class);
        TYPENAME_TO_CLASS.put("MULTILINESTRINGM", MultiLineString.class);
        TYPENAME_TO_CLASS.put("MULTIPOLYGON", MultiPolygon.class);
        TYPENAME_TO_CLASS.put("MULTIPOLYGONM", MultiPolygon.class);
        TYPENAME_TO_CLASS.put("GEOMETRYCOLLECTION", GeometryCollection.class);
        TYPENAME_TO_CLASS.put("GEOMETRYCOLLECTIONM", GeometryCollection.class);

        CLASS_TO_TYPENAME.put(Geometry.class, "GEOMETRY");
        CLASS_TO_TYPENAME.put(Point.class, "POINT");
        CLASS_TO_TYPENAME.put(LineString.class, "LINESTRING");
        CLASS_TO_TYPENAME.put(Polygon.class, "POLYGON");
        CLASS_TO_TYPENAME.put(MultiPoint.class, "MULTIPOINT");
        CLASS_TO_TYPENAME.put(MultiLineString.class, "MULTILINESTRING");
        CLASS_TO_TYPENAME.put(MultiPolygon.class, "MULTIPOLYGON");
        CLASS_TO_TYPENAME.put(GeometryCollection.class, "GEOMETRYCOLLECTION");

        TYPE_TO_ST_TYPE_MAP.put("GEOMETRY","ST_Geometry");
        TYPE_TO_ST_TYPE_MAP.put("POINT","ST_Point");
        TYPE_TO_ST_TYPE_MAP.put("LINESTRING","ST_LineString");
        TYPE_TO_ST_TYPE_MAP.put("POLYGON","ST_Polygon");
        TYPE_TO_ST_TYPE_MAP.put("MULTIPOINT","ST_MultiPoint");
        TYPE_TO_ST_TYPE_MAP.put("MULTILINESTRING","ST_MultiLineString");
        TYPE_TO_ST_TYPE_MAP.put("MULTIPOLYGON","ST_MultiPolygon");
        TYPE_TO_ST_TYPE_MAP.put("GEOMETRYCOLLECTION","ST_GeometryCollection");

        //postgis 1+ geometry and referencing
        IGNORE_TABLES.add("spatial_ref_sys");
        IGNORE_TABLES.add("geometry_columns");
        IGNORE_TABLES.add("geography_columns");


        //filter capabilities
        final String version = null;
        //ID capabilities, support : EID, FID
        final DefaultIdCapabilities idCapa = new DefaultIdCapabilities(true, true);
        //Spatial capabilities
        final GeometryOperand[] geometryOperands = new GeometryOperand[]{
            GeometryOperand.Point,
            GeometryOperand.LineString,
            GeometryOperand.Polygon,
            GeometryOperand.Envelope
        };
        final SpatialOperator[] spatialOperatrs = new SpatialOperator[]{
            new DefaultSpatialOperator(BBOX.NAME      , geometryOperands),
            new DefaultSpatialOperator(Beyond.NAME    , geometryOperands),
            new DefaultSpatialOperator(Contains.NAME  , geometryOperands),
            new DefaultSpatialOperator(Crosses.NAME   , geometryOperands),
            new DefaultSpatialOperator(Disjoint.NAME  , geometryOperands),
            new DefaultSpatialOperator(DWithin.NAME   , geometryOperands),
            new DefaultSpatialOperator(Equals.NAME    , geometryOperands),
            new DefaultSpatialOperator(Intersects.NAME, geometryOperands),
            new DefaultSpatialOperator(Overlaps.NAME  , geometryOperands),
            new DefaultSpatialOperator(Touches.NAME   , geometryOperands),
            new DefaultSpatialOperator(Within.NAME    , geometryOperands)
        };
        final SpatialOperators spatialOperators = new DefaultSpatialOperators(spatialOperatrs);
        final SpatialCapabilities spatialCapa = new DefaultSpatialCapabilities(geometryOperands, spatialOperators);

        //scalar capabilities
        //support : AND, OR, NOT
        final boolean logical = true;
        //support : =, <>, <, <=, >, >=, LIKE, BEETWEN, NULL
        final Operator[] comparaisonOps = new Operator[]{
            new DefaultOperator(PropertyIsEqualTo.NAME),
            new DefaultOperator(PropertyIsNotEqualTo.NAME),
            new DefaultOperator(PropertyIsLessThan.NAME),
            new DefaultOperator(PropertyIsLessThanOrEqualTo.NAME),
            new DefaultOperator(PropertyIsGreaterThan.NAME),
            new DefaultOperator(PropertyIsGreaterThanOrEqualTo.NAME),
            new DefaultOperator(PropertyIsLike.NAME),
            new DefaultOperator(PropertyIsBetween.NAME),
            new DefaultOperator(PropertyIsNull.NAME)
        };
        final ComparisonOperators comparisonOperators = new DefaultComparisonOperators(comparaisonOps);
        //support : +, -, *, /
        final boolean arithmeticSimple = true;
        //support various functions
        final FunctionName[] functionNames = new FunctionName[0];
        final Functions functions = new DefaultFunctions(functionNames);
        final ArithmeticOperators arithmeticOperators = new DefaultArithmeticOperators(arithmeticSimple, functions);
        final ScalarCapabilities scalarCapa = new DefaultScalarCapabilities(logical, comparisonOperators, arithmeticOperators);

        //temporal capabilities
        final TemporalOperand[] temporalOperands = new TemporalOperand[0];
        final TemporalOperator[] temporalOperatrs = new TemporalOperator[0];
        final TemporalOperators temporalOperators = new DefaultTemporalOperators(temporalOperatrs);
        final TemporalCapabilities temporalCapa = new DefaultTemporalCapabilities(temporalOperands, temporalOperators);

        FILTER_CAPABILITIES = new DefaultFilterCapabilities(version, idCapa, spatialCapa, scalarCapa, temporalCapa);
    }

    private final DefaultJDBCFeatureStore featurestore;

    //readers
    private final ThreadLocal<WKBReader> wkbReader = new ThreadLocal<WKBReader>();

    //cache
    private Version version = null;

    OracleDialect(DefaultJDBCFeatureStore datastore) {
        this.featurestore = datastore;
    }

    DefaultJDBCFeatureStore getFeaturestore() {
        return featurestore;
    }

    @Override
    public boolean supportGlobalMetadata() {
        return true;
    }

    @Override
    public FilterCapabilities getFilterCapabilities() {
        return FILTER_CAPABILITIES;
    }

    @Override
    public FilterToSQL getFilterToSQL(ComplexType featureType) {
        try{
            PrimaryKey pk = null;
            if(featureType!=null){
                pk = featurestore.getDatabaseModel().getPrimaryKey(featureType.getName());
            }
            return new OracleFilterToSQL(featureType, pk);
        }catch(DataStoreException ex){
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    @Override
    public String getTableEscape() {
        return "\"";
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
    public String getSQLType(Class javaType) throws SQLException{
        String sqlName = CLASS_TO_TYPENAME.get(javaType);
        if(javaType.isArray()){
            sqlName = getSQLType(javaType.getComponentType());
            if(sqlName == null) throw new SQLException("No database mapping for type "+ javaType);
            sqlName = sqlName+"[]";
        }

        if(sqlName == null) throw new SQLException("No database mapping for type "+ javaType);
        return sqlName;
    }

    @Override
    public String getColumnSequence(Connection cx, String schemaName, String tableName, String columnName) throws SQLException {
        //TODO
        return null;
    }

    @Override
    public boolean ignoreTable(String name) {
        name = name.toLowerCase();
        //ignore the versioning tables
        if(name.contains("$")){
            return true;
        }
        return IGNORE_TABLES.contains(name.toLowerCase());
    }

    @Override
    public Version getVersion(String schema){
        if(version != null){
            return version;
        }

        //TODO
        version = new Version("1.0.0");
        return version;
    }

    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO CREATE SQL QUERIES ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public String encodeFilter(Filter filter, ComplexType type) {
        final FilterToSQL fts = getFilterToSQL(type);
        final StringBuilder sb = (StringBuilder)filter.accept(fts, new StringBuilder());
        return sb.toString();
    }

    @Override
    public void encodeColumnType(StringBuilder sql, String sqlTypeName, Integer length) {
        if(TYPE_TO_ST_TYPE_MAP.containsKey(sqlTypeName)){
            //geometry type, will be added as a constraint in the postcreate method
            sqlTypeName = "GEOMETRY";
        }

        if(length == null){
            sql.append(sqlTypeName);
        }else{
            final int arrayindex = sqlTypeName.indexOf("[]");
            if(arrayindex>0){
                sql.append(sqlTypeName.substring(0, arrayindex));
                sql.append('(').append(length).append(')');
                sql.append(sqlTypeName.substring(arrayindex));
            }else{
                sql.append(sqlTypeName);
                sql.append('(').append(length).append(')');
            }
        }
    }

    @Override
    public void encodeGeometryColumn(StringBuilder sql, GeometryDescriptor gatt, int srid, Hints hints) {
        throw new RuntimeException("Not supported yet.");
    }

    @Override
    public void encodeLimitOffset(StringBuilder sql, Integer limit, int offset) {
        if (limit != null && limit > 0 && limit < Integer.MAX_VALUE) {
            sql.append(" LIMIT ").append(limit);
        }
        if (offset > 0) {
            sql.append(" OFFSET ").append(offset);
        }
    }

    @Override
    public void encodeValue(StringBuilder sql, Object value, Class type) {
        //turn the value into a literal and use FilterToSQL to encode it
        final Literal literal = featurestore.getFilterFactory().literal(value);
        literal.accept(getFilterToSQL(null), sql);
    }

    @Override
    public void encodeGeometryValue(StringBuilder sql, Geometry value, int srid) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void encodeCoverageValue(StringBuilder sql, Coverage value) throws DataStoreException {
        throw new DataStoreException("Coverage type not supported.");
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
    public void postCreateTable(String schemaName, final FeatureType featureType,
            final Connection cx) throws SQLException{
    }


    ////////////////////////////////////////////////////////////////////////////
    // PRIMARY KEY CALCULATION METHOS //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Object nextValue(final ColumnMetaModel column, final Connection cx) throws SQLException, DataStoreException {
        if(column.getType() == ColumnMetaModel.Type.SEQUENCED){
            final Statement st = cx.createStatement();
            ResultSet rs = null;
            try {
                final String sql = "SELECT nextval('" + column.getSequenceName() + "')";
                rs = st.executeQuery(sql);
                if (rs.next()) {
                    return rs.getLong(1);
                }
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(featurestore.getLogger(), null,st,rs);
            }
            return null;
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO READ FROM RESULTSET //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void decodeColumnType(final AttributeTypeBuilder atb, final Connection cx,
            String typeName, final int datatype, final String schemaName,
            final String tableName, final String columnName) throws SQLException {
        super.decodeColumnType(atb, cx, typeName, datatype,
                    schemaName, tableName,columnName);
    }


    @Override
    public void decodeGeometryColumnType(final AttributeTypeBuilder atb, final Connection cx,
            final ResultSet rs, final int columnIndex, boolean customQuery) throws SQLException {
        throw new SQLException("Not supported yet.");
    }

    @Override
    public Integer getGeometrySRID(String schemaName, final String tableName, final String columnName,
            Map metas, final Connection cx) throws SQLException{
        throw new SQLException("Not supported yet.");
    }

    @Override
    public CoordinateReferenceSystem createCRS(int srid, Connection cx) throws SQLException {
        CoordinateReferenceSystem crs = CRS_CACHE.get(srid);
        if (crs == null && srid!=0) {
            try {
                crs = CRS.decode("EPSG:" + srid,true);
                CRS_CACHE.put(srid, crs);
            } catch(Exception e) {
                if(featurestore.getLogger().isLoggable(Level.FINE)) {
                    featurestore.getLogger().log(Level.FINE, "Could not decode " + srid + " using the built-in EPSG database", e);
                }
                return null;
            }
        }
        return crs;
    }

    @Override
    public Object decodeAttributeValue(AttributeDescriptor descriptor, ResultSet rs,
            int i) throws SQLException{
        final Class binding = descriptor.getType().getBinding();
        if(binding.isArray()){
            if(byte.class.equals(binding.getComponentType())){
                //blob or binary field type
                final Blob blob = rs.getBlob(i);
                return blob.getBytes(1, (int)blob.length());

            }else if(rs.getArray(i) != null) {
                Object baseArray = rs.getArray(i).getArray();

                final Class c = binding.getComponentType();
                if(!baseArray.getClass().getComponentType().equals(c)){
                    int nbdim=1;
                    Class base = baseArray.getClass().getComponentType();
                    while(base.isArray()){
                        base = base.getComponentType();
                        nbdim++;
                    }

                    baseArray = rebuildArray(baseArray, c, nbdim);
                }
                return baseArray;
            } else {
                return null;
            }
            
        }else{
            if(String.class.equals(binding)){
                //solve nclob string
                return rs.getString(i);
            }else{
                return rs.getObject(i);
            }
        }
    }

    private Object rebuildArray(Object candidate, Class componentType, int depth){
        if(candidate==null) return null;

        if(candidate.getClass().isArray()){
            final int size = Array.getLength(candidate);
            final int[] dims = new int[depth];
            dims[0] = size;
            final Object rarray = Array.newInstance(componentType, dims);
            depth--;
            for(int k=0; k<size; k++){
                Array.set(rarray, k, rebuildArray(Array.get(candidate, k), componentType, depth));
            }
            return rarray;
        }else{
            return ObjectConverters.convert(candidate, componentType);
        }
    }


    @Override
    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs,
        String column) throws IOException, SQLException {
        throw new IOException("Not supported yet.");
    }

    @Override
    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs,
        int column) throws IOException, SQLException {
        throw new IOException("Not supported yet.");
    }

    @Override
    public Coverage decodeCoverageValue(GeometryDescriptor descriptor, ResultSet rs, String column) throws IOException, SQLException {
        throw new IOException("Coverage type not supported.");
    }

    @Override
    public Coverage decodeCoverageValue(GeometryDescriptor descriptor, ResultSet rs, int column) throws IOException, SQLException {
        throw new IOException("Coverage type not supported.");
    }

}

