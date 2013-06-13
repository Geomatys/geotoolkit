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
package org.geotoolkit.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.*;
import static org.geotoolkit.data.AbstractFolderFeatureStoreFactory.*;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersionHistory;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Handle a folder of single file FeatureStore.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class DefaultFolderFeatureStore extends AbstractFeatureStore{

    private final ParameterValueGroup folderParameters;
    private final AbstractFolderFeatureStoreFactory folderFactory;
    private final FileFeatureStoreFactory singleFileFactory;
    private final ParameterValueGroup singleFileDefaultParameters;
    private Map<Name,FeatureStore> stores = null;

    public DefaultFolderFeatureStore(final ParameterValueGroup params, final AbstractFolderFeatureStoreFactory factory){
        super(params);
        this.folderParameters = params;
        this.folderFactory = factory;
        this.singleFileFactory = this.folderFactory.getSingleFileFactory();

        final ParameterDescriptorGroup desc = singleFileFactory.getParametersDescriptor();
        singleFileDefaultParameters = desc.createValue();
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            if(pdesc == URLP || pdesc.getName().getCode().equals(IDENTIFIER.getName().getCode())) {
                continue;
            }
            Parameters.getOrCreate((ParameterDescriptor)pdesc, singleFileDefaultParameters)
                    .setValue(folderParameters.parameter(pdesc.getName().getCode()).getValue());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return folderFactory;
    }

    /**
     * Fallthrought to sub feature stores.
     */
    @Override
    public VersionControl getVersioning(Name typeName) throws VersioningException {
        try {
            typeCheck(typeName);
        } catch (DataStoreException ex) {
            throw new VersioningException(ex);
        }
        final FeatureStore store = stores.get(typeName);
        return store.getVersioning(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {

        if(stores == null){
            this.stores = new HashMap<Name, FeatureStore>();
            final File folder = getFolder(folderParameters);

            if(!folder.exists()){
                try{
                    folder.mkdirs();
                }catch(SecurityException ex){
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            Boolean recursive = Parameters.value(RECURSIVE, folderParameters);
            if (recursive == null) {
                recursive = RECURSIVE.getDefaultValue();
            }

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
            try {
                Parameters.getOrCreate(URLP, params)
                    .setValue(candidate.toURI().toURL());
            } catch (MalformedURLException ex) {
                getLogger().log(Level.FINE, ex.getLocalizedMessage(),ex);
            }
            if(singleFileFactory.canProcess(params)){
                try {
                    final FeatureStore fileDS = singleFileFactory.open(params);
                    stores.put(fileDS.getNames().iterator().next(), fileDS);
                } catch (DataStoreException ex) {
                    getLogger().log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {

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

        final FeatureStore store = singleFileFactory.create(params);
        store.createFeatureType(typeName, featureType);
        stores.put(typeName, store);
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void updateFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void deleteFeatureType(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.getFeatureType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable(final Name typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.isWritable(typeName);
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        return store.addFeatures(groupName, newFeatures);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        store.updateFeatures(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        store.removeFeatures(groupName, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final Name name = query.getTypeName();
        typeCheck(name);
        final FeatureStore store = stores.get(name);
        return store.getFeatureReader(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.getFeatureWriter(typeName, filter, hints);
    }

    private File getFolder(final ParameterValueGroup params) throws DataStoreException{
        final URL url = Parameters.value(URLFOLDER, params);

        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new DataStoreException(e);
        }
    }

    @Override
    public void refreshMetaModel() {
        stores=null;
    }

}
