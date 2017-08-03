
package org.geotoolkit.pending.demo.processing;

import java.net.URI;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.db.postgres.PostgresFeatureStoreFactory;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.datastore.copy.CopyDescriptor;
import org.geotoolkit.storage.DataStores;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Copy between feature stores.
 */
public class FeatureCopyDemo {

    public static void main(String[] args) throws Exception {

        final ParameterValueGroup shpParams = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        shpParams.parameter("path").setValue(URI.create("file:/...someshapefile"));

        final FeatureStore source = (FeatureStore) DataStores.open(shpParams);

        final ParameterValueGroup pgParams = PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        pgParams.parameter("host").setValue("host");
        pgParams.parameter("port").setValue(5432);
        pgParams.parameter("database").setValue("database");
        pgParams.parameter("user").setValue("user");
        pgParams.parameter("password").setValue("secret");

        final FeatureStore target = (FeatureStore) DataStores.open(pgParams);

        final ParameterValueGroup copyParams = CopyDescriptor.INPUT_DESC.createValue();
        copyParams.parameter("source_datastore").setValue(source);
        copyParams.parameter("target_datastore").setValue(target);
        final Process process = CopyDescriptor.INSTANCE.createProcess(copyParams);
        process.call();

    }


}
