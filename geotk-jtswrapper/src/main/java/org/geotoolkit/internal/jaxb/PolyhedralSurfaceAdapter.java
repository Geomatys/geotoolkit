
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class PolyhedralSurfaceAdapter extends XmlAdapter<PolyhedralSurfaceType, JTSPolyhedralSurface> {



    @Override
    public JTSPolyhedralSurface unmarshal(final PolyhedralSurfaceType v) throws Exception {
        if (v != null) {
            JTSPolyhedralSurface result = new JTSPolyhedralSurface(v.getCoordinateReferenceSystem());
            result.getPatches().addAll(v.getPatches());
        }
        return null;
    }

    @Override
    public PolyhedralSurfaceType marshal(final JTSPolyhedralSurface v) throws Exception {
        return new PolyhedralSurfaceType(v);
    }

}
