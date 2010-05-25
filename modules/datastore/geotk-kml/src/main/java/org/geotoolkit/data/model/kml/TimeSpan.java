package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface TimeSpan extends AbstractTimePrimitive{

    public String getBegin();
    public String getEnd();
    public List<SimpleType> getTimeSpanSimpleExtensions();
    public List<AbstractObject> getTimeSpanObjectExtensions();
}