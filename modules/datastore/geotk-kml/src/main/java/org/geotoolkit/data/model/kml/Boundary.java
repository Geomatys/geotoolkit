package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Boundary {

    public LinearRing getLinearRing();
    public List<SimpleType> getBoundarySimpleExtensions();
    public List<AbstractObject> getBoundaryObjectExtensions();

}
