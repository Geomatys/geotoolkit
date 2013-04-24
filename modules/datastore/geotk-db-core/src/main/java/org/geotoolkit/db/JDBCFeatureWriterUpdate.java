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
package org.geotoolkit.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

public class JDBCFeatureWriterUpdate extends JDBCFeatureReader implements
        FeatureWriter<FeatureType, Feature> {
    
    //we keep the reference a bit longer
    private JDBCComplexFeature last;
    
    public JDBCFeatureWriterUpdate(final DefaultJDBCFeatureStore store, final String sql, 
            final FeatureType type, final Hints hints)
            throws SQLException, IOException,DataStoreException {        
        super(store, sql, type, hints);
    }

    @Override
    protected JDBCComplexFeature toFeature(ResultSet rs) throws SQLException {
        last = super.toFeature(rs);
        return last;
    }
    
    @Override
    public void remove() throws FeatureStoreRuntimeException {
        if(last==null){
            throw new FeatureStoreRuntimeException("Cursor is not on a record.");
        }
        
        try {
            rs.deleteRow();
        } catch (SQLException e) {
            throw new FeatureStoreRuntimeException(e);
        }
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        if(last==null){
            throw new FeatureStoreRuntimeException("Cursor is not on a record.");
        }
        
        last.updateResultSet(rs);
    }

}
