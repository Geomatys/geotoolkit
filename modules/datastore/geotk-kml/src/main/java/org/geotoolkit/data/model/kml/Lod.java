package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Lod extends AbstractObject{

    public double getMinLodPixels();
    public double getMaxLodPixels();
    public double getMinFadeExtent();
    public double getMaxFadeExtent();
    public List<SimpleType> getLodSimpleExtensions();
    public List<AbstractObject> getLodObjectExtensions();

}
