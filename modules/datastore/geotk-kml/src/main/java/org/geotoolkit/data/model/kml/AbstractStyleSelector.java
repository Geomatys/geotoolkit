package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractStyleSelector extends AbstractObject {

    public List<SimpleType> getAbstractStyleSelectorSimpleExtensions();
    public List<AbstractObject> getAbstractStyleSelectorObjectExtensions();

}
