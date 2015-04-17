
package org.geotoolkit.feature.xml;

import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface XSDFeatureStore {
    
    Map<String,String> getSchema(final GenericName name) throws DataStoreException;
}
