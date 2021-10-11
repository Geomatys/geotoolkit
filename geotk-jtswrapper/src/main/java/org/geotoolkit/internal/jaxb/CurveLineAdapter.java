

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.opengis.geometry.primitive.OrientableCurve;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CurveLineAdapter<T> extends XmlAdapter<CurveLineAdapter, OrientableCurve> {

    private static ObjectFactory FACTORY = new ObjectFactory();


    @XmlElementRef(name="LineString", namespace = "http://www.opengis.net/gml")
    private JAXBElement<LineStringPosListType> lineString;

    public CurveLineAdapter() {

    }

    public CurveLineAdapter(final OrientableCurve curve) {
        if (curve instanceof JTSCurve) {
            JTSLineString line = (JTSLineString) ((JTSCurve)curve).getSegments().get(0);
            this.lineString = FACTORY.createLineStringPosListType(new LineStringPosListType(line));

        } else if (curve != null) {
            System.out.println("unexpected curve:" + curve.getClass().getName());
        }
    }


    @Override
    public OrientableCurve unmarshal(final CurveLineAdapter v) throws Exception {
        if (v != null && v.lineString != null && v.lineString.getValue() != null) {
            LineStringPosListType posList = (LineStringPosListType) v.lineString.getValue();
            JTSLineString line = posList.getJTSLineString();
            JTSCurve curve = new JTSCurve(null);
            curve.getSegments().add(line);
            return curve;

        }
        return null;
    }

    @Override
    public CurveLineAdapter marshal(final OrientableCurve v) throws Exception {
        return new CurveLineAdapter(v);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[CurveLineAdapter]\n");
        if (lineString != null) {
            sb.append(lineString.getValue());

        }
        return sb.toString();
    }
}
