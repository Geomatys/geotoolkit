

package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPolyhedralSurface;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
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

    public PolyhedralSurfaceType(JTSPolyhedralSurface poly) {
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
    public void setPatches(List<JTSPolygon> patches) {
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
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateReferenceSystem) {
        this.coordinateReferenceSystem = coordinateReferenceSystem;
    }

    public JTSPolyhedralSurface getIsoPolyHedralSurface() {

        JTSPolyhedralSurface result = new JTSPolyhedralSurface(coordinateReferenceSystem);
        for (JTSSurfaceBoundary s : patchList.getPatches()) {
            s.setCoordinateReferenceSystem(coordinateReferenceSystem);
            result.getPatches().add(new JTSPolygon(s));
        }
        return result;
    }
}
