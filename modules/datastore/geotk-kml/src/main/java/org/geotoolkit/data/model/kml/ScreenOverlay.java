package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface ScreenOverlay extends AbstractOverlay {

    public Vec2 getOverlayXY();
    public Vec2 getScreenXY();
    public Vec2 getRotationXY();
    public Vec2 getSize();
    public Angle180 getRotation();
    public List<SimpleType> getScreenOverlaySimpleExtensions();
    public List<AbstractObject> getScreenOverlayObjectExtensions();

}
