package org.geotoolkit.pending.demo.datamodel.mapinfo;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.storage.feature.DefiningFeatureSet;
import org.geotoolkit.data.mapinfo.mif.MIFProvider;
import org.geotoolkit.data.mapinfo.mif.MIFStore;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.NoSuchIdentifierException;

/**
 * A simple example for MIF/MID feature store.
 *
 * Use case : Read a MIF/MID file couple, and copy it into new file.
 *
 * @author Alexis Manin (Geomatys)
 */
public class MifDemo {

    public static final Logger LOGGER = Logger.getLogger("org.geotoolkit.pending.demo.datamodel.mapinfo");

    public static final String DESTINATION_MIF = "file:/tmp/test.mif";
    public static final String DESTINATION_MID = "file:/tmp/test.mid";

    public static void main(String[] args) throws DataStoreException, NoSuchIdentifierException {
        Demos.init();

        try {

            // First of all, we delete the files we want to write in.
            URL destinationURL = new URL(DESTINATION_MIF);
            File tmpMIF = new File(destinationURL.toURI());
            if (tmpMIF.exists()) {
                tmpMIF.delete();
            }

            File tmpMID = new File(DESTINATION_MID);
            if (tmpMID.exists()) {
                tmpMID.delete();
            }

            // To build a valid MIFFeatureStore, the MIF file URL must be given to the store parameters.
            URL dataLocation = MifDemo.class.getResource("/data/world/HY_WATER_AREA_POLYGON.mif");
            System.out.println(MIFProvider.PARAMETERS_DESCRIPTOR);

            final Parameters parameters = Parameters.castOrWrap(MIFProvider.PARAMETERS_DESCRIPTOR.createValue());
            parameters.getOrCreate(MIFProvider.PATH).setValue(dataLocation.toURI());

            // Initialize the store, and create a session to browse it's data.
            final DataStore store1 = DataStores.open(parameters);

            // Create a mif featureStore for writing operation.
            final Parameters writerParam = Parameters.castOrWrap(MIFProvider.PARAMETERS_DESCRIPTOR.createValue());
            writerParam.getOrCreate(MIFProvider.PATH).setValue(destinationURL.toURI());
            final MIFStore writingStore = new MIFStore(writerParam);
            //Here we get a function to set mid file attributes delimiter. MID file is a sort of CSV, and default
            // delimiter (which is \t) can be changed by user. Here I choose coma.
            writingStore.setDelimiter(',');

            // Names should contain several feature types :
            // - The base type, which describe the attributes all features must have.
            // - The geometry types. They're feature types describing a specific geometry type we can find in the source
            // file. All those types inherit from base type, so we get all attributes associated with the geometry.
            for (FeatureSet fs : DataStores.flatten(store1, true, FeatureSet.class)) {

                final FeatureType fType = fs.getType();
                // Get all features of given type.
                try (Stream<Feature> stream = fs.features(false)) {
                    // If the type we got don't get super type, it's the store base type. Just print info.
                    if (fType.getSuperTypes().isEmpty()) {
                        Iterator it = stream.iterator();
                        while (it.hasNext()) {
                            System.out.println(it.next());
                        }
                    } else {
                        // If we got the geometric data, we write it into new MIF/MID files.
                        // First we must specify we must add a featureType to the store. If no base Type have already been
                        // specified, the given feature type parent will be used. Else, we check that given type is compliant
                        // with stored base type.
                        WritableFeatureSet nr = (WritableFeatureSet) writingStore.add(new DefiningFeatureSet(fType, null));
                        nr.add(stream.iterator());
                    }
                }

            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception happened.", ex);
        }

    }
}
