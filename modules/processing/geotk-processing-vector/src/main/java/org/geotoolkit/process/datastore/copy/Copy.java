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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


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
    public Copy(final ParameterValueGroup input) {
        super(CopyDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public ParameterValueGroup call() {
        fireStartEvent(new ProcessEvent(this,"Starting copy.",0, null));
                
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
            fireFailEvent(new ProcessEvent(this, null, 0, ex));
            return outputParameters;
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
                            fireFailEvent(new ProcessEvent(this, null,0, ex));
                            return outputParameters;
                        }
                    }
                }

                if(targetDS == null){
                    fireFailEvent(new ProcessEvent(this, null,0, new DataStoreException(
                            "Failed to found a factory to create datastore for parameters : "+targetDSparams)));
                    return outputParameters;
                }
            }
            
            //through error
            fireFailEvent(new ProcessEvent(this, null,0, exp));
            return outputParameters;
        }
        
        final Set<Name> names;
        if("*".equals(typenameParam)){
            //all values
            try {
                names = sourceDS.getNames();
            } catch (DataStoreException ex) {
                fireFailEvent(new ProcessEvent(this, null,0, ex));
                return outputParameters;
            }
        }else{
            //pick only the wanted names
            names = new HashSet<Name>();
            final List<String> wanted = UnmodifiableArrayList.wrap(typenameParam.split(","));
            for(String s : wanted){
                try{
                    final FeatureType type = sourceDS.getFeatureType(s);
                    names.add(type.getName());
                } catch (DataStoreException ex) {
                    fireFailEvent(new ProcessEvent(this, null,0, ex));
                    return outputParameters;
                }
            }
        }
                
        final float size = names.size();
        int inc = 0;
        for(Name n : names){
            fireProgressEvent(new ProcessEvent(this, "Copying "+n+".",(int)((inc*100f)/size), null));
            try {
                insert(n, sourceDS, targetDS, eraseParam);
            } catch (DataStoreException ex) {
                fireFailEvent(new ProcessEvent(this, null,50, ex));
                return outputParameters;
            }
            inc++;
        }
        
        fireEndEvent(new ProcessEvent(this, "Copy successful.",100, null));
        return outputParameters;
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
