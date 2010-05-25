package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractLatLonBox extends AbstractObject {

    public Angle180 getNorth();
    public Angle180 getSouth();
    public Angle180 getEast();
    public Angle180 getWest();
    public List<SimpleType> getAbstractLatLonBoxSimpleExtensions();
    public List<AbstractObject> getAbstractLatLonBoxObjectExtensions();

}
