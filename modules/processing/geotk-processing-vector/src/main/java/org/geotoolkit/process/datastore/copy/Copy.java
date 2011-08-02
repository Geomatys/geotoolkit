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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFactory;
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
                
        final Map sourceDSparams    = Parameters.value(CopyDescriptor.SOURCE_STORE_PARAMS,  inputParameters);
        final Map targetDSparams    = Parameters.value(CopyDescriptor.TARGET_STORE_PARAMS,  inputParameters);
        final Boolean eraseParam    = Parameters.value(CopyDescriptor.ERASE,                inputParameters);
        final Boolean createParam   = Parameters.value(CopyDescriptor.CREATE,               inputParameters);
        final String typenameParam  = Parameters.value(CopyDescriptor.TYPE_NAME,            inputParameters);
        
        final DataStore sourceDS;
        DataStore targetDS = null;
        try{
            sourceDS = DataStoreFinder.getDataStore(sourceDSparams);
            if(sourceDS == null){
                throw new DataStoreException("No datastore for parameters :"+sourceDSparams);
            }
        }catch(DataStoreException ex){
            getMonitor().failed(new ProcessEvent(this, 5, null, ex));
            return;
        }
        
        DataStoreException exp = null;
        try{
            targetDS = DataStoreFinder.getDataStore(targetDSparams);
            if(targetDS == null){
                exp = new DataStoreException("No datastore for parameters :"+targetDSparams);
            }
        }catch(DataStoreException ex){
            exp = ex;
        }
        
        if(exp != null){
            if(createParam){
                //try to create the datastore
                final Iterator<DataStoreFactory> ite = DataStoreFinder.getAvailableDataStores();
                while(ite.hasNext()){
                    final DataStoreFactory factory = ite.next();
                    if(factory.canProcess(targetDSparams)){
                        try{
                            targetDS = factory.createNewDataStore(targetDSparams);
                        }catch(DataStoreException ex){
                            getMonitor().failed(new ProcessEvent(this, 8, null, ex));
                            return;
                        }
                    }
                }

                if(targetDS == null){
                    getMonitor().failed(new ProcessEvent(this, 8, null, new DataStoreException(
                            "Failed to found a factory to create datastore for parameters : "+targetDSparams)));
                    return;
                }
            }
            
            //through error
            getMonitor().failed(new ProcessEvent(this, 8, null, exp));
            return;
        }
        
        final Set<Name> names;
        if("*".equals(typenameParam)){
            //all values
            try {
                names = sourceDS.getNames();
            } catch (DataStoreException ex) {
                getMonitor().failed(new ProcessEvent(this, 20, null, ex));
                return;
            }
        }else{
            //pick only the wanted names
            try{
                FeatureType type = sourceDS.getFeatureType(typenameParam);
                names = Collections.singleton(type.getName());
            } catch (DataStoreException ex) {
                getMonitor().failed(new ProcessEvent(this, 20, null, ex));
                return;
            }
        }
                
        for(Name n : names){
            try {
                insert(n, sourceDS, targetDS, eraseParam);
            } catch (DataStoreException ex) {
                getMonitor().failed(new ProcessEvent(this, 50, null, ex));
                return;
            }
        }
        
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }
    
    private void insert(final Name name, final DataStore source, final DataStore target, final boolean erase) throws DataStoreException{
        
        final FeatureType type = source.getFeatureType(name);        
        final Session session = source.createSession(false);
        final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name));
        
        if(target.getNames().contains(name)){
            if(erase){
                target.deleteSchema(name);
                target.createSchema(name, type);
            }
        }else{
            target.createSchema(name, type);
        }
        
        final Hints hints = new Hints();
        hints.put(HintsPending.UPDATE_ID_ON_INSERT, Boolean.FALSE);
        target.addFeatures(name, collection, hints);
        
    }
    
}
