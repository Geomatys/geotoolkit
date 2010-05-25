package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LabelStyle extends AbstractColorStyle {

    public double getScale();
    public List<SimpleType> getLabelStyleSimpleExtensions();
    public List<AbstractObject> getLabelStyleObjectExtensions();

}
