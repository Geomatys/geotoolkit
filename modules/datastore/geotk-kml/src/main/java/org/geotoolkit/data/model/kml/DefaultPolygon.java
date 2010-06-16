package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPolygon extends DefaultAbstractGeometry implements Polygon {

    private final boolean extrude;
    private final boolean tessellate;
    private final AltitudeMode altitudeMode;
    private final Boundary outerBoundaryIs;
    private final List<Boundary> innerBoundariesAre;
    private final List<SimpleType> polygonSimpleExtensions;
    private final List<AbstractObject> polygonObjectExtensions;

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

}
