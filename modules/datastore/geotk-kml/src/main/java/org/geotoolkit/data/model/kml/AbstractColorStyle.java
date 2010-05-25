package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractColorStyle extends AbstractSubStyle {

    public Color getColor();
    public ColorMode getColorMode();
    public List<SimpleType> getColorStyleSimpleExtensions();
    public List<AbstractObject> getColorStyleObjectExtensions();
}