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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * S-57 FeatureStore.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class S57FeatureStore extends AbstractFeatureStore{

    static final String BUNDLE_PATH = "org/geotoolkit/s57/bundle";
    
    private final File file;
    
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
