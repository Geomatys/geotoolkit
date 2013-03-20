package org.geotoolkit.pending.demo.datamodel.mapinfo;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.mapinfo.mif.MIFFeatureStore;
import org.geotoolkit.data.mapinfo.mif.MIFFeatureStoreFactory;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple example for MIF/MID feature store.
 *
 * Use case : Read a MIF/MID file couple, and copy it into new file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 12/03/13
 */
public class MifDemo {

    public static final Logger LOGGER = Logger.getLogger(MifDemo.class.getName());

    public static void main(String[] args) throws DataStoreException, NoSuchIdentifierException {
        Demos.init();

        ParameterValueGroup gr =FactoryFinder.getMathTransformFactory(null).getDefaultParameters("Mercator_1SP");
        System.out.println(gr);
        try {

            // To build a valid MIFFeatureStore, the MIF file URL must be given to the store parameters.
            URL dataLocation = MifDemo.class.getResource("/data/world/HY_WATER_AREA_POLYGON.mif");

            System.out.println(MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

            final ParameterValueGroup parameters = MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
            Parameters.getOrCreate(MIFFeatureStoreFactory.URLP, parameters).setValue(dataLocation);

            // Initialize the store, and create a session to browse it's data.
            final FeatureStore store1 = FeatureStoreFinder.open(parameters);
            Session session = store1.createSession(false);

            // Create a mif featureStore for writing operation.
            final ParameterValueGroup writerParam = MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
            Parameters.getOrCreate(MIFFeatureStoreFactory.URLP, writerParam).setValue(new URL("file:/tmp/test.mif"));
            final MIFFeatureStore writingStore = new MIFFeatureStore(writerParam);
            //Here we get a function to set mid file attributes delimiter. MID file is a sort of CSV, and default
            // delimiter (which is \t) can be changed by user. Here I choose coma.
            writingStore.setDelimiter(',');

            // Names should contain several feature types :
            // - The base type, which describe the attributes all features must have.
            // - The geometry types. They're feature types describing a specific geometry type we can find in the source
            // file. All those types inherit from base type, so we get all attributes associated with the geometry.
            Set<Name> names = store1.getNames();

            for(Name typeName : names) {
                final FeatureType fType = store1.getFeatureType(typeName);
                // Get all features of given type.
                FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(typeName));
                // If the type we got don't get super type, it's the store base type. Just print info.
                if(fType.getSuper() == null) {
                    FeatureIterator it = collection.iterator();
                    while(it.hasNext()) {
                        System.out.println(it.next());
                    }
                } else {
                    // If we got the geometric data, we write it into new MIF/MID files.

                    // First we must specify we must add a featureType to the store. If no base Type have already been
                    // specified, the given feature type parent will be used. Else, we check that given type is compliant
                    // with stored base type.
                    writingStore.createSchema(typeName, fType);

                    writingStore.addFeatures(typeName, collection);
                }
            }

        } catch(Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception happened.", ex);
        }

    }
}
