/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;

/**
 *
 * @author guilhem
 */
public class PolygonPropertyAdapter extends XmlAdapter<PolygonPropertyAdapter, JTSPolygon> {

    @XmlElement(name= "Polygon", namespace = "http://www.opengis.net/gml")
    private PolygonType polygon;

    public PolygonPropertyAdapter() {

    }

    public PolygonPropertyAdapter(PolygonType polygon) {
        this.polygon = polygon;
    }

    @Override
    public JTSPolygon unmarshal(PolygonPropertyAdapter v) throws Exception {
        if (v != null && v.polygon != null) {
            return new JTSPolygon(v.polygon.getSurfaceBoundary());
        }
        return null;
    }

    @Override
    public PolygonPropertyAdapter marshal(JTSPolygon v) throws Exception {
        return new PolygonPropertyAdapter(new PolygonType(v));
    }
}
