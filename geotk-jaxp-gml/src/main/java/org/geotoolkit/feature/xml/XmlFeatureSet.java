
package org.geotoolkit.feature.xml;

import java.util.Map;
import org.apache.sis.storage.DataStoreException;

/**
 * Data stores resources may have some specific XSD definitions.
 * The base featuretype to XSD transformer do not produce the exact same XSD
 * as the original, this is because some informations are xml specific and are
 * lost when mapped to feature types.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface XmlFeatureSet {

    /**
     * Get a list of namespace -> location to add in the generated xsd.
     * The values can be a String, in this case it is a location where is xsd can be found.
     * Or it can be an Schema object.
     *
     * @return
     * @throws DataStoreException
     */
    Map<String,?> getSchema() throws DataStoreException;

}
