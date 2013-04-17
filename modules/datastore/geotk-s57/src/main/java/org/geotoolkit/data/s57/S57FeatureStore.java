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

package org.geotoolkit.data.s57;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.s57.annexe.S57TypeBank;
import org.geotoolkit.data.s57.model.DataSetIdentification;
import org.geotoolkit.data.s57.model.DataSetParameter;
import org.geotoolkit.data.s57.model.FeatureRecord;
import org.geotoolkit.data.s57.model.S57FileReader;
import org.geotoolkit.data.s57.model.S57Object;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.data.s57.model.S57Reader;
import org.geotoolkit.data.s57.model.S57UpdatedReader;

/**
 * S-57 FeatureStore.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class S57FeatureStore extends AbstractFeatureStore{

    public static final String S57TYPECODE = "S-57 Code";
    
    static final String BUNDLE_PATH = "org/geotoolkit/s57/bundle";
    
    private final File file;
    private Set<FeatureType> types = null;
    private Set<Name> names = null;
    
    //S-57 metadata/description records
    private DataSetIdentification datasetIdentification;
    private DataSetParameter datasetParameter;
    
    
    public S57FeatureStore(final ParameterValueGroup params) throws DataStoreException{
        super(params);
        
        final URL url = (URL) params.parameter(S57FeatureStoreFactory.URLP.getName().toString()).getValue();
        try {
            this.file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
        
    }
    
    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(S57FeatureStoreFactory.NAME);
    }
    
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return null;
    }
    
    @Override
    public void refreshMetaModel() {
        //do nothing, types are not dynamic like a database
    }
    
    @Override
    public Set<Name> getNames() throws DataStoreException {
        loadTypes();
        return names;
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        loadTypes();
        for(FeatureType ft : types){
            if(DefaultName.match(ft.getName(),typeName)){
                return ft;
            }
        }
        throw new DataStoreException("Type "+typeName+" does not exist.");
    }

    private synchronized void loadTypes()throws DataStoreException {
        if(types!= null) return;
        types = new HashSet<FeatureType>();
        names = new HashSet<Name>();
        
        final S57Reader reader = getS57Reader();
        try{
            while(reader.hasNext()){
                final S57Object obj = reader.next();
                if(obj instanceof DataSetIdentification){
                    datasetIdentification = (DataSetIdentification) obj;
                }else if(obj instanceof DataSetParameter){
                    datasetParameter = (DataSetParameter) obj;
                }else if(obj instanceof FeatureRecord){
                    final FeatureRecord rec = (FeatureRecord) obj;
                    final int objlCode = rec.code;
                    final FeatureType type = S57TypeBank.getFeatureType(objlCode);
                    if(type == null){
                        throw new DataStoreException("Unknown feature type OBJL : "+objlCode);
                    }
                    type.getUserData().put(S57TYPECODE, rec.code);
                    types.add(type);
                    names.add(type.getName());
                }                
            }
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }finally{
            try {
                reader.dispose();
            } catch (IOException ex) {
                //we tryed
                getLogger().log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final S57Reader s57reader = getS57Reader();
        final FeatureReader reader = new S57FeatureReader(ft,
                (Integer)ft.getUserData().get(S57TYPECODE),s57reader,
                datasetIdentification,datasetParameter);
        return handleRemaining(reader, query);
    }

    private S57Reader getS57Reader() throws DataStoreException{
        
        final File baseFile = findMainFile(null);
        String baseName = baseFile.getName();
        baseName = baseName.substring(0, baseName.length()-4);
        final List<File> updateFiles = findUpdateFiles(baseName, null, null);
        //sort files
        Collections.sort(updateFiles);
        
        //create base reader
        S57Reader reader = new S57FileReader();
        try {
            reader.setInput(baseFile);            
            reader.setDsid(datasetIdentification);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        
        //encapsulated with each update
        for(File uf : updateFiles){
            reader = new S57UpdatedReader(reader,uf);
            reader.setDsid(datasetIdentification);
        }
        
        return reader;
    }
    
    /**
     * Search for the main file, it must be somewhere in the given path and have the
     * extension .000
     * @return File if found, DataStoreException otherwise
     */
    private File findMainFile(File root) throws DataStoreException{
        if(root == null){
            root = file;
        }
        
        if(root.isDirectory()){
            //search sub files
            for(File f : root.listFiles()){
                File candidate = findMainFile(f);
                if(candidate!=null) return candidate;
            }            
        }else{
            if(root.getName().endsWith(".000")){
                return root;
            }
        }
        throw new DataStoreException("Could not find any S-57 base file, named xxx.000");
    }
    
    /**
     * Search for update files.
     * @param updateFiles
     * @param root
     * @return List<File>, never null
     */
    private List<File> findUpdateFiles(String baseName,List<File> updateFiles, File root) throws DataStoreException{
        if(root == null){
            root = file;
        }
        if(baseName==null){
            File main = findMainFile(null);
            baseName = main.getName().substring(0, main.getName().length()-4);
        }        
        if(updateFiles==null){
            updateFiles = new ArrayList<File>();
        }
        if(root.isDirectory()){
            //search sub files
            for(File f : root.listFiles()){
                findUpdateFiles(baseName, updateFiles, f);
            }            
        }else{
            if(root.getName().startsWith(baseName) && !root.getName().endsWith(".000")){
                updateFiles.add(root);
            }
        }
        return updateFiles;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // WRITING OPERATIONS //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
        
    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }
    
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Writing not supported yet.");
    }
    
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        return handleWriter(typeName, filter, hints);
    }

}
