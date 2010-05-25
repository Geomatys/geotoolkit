package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface LineStyle extends AbstractColorStyle {

    public double getWidth();
    public List<SimpleType> getLineStyleSimpleExtensions();
    public List<AbstractObject> getLineStyleObjectExtensions();

}
