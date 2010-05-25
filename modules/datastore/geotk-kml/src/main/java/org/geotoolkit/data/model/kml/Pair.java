package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Pair extends AbstractObject {

    public StyleState getKey();
    public String getStyleUrl();
    public AbstractStyleSelector getAbstractStyleSelector();
    public List<SimpleType> getPairSimpleExtensions();
    public List<AbstractObject> getPairObjectExtensions();

}
