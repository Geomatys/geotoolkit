package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPolygon extends DefaultAbstractGeometry implements Polygon {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Boundary outerBoundaryIs;
    private List<Boundary> innerBoundariesAre;
    private List<SimpleType> polygonSimpleExtensions;
    private List<AbstractObject> polygonObjectExtensions;

    /**
     *
     */
    public DefaultPolygon(){
        this.extrude = DEF_EXTRUDE;
        this.tessellate = DEF_TESSELLATE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.innerBoundariesAre = EMPTY_LIST;
        this.polygonSimpleExtensions = EMPTY_LIST;
        this.polygonObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param outerBoundaryIs
     * @param innerBoundariesAre
     * @param polygonSimpleExtensions
     * @param polygonObjectExtensions
     */
    public DefaultPolygon(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundaryIs, List<Boundary> innerBoundariesAre,
            List<SimpleType> polygonSimpleExtensions, List<AbstractObject> polygonObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.extrude = extrude;
        this.tessellate = tessellate;
        this.altitudeMode = altitudeMode;
        this.outerBoundaryIs = outerBoundaryIs;
        this.innerBoundariesAre = (innerBoundariesAre == null) ? EMPTY_LIST : innerBoundariesAre;
        this.polygonSimpleExtensions = (polygonSimpleExtensions == null) ? EMPTY_LIST : polygonSimpleExtensions;
        this.polygonObjectExtensions = (polygonObjectExtensions == null) ? EMPTY_LIST : polygonObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getExtrude() {return this.extrude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getTessellate() {return this.tessellate;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Boundary getOuterBoundaryIs() {return this.outerBoundaryIs;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Boundary> getInnerBoundariesAre() {return this.innerBoundariesAre;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPolygonSimpleExtensions() {return this.polygonSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPolygonObjectExtensions() {return this.polygonObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtrude(boolean extrude) {
        this.extrude = extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTessellate(boolean tesselate) {
        this.tessellate = tesselate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOuterBoundaryIs(Boundary outerBoundaryIs) {
        this.outerBoundaryIs = outerBoundaryIs;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setInnerBoundariesAre(List<Boundary> innerBoundariesAre) {
        this.innerBoundariesAre = innerBoundariesAre;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolygonSimpleExtensions(List<SimpleType> polygonSimpleExtensions) {
        this.polygonSimpleExtensions = polygonSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPolygonObjectExtensions(List<AbstractObject> polygonObjectExtensions) {
        this.polygonObjectExtensions = polygonObjectExtensions;
    }

}
