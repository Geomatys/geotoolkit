
package org.geotoolkit.internal.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
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
        return new JTSPolygon(new JTSSurfaceBoundary(coordinateReferenceSystem, exterior, interior));
    }
}
