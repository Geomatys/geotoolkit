package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface Link extends AbstractObject {

    public String getHref();
    public List<SimpleType> getBasicLinkSimpleExtensions();
    public List<AbstractObject> getBasicLinkObjectExtensions();
    public RefreshMode getRefreshMode();
    public double getRefreshInterval();
    public ViewRefreshMode getViewRefreshMode();
    public double getViewRefreshTime();
    public double getViewBoundScale();
    public String getViewFormat();
    public String getHttpQuery();
    public List<SimpleType> getLinkSimpleExtensions();
    public List<AbstractObject> getLinkObjectExtensions();
}
