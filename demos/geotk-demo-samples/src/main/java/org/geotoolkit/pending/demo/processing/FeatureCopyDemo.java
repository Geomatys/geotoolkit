
package org.geotoolkit.pending.demo.processing;

import java.net.URI;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.db.postgres.PostgresFeatureStoreFactory;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.datastore.copy.CopyDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Copy between feature stores.
 */
public class FeatureCopyDemo {
    
    public static void main(String[] args) throws Exception {
        
        final ParameterValueGroup shpParams = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        ParametersExt.getOrCreateValue(shpParams,"path").setValue(URI.create("file:/...someshapefile"));
        
        final FeatureStore source = FeatureStoreFinder.open(shpParams);
        
        final ParameterValueGroup pgParams = PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        ParametersExt.getOrCreateValue(pgParams,"host").setValue("host");
        ParametersExt.getOrCreateValue(pgParams,"port").setValue(5432);
        ParametersExt.getOrCreateValue(pgParams,"database").setValue("database");
        ParametersExt.getOrCreateValue(pgParams,"user").setValue("user");
        ParametersExt.getOrCreateValue(pgParams,"password").setValue("secret");
        
        final FeatureStore target = FeatureStoreFinder.open(pgParams);
        
        final ParameterValueGroup copyParams = CopyDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(copyParams, "source_datastore").setValue(source);
        ParametersExt.getOrCreateValue(copyParams, "target_datastore").setValue(target);
        final Process process = CopyDescriptor.INSTANCE.createProcess(copyParams);
        process.call();
        
    }
    
    
}
