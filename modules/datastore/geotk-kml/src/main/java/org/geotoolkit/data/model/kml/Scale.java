package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Scale extends AbstractObject {

    public double getX();
    public double getY();
    public double getZ();
    public List<SimpleType> getScaleSimpleExtensions();
    public List<AbstractObject> getScaleObjectExtensions();

}
