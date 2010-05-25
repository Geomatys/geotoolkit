package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractSubStyle extends AbstractObject {

    public List<SimpleType> getSubStyleSimpleExtensions();
    public List<AbstractObject> getSubStyleObjectExtensions();
}
