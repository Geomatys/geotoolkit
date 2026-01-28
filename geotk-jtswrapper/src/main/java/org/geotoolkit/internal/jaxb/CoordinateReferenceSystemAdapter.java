
package org.geotoolkit.internal.jaxb;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.collection.Cache;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CoordinateReferenceSystemAdapter  extends XmlAdapter<String, CoordinateReferenceSystem> {

    /**
     * Fallback CRS
     */
    private static final CoordinateReferenceSystem DEFAULT_CRS = CommonCRS.WGS84.normalizedGeographic();

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

                    CoordinateReferenceSystem candidate;
                    if (CRS.equivalent(crs, DEFAULT_CRS) ||
                        org.apache.sis.referencing.CRS.findOperation(crs, DEFAULT_CRS, null).getMathTransform().isIdentity()) {
                        candidate = DEFAULT_CRS;
                    } else {
                        candidate = crs;
                    }

                    srsName = IdentifiedObjects.lookupURN(candidate, Citations.EPSG);
                    if (srsName == null) {
                        srsName = IdentifiedObjects.lookupURN(candidate, null);
                        if (srsName == null) {
                            srsName = IdentifiedObjects.getIdentifierOrName(candidate);
                        }
                    }
                    CoordinateReferenceSystemAdapter.cachedIdentifier.put(crs, srsName);
                }
            } catch (FactoryException ex) {
                Logger.getLogger("org.geotoolkit.referencing").log(Level.WARNING, null, ex);
            }
        }
        return srsName;
    }
}
