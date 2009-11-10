
package org.geotoolkit.internal.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CoordinateReferenceSystemAdapter  extends XmlAdapter<String, CoordinateReferenceSystem> {

    public static final Map<CoordinateReferenceSystem, String> cachedIdentifier = new HashMap<CoordinateReferenceSystem, String>();

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
                identifier = CRS.lookupIdentifier(v, false);
                cachedIdentifier.put(v, identifier);
            }
            return identifier;
        }
        return null;
    }

}
