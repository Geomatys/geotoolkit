package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Point extends AbstractGeometry{

    public boolean getExtrude();
    public AltitudeMode getAltitudeMode();
    public Coordinates getCoordinates();
    public List<SimpleType> getPointSimpleExtensions();
    public List<AbstractObject> getPointObjectExtensions();
}
