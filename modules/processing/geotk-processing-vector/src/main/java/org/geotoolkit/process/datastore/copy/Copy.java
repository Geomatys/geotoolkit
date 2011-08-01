/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.datastore.copy;

import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;


/**
 * Copy feature from one datastore to another.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Copy extends AbstractProcess {

    /**
     * Default constructor
     */
    public Copy() {
        super(CopyDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        getMonitor().started(new ProcessEvent(this, 0, null, null));
                
        final Map sourceDSparams = Parameters.value(CopyDescriptor.SOURCE_STORE_PARAMS, inputParameters);
        final Map targetDSparams = Parameters.value(CopyDescriptor.TARGET_STORE_PARAMS, inputParameters);
        
        final DataStore sourceDS;
        final DataStore targetDS;
        try{
            sourceDS = DataStoreFinder.getDataStore(sourceDSparams);
            if(sourceDS == null){
                throw new DataStoreException("No datastore for parameters :"+sourceDSparams);
            }
        }catch(DataStoreException ex){
            getMonitor().failed(new ProcessEvent(this, 5, null, ex));
            return;
        }
                
        try{
            targetDS = DataStoreFinder.getDataStore(targetDSparams);
            if(targetDS == null){
                throw new DataStoreException("No datastore for parameters :"+targetDSparams);
            }
        }catch(DataStoreException ex){
            getMonitor().failed(new ProcessEvent(this, 5, null, ex));
            return;
        }
        
        final Set<Name> names;
        try {
            names = sourceDS.getNames();
        } catch (DataStoreException ex) {
            getMonitor().failed(new ProcessEvent(this, 10, null, ex));
            return;
        }
        
        for(Name n : names){
            try {
                insert(n, sourceDS, targetDS);
            } catch (DataStoreException ex) {
                getMonitor().failed(new ProcessEvent(this, 50, null, ex));
                return;
            }
        }
        
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }
    
    private void insert(final Name name, final DataStore source, final DataStore target) throws DataStoreException{
        
        final FeatureType type = source.getFeatureType(name);        
        final Session session = source.createSession(false);
        final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name));
        
        target.createSchema(name, type);
        
        final Hints hints = new Hints();
        hints.put(HintsPending.UPDATE_ID_ON_INSERT, Boolean.FALSE);
        target.addFeatures(name, collection, hints);
        
    }
    
}
