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
import java.sql.SQLException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.DataBaseModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
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
    private final String escape;

    public SQLQueryBuilder(DefaultJDBCFeatureStore store) {
        this.store = store;
        this.databaseSchema = store.getDatabaseSchema();
        this.dialect = store.getDialect();
        this.escape = dialect.getTableEscape();
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
        encodeSchemaAndTableName(featureType.getName().getLocalPart(), sql);

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
     * Encode schema and table name portion of an sql query.
     * @param tableName
     * @param sql 
     */
    public void encodeSchemaAndTableName(final String tableName, final StringBuilder sql) {
        if (databaseSchema != null && !databaseSchema.isEmpty()) {
            dialect.encodeSchemaName(sql, databaseSchema);
            sql.append('.');
        }
        dialect.encodeTableName(sql, tableName);
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
    public static int getGeometrySRID(final Geometry g, final AttributeDescriptor descriptor) {
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
    public static int getDescriptorSRID(final AttributeDescriptor descriptor) {
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
