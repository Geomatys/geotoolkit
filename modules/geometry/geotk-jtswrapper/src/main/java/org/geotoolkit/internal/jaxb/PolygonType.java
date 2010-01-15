
package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PolygonType", namespace="http://www.opengis.net/gml")
public class PolygonType {

    /**
     * CRS for this geometry.
     */
    @XmlAttribute(name="srsName")
    @XmlJavaTypeAdapter(CoordinateReferenceSystemAdapter.class)
    private CoordinateReferenceSystem coordinateReferenceSystem;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(RingAdapter.class)
    private Ring exterior;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(RingAdapter.class)
    private List<Ring> interior;

    public PolygonType() {

    }

    public PolygonType(JTSPolygon poly) {
        if (poly != null && poly.getBoundary() != null) {
            this.coordinateReferenceSystem = poly.getBoundary().getCoordinateReferenceSystem();
            this.exterior                  = poly.getBoundary().getExterior();
            this.interior                  = poly.getBoundary().getInteriors();
        }
    }

    /**
     * @return the exterior
     */
    public Ring getExterior() {
        return exterior;
    }

    /**
     * @param exterior the exterior to set
     */
    public void setExterior(Ring exterior) {
        this.exterior = exterior;
    }

    /**
     * @return the interior
     */
    public List<Ring> getInterior() {
        return interior;
    }

    /**
     * @param interior the interior to set
     */
    public void setInterior(List<Ring> interior) {
        this.interior = interior;
    }

    /**
     * @return the coordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return coordinateReferenceSystem;
    }

    /**
     * @param coordinateReferenceSystem the coordinateReferenceSystem to set
     */
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateReferenceSystem) {
        this.coordinateReferenceSystem = coordinateReferenceSystem;
    }

    public SurfaceBoundary getSurfaceBoundary() {
        return new JTSSurfaceBoundary(coordinateReferenceSystem, exterior, interior);
    }

    public JTSPolygon getJTSPolygon() {
        ((JTSRing)exterior).setCoordinateReferenceSystem(coordinateReferenceSystem);
            for (Primitive p : exterior.getElements()) {
                if (p instanceof JTSCurve) {
                    JTSCurve curve = (JTSCurve) p;
                    curve.setCoordinateReferenceSystem(coordinateReferenceSystem);
                    for (CurveSegment cv :curve.getSegments()) {
                        if (cv instanceof JTSLineString) {
                            JTSLineString line = (JTSLineString) cv;
                            line.setCoordinateReferenceSystem(coordinateReferenceSystem);
                            PointArray pa = line.getControlPoints();
                            List<Position> newPositions = new ArrayList<Position>();
                            for (Position pos : pa.positions()) {
                                if (pos instanceof GeneralDirectPosition) {
                                    ((GeneralDirectPosition)pos).setCoordinateReferenceSystem(coordinateReferenceSystem);
                                    newPositions.add(pos);
                                }
                            }
                            line.getControlPoints().clear();
                            line.getControlPoints().addAll(newPositions);
                        }
                    }
                }
            }
         if (interior != null) {
            for (Ring ring : interior) {
                ((JTSRing)ring).setCoordinateReferenceSystem(coordinateReferenceSystem);
                for (Primitive p : ring.getElements()) {
                    if (p instanceof JTSCurve) {
                        JTSCurve curve = (JTSCurve) p;
                        curve.setCoordinateReferenceSystem(coordinateReferenceSystem);
                        for (CurveSegment cv :curve.getSegments()) {
                            if (cv instanceof JTSLineString) {
                                JTSLineString line = (JTSLineString) cv;
                                line.setCoordinateReferenceSystem(coordinateReferenceSystem);
                                PointArray pa = line.getControlPoints();
                                List<Position> newPositions = new ArrayList<Position>();
                                for (Position pos : pa.positions()) {
                                    if (pos instanceof GeneralDirectPosition) {
                                        ((GeneralDirectPosition)pos).setCoordinateReferenceSystem(coordinateReferenceSystem);
                                        newPositions.add(pos);
                                    }
                                }
                                line.getControlPoints().clear();
                                line.getControlPoints().addAll(newPositions);
                            }
                        }
                    }
                }
            }
        } else {
            interior = new ArrayList<Ring>();
        }
        return new JTSPolygon(new JTSSurfaceBoundary(coordinateReferenceSystem, exterior, interior));
    }
}
