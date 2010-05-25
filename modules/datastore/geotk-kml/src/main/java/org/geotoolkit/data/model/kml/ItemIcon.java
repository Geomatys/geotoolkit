package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface ItemIcon extends AbstractObject {

    public List<ItemIconState> getStates();
    public String getHref();
    public List<SimpleType> getItemIconSimpleExtensions();
    public List<AbstractObject> getItemIconObjectExtensions();

}
