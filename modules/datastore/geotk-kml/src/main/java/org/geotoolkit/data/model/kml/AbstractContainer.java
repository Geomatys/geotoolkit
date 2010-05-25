package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractContainer extends AbstractFeature {

    public List<SimpleType> getAbstractContainerSimpleExtensions();
    public List<AbstractObject> getAbstractContainerObjectExtensions();

}
