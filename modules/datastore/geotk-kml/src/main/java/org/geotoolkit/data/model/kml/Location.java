package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Location extends AbstractObject {

    public Angle180 getLongitude();
    public Angle90 getLatitude();
    public double getAltitude();
    public List<SimpleType> getLocationSimpleExtensions();
    public List<AbstractObject> getLocationObjectExtensions();

}