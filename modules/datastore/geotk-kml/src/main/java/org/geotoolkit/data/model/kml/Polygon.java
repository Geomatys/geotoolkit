package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Polygon extends AbstractGeometry {

    public boolean getExtrude();
    public boolean getTessellate();
    public AltitudeMode getAltitudeMode();
    public Boundary getOuterBoundaryIs();
    public List<Boundary> getInnerBoundariesAre();
    public List<SimpleType> getPolygonSimpleExtensions();
    public List<AbstractObject> getPolygonObjectExtensions();
}
