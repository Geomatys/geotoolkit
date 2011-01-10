

package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.AbstractJTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.geotoolkit.internal.jaxb.PolygonPropertyAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal
 */
@XmlType(name="MultiPolygonType", namespace="http://www.opengis.net/gml")
public class JTSMultiPolygon extends AbstractJTSGeometry {

     private Set<JTSPolygon> elements = new LinkedHashSet();

    public JTSMultiPolygon() {
        super();
    }

    public JTSMultiPolygon(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected com.vividsolutions.jts.geom.Geometry computeJTSPeer() {
        List<com.vividsolutions.jts.geom.Geometry> childParts = new ArrayList<com.vividsolutions.jts.geom.Geometry>();
        for(Polygon prim : elements) {
            if(prim instanceof JTSGeometry){
                JTSGeometry jtsGeom = (JTSGeometry) prim;
                childParts.add(jtsGeom.getJTSGeometry());
            }else{
                throw new IllegalStateException("Only JTSGeometries are allowed in the JTSMultiPolygon class.");
            }
        }

        // we want a multi geometry event if there is only one geometry
        if (childParts.size() == 1) {
            com.vividsolutions.jts.geom.Geometry geom = childParts.get(0);
            if (geom instanceof com.vividsolutions.jts.geom.Polygon) {
                return JTSUtils.GEOMETRY_FACTORY.createMultiPolygon(new com.vividsolutions.jts.geom.Polygon[] {(com.vividsolutions.jts.geom.Polygon)geom});
            }

        }

        return JTSUtils.GEOMETRY_FACTORY.buildGeometry(childParts);
    }

    /**
     * {@inheritDoc }
     */
    public Set<JTSPolygon> getElements() {
        return elements;
    }

    @XmlElement(name="polygonMember", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(PolygonPropertyAdapter.class)
    public void setElements(final Set<JTSPolygon> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("elements:").append('\n');
        for (Polygon g : elements) {
            sb.append(g).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object instanceof JTSMultiPolygon && super.equals(object)) {
            JTSMultiPolygon that = (JTSMultiPolygon) object;
            return Utilities.equals(this.elements, that.elements);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + (this.elements != null ? this.elements.hashCode() : 0);
        return hash;
    }

    public void applyCRSOnchild() {

        for (JTSPolygon polygon : elements) {
            JTSRing exterior = (JTSRing) polygon.getBoundary().getExterior();
            exterior.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
            
            for (Primitive p : (Collection<Primitive>) exterior.getElements()) {
                if (p instanceof JTSCurve) {
                    JTSCurve curve = (JTSCurve) p;
                    curve.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                    for (CurveSegment cv : curve.getSegments()) {
                        if (cv instanceof JTSLineString) {
                            JTSLineString line = (JTSLineString) cv;
                            line.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                            PointArray pa = line.getControlPoints();
                            List<Position> newPositions = new ArrayList<Position>();
                            for (Position pos : pa.positions()) {
                                if (pos instanceof GeneralDirectPosition) {
                                    ((GeneralDirectPosition) pos).setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                                    newPositions.add(pos);
                                }
                            }
                            line.getControlPoints().clear();
                            line.getControlPoints().addAll(newPositions);
                        }
                    }
                }
            }
            if (polygon.getBoundary().getInteriors() != null) {
                for (Ring ring : polygon.getBoundary().getInteriors()) {
                    ((JTSRing) ring).setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                    for (Primitive p : ring.getElements()) {
                        if (p instanceof JTSCurve) {
                            JTSCurve curve = (JTSCurve) p;
                            curve.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                            for (CurveSegment cv : curve.getSegments()) {
                                if (cv instanceof JTSLineString) {
                                    JTSLineString line = (JTSLineString) cv;
                                    line.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
                                    PointArray pa = line.getControlPoints();
                                    List<Position> newPositions = new ArrayList<Position>();
                                    for (Position pos : pa.positions()) {
                                        if (pos instanceof GeneralDirectPosition) {
                                            ((GeneralDirectPosition) pos).setCoordinateReferenceSystem(getCoordinateReferenceSystem());
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
            polygon.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
        }

    }
}
