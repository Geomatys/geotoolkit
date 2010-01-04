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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.jdbc.fid.PrimaryKey;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Id;

public class JDBCUpdateFeatureWriter extends JDBCFeatureReader implements
        FeatureWriter<SimpleFeatureType, SimpleFeature> {

    ResultSetFeature last;
    
    public JDBCUpdateFeatureWriter(String sql, Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, Hints hints)
            throws SQLException, IOException,DataStoreException {
        
        super(sql, cx, store, groupName, type, hints);
        last = new ResultSetFeature( rs, cx );
    }
    
    public JDBCUpdateFeatureWriter(PreparedStatement ps, Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, Hints hints)
            throws SQLException, IOException, DataStoreException {
        
        super(ps, cx, store, groupName, type, hints);
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
    
    public void remove() throws DataStoreRuntimeException {
        try {
            dataStore.delete(featureType, last.getID(), st.getConnection());
        } catch (SQLException e) {
            throw new DataStoreRuntimeException(e);
        } catch (IOException e) {
            throw new DataStoreRuntimeException(e);
        }
    }

    public void write() throws DataStoreRuntimeException {
        try {
            //figure out what the fid is
            PrimaryKey key = dataStore.getPrimaryKey(featureType);
            String fid = dataStore.encodeFID(key, rs);

            Id filter = dataStore.getFilterFactory()
                                 .id(Collections.singleton(dataStore.getFilterFactory()
                                                                    .featureId(fid)));

            //figure out which attributes changed
            List<AttributeDescriptor> changed = new ArrayList<AttributeDescriptor>();
            List<Object> values = new ArrayList<Object>();

            for (AttributeDescriptor att : featureType.getAttributeDescriptors()) {
                if (last.isDirrty(att.getLocalName())) {
                    changed.add(att);
                    values.add(last.getAttribute(att.getLocalName()));
                }
            }

            //do the write
            dataStore.update(featureType, changed, values, filter, st.getConnection());
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
