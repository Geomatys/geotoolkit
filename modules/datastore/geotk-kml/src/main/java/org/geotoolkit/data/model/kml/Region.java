package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Region extends AbstractObject{

    public LatLonAltBox getLatLonAltBox();
    public Lod getLod();
    public List<SimpleType> getRegionSimpleExtensions();
    public List<AbstractObject> getRegionObjectExtensions();

}
