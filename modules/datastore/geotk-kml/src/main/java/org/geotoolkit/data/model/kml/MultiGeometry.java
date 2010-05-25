package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface MultiGeometry extends AbstractGeometry {

    public List<AbstractGeometry> getGeometries();
    public List<SimpleType> getMultiGeometrySimpleExtensions();
    public List<AbstractObject> getMultiGeometryObjectExtensions();

}
