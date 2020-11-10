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
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Feature writer for insertion only.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JDBCFeatureWriterInsert extends JDBCFeatureReader implements FeatureWriter {

    private boolean batchInsert;
    private Collection<Feature> toAdd;

    //private String id;
    private Feature last;

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
        last = type.newInstance();
        if(hints != null){
            batchInsert = Boolean.FALSE.equals(hints.get(Hints.UPDATE_ID_ON_INSERT));
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
        last = type.newInstance();
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
            last = type.newInstance();
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
                //TODO
//                final String id = (String) last.getUserData().get("fid");
//                if (id != null) {
//                    last.setIdentifier(new DefaultFeatureId(id));
//                }
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
