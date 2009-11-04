

package org.geotoolkit.internal.jaxb;

import org.opengis.geometry.Geometry;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.Point;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GeometryAdapter<T> extends XmlAdapter<GeometryAdapter, Geometry> {

    private static ObjectFactory FACTORY = new ObjectFactory();
    
    @XmlElementRef(name="Point", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSPoint> point;

    @XmlElementRef(name="Curve", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSCurve> curve;

    public GeometryAdapter() {

    }

    public GeometryAdapter(Geometry geom) {
        if (geom instanceof JTSPoint) {
            this.point = FACTORY.createJTSPoint((JTSPoint) geom);
        } else if (geom instanceof JTSCurve) {
            this.curve = FACTORY.createJTSCurve((JTSCurve) geom);
        }
    }


    @Override
    public Geometry unmarshal(GeometryAdapter v) throws Exception {
        if (v != null && v.point != null) {
            return (Geometry) v.point.getValue();
        } else if (v != null && v.curve != null) {
            return (Geometry) v.curve.getValue();
        }
        return null;
    }

    @Override
    public GeometryAdapter marshal(Geometry v) throws Exception {
        return new GeometryAdapter(v);
    }

}
