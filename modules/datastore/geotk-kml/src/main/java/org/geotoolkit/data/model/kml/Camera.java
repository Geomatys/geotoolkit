package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Camera extends AbstractView {

    public Angle180 getLongitude();
    public Angle90 getLatitude();
    public double getAltitude();
    public Angle360 getHeading();
    public Anglepos180 getTilt();
    public Angle180 getRoll();
    public List<SimpleType> getCameraSimpleExtensions();
    public List<AbstractObject> getCameraObjectExtensions();

}
