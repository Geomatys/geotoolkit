package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Model extends AbstractGeometry {

    public AltitudeMode getAltitudeMode();
    public Location getLocation();
    public Orientation getOrientation();
    public Scale getScale();
    public Link getLink();
    public ResourceMap getRessourceMap();
    public List<SimpleType> getModelSimpleExtensions();
    public List<AbstractObject> getModelObjectExtensions();

}
