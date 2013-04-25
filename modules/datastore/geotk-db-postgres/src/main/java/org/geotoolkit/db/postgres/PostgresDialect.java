/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db.postgres;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
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
import org.apache.sis.util.Version;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStoreUtilities;
import static org.geotoolkit.db.JDBCFeatureStoreUtilities.*;
import org.geotoolkit.db.dialect.AbstractSQLDialect;
import org.geotoolkit.db.postgres.ewkb.JtsBinaryParser;
import org.geotoolkit.db.postgres.wkb.WKBAttributeIO;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresDialect extends AbstractSQLDialect{

    private static final String GEOM_ENCODING = "Encoding";
    private static enum GeometryEncoding{
        HEXEWKB,
        WKB,
        WKT,
        UNKNOWNED
    }

    protected final Map<Integer, CoordinateReferenceSystem> CRS_CACHE = new HashMap<Integer, CoordinateReferenceSystem>();
    
    private static final Map<Integer,Class> TYPE_TO_CLASS = new HashMap<Integer, Class>();
    private static final Map<String,Class> TYPENAME_TO_CLASS = new HashMap<String, Class>();
    private static final Map<Class,String> CLASS_TO_TYPENAME = new HashMap<Class,String>();
    private static final Map<String, String> TYPE_TO_ST_TYPE_MAP = new HashMap<String, String>();
    private static final Set<String> IGNORE_TABLES = new HashSet<String>();
        
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



        //POSTGIS extension
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
        //postgis 2 raster
        IGNORE_TABLES.add("raster_columns");
        IGNORE_TABLES.add("raster_overviews");
        
    }
        
    private final DefaultJDBCFeatureStore featurestore;
    
    //readers
    private final ThreadLocal<WKBAttributeIO> wkbReader = new ThreadLocal<WKBAttributeIO>();
    private final JtsBinaryParser hexewkbReader = new JtsBinaryParser(){

        @Override
        public Geometry parse(String value) {
            final Geometry geom =  super.parse(value);

            if(geom != null){
                final int srid = geom.getSRID();
                if(srid >= 0){
                    try {
                        //set the real crs
                        geom.setUserData(decodeCRS(geom.getSRID(), null));
                    } catch (SQLException ex) {
                        featurestore.getLogger().log(Level.WARNING, ex.getLocalizedMessage(),ex);
                    }
                }
            }
            return geom;
        }

    };
    
    //cache
    private Version version = null;

    public PostgresDialect(DefaultJDBCFeatureStore datastore) {
        this.featurestore = datastore;
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
            sqlName = CLASS_TO_TYPENAME.get(javaType.getComponentType());
            if(sqlName == null) throw new SQLException("No database mapping for type "+ javaType);
            sqlName = sqlName+"[]";
        }
        
        if(sqlName == null) throw new SQLException("No database mapping for type "+ javaType);
        return sqlName;
    }

    @Override
    public String getColumnSequence(Connection cx, String schemaName, String tableName, String columnName) throws SQLException {
        final Statement st = cx.createStatement();
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append("SELECT pg_get_serial_sequence('");
            encodeSchemaAndTableName(sb, schemaName, tableName);
            sb.append("', '").append(columnName).append("')");
            final String sql = sb.toString();
            final ResultSet rs = st.executeQuery(sql);
            try {
                if(rs.next()){
                    return rs.getString(1);
                }
            } finally {
                closeSafe(featurestore.getLogger(),rs);
            }
        } finally {
            closeSafe(featurestore.getLogger(),st);
        }
        return null;
    }

    @Override
    public boolean ignoreTable(String name) {
        return IGNORE_TABLES.contains(name.toLowerCase());
    }

    @Override
    public Filter[] splitFilter(Filter filter) {
        //TODO
        final Filter[] divided = new Filter[2];
        divided[0] = Filter.INCLUDE;
        divided[1] = filter;
        return divided;
    }

    @Override
    public Version getVersion(String schema){
        if(version != null){
            return version;
        }
        
        Connection cx = null;
        Statement statement = null;
        ResultSet result = null;        
        try {
            cx = featurestore.getDataSource().getConnection();
            statement = cx.createStatement();
            result = statement.executeQuery("SELECT postgis_lib_version();");

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
    
    ////////////////////////////////////////////////////////////////////////////
    // METHODS TO CREATE SQL QUERIES ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public String encodeFilter(Filter filter) {
        
        if(filter == Filter.EXCLUDE){
            return "1 = 0";
        }
        //TODO
        return "";
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
            if(sqlTypeName.endsWith("[]")){
                sql.append(sqlTypeName.substring(0, sqlTypeName.length()-2));
                sql.append('(').append(length).append(')');
                sql.append("[]");
            }else{
                sql.append(sqlTypeName);
                sql.append('(').append(length).append(')');
            }
        }
    }
    
    @Override
    public void encodeGeometryColumn(StringBuilder sql, GeometryDescriptor gatt, int srid, Hints hints) {
        double res = 0;
        if(hints != null){
            double[] ress = (double[]) hints.get(JDBCFeatureStore.RESAMPLING);
            if(ress != null){
                res = Math.min(ress[0], ress[1]);
            }
        }
        if(Double.isInfinite(res)){
            res = Double.MAX_VALUE;
        }


        final CoordinateReferenceSystem crs = gatt.getCoordinateReferenceSystem();
        final int dimensions = (crs == null) ? 2 : crs.getCoordinateSystem().getDimension();
        sql.append("encode(");

        if(res > 0){
            if (dimensions > 2) {
                sql.append("ST_AsEWKB(ST_simplify(");
                encodeColumnName(sql, gatt.getLocalName());
                sql.append(",").append(res).append(")");
            } else {
                sql.append("ST_AsBinary(ST_simplify(");
                encodeColumnName(sql, gatt.getLocalName());
                sql.append(",").append(res).append(")");
            }
            sql.append(") ");
        }else{
            if (dimensions > 2) {
                sql.append("ST_AsEWKB(");
                encodeColumnName(sql, gatt.getLocalName());
            } else {
                sql.append("ST_AsBinary(");
                encodeColumnName(sql, gatt.getLocalName());
            }
            sql.append(") ");
        }

        sql.append(",'base64')");
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
        throw new RuntimeException("Not yet implemented.");
//        //turn the value into a literal and use FilterToSQL to encode it
//        final Literal literal = featurestore.getFilterFactory().literal(value);
//        final FilterToSQL filterToSQL = featurestore.createFilterToSQL(null);
//
//        final StringWriter w = new StringWriter();
//        filterToSQL.setWriter(w);
//        filterToSQL.visit(literal,type);
//
//        sql.append(w.getBuffer().toString());
    }

    @Override
    public void encodeGeometryValue(StringBuilder sql, Geometry value, int srid) throws DataStoreException {
        if (value == null) {
            sql.append("NULL");
        } else {
            if (value instanceof LinearRing) {
                //postgis does not handle linear rings, convert to just a line string
                value = value.getFactory().createLineString(((LinearRing) value).getCoordinateSequence());
            }
            
            if(value.isEmpty() && ((Comparable)getVersion(null).getMajor()).compareTo((Comparable)Integer.valueOf(2)) < 0){
                //empty geometries are interpreted as Geometrycollection in postgis < 2
                //this breaks the column geometry type constraint so we replace those by null
                sql.append("NULL");
            }else{
                sql.append("st_geomfromtext('").append(value.toText()).append("', ").append(srid).append(")");
            }
        }
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
    public void postCreateTable(String schemaName, final SimpleFeatureType featureType,
            final Connection cx) throws SQLException{
        if (schemaName == null) {
            schemaName = "public";
        }
        final String tableName = featureType.getName().getLocalPart();

        Statement st = null;
        try {
            st = cx.createStatement();

            // register all geometry columns in the database
            for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
                if (att instanceof GeometryDescriptor) {
                    final GeometryDescriptor gd = (GeometryDescriptor) att;

                    // lookup or reverse engineer the srid
                    int srid = -1;
                    if (gd.getUserData().get(JDBCFeatureStore.JDBC_NATIVE_SRID) != null) {
                        srid = (Integer) gd.getUserData().get(JDBCFeatureStore.JDBC_NATIVE_SRID);
                    } else if (gd.getCoordinateReferenceSystem() != null) {
                        try {
                            final Integer result = IdentifiedObjects.lookupEpsgCode(gd.getCoordinateReferenceSystem(), true);
                            if (result != null) {
                                srid = result;
                            }
                        } catch (FactoryException e) {
                            featurestore.getLogger().log(Level.FINE, "Error looking up the "
                                    + "epsg code for metadata "
                                    + "insertion, assuming -1", e);
                        }
                    }

                    // assume 2 dimensions, but ease future customisation
                    final int dimensions = 2;

                    // grab the geometry type
                    String geomType = getSQLType(gd.getType().getBinding());
                    if (geomType == null)
                        geomType = "GEOMETRY";

                    // register the geometry type, first remove and eventual
                    // leftover, then write out the real one
                    StringBuilder sb = new StringBuilder("DELETE FROM GEOMETRY_COLUMNS");
                    sb.append(" WHERE f_table_catalog=''");
                    sb.append(" AND f_table_schema = '").append(schemaName).append('\'');
                    sb.append(" AND f_table_name = '").append(tableName).append('\'');
                    sb.append(" AND f_geometry_column = '").append(gd.getLocalName()).append('\'');
                    String sql = sb.toString();

                    featurestore.getLogger().fine( sql );
                    st.execute( sql );

                    sb = new StringBuilder("INSERT INTO GEOMETRY_COLUMNS VALUES ('',");
                    sb.append('\'').append(schemaName).append("',");
                    sb.append('\'').append(tableName).append("',");
                    sb.append('\'').append(gd.getLocalName()).append("',");
                    sb.append(dimensions).append(',');
                    sb.append(srid).append(',');
                    sb.append('\'').append(geomType).append("')");
                    sql = sb.toString();
                    featurestore.getLogger().fine( sql );
                    st.execute( sql );

                    // add srid checks
                    if (srid > -1) {
                        sb = new StringBuilder("ALTER TABLE \"").append(tableName).append('"');
                        sb.append(" ADD CONSTRAINT \"enforce_srid_");
                        sb.append(gd.getLocalName()).append('"');
                        sb.append(" CHECK (st_srid(");
                        sb.append('"').append(gd.getLocalName()).append('"');
                        sb.append(") = ").append(srid).append(')');
                        sql = sb.toString();
                        featurestore.getLogger().fine( sql );
                        st.execute(sql);
                    }

                    // add dimension checks

                    sb = new StringBuilder("ALTER TABLE \"").append(tableName).append('"');
                    sb.append(" ADD CONSTRAINT \"enforce_dims_");
                    sb.append(gd.getLocalName()).append('"');
                    sb.append(" CHECK (st_ndims(\"").append(gd.getLocalName()).append("\") = 2)");
                    sql = sb.toString();
                    featurestore.getLogger().fine(sql);
                    st.execute(sql);

                    // add geometry type checks
                    sb = new StringBuilder("ALTER TABLE \"").append(tableName);
                    sb.append('"');
                    sb.append(" ADD CONSTRAINT \"enforce_geotype_");
                    sb.append(gd.getLocalName()).append('"');
                    sb.append(" CHECK (st_geometrytype(");
                    sb.append('"').append(gd.getLocalName()).append('"');
                    sb.append(") = '").append(TYPE_TO_ST_TYPE_MAP.get(geomType)).append("'::text OR \"");
                    sb.append(gd.getLocalName()).append('"').append(" IS NULL)");
                    sql = sb.toString();
                    featurestore.getLogger().fine(sql);
                    st.execute(sql);

                    // add the spatial index
                    sb = new StringBuilder("CREATE INDEX \"spatial_").append(tableName);
                    sb.append('_').append(gd.getLocalName().toLowerCase()).append('"');
                    sb.append(" ON ");
                    sb.append('"').append(tableName).append('"');
                    sb.append(" USING GIST (");
                    sb.append('"').append(gd.getLocalName()).append('"').append(')');
                    sql = sb.toString();
                    featurestore.getLogger().fine(sql);
                    st.execute(sql);
                }
            }
            if(!cx.getAutoCommit()){
                cx.commit();
            }
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(featurestore.getLogger(),st);
        }
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
        
        typeName = typeName.toUpperCase();
        if (!TYPE_TO_ST_TYPE_MAP.containsKey(typeName)) {
            return;
        }

        String gType = null;
        if(tableName == null || tableName.isEmpty()){
            //this column informations seems to come from a custom sql query
            //the result will be the natural encoding of postgis which is hexadecimal EWKB
            atb.addUserData(GEOM_ENCODING, PostgresDialect.GeometryEncoding.HEXEWKB);

        }else{
            //this column informations comes from a real table
            atb.addUserData(GEOM_ENCODING, PostgresDialect.GeometryEncoding.WKB);

            //first attempt, try with the geometry metadata
            Statement statement = null;
            ResultSet result = null;
            try {
                final StringBuilder sb = new StringBuilder("SELECT TYPE FROM GEOMETRY_COLUMNS WHERE ");
                sb.append("F_TABLE_SCHEMA = '").append(schemaName).append("' ");
                sb.append("AND F_TABLE_NAME = '").append(tableName).append("' ");
                sb.append("AND F_GEOMETRY_COLUMN = '").append(columnName).append('\'');
                final String sqlStatement = sb.toString();
                featurestore.getLogger().log(Level.FINE, "Geometry type check; {0} ", sqlStatement);
                statement = cx.createStatement();
                result = statement.executeQuery(sqlStatement);
                if (result.next()) {
                    gType = result.getString(1);
                }
            } finally {
                JDBCFeatureStoreUtilities.closeSafe(featurestore.getLogger(),null,statement,result);
            }
        }

        // decode the type
        Class geometryClass = null;
        if (gType != null) {
            geometryClass = (Class) TYPENAME_TO_CLASS.get(gType.replaceFirst("ST_", "").toUpperCase());
        }
        if (geometryClass == null) {
            geometryClass = Geometry.class;
        }

        atb.setBinding(geometryClass);
    }
    
    
    @Override
    public Integer getGeometrySRID(String schemaName, final String tableName, final String columnName,
            final Connection cx) throws SQLException{

        // first attempt, try with the geometry metadata
        Statement statement = null;
        ResultSet result = null;
        Integer srid = null;
        try {
            final StringBuilder sb = new StringBuilder("SELECT SRID FROM GEOMETRY_COLUMNS WHERE ");
            if (schemaName != null && !schemaName.isEmpty()) {
                sb.append("F_TABLE_SCHEMA = '").append(schemaName).append("' ");
                sb.append(" AND ");
            }
            sb.append("F_TABLE_NAME = '").append(tableName).append("' ");
            sb.append("AND F_GEOMETRY_COLUMN = '").append(columnName).append('\'');
            final String sqlStatement = sb.toString();

            featurestore.getLogger().log(Level.FINE, "Geometry type check; {0} ", sqlStatement);
            statement = cx.createStatement();
            result = statement.executeQuery(sqlStatement);

            if (result.next()) {
                srid = result.getInt(1);
            }
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(featurestore.getLogger(), null,statement,result);
        }

        return srid;
    }

    
    @Override
    public CoordinateReferenceSystem createCRS(int srid, Connection cx) throws SQLException {
        CoordinateReferenceSystem crs = CRS_CACHE.get(srid);
        if (crs == null) {
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
    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, 
        String column, GeometryFactory factory) throws IOException, SQLException {
        
        switch((GeometryEncoding)descriptor.getType().getUserData().get(GEOM_ENCODING)){
            case HEXEWKB:
                return hexewkbReader.parse(rs.getString(column));
            case WKB:
                WKBAttributeIO reader = wkbReader.get();
                if (reader == null) {
                    reader = new WKBAttributeIO(factory);
                    wkbReader.set(reader);
                }
                return (Geometry) reader.read(rs, column);
            default:
                throw new IllegalStateException("Can not decode geometry not knowing it's encoding.");
        }
    }

    @Override
    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, 
        int column, GeometryFactory factory) throws IOException, SQLException {
        
        switch((GeometryEncoding)descriptor.getType().getUserData().get(GEOM_ENCODING)){
            case HEXEWKB:
                return hexewkbReader.parse(rs.getString(column));
            case WKB:
                WKBAttributeIO reader = wkbReader.get();
                if (reader == null) {
                    reader = new WKBAttributeIO(factory);
                    wkbReader.set(reader);
                }
                return (Geometry) reader.read(rs, column);
            default:
                throw new IllegalStateException("Can not decode geometry not knowing it's encoding.");
        }
    }
    
    public CoordinateReferenceSystem decodeCRS(final int srid, final Connection cx) throws SQLException{
        CoordinateReferenceSystem crs = CRS_CACHE.get(srid);
        if (crs == null) {
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
    
}
