package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Placemark extends AbstractFeature {

    public AbstractGeometry getAbstractGeometry();
    public List<SimpleType> getPlacemarkSimpleExtensions();
    public List<AbstractObject> getPlacemarkObjectExtensions();

}
