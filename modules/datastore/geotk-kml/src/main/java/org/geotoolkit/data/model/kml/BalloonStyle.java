package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface BalloonStyle extends AbstractSubStyle {

    public Color getBgColor();
    public Color getTextColor();
    public String getText();
    public DisplayMode getDisplayMode();
    public List<SimpleType> getBalloonStyleSimpleExtensions();
    public List<AbstractObject> getBalloonStyleObjectExtensions();

}
