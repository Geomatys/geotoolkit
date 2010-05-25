package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractView extends AbstractObject{

    public List<SimpleType> getAbstractViewSimpleExtensions();
    public List<AbstractObject> getAbstractViewObjectExtensions();

}
