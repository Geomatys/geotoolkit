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
package org.geotoolkit.data.postgis;

import com.vividsolutions.jts.geom.Envelope;
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
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.data.jdbc.FilterToSQL;
import org.geotoolkit.data.postgis.ewkb.JtsBinaryParser;
import org.geotoolkit.data.postgis.wkb.WKBAttributeIO;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.dialect.AbstractSQLDialect;
import org.geotoolkit.jdbc.reverse.DataBaseModel;
import org.geotoolkit.jdbc.reverse.SchemaMetaModel;
import org.geotoolkit.jdbc.reverse.TableMetaModel;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.postgresql.jdbc4.Jdbc4ResultSetMetaData;

import static org.geotoolkit.jdbc.AbstractJDBCDataStore.*;


public class PostGISDialect extends AbstractSQLDialect {

    private static final String GEOM_ENCODING = "Encoding";
    private static enum GeometryEncoding{
        HEXEWKB,
        WKB,
        WKT,
        UNKNOWNED
    }

    private static final Map<String, Class> TYPE_TO_CLASS_MAP = new HashMap<String, Class>();
    private static final Map<Class, String> CLASS_TO_TYPE_MAP = new HashMap<Class, String>();

    static{
        TYPE_TO_CLASS_MAP.put("GEOMETRY", Geometry.class);
        TYPE_TO_CLASS_MAP.put("POINT", Point.class);
        TYPE_TO_CLASS_MAP.put("POINTM", Point.class);
        TYPE_TO_CLASS_MAP.put("LINESTRING", LineString.class);
        TYPE_TO_CLASS_MAP.put("LINESTRINGM", LineString.class);
        TYPE_TO_CLASS_MAP.put("POLYGON", Polygon.class);
        TYPE_TO_CLASS_MAP.put("POLYGONM", Polygon.class);
        TYPE_TO_CLASS_MAP.put("MULTIPOINT", MultiPoint.class);
        TYPE_TO_CLASS_MAP.put("MULTIPOINTM", MultiPoint.class);
        TYPE_TO_CLASS_MAP.put("MULTILINESTRING", MultiLineString.class);
        TYPE_TO_CLASS_MAP.put("MULTILINESTRINGM", MultiLineString.class);
        TYPE_TO_CLASS_MAP.put("MULTIPOLYGON", MultiPolygon.class);
        TYPE_TO_CLASS_MAP.put("MULTIPOLYGONM", MultiPolygon.class);
        TYPE_TO_CLASS_MAP.put("GEOMETRYCOLLECTION", GeometryCollection.class);
        TYPE_TO_CLASS_MAP.put("GEOMETRYCOLLECTIONM", GeometryCollection.class);

        CLASS_TO_TYPE_MAP.put(Geometry.class, "GEOMETRY");
        CLASS_TO_TYPE_MAP.put(Point.class, "POINT");
        CLASS_TO_TYPE_MAP.put(LineString.class, "LINESTRING");
        CLASS_TO_TYPE_MAP.put(Polygon.class, "POLYGON");
        CLASS_TO_TYPE_MAP.put(MultiPoint.class, "MULTIPOINT");
        CLASS_TO_TYPE_MAP.put(MultiLineString.class, "MULTILINESTRING");
        CLASS_TO_TYPE_MAP.put(MultiPolygon.class, "MULTIPOLYGON");
        CLASS_TO_TYPE_MAP.put(GeometryCollection.class, "GEOMETRYCOLLECTION");
    }

    private final ThreadLocal<WKBAttributeIO> wkbReader = new ThreadLocal<WKBAttributeIO>();
    private final JtsBinaryParser hexewkbReader = new JtsBinaryParser(){

        @Override
        public Geometry parse(String value) {
            final Geometry geom =  super.parse(value);

            final int srid = geom.getSRID();
            if(srid >= 0){
                try {
                    //set the real crs
                    geom.setUserData(createCRS(geom.getSRID(), null));
                } catch (SQLException ex) {
                    dataStore.getLogger().log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }
            return geom;
        }

    };
    private boolean looseBBOXEnabled = false;
    private boolean estimatedExtentsEnabled = false;

    public PostGISDialect(final JDBCDataStore dataStore) {
        super(dataStore);
        //register the base mapping
        initBaseClassToSqlMappings(classToSqlTypeMappings);
        initBaseSqlTypeNameToClassMappings(sqlTypeNameToClassMappings);
        initBaseSqlTypeToClassMappings(sqlTypeToClassMappings);
        initBaseSqlTypeToSqlTypeNameOverrides(sqlTypeToSqlTypeNameOverrides);

        // jdbc metadata for geom columns reports DATA_TYPE=1111=Types.OTHER
        classToSqlTypeMappings.put(Geometry.class, Types.OTHER);

        sqlTypeNameToClassMappings.put("geometry", Geometry.class);

        sqlTypeToSqlTypeNameOverrides.put(Types.VARCHAR, "VARCHAR");
        sqlTypeToSqlTypeNameOverrides.put(Types.BOOLEAN, "BOOL");

    }

    public boolean isLooseBBOXEnabled(){
        return looseBBOXEnabled;
    }

    public void setLooseBBOXEnabled(final boolean looseBBOXEnabled){
        this.looseBBOXEnabled = looseBBOXEnabled;
    }

    @Override
    public boolean includeTable(final String schemaName, final String tableName, final Connection cx)
                                throws SQLException{
        if (tableName.equals("geometry_columns")) {
            //table
            return false;
        } else if (tableName.startsWith("spatial_ref_sys")) {
            //table
            return false;
        } else if (tableName.startsWith("geography_columns")) {
            //view
            return false;
        }

        // others?
        return true;
    }

    @Override
    public Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
            final String column, final GeometryFactory factory, final Connection cx)
            throws IOException, SQLException{

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
    public Geometry decodeGeometryValue(final GeometryDescriptor descriptor, final ResultSet rs,
            final int column, final GeometryFactory factory, final Connection cx)
            throws IOException, SQLException{

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
    public void encodeGeometryColumn(final GeometryDescriptor gatt, final int srid,
                                     final StringBuilder sql,final Hints hints){
        
        double res = 0;
        if(hints != null){
            double[] ress = (double[]) hints.get(JDBCDataStore.RESAMPLING);
            if(ress != null){
                res = Math.min(ress[0], ress[1]);
            }
        }
        
        final CoordinateReferenceSystem crs = gatt.getCoordinateReferenceSystem();
        final int dimensions = (crs == null) ? 2 : crs.getCoordinateSystem().getDimension();
        sql.append("encode(");
        
        if(res > 0){
            if (dimensions > 2) {
                sql.append("ST_AsEWKB(ST_simplify(");
                encodeColumnName(gatt.getLocalName(), sql);
                sql.append(",").append(res).append(")");
            } else {
                sql.append("ST_AsBinary(ST_simplify(");
                encodeColumnName(gatt.getLocalName(), sql);
                sql.append(",").append(res).append(")");
            }
            sql.append(") ");
        }else{
            if (dimensions > 2) {
                sql.append("ST_AsEWKB(");
                encodeColumnName(gatt.getLocalName(), sql);
            } else {
                sql.append("ST_AsBinary(");
                encodeColumnName(gatt.getLocalName(), sql);
            }
            sql.append(") ");
        }
        
        sql.append(",'base64')");
        
    }

    @Override
    public void encodeGeometryEnvelope(final String tableName, final String geometryColumn,
            final StringBuilder sql){
        if (estimatedExtentsEnabled) {
            sql.append("ST_Estimated_Extent('");
            sql.append(tableName).append("','");
            sql.append(geometryColumn).append("'))));");
        } else {
            sql.append("ST_AsText(ST_Force_2D(Envelope(ST_Extent(\"");
            sql.append(geometryColumn);
            sql.append("\"::geometry))))");
        }
    }

    @Override
    public Envelope decodeGeometryEnvelope(final ResultSet rs, final int column,
            final Connection cx) throws SQLException, IOException{
        try {
            final String envelope = rs.getString(column);
            if (envelope != null) {
                return new WKTReader().read(envelope).getEnvelopeInternal();
            } else {
                // empty one
                final Envelope env = new Envelope();
                env.init(0, 0, 0, 0);
                return env;
            }
        } catch (ParseException e) {
            throw (IOException) new IOException(
                    "Error occurred parsing the bounds WKT").initCause(e);
        }
    }

    @Override
    public void buildMapping(final AttributeTypeBuilder atb, final Connection cx,
            final String typeName, final int datatype, final String schemaName,
            final String tableName, final String columnName) throws SQLException {

        if (!typeName.equals("geometry")) {
            //not a geometry, fallback on type mappings
            super.buildMapping(atb, cx, typeName, datatype,
                    schemaName, tableName,columnName);
            return;
        }

        String gType = null;
        if(tableName == null || tableName.isEmpty()){
            //this column informations seems to come from a custom sql query
            //the result will be the natural encoding of postgis which is hexadecimal EWKB
            atb.addUserData(GEOM_ENCODING, GeometryEncoding.HEXEWKB);

        }else{
            //this column informations comes from a real table
            atb.addUserData(GEOM_ENCODING, GeometryEncoding.WKB);

            //first attempt, try with the geometry metadata
            Statement statement = null;
            ResultSet result = null;
            try {
                final StringBuilder sb = new StringBuilder("SELECT TYPE FROM GEOMETRY_COLUMNS WHERE ");
                sb.append("F_TABLE_SCHEMA = '").append(schemaName).append("' ");
                sb.append("AND F_TABLE_NAME = '").append(tableName).append("' ");
                sb.append("AND F_GEOMETRY_COLUMN = '").append(columnName).append('\'');
                final String sqlStatement = sb.toString();
                LOGGER.log(Level.FINE, "Geometry type check; {0} ", sqlStatement);
                statement = cx.createStatement();
                result = statement.executeQuery(sqlStatement);
                if (result.next()) {
                    gType = result.getString(1);
                }
            } finally {
                dataStore.closeSafe(result);
                dataStore.closeSafe(statement);
            }
        }

        // decode the type
        Class geometryClass = (Class) TYPE_TO_CLASS_MAP.get(gType);
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
            if (schemaName == null) {
                schemaName = "public";
            }
            final StringBuilder sb = new StringBuilder("SELECT SRID FROM GEOMETRY_COLUMNS WHERE ");
            sb.append("F_TABLE_SCHEMA = '").append(schemaName).append("' ");
            sb.append("AND F_TABLE_NAME = '").append(tableName).append("' ");
            sb.append("AND F_GEOMETRY_COLUMN = '").append(columnName).append('\'');
            final String sqlStatement = sb.toString();

            LOGGER.log(Level.FINE, "Geometry type check; {0} ", sqlStatement);
            statement = cx.createStatement();
            result = statement.executeQuery(sqlStatement);

            if (result.next()) {
                srid = result.getInt(1);
            }
        } finally {
            dataStore.closeSafe(result);
            dataStore.closeSafe(statement);
        }

        // TODO: implement inference from the first feature
        // try asking the first feature for its srid
        // sql = new StringBuilder();
        // sql.append("SELECT SRID(\"");
        // sql.append(geometryColumnName);
        // sql.append("\") FROM \"");
        // if (schemaEnabled && dbSchema != null && dbSchema.length() > 0) {
        // sql.append(dbSchema);
        // sql.append("\".\"");
        // }
        // sql.append(tableName);
        // sql.append("\" LIMIT 1");
        // sqlStatement = sql.toString();
        // result = statement.executeQuery(sqlStatement);
        // if (result.next()) {
        // int retSrid = result.getInt(1);
        // JDBCUtils.close(statement);
        // return retSrid;
        // }

        return srid;
    }

    @Override
    public String getSequenceForColumn(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException{
        final Statement st = cx.createStatement();
        try {
            // pg_get_serial_sequence oddity: table name needs to be
            // escaped with "", whilst column name, doesn't...
            final StringBuilder sb = new StringBuilder("SELECT pg_get_serial_sequence('\"");
            if (schemaName != null && !"".equals(schemaName))
                sb.append(schemaName).append("\".\"");
            sb.append(tableName).append("\"', '").append(columnName).append("')");
            final String sql = sb.toString();
            dataStore.getLogger().fine(sql);
            final ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getString(1);
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }

        return null;
    }

    @Override
    public Object getNextSequenceValue(final String schemaName, final String sequenceName,
            final Connection cx) throws SQLException{
        final Statement st = cx.createStatement();
        try {
            final String sql = "SELECT nextval('" + sequenceName + "')";

            dataStore.getLogger().fine(sql);
            final ResultSet rs = st.executeQuery(sql);
            try {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            } finally {
                dataStore.closeSafe(rs);
            }
        } finally {
            dataStore.closeSafe(st);
        }

        return null;
    }

    @Override
    public Object getNextAutoGeneratedValue(final String schemaName, final String tableName,
            final String columnName, final Connection cx) throws SQLException{
        return null;

        // the code to grab the current sequence value is here,
        // but it will work only _after_ the insert occurred

        // Statement st = cx.createStatement();
        // try {
        // String sql = "SELECT currval(pg_get_serial_sequence('" + tableName +
        // "', '" + columnName + "'))";
        //
        // dataStore.getLogger().fine( sql);
        // ResultSet rs = st.executeQuery( sql);
        // try {
        // if ( rs.next() ) {
        // return rs.getLong(1);
        // }
        // } finally {
        // dataStore.closeSafe(rs);
        // }
        // }
        // finally {
        // dataStore.closeSafe(st);
        // }
        //
        // return null;
    }

    @Override
    public String getGeometryTypeName(final Integer type) {
        return "geometry";
    }

    @Override
    public void encodePrimaryKey(final Class binding, final String sqlType, final StringBuilder sql) {
        if(Integer.class.isAssignableFrom(binding) || Short.class.isAssignableFrom(binding)){
            sql.append(" SERIAL ");
        }else if(Long.class.isAssignableFrom(binding)){
            sql.append(" BIGSERIAL ");
        }else{
             sql.append(' ').append(sqlType).append(' ');
        }
        sql.append("PRIMARY KEY");
    }

    /**
     * Creates GEOMETRY_COLUMN registrations and spatial indexes for all
     * geometry columns
     */
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
                    if (gd.getUserData().get(JDBCDataStore.JDBC_NATIVE_SRID) != null) {
                        srid = (Integer) gd.getUserData().get(
                                JDBCDataStore.JDBC_NATIVE_SRID);
                    } else if (gd.getCoordinateReferenceSystem() != null) {
                        try {
                            final Integer result = IdentifiedObjects.lookupEpsgCode(gd.getCoordinateReferenceSystem(), true);
                            if (result != null) {
                                srid = result;
                            }
                        } catch (FactoryException e) {
                            LOGGER.log(Level.FINE, "Error looking up the "
                                    + "epsg code for metadata "
                                    + "insertion, assuming -1", e);
                        }
                    }

                    // assume 2 dimensions, but ease future customisation
                    final int dimensions = 2;

                    // grab the geometry type
                    String geomType = CLASS_TO_TYPE_MAP.get(gd.getType().getBinding());
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

                    LOGGER.fine( sql );
                    st.execute( sql );

                    sb = new StringBuilder("INSERT INTO GEOMETRY_COLUMNS VALUES ('',");
                    sb.append('\'').append(schemaName).append("',");
                    sb.append('\'').append(tableName).append("',");
                    sb.append('\'').append(gd.getLocalName()).append("',");
                    sb.append(dimensions).append(',');
                    sb.append(srid).append(',');
                    sb.append('\'').append(geomType).append("')");
                    sql = sb.toString();
                    LOGGER.fine( sql );
                    st.execute( sql );

                    // add srid checks
                    if (srid > -1) {
                        sb = new StringBuilder("ALTER TABLE \"").append(tableName).append('"');
                        sb.append(" ADD CONSTRAINT \"enforce_srid_");
                        sb.append(gd.getLocalName()).append('"');
                        sb.append(" CHECK (SRID(");
                        sb.append('"').append(gd.getLocalName()).append('"');
                        sb.append(") = ").append(srid).append(')');
                        sql = sb.toString();
                        LOGGER.fine( sql );
                        st.execute(sql);
                    }

                    // add dimension checks

                    sb = new StringBuilder("ALTER TABLE \"").append(tableName).append('"');
                    sb.append(" ADD CONSTRAINT \"enforce_dims_");
                    sb.append(gd.getLocalName()).append('"');
                    sb.append(" CHECK (ndims(\"").append(gd.getLocalName()).append("\") = 2)");
                    sql = sb.toString();
                    LOGGER.fine(sql);
                    st.execute(sql);

                    // add geometry type checks
                    if (!geomType.equals("GEOMETRY")) {
                        sb = new StringBuilder("ALTER TABLE \"").append(tableName);
                        sb.append('"');
                        sb.append(" ADD CONSTRAINT \"enforce_geotype_");
                        sb.append(gd.getLocalName()).append('"');
                        sb.append(" CHECK (geometrytype(");
                        sb.append('"').append(gd.getLocalName()).append('"');
                        sb.append(") = '").append(geomType).append("'::text OR \"");
                        sb.append(gd.getLocalName()).append('"').append(" IS NULL)");
                        sql = sb.toString();
                        LOGGER.fine(sql);
                        st.execute(sql);
                    }

                    // add the spatial index
                    sb = new StringBuilder("CREATE INDEX \"spatial_").append(tableName);
                    sb.append('_').append(gd.getLocalName().toLowerCase()).append('"');
                    sb.append(" ON ");
                    sb.append('"').append(tableName).append('"');
                    sb.append(" USING GIST (");
                    sb.append('"').append(gd.getLocalName()).append('"').append(')');
                    sql = sb.toString();
                    LOGGER.fine(sql);
                    st.execute(sql);
                }
            }
            if(!cx.getAutoCommit()){
                cx.commit();
            }
        } finally {
            dataStore.closeSafe(st);
        }
    }

    @Override
    public void encodeGeometryValue(Geometry value, final int srid, final StringBuilder sql) throws IOException{
        if (value == null) {
            sql.append("NULL");
        } else {
            if (value instanceof LinearRing) {
                //postgis does not handle linear rings, convert to just a line string
                value = value.getFactory().createLineString(((LinearRing) value).getCoordinateSequence());
            }

            sql.append("GeomFromText('").append(value.toText()).append("', ").append(srid).append(")");
        }
    }

    @Override
    public FilterToSQL createFilterToSQL(){
        final PostgisFilterToSQL sql = new PostgisFilterToSQL(this);
        sql.setLooseBBOXEnabled(looseBBOXEnabled);
        return sql;
    }

    @Override
    public boolean isLimitOffsetSupported(){
        return true;
    }

    @Override
    public void applyLimitOffset(final StringBuilder sql, final int limit, final int offset){
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
    public CoordinateReferenceSystem createCRS(final int srid, final Connection cx) throws SQLException{
        try {
            return CRS.decode("EPSG:" + srid,true);
        } catch(Exception e) {
            if(LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Could not decode " + srid + " using the built-in EPSG database", e);
            }
            return null;
        }
    }

    @Override
    public void analyzeResult(final DataBaseModel model, final FeatureTypeBuilder ftb, 
            final ResultSet result) throws SQLException,DataStoreException{
        
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                
        final String namespace = ftb.getName().getNamespaceURI();
        
        //postgis jdbc driver provide more informations on properties
        //we recreate them from scratch
        ftb.getProperties().clear();
        
        final Jdbc4ResultSetMetaData metadata = (Jdbc4ResultSetMetaData)result.getMetaData();
        final int nbcol = metadata.getColumnCount();

        for(int i=1; i<=nbcol; i++){
            final String columnName = metadata.getColumnName(i);
            final String columnbaseName = metadata.getBaseColumnName(i);
            final String typeName = metadata.getColumnTypeName(i);
            final String schemaName = metadata.getBaseSchemaName(i);
            final String tableName = metadata.getBaseTableName(i);
            final int type = metadata.getColumnType(i);
            
            //search if we already have this minute
            PropertyDescriptor desc = null;
            final SchemaMetaModel schema = model.getSchemaMetaModel(schemaName);
            if(schema != null){
                final TableMetaModel table = schema.getTable(tableName);
                if(table != null){
                    desc = table.getSimpleType().getDescriptor(columnbaseName);
                    
                    //column name might have change
                    //and user data must be changed
                    adb.reset();
                    adb.copy(desc);
                    adb.setName(desc.getName().getNamespaceURI(), columnName);
                    
                    atb.reset();
                    atb.copy((AttributeType)desc.getType());                    
                    if(Geometry.class.isAssignableFrom(atb.getBinding())){
                        atb.addUserData(GEOM_ENCODING, GeometryEncoding.HEXEWKB);
                        adb.setType(atb.buildGeometryType());
                    }else{
                        adb.setType(atb.buildType());
                    }
                    
                    desc = adb.buildDescriptor();
                }
            }

            if(desc == null){
                //could not find the original type
                //this column must be calculated
                adb.reset();
                atb.reset();
                
                adb.setName(ensureGMLNS(namespace, columnName));
                adb.setMinOccurs(1);
                adb.setMaxOccurs(1);

                final int nullable = metadata.isNullable(i);
                adb.setNillable(nullable == ResultSetMetaData.columnNullable);


                atb.setName(ensureGMLNS(namespace, columnName));
                Connection cx = null;
                try {
                    cx = dataStore.getDataSource().getConnection();
                    buildMapping(atb, cx, typeName, type,
                            schemaName, tableName, columnName);
                } catch (SQLException e) {
                    throw new DataStoreException("Error occurred analyzing column : " + columnName, e);
                } finally {
                    dataStore.closeSafe(cx);
                }

                if(Geometry.class.isAssignableFrom(atb.getBinding())){
                    atb.addUserData(GEOM_ENCODING, GeometryEncoding.HEXEWKB);
                    adb.setType(atb.buildGeometryType());
                }else{
                    adb.setType(atb.buildType());
                }
                
                adb.findBestDefaultValue();
                desc = adb.buildDescriptor();
            }

            ftb.add(desc);
        }

    }
    
}
