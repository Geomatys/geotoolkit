

package org.geotoolkit.internal.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.geometry.primitive.CurveInterpolation;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CurveInterpolationAdapter extends XmlAdapter<String, CurveInterpolation>{

    @Override
    public CurveInterpolation unmarshal(final String v) throws Exception {
        return CurveInterpolation.valueOf(v);
    }

    @Override
    public String marshal(final CurveInterpolation v) throws Exception {
        if (v != null)
            return v.identifier().orElse(null);
        return null;
    }
}
