

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

public class FishDataStore extends AbstractDataStore{


    private final File storage;
    private final SimpleFeatureType type;

    public FishDataStore(URL url, String namespace) throws URISyntaxException{
        super(namespace);

        storage = new File(url.toURI());

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(namespace,"Fish");
        ftb.add("name", String.class);
        ftb.add("length", Integer.class);
        ftb.add("position", Point.class, DefaultGeographicCRS.WGS84);
        ftb.setDefaultGeometry("position");
        type = ftb.buildSimpleFeatureType();
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return Collections.singleton(type.getName());
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
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
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
