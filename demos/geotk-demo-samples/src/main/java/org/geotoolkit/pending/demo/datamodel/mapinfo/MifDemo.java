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
 * A simple example for MIF/MID reading.
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
            URL dataLocation = MifDemo.class.getResource("/data/world/HY_WATER_AREA_POLYGON.mif");
            //create using a Parameters object--------------------------------------
            System.out.println(MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

            final ParameterValueGroup parameters = MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
            Parameters.getOrCreate(MIFFeatureStoreFactory.URLP, parameters).setValue(dataLocation);

            final FeatureStore store1 = FeatureStoreFinder.open(parameters);
            Set<Name> names = store1.getNames();

            Name type0 = (Name) names.toArray()[0];
            Name type1 = (Name) names.toArray()[1];
            final FeatureType fType1 = store1.getFeatureType(type1);

            Session session = store1.createSession(false);

            FeatureCollection coll0 = session.getFeatureCollection(QueryBuilder.all(type0));
            FeatureIterator it0 = coll0.iterator();
            Feature f;
            while(it0.hasNext()) {
                System.out.println(it0.next());
            }

            FeatureCollection coll1 = session.getFeatureCollection(QueryBuilder.all(type1));
//            FeatureIterator it1 = coll1.iterator();
//            while(it1.hasNext()) {
//                f = it1.next();
//                if(f.getDefaultGeometryProperty() != null) {
//                    System.out.println(f.getDefaultGeometryProperty());
//                } else {
//                    System.out.println(f);
//                }
//            }

            final ParameterValueGroup writerParam = MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
            Parameters.getOrCreate(MIFFeatureStoreFactory.URLP, writerParam).setValue(new URL("file:/tmp/test.mif"));
            final MIFFeatureStore writingStore = new MIFFeatureStore(writerParam);
            writingStore.setDelimiter(',');
            writingStore.createSchema(type1, fType1);
            writingStore.addFeatures(type1, coll1);

        } catch(Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception happened.", ex);
        }

    }
}
