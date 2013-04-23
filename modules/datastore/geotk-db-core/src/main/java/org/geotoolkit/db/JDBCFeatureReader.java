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
package org.geotoolkit.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import static org.geotoolkit.db.JDBCFeatureStoreUtilities.*;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 * JDBC Feature reader, both simple and complexe features.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JDBCFeatureReader implements FeatureReader<FeatureType, Feature> {

    protected final FeatureType type;
    protected final DefaultJDBCFeatureStore store;
    protected final PrimaryKey pkey;
    protected final String sql;
    protected final String fidBase;
    
    /**
     * statement,result set that is being worked from.
     */
    protected final Statement st;
    protected final ResultSet rs;
    protected final Connection cx;
    /**
     * flag indicating if the iterator has another feature
     */
    protected Boolean next;
    
    public JDBCFeatureReader(final String sql, final FeatureType type, 
            final DefaultJDBCFeatureStore store) throws SQLException,DataStoreException {
        final Name typeName = type.getName();
        final String name = typeName.getLocalPart();
        this.fidBase = name + ".";
        
        this.type = type;
        this.store = store;
        this.pkey = store.getDatabaseModel().getPrimaryKey(typeName);
        
        this.sql = sql;        
        this.cx = store.getDataSource().getConnection();
        this.st = cx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        this.rs = this.st.executeQuery(sql);
    }
    
    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        try {
            return new JDBCComplexFeature(store, rs, type, 
                    new DefaultFeatureId(fidBase + pkey.encodeFID(rs)));
        } catch (SQLException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } finally{
            next = null;
        }
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if (next == null) {
            try {
                next = Boolean.valueOf(rs.next());
            } catch (SQLException e) {
                throw new FeatureStoreRuntimeException(e);
            }
        }
        return next.booleanValue();
    }

    @Override
    public void close() {
        closeSafe(store.getLogger(),cx,st,rs);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
