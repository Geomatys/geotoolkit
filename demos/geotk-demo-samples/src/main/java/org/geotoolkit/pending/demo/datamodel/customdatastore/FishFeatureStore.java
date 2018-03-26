

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

public class FishFeatureStore extends AbstractFeatureStore{


    private final File storage;
    private final FeatureType type;

    public FishFeatureStore(ParameterValueGroup params) throws DataStoreException{
        super(params);

        URI uri = (URI) params.parameter(FishDatastoreFactory.PATH.getName().toString()).getValue();
        storage = new File(uri);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(Point.class).setName("position").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        type = ftb.build();
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(FishDatastoreFactory.NAME);
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
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        typeCheck(gquery.getTypeName());

        FeatureReader reader;
        try {
            reader = new FishReader(storage, type);
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex);
        }

        //use the generic methode to take to care of everything for us.
        reader = FeatureStreams.subset(reader, gquery);
        return reader;
    }


    ///////////////////////////////////////////////////////////////
    // SCHEMA MANIPULATION ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void refreshMetaModel() {
    }

}
