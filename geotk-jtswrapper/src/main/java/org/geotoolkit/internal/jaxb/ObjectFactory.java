

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSEnvelope;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiPrimitive;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.complex.JTSCompositeCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPoint;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.opengis.geometry.Geometry;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName POINT_QNAME              = new QName("http://www.opengis.net/gml", "Point");
    private static final QName VALUE_QNAME              = new QName("http://www.opengis.net/wfs", "Value");
    private static final QName LINE_STRING_QNAME        = new QName("http://www.opengis.net/gml", "LineString");
    private static final QName RING_QNAME               = new QName("http://www.opengis.net/gml", "LinearRing");
    private static final QName CURVE_QNAME              = new QName("http://www.opengis.net/gml", "Curve");
    private static final QName ENVELOPE_QNAME           = new QName("http://www.opengis.net/gml", "Envelope");
    private static final QName MULTI_POINT_QNAME        = new QName("http://www.opengis.net/gml", "MultiPoint");
    private static final QName MULTI_CURVE_QNAME        = new QName("http://www.opengis.net/gml", "MultiCurve");
    private static final QName MULTI_SURFACE_QNAME      = new QName("http://www.opengis.net/gml", "MultiSurface");
    private static final QName MULTI_POLYGON_QNAME        = new QName("http://www.opengis.net/gml", "MultiPolygon");
    private static final QName COMPOSITE_CURVE_QNAME    = new QName("http://www.opengis.net/gml", "CompositeCurve");
    private static final QName POLYHEDRAL_SURFACE_QNAME = new QName("http://www.opengis.net/gml", "PolyhedralSurface");
    private static final QName POLYGON_QNAME            = new QName("http://www.opengis.net/gml", "Polygon");
    private static final QName MULTI_GEOMETRY_QNAME     = new QName("http://www.opengis.net/gml", "MultiGeometry");

    public JTSPoint createJTSPoint() {
        return new JTSPoint();
    }

    public JTSCurve createJTSCurve() {
        return new JTSCurve();
    }

    public JTSLineString createJTSLineString() {
        return new JTSLineString();
    }

    public JTSMultiPoint createJTSMultiPoint() {
        return new JTSMultiPoint();
    }

    public JTSMultiCurve createJTSMultiCurve() {
        return new JTSMultiCurve();
    }

    public JTSMultiSurface createJTSMultiSurface() {
        return new JTSMultiSurface();
    }

    public JTSEnvelope createJTSEnvelope() {
        return new JTSEnvelope();
    }

    public JTSPolygon createJTSPolygon() {
        return new JTSPolygon();
    }

    public JTSMultiPolygon createJTSMultiPolygon() {
        return new JTSMultiPolygon();
    }

    public PolygonType createPolygonType() {
        return new PolygonType();
    }

    public JTSRing createJTSRing() {
        return new JTSRing();
    }

    public JTSSurfaceBoundary createJTSSurfaceBoundary() {
        return new JTSSurfaceBoundary();
    }

    public LineStringPosListType createLineStringPosListType() {
        return new LineStringPosListType();
    }

    public CoordinatesType createCoordinatesType() {
        return new CoordinatesType();
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Value")
    public JAXBElement<Object> createValue(final Object value) {
        return new JAXBElement<Object>(VALUE_QNAME, Object.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Point")
    public JAXBElement<JTSPoint> createJTSPoint(final JTSPoint value) {
        return new JAXBElement<JTSPoint>(POINT_QNAME, JTSPoint.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LineString")
    public JAXBElement<LineStringPosListType> createLineStringPosListType(final LineStringPosListType value) {
        return new JAXBElement<LineStringPosListType>(LINE_STRING_QNAME, LineStringPosListType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "LinearRing")
    public JAXBElement<JTSRing> createJTSRing(final JTSRing value) {
        return new JAXBElement<JTSRing>(RING_QNAME, JTSRing.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Curve")
    public JAXBElement<JTSCurve> createJTSCurve(final JTSCurve value) {
        return new JAXBElement<JTSCurve>(CURVE_QNAME, JTSCurve.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Envelope")
    public JAXBElement<JTSEnvelope> createJTSEnvelope(final JTSEnvelope value) {
        return new JAXBElement<JTSEnvelope>(ENVELOPE_QNAME, JTSEnvelope.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiPoint")
    public JAXBElement<JTSMultiPoint> createJTSMultiPoint(final JTSMultiPoint value) {
        return new JAXBElement<JTSMultiPoint>(MULTI_POINT_QNAME, JTSMultiPoint.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiSurface")
    public JAXBElement<JTSMultiSurface> createJTSMultiSurface(final JTSMultiSurface value) {
        return new JAXBElement<JTSMultiSurface>(MULTI_SURFACE_QNAME, JTSMultiSurface.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiCurve")
    public JAXBElement<JTSMultiCurve> createJTSMultiCurve(final JTSMultiCurve value) {
        return new JAXBElement<JTSMultiCurve>(MULTI_CURVE_QNAME, JTSMultiCurve.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiPolygon")
    public JAXBElement<JTSMultiPolygon> createJTSMultiPolygon(final JTSMultiPolygon value) {
        return new JAXBElement<JTSMultiPolygon>(MULTI_POLYGON_QNAME, JTSMultiPolygon.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "MultiGeometry")
    public JAXBElement<JTSMultiPrimitive> createJTSMultiGeometry(final JTSMultiPrimitive value) {
        return new JAXBElement<JTSMultiPrimitive>(MULTI_GEOMETRY_QNAME, JTSMultiPrimitive.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "CompositeCurve")
    public JAXBElement<JTSCompositeCurve> createJTSCompositeCurve(final JTSCompositeCurve value) {
        return new JAXBElement<JTSCompositeCurve>(COMPOSITE_CURVE_QNAME, JTSCompositeCurve.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "PolyhedralSurface")
    @XmlJavaTypeAdapter(PolyhedralSurfaceAdapter.class)
    public JAXBElement<JTSPolyhedralSurface> createJTSPolyhedralSurface(final JTSPolyhedralSurface value) {
        return new JAXBElement<JTSPolyhedralSurface>(POLYHEDRAL_SURFACE_QNAME, JTSPolyhedralSurface.class, null, value);
    }

    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Polygon")
    @XmlJavaTypeAdapter(PolygonAdapter.class)
    public JAXBElement<JTSPolygon> createJTSPolygon(final JTSPolygon value) {
        return new JAXBElement<JTSPolygon>(POLYGON_QNAME, JTSPolygon.class, null, value);
    }

    public JAXBElement<?> buildAnyGeometry(final Geometry value) {
        if (value instanceof JTSPoint) {
            return createJTSPoint((JTSPoint) value);

        } else if (value instanceof JTSMultiPoint) {
            return createJTSMultiPoint((JTSMultiPoint) value);

        } else if (value instanceof JTSMultiSurface) {
            return createJTSMultiSurface((JTSMultiSurface) value);

        } else if (value instanceof JTSMultiCurve) {
            return createJTSMultiCurve((JTSMultiCurve) value);

        } else if (value instanceof JTSCompositeCurve) {
            return createJTSCompositeCurve((JTSCompositeCurve) value);

        } else if (value instanceof JTSCurve) {
            return createJTSCurve((JTSCurve) value);

        } else if (value instanceof JTSLineString) {
            return createLineStringPosListType(new LineStringPosListType((JTSLineString) value));

        } else if (value instanceof JTSEnvelope) {
            return createJTSEnvelope((JTSEnvelope) value);

        } else if (value instanceof JTSMultiPrimitive) {
            return createJTSMultiGeometry((JTSMultiPrimitive) value);

        } else if (value instanceof JTSMultiPolygon) {
            return createJTSMultiPolygon((JTSMultiPolygon) value);

        } else if (value instanceof JTSPolygon) {
            return createJTSPolygon((JTSPolygon) value);

        } else if (value instanceof JTSPolyhedralSurface) {
            return createJTSPolyhedralSurface((JTSPolyhedralSurface) value);

        } else if (value instanceof JTSRing) {
            return createJTSRing((JTSRing) value);

        } else if (value != null) {
            throw new IllegalArgumentException("unexpected geometry type:" + value.getClass().getName());
        }
        return null;
    }

}
