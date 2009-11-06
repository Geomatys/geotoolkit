

package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSSurfaceBoundary;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PolygonPatchesListType {

    @XmlElement(name = "PolygonPatch", namespace = "http://www.opengis.net/gml")
    private List<JTSSurfaceBoundary> patches = new ArrayList();

    public PolygonPatchesListType() {

    }

    public PolygonPatchesListType(List<JTSPolygon> patches) {
        for (JTSPolygon polygon : patches) {
            JTSSurfaceBoundary patch = (JTSSurfaceBoundary) polygon.getBoundary();
            patch.setCoordinateReferenceSystem(null);
            this.patches.add(patch);
        }
            
    }

    /**
     * @return the patches
     */
    public List<JTSSurfaceBoundary> getPatches() {
        return patches;
    }

    /**
     * @param patches the patches to set
     */
    public void setPatches(List<JTSSurfaceBoundary> patches) {
        this.patches = patches;
    }

    /**
     * @param patches the patches to set
     */
    public void setPolygonPatches(List<JTSPolygon> patches) {
        this.patches = new ArrayList<JTSSurfaceBoundary>();
        for (JTSPolygon p : patches) {
            this.patches.add((JTSSurfaceBoundary) p.getBoundary());
        }
    }
}
