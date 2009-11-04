
package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class PolygonAdapter extends XmlAdapter<PolygonType, JTSPolygon> {

    @Override
    public JTSPolygon unmarshal(PolygonType v) throws Exception {
        if (v != null) {
            return new JTSPolygon(v.getSurfaceBoundary());
        }
        return null;
    }

    @Override
    public PolygonType marshal(JTSPolygon v) throws Exception {
        return new PolygonType(v);
    }

}
