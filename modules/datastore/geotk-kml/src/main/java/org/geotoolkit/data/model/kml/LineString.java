package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LineString extends AbstractGeometry {

    public boolean getExtrude();
    public boolean getTessellate();
    public AltitudeMode getAltitudeMode();
    public Coordinates getCoordinates();
    public List<SimpleType> getLineStringSimpleExtensions();
    public List<AbstractObject> getLineStringObjectExtensions();

}
