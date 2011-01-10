
package org.geotoolkit.internal.jaxb;

import java.util.logging.Level;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.util.logging.Logging;
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
            return CRS.decode(v);
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
                    srsName = CRS.lookupIdentifier(Citations.URN_OGC, crs, false);
                    if (srsName == null) {
                        srsName = CRS.getDeclaredIdentifier(crs);
                    }
                    CoordinateReferenceSystemAdapter.cachedIdentifier.put(crs, srsName);

                }
            } catch (FactoryException ex) {
                Logging.getLogger(DirectPositionType.class).log(Level.WARNING, null, ex);
            }
        }
        return srsName;
    }
}
