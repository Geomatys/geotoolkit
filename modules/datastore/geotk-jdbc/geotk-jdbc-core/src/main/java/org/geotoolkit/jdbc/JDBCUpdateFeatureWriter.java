/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.jdbc.fid.PrimaryKey;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;

public class JDBCUpdateFeatureWriter extends JDBCFeatureReader implements
        FeatureWriter<SimpleFeatureType, SimpleFeature> {

    ResultSetFeature last;
    
    public JDBCUpdateFeatureWriter(String sql, Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, PrimaryKey pkey, Hints hints)
            throws SQLException, IOException,DataStoreException {
        
        super(sql, cx, store, groupName, type, pkey, hints);
        last = new ResultSetFeature( rs, cx );
    }
    
    public JDBCUpdateFeatureWriter(PreparedStatement ps, Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, PrimaryKey pkey, Hints hints)
            throws SQLException, IOException, DataStoreException {
        
        super(ps, cx, store, groupName, type, pkey, hints);
        last = new ResultSetFeature( rs, ps.getConnection());
    }

    @Override
    public SimpleFeature next() throws DataStoreRuntimeException {
        
        ensureNext();
        
        try {
            last.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
     
        //reset next flag
        next = null;
        
        return last;
    }
    
    @Override
    public void remove() throws DataStoreRuntimeException {
        final Filter filter = dataStore.getFilterFactory().id(
                Collections.singleton(last.getIdentifier()));
        try {
            dataStore.delete(featureType, filter, st.getConnection());
        } catch (SQLException e) {
            throw new DataStoreRuntimeException(e);
        } catch (IOException e) {
            throw new DataStoreRuntimeException(e);
        }
    }

    @Override
    public void write() throws DataStoreRuntimeException {
        try {
            //figure out what the fid is
            final PrimaryKey key = dataStore.getPrimaryKey(featureType.getName());
            final String fid = PrimaryKey.encodeFID(key, rs);

            final Id filter = dataStore.getFilterFactory()
                                 .id(Collections.singleton(dataStore.getFilterFactory()
                                                                    .featureId(fid)));

            //figure out which attributes changed
            final Map<AttributeDescriptor,Object> changes = new HashMap<AttributeDescriptor, Object>();

            for (final AttributeDescriptor att : featureType.getAttributeDescriptors()) {
                final String attName = att.getLocalName();
                if (last.isDirrty(attName)) {
                    changes.put(att, last.getAttribute(attName));
                }
            }

            //do the write
            dataStore.update(featureType, changes, filter, st.getConnection());
        } catch (Exception e) {
            throw new DataStoreRuntimeException(e);
        }
    }

    @Override
    public void close() throws DataStoreRuntimeException {
        super.close();
        if ( last != null ) {
            last.close();
            last = null;    
        }
    }
}
