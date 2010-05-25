package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface NetworkLink extends AbstractFeature {

    public boolean getRefreshVisibility();
    public boolean getFlyToView();
    public Link getLink();
    public List<SimpleType> getNetworkLinkSimpleExtensions();
    public List<AbstractObject> getNetworkLinkObjectExtensions();

}
