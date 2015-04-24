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
package org.geotoolkit.db.postgres;

import java.sql.SQLException;
import java.util.Date;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.dialect.SQLQueryBuilder;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 * Extends SQLQueryBuilder, support versioning.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PostgresQueryBuilder extends SQLQueryBuilder{

    public PostgresQueryBuilder(DefaultJDBCFeatureStore store) {
        super(store);
    }

    @Override
    public String selectSQL(final FeatureType featureType, final Query query) throws SQLException, DataStoreException {
        Date vdate = query.getVersionDate();
        String vlabel = query.getVersionLabel();
        if(vdate==null && vlabel==null){
            //no versioning, fallback on default method
            return super.selectSQL(featureType, query);
        }
        
        //search version date
        final PostgresVersionControl versionControl;
        try{
            versionControl = (PostgresVersionControl) store.getVersioning(featureType.getName().toString());
            if(vlabel!=null){
                vdate = versionControl.getVersion(vlabel).getDate();
            }
        }catch(VersioningException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }
        
        
        final StringBuilder sql = new StringBuilder("SELECT ");

        final PrimaryKey key = store.getDatabaseModel().getPrimaryKey(featureType.getName().toString());

        // column names        
        encodeSelectColumnNames(sql, featureType, query.getHints());

        sql.append(" FROM ");
        dialect.encodeSchemaAndTableName(sql, databaseSchema, versionControl.getHSTableName());

        // filtering and version/time filter
        Filter filter = query.getFilter();
        final FilterFactory ff = store.getFilterFactory();
        Filter tempFilter = 
            ff.and(
                    ff.lessOrEqual(ff.property("HS_Begin"),ff.literal(vdate)),
                    ff.or(
                         ff.greater(ff.property("HS_End"), ff.literal(vdate)),
                         ff.isNull(ff.property("HS_End"))
                         )
                  );
        filter = ff.and(filter, tempFilter);
        filter = (Filter)filter.accept(new SimplifyingFilterVisitor(),null);
        
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
    
}
