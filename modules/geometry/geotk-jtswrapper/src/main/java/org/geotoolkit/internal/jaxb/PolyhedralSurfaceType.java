

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
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
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
@XmlType(name="PolyhedralSurfaceType", namespace="http://www.opengis.net/gml")
public class PolyhedralSurfaceType {

    /**
     * CRS for this geometry.
     */
    @XmlAttribute(name="srsName")
    @XmlJavaTypeAdapter(CoordinateReferenceSystemAdapter.class)
    private CoordinateReferenceSystem coordinateReferenceSystem;

    @XmlElement(name = "polygonPatches", namespace = "http://www.opengis.net/gml")
    private PolygonPatchesListType patchList;

    public PolyhedralSurfaceType() {

    }

    public PolyhedralSurfaceType(final JTSPolyhedralSurface poly) {
        this.coordinateReferenceSystem = poly.getCoordinateReferenceSystem();
        this.patchList = new PolygonPatchesListType(poly.getPatches());

    }

    /**
     * @return the patches
     */
    public List<JTSPolygon> getPatches() {
        List<JTSPolygon> result = new ArrayList<JTSPolygon>();
        for (SurfaceBoundary sb : patchList.getPatches()) {
            result.add(new JTSPolygon(sb));
        }
        return result;
    }

    /**
     * @param patches the patches to set
     */
    public void setPatches(final List<JTSPolygon> patches) {
        this.patchList.setPolygonPatches(patches);
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
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem coordinateReferenceSystem) {
        this.coordinateReferenceSystem = coordinateReferenceSystem;
    }

    public JTSPolyhedralSurface getIsoPolyHedralSurface() {

        JTSPolyhedralSurface result = new JTSPolyhedralSurface(coordinateReferenceSystem);
        for (JTSSurfaceBoundary s : patchList.getPatches()) {
            s.setCoordinateReferenceSystem(coordinateReferenceSystem);
            ((JTSRing)s.getExterior()).setCoordinateReferenceSystem(coordinateReferenceSystem);
            for (Primitive p : s.getExterior().getElements()) {
                if (p instanceof JTSCurve) {
                    JTSCurve curve = (JTSCurve) p;
                    curve.setCoordinateReferenceSystem(coordinateReferenceSystem);
                    for (CurveSegment cv :curve.getSegments()) {
                        if (cv instanceof JTSLineString) {
                            JTSLineString line = (JTSLineString) cv;
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

            if (s.getInteriors() != null) {
                for (Ring ring : s.getInteriors()) {
                    ((JTSRing)ring).setCoordinateReferenceSystem(coordinateReferenceSystem);
                    for (Primitive p : ring.getElements()) {
                        if (p instanceof JTSCurve) {
                            JTSCurve curve = (JTSCurve) p;
                            curve.setCoordinateReferenceSystem(coordinateReferenceSystem);
                            for (CurveSegment cv :curve.getSegments()) {
                                if (cv instanceof JTSLineString) {
                                    JTSLineString line = (JTSLineString) cv;
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
            }
            result.getPatches().add(new JTSPolygon(s));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[polyHedralSurfaceType]\n");
        if (coordinateReferenceSystem != null) {
            sb.append("crs:").append(coordinateReferenceSystem);
        }
        if (patchList != null) {
            sb.append("patchList:").append(patchList);
        }
        return sb.toString();
    }
}
