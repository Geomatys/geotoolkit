package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Orientation extends AbstractObject {

    public Angle360 getHeading();
    public Anglepos180 getTilt();
    public Angle180 getRoll();
    public List<SimpleType> getOrientationSimpleExtensions();
    public List<AbstractObject> getOrientationObjectExtensions();
}
