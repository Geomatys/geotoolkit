
package org.geotoolkit.internal.jaxb;

import java.util.logging.Level;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CoordinateReferenceSystemAdapter  extends XmlAdapter<String, CoordinateReferenceSystem> {

    private static final Cache<CoordinateReferenceSystem, String> cachedIdentifier = new Cache<CoordinateReferenceSystem, String>();

    @Override
    public CoordinateReferenceSystem unmarshal(final String v) throws Exception {
        if (v != null) {
            return CRS.forCode(v);
        }
        return null;
    }

    @Override
    public String marshal(final CoordinateReferenceSystem v) throws Exception {
        return getSrsName(v);
    }

    public static String getSrsName(final CoordinateReferenceSystem crs) {
        String srsName = null;
        if (crs != null) {
            try {
                srsName = CoordinateReferenceSystemAdapter.cachedIdentifier.get(crs);
                if (srsName == null && !CoordinateReferenceSystemAdapter.cachedIdentifier.containsKey(crs)) {
                    srsName = org.apache.sis.referencing.IdentifiedObjects.lookupURN(crs, null);
                    if (srsName == null) {
                        srsName = IdentifiedObjects.getIdentifierOrName(crs);
                    } else {
                        srsName = srsName.toLowerCase();
                    }
                    CoordinateReferenceSystemAdapter.cachedIdentifier.put(crs, srsName);

                }
            } catch (FactoryException ex) {
                Logging.getLogger("org.geotoolkit.referencing").log(Level.WARNING, null, ex);
            }
        }
        return srsName;
    }
}
