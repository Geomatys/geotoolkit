package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;
import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractObject {

    public List<SimpleType> getObjectSimpleExtensions();
    public IdAttributes getIdAttributes();
}
