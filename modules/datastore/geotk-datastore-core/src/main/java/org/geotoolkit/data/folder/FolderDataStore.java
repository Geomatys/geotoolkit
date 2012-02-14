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
package org.geotoolkit.data.folder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.data.folder.AbstractFolderDataStoreFactory.*;
import static org.geotoolkit.data.AbstractFileDataStoreFactory.*;

/**
 * Handle a folder of single file datastore.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FolderDataStore extends AbstractDataStore{

    private final ParameterValueGroup folderParameters;
    private final AbstractFolderDataStoreFactory folderFactory;
    private final FileDataStoreFactory singleFileFactory;
    private final ParameterValueGroup singleFileDefaultParameters;
    private Map<Name,DataStore> stores = null;
    
    public FolderDataStore(final ParameterValueGroup params, final AbstractFolderDataStoreFactory factory){
        super(Parameters.value(AbstractFileDataStoreFactory.NAMESPACE, params));
        this.folderParameters = params;
        this.folderFactory = factory;
        this.singleFileFactory = this.folderFactory.getSingleFileFactory();
        
        final ParameterDescriptorGroup desc = singleFileFactory.getParametersDescriptor();
        singleFileDefaultParameters = desc.createValue();
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            if(pdesc == URLP){
                continue;
            }
            Parameters.getOrCreate((ParameterDescriptor)pdesc, singleFileDefaultParameters)
                    .setValue(folderParameters.parameter(pdesc.getName().getCode()).getValue());
        }
        
    }
    
    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {
        
        if(stores == null){
            this.stores = new HashMap<Name, DataStore>();
            final File folder = getFolder(folderParameters);
            
            if(!folder.exists()){
                try{
                    folder.mkdirs();
                }catch(SecurityException ex){
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }
            
            final Boolean recursive = Parameters.value(RECURSIVE, folderParameters);
            for(File f : folder.listFiles()){
                explore(f,recursive);
            }
        }
        
        return stores.keySet();
    }

    private void explore(final File candidate, boolean recursive){
        if(candidate.isDirectory()){
            if(recursive){
                for(File f : candidate.listFiles()){
                    explore(f,recursive);
                }
            }
        }else{
            //test the file
            final ParameterValueGroup params = singleFileDefaultParameters.clone();
            final ParameterValue<URL> urlparam = URLP.createValue();
            try {
                urlparam.setValue(candidate.toURI().toURL());
            } catch (MalformedURLException ex) {
                getLogger().log(Level.FINE, ex.getLocalizedMessage(),ex);
            }
            params.values().add(urlparam);
            if(singleFileFactory.canProcess(params)){
                try {
                    final DataStore fileDS = singleFileFactory.create(params);
                    stores.put(fileDS.getNames().iterator().next(), fileDS);
                } catch (DataStoreException ex) {
                    getLogger().log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }
        }
    }
    
    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        
        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(!featureType.getName().equals(typeName)){
            throw new DataStoreException("Type name must match feature type name.");
        }

        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }
        
        final ParameterValueGroup params = singleFileDefaultParameters.clone();
        try {
            final File folder = getFolder(folderParameters);
            final File newFile = new File(folder, typeName.getLocalPart()+singleFileFactory.getFileExtensions()[0]);
            Parameters.getOrCreate(URLP, params).setValue(newFile.toURI().toURL());
        } catch (MalformedURLException ex) {
            throw new DataStoreException(ex);
        }
        
        final DataStore store = singleFileFactory.createNew(params);
        store.createSchema(typeName, featureType);
        stores.put(typeName, store);
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        typeCheck(typeName);
        final DataStore store = stores.get(typeName);
        return store.getFeatureType(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures, 
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final DataStore store = stores.get(groupName);
        return store.addFeatures(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(final Name groupName, final Filter filter, 
            final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        typeCheck(groupName);
        final DataStore store = stores.get(groupName);
        store.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);
        final DataStore store = stores.get(groupName);
        store.removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final Name name = query.getTypeName();
        typeCheck(name);
        final DataStore store = stores.get(name);
        return store.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        typeCheck(typeName);
        final DataStore store = stores.get(typeName);
        return store.getFeatureWriter(typeName, filter, hints);
    }

    private File getFolder(ParameterValueGroup params) throws DataStoreException{
        final URL url = Parameters.value(URLFOLDER, params);
        
        //strip the postfix to obtain the containing folder url
        final String path = url.getPath();
        final String lpath = path.toLowerCase();
        for(String ext : singleFileFactory.getFileExtensions()){
            ext = "*"+ext;
            if(lpath.endsWith(ext)){
                final String fpath = path.substring(0, path.length()-ext.length());
                return new File(fpath);
            }
        }
        throw new DataStoreException("Unvalid folder path "+url);
    }
    
    
}
