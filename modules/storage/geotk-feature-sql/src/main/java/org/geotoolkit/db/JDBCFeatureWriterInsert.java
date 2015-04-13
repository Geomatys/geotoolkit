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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;

/**
 * Feature writer for insertion only.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JDBCFeatureWriterInsert extends JDBCFeatureReader implements FeatureWriter {

    private boolean batchInsert;
    private Collection<Feature> toAdd;
    
    //private String id;
    private AbstractFeature last;

    public JDBCFeatureWriterInsert(final DefaultJDBCFeatureStore store, final String sql, 
            final FeatureType type, Connection cnx, boolean release, final Hints hints)
            throws SQLException, IOException, DataStoreException {
        super(store, sql, type, cnx, release, hints);
        init();
    }

    public JDBCFeatureWriterInsert(final JDBCFeatureReader other) throws SQLException {
        super(other);
        init();
    }
    
    private void init(){
        last = (AbstractFeature)FeatureUtilities.defaultFeature(type, "-1");
//        last = new AbstractFeature<Collection<Property>>(type, (FeatureId)null) {
//            @Override
//            public FeatureId getIdentifier() {
//                return new DefaultFeatureId(JDBCFeatureWriterInsert.this.id);
//            }
//        };
        if(hints != null){
            batchInsert = Boolean.FALSE.equals(hints.get(HintsPending.UPDATE_ID_ON_INSERT));
        }else{
            batchInsert = Boolean.FALSE;
        }
        toAdd = (batchInsert) ? new ArrayList<Feature>() : null;
    }
    
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        return false;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        FeatureUtilities.resetProperty(last);
        return last;
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        throw new FeatureStoreRuntimeException("Removing not supported in Insert writer.");
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        
        if(batchInsert){
            toAdd.add(last);
            last = (AbstractFeature)FeatureUtilities.defaultFeature(type, "-1");
            if(toAdd.size() > 1000){
                try {
                    store.insert(toAdd, type, cx);
                } catch (DataStoreException e) {
                    throw new FeatureStoreRuntimeException(e);
                }
                toAdd.clear();
            }
        }else{
            try {
                store.insert(last, type, cx);
                //the featurestore sets as userData, grab it and update the fid
                final String id = (String) last.getUserData().get("fid");
                if (id != null) {
                    last.setIdentifier(new DefaultFeatureId(id));
                }
            } catch (DataStoreException e) {
                throw new FeatureStoreRuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws FeatureStoreRuntimeException {

        if(batchInsert && !toAdd.isEmpty()){
            try {
                //do the insert
                store.insert(toAdd, type, cx);
            } catch (DataStoreException e) {
                throw new FeatureStoreRuntimeException(e);
            }
            toAdd.clear();
        }
        
        super.close();
    }
}
