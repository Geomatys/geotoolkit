package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Style extends AbstractStyleSelector {

    public IconStyle getIconStyle();
    public LabelStyle getLabelStyle();
    public LineStyle getLineStyle();
    public PolyStyle getPolyStyle();
    public BalloonStyle getBalloonStyle();
    public ListStyle getListStyle();
    public List<SimpleType> getStyleSimpleExtensions();
    public List<AbstractObject> getStyleObjectExtensions();
}
