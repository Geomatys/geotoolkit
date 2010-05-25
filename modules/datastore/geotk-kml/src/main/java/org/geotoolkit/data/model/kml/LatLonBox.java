package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LatLonBox extends AbstractLatLonBox {

    public Angle180 getRotation();
    public List<SimpleType> getLatLonBoxSimpleExtensions();
    public List<AbstractObject> getLatLonBoxObjectExtensions();

}
