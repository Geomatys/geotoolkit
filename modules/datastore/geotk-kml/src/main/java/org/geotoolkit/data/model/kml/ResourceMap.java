package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface ResourceMap extends AbstractObject {

    public List<Alias> getAliases();
    public List<SimpleType> getResourceMapSimpleExtensions();
    public List<AbstractObject> getResourceMapObjectExtensions();
    
}
