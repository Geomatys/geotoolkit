package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public interface NetworkLinkControl {

    public double getMinRefreshPeriod();
    public double getMaxSessionLength();
    public String getCookie();
    public String getMessage();
    public String getLinkName();
    public String getLinkDescription();
    public Snippet getLinkSnippet();
    public String getExpire();
    public Update getUpdate();
    public AbstractView getView();
    public List<SimpleType> getNetworkLinkControlSimpleExtensions();
    public List<AbstractObject> getNetworkLinkControlObjectExtensions();

}