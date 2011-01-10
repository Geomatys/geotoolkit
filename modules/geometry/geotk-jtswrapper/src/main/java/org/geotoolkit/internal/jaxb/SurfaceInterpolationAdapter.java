
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.geometry.primitive.SurfaceInterpolation;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SurfaceInterpolationAdapter  extends XmlAdapter<String, SurfaceInterpolation>{

    @Override
    public SurfaceInterpolation unmarshal(final String v) throws Exception {
        return SurfaceInterpolation.valueOf(v);
    }

    @Override
    public String marshal(final SurfaceInterpolation v) throws Exception {
        if (v != null)
            return v.identifier();
        return null;
    }

}
