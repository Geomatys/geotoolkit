package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface GroundOverlay extends AbstractOverlay {

    public double getAltitude();
    public AltitudeMode getAltitudeMode();
    public LatLonBox getLatLonBox();
    public List<SimpleType> getGroundOverlaySimpleExtensions();
    public List<AbstractObject> getGroundOverlayObjectExtensions();

}
