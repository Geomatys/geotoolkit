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

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

public class JDBCUpdateInsertFeatureWriter extends JDBCUpdateFeatureWriter {

    JDBCInsertFeatureWriter inserter;
    
    public JDBCUpdateInsertFeatureWriter(final String sql, final Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, final Hints hints)
            throws SQLException, IOException, DataStoreException
    {
        super(sql, cx, store, groupName, type, hints);
    }
    
    public JDBCUpdateInsertFeatureWriter(final PreparedStatement ps, final Connection cx, final JDBCDataStore store,
            final Name groupName, SimpleFeatureType type, final Name[] attributeNames, final Hints hints)
            throws SQLException, IOException, DataStoreException
    {
        super(ps, cx, store, groupName, type, hints);
    }
    
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        if ( inserter != null ) {
            return inserter.hasNext();
        }
        
        //check parent
        boolean hasNext = super.hasNext();
        if ( !hasNext ) {
            //update phase is up, switch to insert mode
            inserter = new JDBCInsertFeatureWriter( this );
            return inserter.hasNext();
        }
    
        return hasNext;
    }

    @Override
    public SimpleFeature next() throws DataStoreRuntimeException {
        if ( inserter != null ) {
            return inserter.next();
        }
        
        return super.next();
    }
    
    @Override
    public void remove() throws DataStoreRuntimeException {
        if ( inserter != null ) {
            inserter.remove();
            return;
        }
        
        super.remove();
    }
    
    @Override
    public void write() throws DataStoreRuntimeException {
        if ( inserter != null ) {
            inserter.write();
            return;
        }
        
        super.write();
    }
    
    @Override
    public void close() throws DataStoreRuntimeException {
        if ( inserter != null ) {
            //JD: do not call close because the inserter borrowed all of its state
            // from this reader... super will deal with it.
            // AA: yet, make it throw away all references so that we won't get
            // false positive information about connection leaks
            inserter.cleanup();
            inserter = null;
        }
        
        super.close();
    }
    
}
