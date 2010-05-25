package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractGeometry extends AbstractObject{

    public List<SimpleType> getAbstractGeometrySimpleExtensions();
    public List<AbstractObject> getAbstractGeometryObjectExtensions();
}
