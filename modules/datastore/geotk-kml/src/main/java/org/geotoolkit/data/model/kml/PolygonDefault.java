package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class PolygonDefault extends AbstractGeometryDefault implements Polygon {

    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Boundary outerBoundaryIs;
    private List<Boundary> innerBoundariesAre;
    private List<SimpleType> polygonSimpleExtensions;
    private List<AbstractObject> polygonObjectExtensions;

    public PolygonDefault(List<SimpleType> objectSimpleExtensions,
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
        this.innerBoundariesAre = innerBoundariesAre;
        this.polygonSimpleExtensions = polygonSimpleExtensions;
        this.polygonObjectExtensions = polygonObjectExtensions;
    }

    @Override
    public boolean getExtrude() {return this.extrude;}

    @Override
    public boolean getTessellate() {return this.tessellate;}

    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    @Override
    public Boundary getOuterBoundaryIs() {return this.outerBoundaryIs;}

    @Override
    public List<Boundary> getInnerBoundariesAre() {return this.innerBoundariesAre;}

    @Override
    public List<SimpleType> getPolygonSimpleExtensions() {return this.polygonSimpleExtensions;}

    @Override
    public List<AbstractObject> getPolygonObjectExtensions() {return this.polygonObjectExtensions;}

}
