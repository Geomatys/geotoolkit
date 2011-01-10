

package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LinearRingPosListType {

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private PosListType posList;

    public LinearRingPosListType() {

    }

    public LinearRingPosListType(final List<Double> value) {
        this.posList = new PosListType(value);
    }

    public LinearRingPosListType(final JTSRing ring) {
        //this.srsName = CoordinateReferenceSystemAdapter.getSrsName(lineString.getCoordinateReferenceSystem());

        if (ring.getElements().size() == 1) {
            Object curveObj = ring.getElements().iterator().next();
            if (curveObj instanceof Curve) {
                Curve curve = (Curve) curveObj;

                List<Double> value = new ArrayList<Double>();
                for (CurveSegment cv : curve.getSegments()) {
                    JTSLineString line = (JTSLineString) cv;

                    for (Position p : line.getPositions()) {
                        for (int i = 0; i < p.getDirectPosition().getDimension(); i++) {
                            value.add(p.getDirectPosition().getOrdinate(i));
                        }
                    }
                }
                posList = new PosListType(value);
            }
        } else {
            throw new IllegalArgumentException("the ring is not linear");
        }
    }

    public JTSRing getJTSRing() {

        JTSLineString line = new JTSLineString();
        for (int i = 0; i < posList.getValue().size() -1; i = i + 2) {
            double x = posList.getValue().get(i);
            double y = posList.getValue().get(i + 1);
            DirectPosition pos = new GeneralDirectPosition(x , y);
            line.getControlPoints().add(pos);
        }

        JTSCurve curve = new JTSCurve();
        curve.getSegments().add(line);
        JTSRing ring = new JTSRing();
        ring.getElements().add(curve);
        return ring;
    }

    /**
     * @return the posList
     */
    public PosListType getPosList() {
        return posList;
    }

    /**
     * @param posList the posList to set
     */
    public void setPosList(final PosListType posList) {
        this.posList = posList;
    }
}
