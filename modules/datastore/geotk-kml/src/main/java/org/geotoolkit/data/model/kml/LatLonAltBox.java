package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LatLonAltBox extends AbstractLatLonBox{

    public double getMinAltitude();
    public double getMaxAltitude();
    public AltitudeMode getAltitudeMode();
    public List<SimpleType> getLatLonAltBoxSimpleExtensions();
    public List<AbstractObject> getLatLonAltBoxObjectExtensions();

}
