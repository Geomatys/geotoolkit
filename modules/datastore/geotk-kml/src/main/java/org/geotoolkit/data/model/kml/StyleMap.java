package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface StyleMap extends AbstractStyleSelector {

    public List<Pair> getPairs();
    public List<SimpleType> getStyleMapSimpleExtensions();
    public List<AbstractObject> getStyleMapObjectExtensions();

}
