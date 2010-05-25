package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractTimePrimitive extends AbstractObject {

    public List<SimpleType> getAbstractTimePrimitiveSimpleExtensions();
    public List<AbstractObject> getAbstractTimePrimitiveObjectExtensions();

}
