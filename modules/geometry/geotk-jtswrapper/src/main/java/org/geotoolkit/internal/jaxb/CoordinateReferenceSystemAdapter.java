
package org.geotoolkit.internal.jaxb;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CoordinateReferenceSystemAdapter  extends XmlAdapter<String, CoordinateReferenceSystem> {

    private static final Map<CoordinateReferenceSystem, String> cachedIdentifier = new HashMap<CoordinateReferenceSystem, String>();

    @Override
    public CoordinateReferenceSystem unmarshal(String v) throws Exception {
        if (v != null) {
            return CRS.decode(v);
        }
        return null;
    }

    @Override
    public String marshal(CoordinateReferenceSystem v) throws Exception {
        if (v != null) {
            String identifier  = cachedIdentifier.get(v);
            if (identifier == null && !cachedIdentifier.containsKey(v)) {
                identifier = CRS.toSRS(v);
                if (identifier == null) {
                    identifier = CRS.lookupIdentifier(v, false);
                }
                cachedIdentifier.put(v, identifier);
            }
            return identifier;
        }
        return null;
    }

    public static String getSrsName(CoordinateReferenceSystem crs) {
        String srsName = null;
        if (crs != null) {
            try {
                srsName = CoordinateReferenceSystemAdapter.cachedIdentifier.get(crs);
                if (srsName == null && !CoordinateReferenceSystemAdapter.cachedIdentifier.containsKey(crs)) {
                    srsName = CRS.toSRS(crs);
                    if (srsName == null) {
                        srsName = CRS.lookupIdentifier(crs, false);
                    }
                    CoordinateReferenceSystemAdapter.cachedIdentifier.put(crs, srsName);

                }
            } catch (FactoryException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.SEVERE, null, ex);
            }
        }
        return srsName;
    }
}
