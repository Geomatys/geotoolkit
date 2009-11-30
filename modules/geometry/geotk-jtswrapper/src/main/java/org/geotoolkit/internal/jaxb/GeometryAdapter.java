

package org.geotoolkit.internal.jaxb;

import org.opengis.geometry.Geometry;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSEnvelope;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex.JTSCompositeCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;

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

    @XmlElementRef(name="LineString", namespace = "http://www.opengis.net/gml")
    private JAXBElement<LineStringPosListType> lineString;

    @XmlElementRef(name="MultiPoint", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSMultiPoint> multiPoint;

    @XmlElementRef(name="MultiGeometry", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSMultiPrimitive> multiPrimitive;

    @XmlElementRef(name="CompositeCurve", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSCompositeCurve> compositeCurve;

    @XmlElementRef(name="Envelope", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSEnvelope> envelope;

    @XmlElementRef(name="PolyhedralSurface", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSPolyhedralSurface> polyhedralSurface;

    @XmlElementRef(name="Polygon", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSPolygon> polygon;

    @XmlElementRef(name="LinearRing", namespace = "http://www.opengis.net/gml")
    private JAXBElement<JTSRing> ring;

    public GeometryAdapter() {

    }

    public GeometryAdapter(Geometry geom) {
        if (geom instanceof JTSPoint) {
            this.point = FACTORY.createJTSPoint((JTSPoint) geom);
        } else if (geom instanceof JTSCurve) {
            this.curve = FACTORY.createJTSCurve((JTSCurve) geom);
        } else if (geom instanceof JTSLineString) {
            this.lineString = FACTORY.createLineStringPosListType(new LineStringPosListType((JTSLineString)geom));
        } else if (geom instanceof JTSMultiPoint) {
            this.multiPoint = FACTORY.createJTSMultiPoint((JTSMultiPoint) geom);
        } else if (geom instanceof JTSCompositeCurve) {
            this.compositeCurve = FACTORY.createJTSCompositeCurve((JTSCompositeCurve) geom);
        } else if (geom instanceof JTSEnvelope) {
            this.envelope = FACTORY.createJTSEnvelope((JTSEnvelope) geom);
        } else if (geom instanceof JTSPolyhedralSurface) {
            this.polyhedralSurface = FACTORY.createJTSPolyhedralSurface((JTSPolyhedralSurface) geom);
        } else if (geom instanceof JTSPolygon) {
            this.polygon = FACTORY.createJTSPolygon((JTSPolygon) geom);
        } else if (geom instanceof JTSMultiPrimitive) {
            this.multiPrimitive = FACTORY.createJTSMultiGeometry((JTSMultiPrimitive) geom);
        } else if (geom instanceof JTSRing) {
            this.ring = FACTORY.createJTSRing((JTSRing) geom);
        } else if (geom != null) {
            System.out.println("unexpected geometry:" + geom.getClass().getName());
        }
    }


    @Override
    public Geometry unmarshal(GeometryAdapter v) throws Exception {
        if (v != null && v.point != null) {
            return (Geometry) v.point.getValue();

        } else if (v != null && v.curve != null) {
            return (Geometry) v.curve.getValue();

        } else if (v != null && v.lineString != null) {
            return (Geometry) v.lineString.getValue();

        } else if (v != null && v.compositeCurve != null) {
            return (Geometry) v.compositeCurve.getValue();

        } else if (v != null && v.multiPoint != null) {
            return (Geometry) v.multiPoint.getValue();

        } else if (v != null && v.envelope != null) {
            return (Geometry) v.envelope.getValue();

        } else if (v != null && v.polygon != null) {
            return (Geometry) v.polygon.getValue();

        } else if (v != null && v.polyhedralSurface != null) {
            PolyhedralSurfaceType poly = (PolyhedralSurfaceType) v.polyhedralSurface.getValue();
            return poly.getIsoPolyHedralSurface();

        } else if (v != null && v.ring != null) {
            return (Geometry) v.ring.getValue();
        
        } else if (v != null && v.multiPrimitive != null) {
            return (Geometry) v.multiPrimitive.getValue();
        }
        return null;
    }

    @Override
    public GeometryAdapter marshal(Geometry v) throws Exception {
        return new GeometryAdapter(v);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[GeometryAdapter]\n");
        if (point != null) {
            sb.append(point.getValue());

        }
        if (curve != null) {
            sb.append(curve.getValue());

        }
        if (lineString != null) {
            sb.append(lineString.getValue());

        }
        if (compositeCurve != null) {
            sb.append(compositeCurve.getValue());

        }
        if (multiPoint != null) {
            sb.append(multiPoint.getValue());

        }
        if (envelope != null) {
            sb.append(envelope.getValue());

        } else if (polygon != null) {
            sb.append(polygon.getValue());

        } else if (polyhedralSurface != null) {
            sb.append("polyHeadralSurface=>").append(polyhedralSurface.getValue());

        } else if (ring != null) {
            sb.append(ring.getValue());
        
        } else if (multiPrimitive != null) {
            sb.append(multiPrimitive.getValue());
        }
        return sb.toString();
    }
}
