

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

public class FishFeatureStore extends AbstractFeatureStore{


    private final File storage;
    private final FeatureType type;

    public FishFeatureStore(ParameterValueGroup params) throws DataStoreException{
        super(params);

        URI uri = (URI) params.parameter(FishDatastoreFactory.PATH.getName().toString()).getValue();
        storage = new File(uri);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(getDefaultNamespace(),"Fish");
        ftb.add("name", String.class);
        ftb.add("length", Integer.class);
        ftb.add("position", Point.class, CommonCRS.WGS84.normalizedGeographic());
        ftb.setDefaultGeometry("position");
        type = ftb.buildSimpleFeatureType();
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(FishDatastoreFactory.NAME);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return Collections.singleton(type.getName());
    }

    @Override
    public FeatureType getFeatureType(GenericName typeName) throws DataStoreException {
        typeCheck(typeName);
        return type;
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        FeatureReader reader;
        try {
            reader = new FishReader(storage, type);
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex);
        }

        //use the generic methode to take to care of everything for us.
        reader = handleRemaining(reader, query);
        return reader;
    }


    ///////////////////////////////////////////////////////////////
    // SCHEMA MANIPULATION ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    @Override
    public void createFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void updateFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteFeatureType(GenericName typeName) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported.");
    }

    ///////////////////////////////////////////////////////////////
    // WRITING ////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    @Override
    public List<FeatureId> addFeatures(GenericName groupName, Collection<? extends Feature> newFeatures, 
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(GenericName groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(GenericName groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public FeatureWriter getFeatureWriter(GenericName typeName, Filter filter,Hints hints) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void refreshMetaModel() {
    }

}
