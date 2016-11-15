

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
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;

import org.opengis.util.GenericName;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
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
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(Point.class).setName("position").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        type = ftb.build();
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(FishDatastoreFactory.NAME);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return Collections.singleton(type.getName());
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
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
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void updateFeatureType(FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteFeatureType(String typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refreshMetaModel() {
    }

}
