/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.dialect;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.DataBaseModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.filter.visitor.FIDFixVisitor;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * SQL query builder, rely on dialect to build conform SQL queries.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class SQLQueryBuilder {
    
    protected final DefaultJDBCFeatureStore store;
    protected final String databaseSchema;
    protected final SQLDialect dialect;

    public SQLQueryBuilder(DefaultJDBCFeatureStore store) {
        this.store = store;
        this.databaseSchema = store.getDatabaseSchema();
        this.dialect = store.getDialect();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // STATEMENT QURIES ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Generates a 'SELECT p1, p2, ... FROM ... WHERE ...' statement.
     *
     * @param featureType
     *            the feature type that the query must return (may contain less
     *            attributes than the native one)
     * @param query
     *            the query to be run. The type name and property will be ignored, as they are
     *            supposed to have been already embedded into the provided feature type
     * @return String
     */
    public String selectSQL(final FeatureType featureType, final Query query) throws SQLException,DataStoreException {
        final StringBuilder sql = new StringBuilder("SELECT ");

        final PrimaryKey key = store.getDatabaseModel().getPrimaryKey(featureType.getName());

        // column names
        encodeSelectColumnNames(sql, featureType, query.getHints());

        sql.append(" FROM ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, featureType.getName().getLocalPart());

        // filtering
        final Filter filter = query.getFilter();
        if (!Filter.INCLUDE.equals(filter)) {
            //encode filter
            sql.append(" WHERE ");
            sql.append(dialect.encodeFilter(filter,featureType));
        }

        // sorting
        encodeSortBy(featureType, query.getSortBy(), key, sql);

        // finally encode limit/offset, if necessary
        dialect.encodeLimitOffset(sql, query.getMaxFeatures(), query.getStartIndex());

        return sql.toString();
    }

    protected void encodeSelectColumnNames(StringBuilder sql, FeatureType featureType, Hints hints){
        for (PropertyDescriptor att : featureType.getDescriptors()) {
            if (att instanceof GeometryDescriptor) {
                //encode as geometry
                encodeGeometryColumn((GeometryDescriptor) att, sql, hints);
                //alias it to be the name of the original geometry
                dialect.encodeColumnAlias(sql, att.getName().getLocalPart());
            } else if (att instanceof AssociationDescriptor) {
                final String str = att.getName().getLocalPart();
                final int sep = str.indexOf(DataBaseModel.ASSOCIATION_SEPARATOR);
                if(sep >= 0){
                    //this is a backreference property, there is no real field for this one
                    continue;
                }else{
                     dialect.encodeColumnName(sql, str);
                }
               
            } else {
                dialect.encodeColumnName(sql, att.getName().getLocalPart());
            }
            sql.append(',');
        }
        sql.setLength(sql.length() - 1);
    }
    
    
    /**
     * Generates a 'INSERT INFO' sql statement.
     * @throws IOException
     */
    public String insertSQL(final FeatureType featureType, final Feature feature,
                               final Object[] keyValues, final Connection cx) throws DataStoreException{
        final PrimaryKey key = store.getDatabaseModel().getPrimaryKey(featureType.getName());
        final List<ColumnMetaModel> keyColumns = key.getColumns();

        final StringBuilder sqlType = new StringBuilder();
        sqlType.append("INSERT INTO ");
        dialect.encodeSchemaAndTableName(sqlType, store.getDatabaseSchema(), featureType.getName().getLocalPart());
        sqlType.append(" ( ");

        final StringBuilder sqlValues = new StringBuilder();
        sqlValues.append(" ) VALUES ( ");

        //add all fields
        fields :
        for(PropertyDescriptor desc : featureType.getDescriptors()){
            final String attName = desc.getName().getLocalPart();
            final Class binding = desc.getType().getBinding();
            final Object value = feature.getProperty(attName).getValue();

            //remove the primary key attribut that wil be auto-generated and null
            for (ColumnMetaModel col : keyColumns) {
                if(col.getName().equals(attName)){
                    //only include if its non auto generating and not null
                    if (col.getType() == ColumnMetaModel.Type.AUTO) {
                        if(value == null ||
                           (value instanceof Number && ((Number)value).intValue() <=0) ||
                           (value instanceof String && ((String)value).isEmpty()) ){
                        continue fields;
                        }
                    }
                }
            }

            //the column
            dialect.encodeColumnName(sqlType, attName);

            //the value
            if (value == null) {
                //maybe it's an auto generated value from a sequence
                boolean found = false;
                for (int k=0; k<keyColumns.size(); k++) {
                    if(keyColumns.get(k).getName().equals(attName)){
                        dialect.encodeValue(sqlValues, keyValues[k], keyColumns.get(k).getJavaType());
                        found = true;
                        break;
                    }
                }

                if(!found){
                    if (!desc.isNillable()) {
                        //TODO: throw an exception
                    }
                    sqlValues.append("null");
                }
            } else {
                if (Geometry.class.isAssignableFrom(binding)) {
                    final Geometry g = (Geometry) value;
                    final int srid = getGeometrySRID(g, desc);
                    dialect.encodeGeometryValue(sqlValues, g, srid);
                } else {
                    dialect.encodeValue(sqlValues, value, binding);
                }
            }

            sqlType.append(',');
            sqlValues.append(',');
        }

        sqlType.setLength(sqlType.length() - 1);
        sqlValues.setLength(sqlValues.length() - 1);
        sqlValues.append(")");

        return sqlType.toString() + sqlValues.toString();
    }

    public String insertSQL(final FeatureType featureType, final Collection<? extends Feature> features,
                               final Object[] keyValues, final Connection cx) throws DataStoreException{
        final PrimaryKey key = store.getDatabaseModel().getPrimaryKey(featureType.getName());
        final List<ColumnMetaModel> keyColumns = key.getColumns();

        final StringBuilder sqlType = new StringBuilder();
        sqlType.append("INSERT INTO ");
        dialect.encodeSchemaAndTableName(sqlType, databaseSchema, featureType.getName().getLocalPart());
        sqlType.append(" ( ");

        //add all fields
        fields :
        for(PropertyDescriptor desc : featureType.getDescriptors()){
            final String attName = desc.getName().getLocalPart();

            //remove the primary key attribut that wil be auto-generated and null
            for (ColumnMetaModel col : keyColumns) {
                if(col.getName().equals(attName)){
                    //only include if its non auto generating and not null
                    if (col.getType() == ColumnMetaModel.Type.AUTO) {
                        continue fields;
                    }
                }
            }

            //the column
            dialect.encodeColumnName(sqlType,attName);
            sqlType.append(',');
        }

        sqlType.setLength(sqlType.length() - 1);
        sqlType.append(" ) ");


        final StringBuilder sqlValues = new StringBuilder();
        sqlValues.append(" VALUES ");

        //add all fields
        for(Feature feature : features){

            sqlValues.append(" (");
            fields :
            for(PropertyDescriptor desc : featureType.getDescriptors()){
                final String attName = desc.getName().getLocalPart();
                final Class binding = desc.getType().getBinding();
                final Object value = feature.getProperty(attName).getValue();

                //remove the primary key attribut that wil be auto-generated and null
                for (ColumnMetaModel col : keyColumns) {
                    if(col.getName().equals(attName)){
                        //only include if its non auto generating and not null
                        if (col.getType() == ColumnMetaModel.Type.AUTO) {
                            continue fields;
                        }
                    }
                }

                //the value
                if (value == null) {
                    //maybe it's an auto generated value from a sequence
                    boolean found = false;
                    for (int k=0; k<keyColumns.size(); k++) {
                        if(keyColumns.get(k).getName().equals(attName)){
                            dialect.encodeValue(sqlValues, keyValues[k], keyColumns.get(k).getJavaType());
                            found = true;
                            break;
                        }
                    }

                    if(!found){
                        if (!desc.isNillable()) {
                            //TODO: throw an exception
                        }
                        sqlValues.append("null");
                    }
                } else {
                    if (Geometry.class.isAssignableFrom(binding)) {
                        final Geometry g = (Geometry) value;
                        final int srid = getGeometrySRID(g, desc);
                        dialect.encodeGeometryValue(sqlValues, g, srid);
                    } else {
                        dialect.encodeValue(sqlValues, value, binding);
                    }
                }

                sqlValues.append(',');
            }
            sqlValues.setLength(sqlValues.length() - 1);
            sqlValues.append(" ),");
        }

        sqlValues.setLength(sqlValues.length() - 1);
        sqlValues.append(';');

        return sqlType.toString() + sqlValues.toString();
    }


    /**
     * Generates an 'UPDATE' sql statement.
     */
    public String updateSQL(final FeatureType featureType, final Map<AttributeDescriptor,Object> changes,
            Filter filter) throws DataStoreException, SQLException{
        final StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, featureType.getName().getLocalPart());

        sql.append(" SET ");

        for(final Map.Entry<AttributeDescriptor,Object> change : changes.entrySet()){
            final AttributeDescriptor attribut = change.getKey();
            final Object value = change.getValue();

            dialect.encodeColumnName(sql,attribut.getLocalName());
            sql.append('=');

            final Class binding = attribut.getType().getBinding();
            if (Geometry.class.isAssignableFrom(binding)) {
                    final Geometry g = (Geometry) value;
                    final int srid = getGeometrySRID(g, attribut);
                    dialect.encodeGeometryValue(sql, g, srid);
            } else {
                dialect.encodeValue(sql, value, binding);
            }

            sql.append(',');
        }

        sql.setLength(sql.length() - 1);
        sql.append(' ');

        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //replace any PropertyEqualsTo in true ID filters
            filter = (Filter) filter.accept(new FIDFixVisitor(), null);
            sql.append(" WHERE ");
            sql.append(dialect.encodeFilter(filter,featureType));
        }

        return sql.toString();
    }

    /**
     * Generates a 'DELETE FROM' sql statement.
     */
    public String deleteSQL(final FeatureType featureType, Filter filter) throws SQLException {
        final StringBuilder sql = new StringBuilder("DELETE FROM ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, featureType.getName().getLocalPart());

        //encode filter if needed
        if(filter != null && !Filter.INCLUDE.equals(filter)){
            //replace any PropertyEqualsTo in true ID filters
            filter = (Filter) filter.accept(new FIDFixVisitor(), null);
            sql.append(" WHERE ");
            sql.append(dialect.encodeFilter(filter,featureType));
        }

        return sql.toString();
    }

    /**
     * Generates a 'CREATE TABLE' sql statement.
     */
    public String createTableSQL(final FeatureType featureType, final Connection cx) throws SQLException {
        //figure out the names and types of the columns
        final String tableName = featureType.getName().getLocalPart();
        final List<PropertyDescriptor> descs = new ArrayList<PropertyDescriptor>(featureType.getDescriptors());
        final int size = descs.size();
        final String[] columnNames = new String[size];
        final Class[] classes = new Class[size];
        final boolean[] nillable = new boolean[size];
        final List<String> pkeyColumn = new ArrayList<String>();

        for (int i=0; i<size; i++) {
            final PropertyDescriptor desc = descs.get(i);
            columnNames[i] = desc.getName().getLocalPart();
            classes[i] = desc.getType().getBinding();
            nillable[i] = desc.getMinOccurs() <= 0 || desc.isNillable();

            if(FeatureTypeUtilities.isPartOfPrimaryKey(desc)){
                pkeyColumn.add(desc.getName().getLocalPart());
            }
        }

        final String[] sqlTypeNames = getSQLTypeNames(classes, cx);

        for (int i=0; i<sqlTypeNames.length; i++) {
            if (sqlTypeNames[i] == null) {
                throw new SQLException("Unable to map " + columnNames[i] + "( " + classes[i].getName() + ")");
            }
        }

        //build the create table sql -------------------------------------------
        final StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, tableName);
        sql.append(" ( ");

        if(pkeyColumn.isEmpty()){
            //we create a primary key, this will modify the geature type but
            //we don't have any other solution
            dialect.encodeColumnName(sql,"fid");
            dialect.encodePrimaryKey(sql, Integer.class,"INTEGER");
            sql.append(", ");
        }

        //normal attributes
        for (int i = 0; i < columnNames.length; i++) {
            final PropertyDescriptor att = featureType.getDescriptor(columnNames[i]);

            //the column name
            dialect.encodeColumnName(sql,columnNames[i]);
            sql.append(' ');

            if(pkeyColumn.contains(columnNames[i])){
                dialect.encodePrimaryKey(sql,att.getType().getBinding(), sqlTypeNames[i]);

            }else if (sqlTypeNames[i].toUpperCase().startsWith("VARCHAR")) {
                Integer length = findVarcharColumnLength(att);
                if (length == null || length < 0) {
                    length = 255;
                }
                
                dialect.encodeColumnType(sql, sqlTypeNames[i],length);
            } else {
                dialect.encodeColumnType(sql, sqlTypeNames[i], null);
            }

            //nullable
            if (nillable != null && !nillable[i]) {
                sql.append(" NOT NULL ");
            }

            //delegate to dialect to encode column postamble
            dialect.encodePostColumnCreateTable(sql, (AttributeDescriptor)att);

            //sql.append(sqlTypeNames[i]);
            if (i < (sqlTypeNames.length - 1)) {
                sql.append(", ");
            }
        }

        sql.append(" ) ");

        //encode anything post create table
        dialect.encodePostCreateTable(sql, tableName);

        return sql.toString();
    }

    /**
     * Generates a 'ALTER TABLE . ADD COLUMN ' sql statement.
     */
    public String alterTableAddColumnSQL(final FeatureType featureType, final PropertyDescriptor desc, final Connection cx) throws SQLException{
        final String tableName = featureType.getName().getLocalPart();
        final boolean nillable = desc.getMinOccurs() <= 0 || desc.isNillable();
        final Class clazz = desc.getType().getBinding();
        final String sqlTypeName = getSQLTypeNames(new Class[]{clazz}, cx)[0];

        final StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, tableName);
        dialect.encodeTableName(sql,tableName);
        sql.append(" ADD COLUMN ");
        dialect.encodeColumnName(sql, desc.getName().getLocalPart());
        sql.append(' ');

        //encode type
        if (sqlTypeName.toUpperCase().startsWith("VARCHAR")) {
            //sql type name
            //JD: some sql dialects require strings / varchars to have an
            // associated size with them
            Integer length = findVarcharColumnLength((AttributeDescriptor)desc);
            if (length == null || length < 0) {
                length = 255;
            }
            dialect.encodeColumnType(sql, sqlTypeName,length);
        } else {
            dialect.encodeColumnType(sql, sqlTypeName,null);
        }

        //nullable
        if (!nillable) {
            sql.append(" NOT NULL ");
        }

        return sql.toString();
    }

    /**
     * Generates a 'ALTER TABLE . DROP COLUMN ' sql statement.
     */
    public String alterTableDropColumnSQL(final FeatureType featureType, final PropertyDescriptor desc, final Connection cx){
        final String tableName = featureType.getName().getLocalPart();
        final StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, tableName);
        sql.append(" DROP COLUMN ");
        dialect.encodeColumnName(sql,desc.getName().getLocalPart());
        return sql.toString();
    }

    /**
     * Generates a 'DROP TABLE' sql statement.
     */
    public String dropSQL(final FeatureType featureType){
        final StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, featureType.getName().getLocalPart());
        sql.append(";");
        return sql.toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // OTHER UTILS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static Integer findVarcharColumnLength(final PropertyDescriptor att) {
        for (final Filter r : att.getType().getRestrictions()) {
            if (r instanceof PropertyIsLessThanOrEqualTo) {
                final PropertyIsLessThanOrEqualTo c = (PropertyIsLessThanOrEqualTo) r;
                if (c.getExpression1() instanceof Function &&
                        ((Function) c.getExpression1()).getName().toLowerCase().endsWith("length")) {
                    if (c.getExpression2() instanceof Literal) {
                        final Integer length = c.getExpression2().evaluate(null, Integer.class);
                        if (length != null) {
                            return length;
                        }
                    }
                }
            }
        }

        return null;
    }
    
    private String[] getSQLTypeNames(final Class[] classes, final Connection cx) throws SQLException {
        //figure out what the sql types are corresponding to the feature type
        // attributes
        final String[] sqlTypeNames = new String[classes.length];

        for (int i = 0; i < classes.length; i++) {
            final Class clazz = classes[i];
            String sqlTypeName = dialect.getSQLType(clazz);
            sqlTypeNames[i] = sqlTypeName;

        }
        return sqlTypeNames;
    }
        
    /**
     * Encodes the sort-by portion of an sql query.
     * @param featureType
     * @param sort
     * @param key
     * @param sql
     * @throws IOException
     */
    public void encodeSortBy(final FeatureType featureType, final SortBy[] sort, final PrimaryKey key,
            final StringBuilder sql) throws DataStoreException {
        if ((sort != null) && (sort.length > 0)) {
            sql.append(" ORDER BY ");

            for (final SortBy sortBy : sort) {
                final String order;
                if (sortBy.getSortOrder() == SortOrder.DESCENDING) {
                    order = " DESC";
                } else {
                    order = " ASC";
                }

                if (SortBy.NATURAL_ORDER.equals(sortBy) || SortBy.REVERSE_ORDER.equals(sortBy)) {
                    if (key.isNull()) {
                        throw new DataStoreException("Cannot do natural order without a primary key");
                    }

                    for (ColumnMetaModel col : key.getColumns()) {
                        dialect.encodeColumnName(sql, col.getName());
                        sql.append(order);
                        sql.append(',');
                    }
                } else {
                    dialect.encodeColumnName(sql, getPropertyName(featureType, sortBy.getPropertyName()) );
                    sql.append(order);
                    sql.append(',');
                }
            }

            sql.setLength(sql.length() - 1);
        }
    }

    /**
     * Encoding a geometry column with respect to hints
     * Supported Hints are provided by {@link SQLDialect#addSupportedHints(Set)}
     *
     * @param gatt
     * @param sql
     * @param hints , may be null
     */
    public void encodeGeometryColumn(final GeometryDescriptor gatt, final StringBuilder sql,
                                        final Hints hints){
        final int srid = getDescriptorSRID(gatt);
        dialect.encodeGeometryColumn(sql, gatt, srid, hints);
    }

    /**
     * Looks up the geometry srs by trying a number of heuristics. Returns -1 if all attempts
     * at guessing the srid failed.
     */
    public static int getGeometrySRID(final Geometry g, final PropertyDescriptor descriptor) {
        int srid = getDescriptorSRID(descriptor);

        if (g == null) {
            return srid;
        }

        // check for srid in the jts geometry then
        if (srid <= 0 && g.getSRID() > 0) {
            srid = g.getSRID();
        }

        // check if the geometry has anything
        if (srid <= 0) {
            // check for crs object
            final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) g.getUserData();

            if (crs != null) {
                try {
                    final Integer candidate = IdentifiedObjects.lookupEpsgCode(crs, false);
                    if (candidate != null) {
                        srid = candidate;
                    }
                } catch (Exception e) {
                    // ok, we tried...
                }
            }
        }

        return srid;
    }

    /**
     * Extracts the eventual native SRID user property from the descriptor,
     * returns -1 if not found
     * @param descriptor
     */
    public static int getDescriptorSRID(final PropertyDescriptor descriptor) {
        // check if we have stored the native srid in the descriptor (we should)
        if (descriptor.getUserData().get(JDBCFeatureStore.JDBC_NATIVE_SRID) != null) {
            return (Integer) descriptor.getUserData().get(JDBCFeatureStore.JDBC_NATIVE_SRID);
        }else{
            return -1;
        }
    }

    /**
     * Helper method for executing a property name against a feature type.
     * <p>
     * This method will fall back on {@link PropertyName#getPropertyName()} if
     * it does not evaulate against the feature type.
     * </p>
     */
    public static String getPropertyName(final FeatureType featureType, final PropertyName propertyName) {
        final PropertyDescriptor att = (PropertyDescriptor) propertyName.evaluate(featureType);

        if (att != null) {
            return att.getName().getLocalPart();
        }

        return propertyName.getPropertyName();
    }
    
}
