package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface PolyStyle extends AbstractColorStyle {

    public boolean getFill();
    public boolean getOutline();
    public List<SimpleType> getPolyStyleSimpleExtensions();
    public List<AbstractObject> getPolyStyleObjectExtensions();

}
