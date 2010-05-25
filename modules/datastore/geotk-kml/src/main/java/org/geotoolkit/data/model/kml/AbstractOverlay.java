package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface AbstractOverlay extends AbstractFeature{

    public Color getColor();
    public int getDrawOrder();
    public Link getIcon();
    public List<SimpleType> getAbstractOverlaySimpleExtensions();
    public List<AbstractObject> getAbstractOverlayObjectExtensions();
}
