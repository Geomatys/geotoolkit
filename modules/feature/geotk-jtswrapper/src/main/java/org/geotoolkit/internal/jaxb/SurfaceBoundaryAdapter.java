

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SurfaceBoundaryAdapter extends XmlAdapter<JTSSurfaceBoundary, SurfaceBoundary> {

    @Override
    public SurfaceBoundary unmarshal(final JTSSurfaceBoundary v) throws Exception {
        return v;
    }

    @Override
    public JTSSurfaceBoundary marshal(final SurfaceBoundary v) throws Exception {
        if (v instanceof JTSSurfaceBoundary) {
            return (JTSSurfaceBoundary) v;
        }
        return null;
    }

}
