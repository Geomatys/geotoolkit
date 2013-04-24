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
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SQLQueryBuilder {
    
    private final DefaultJDBCFeatureStore store;
    private final String databaseSchema;
    private final SQLDialect dialect;

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
        for (PropertyDescriptor att : featureType.getDescriptors()) {
            if (att instanceof GeometryDescriptor) {
                //encode as geometry
                encodeGeometryColumn((GeometryDescriptor) att, sql, query.getHints());
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

        sql.append(" FROM ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, featureType.getName().getLocalPart());

        // filtering
        final Filter filter = query.getFilter();
        if (filter != null && !Filter.INCLUDE.equals(filter)) {
            //encode filter
            sql.append(" WHERE ");
            sql.append(dialect.encodeFilter(filter));
        }

        // sorting
        encodeSortBy(featureType, query.getSortBy(), key, sql);

        // finally encode limit/offset, if necessary
        dialect.encodeLimitOffset(sql, query.getMaxFeatures(), query.getStartIndex());

        return sql.toString();
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
            sql.append(" ");
            sql.append(dialect.encodeFilter(filter));
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
            sql.append(" ");
            sql.append(dialect.encodeFilter(filter));
        }

        return sql.toString();
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // OTHER UTILS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

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
