package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface IconStyle extends AbstractColorStyle {

    public double getScale();
    public Angle360 getHeading();
    public BasicLink getIcon();
    public Vec2 getHotSpot();
    public List<SimpleType> getIconStyleSimpleExtensions();
    public List<AbstractObject> getIconStyleObjectExtensions();
}
