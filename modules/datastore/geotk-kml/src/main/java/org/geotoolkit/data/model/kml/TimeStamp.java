package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface TimeStamp extends AbstractTimePrimitive{

    public String getWhen();
    public List<SimpleType> getTimeStampSimpleExtensions();
    public List<AbstractObject> getTimeStampObjectExtensions();
}