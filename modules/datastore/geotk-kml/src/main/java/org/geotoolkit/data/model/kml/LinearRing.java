package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LinearRing extends AbstractGeometry {

    public boolean getExtrude();
    public boolean getTessellate();
    public AltitudeMode getAltitudeMode();
    public Coordinates getCoordinates();
    public List<SimpleType> getLinearRingSimpleExtensions();
    public List<AbstractObject> getLinearRingObjectExtensions();

}
