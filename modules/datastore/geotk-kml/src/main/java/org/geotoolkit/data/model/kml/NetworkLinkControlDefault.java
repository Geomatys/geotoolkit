package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author SAmuel Andrés
 */
public class NetworkLinkControlDefault implements NetworkLinkControl {

    private double minRefreshPeriod;
    private double maxSessionLength;
    private String cookie;
    private String message;
    private String linkName;
    private String linkDescription;
    private Snippet linkSnippet;
    private String expire;
    private Update update;
    private AbstractView view;
    private List<SimpleType> networkLinkControlSimpleExtensions;
    private List<AbstractObject> networkLinkControlObjectExtensions;

    public NetworkLinkControlDefault(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message, String linkName, String linkDescription,
            Snippet linkSnippet, String expire, Update update, AbstractView view,
            List<SimpleType> networkLinkControlSimpleExtensions, List<AbstractObject> networkLinkControlObjectExtensions){
        this.minRefreshPeriod = minRefreshPeriod;
        this.maxSessionLength = maxSessionLength;
        this.cookie = cookie;
        this.message = message;
        this.linkName = linkName;
        this.linkDescription = linkDescription;
        this.linkSnippet = linkSnippet;
        this.expire = expire;
        this.update = update;
        this.view = view;
        this.networkLinkControlSimpleExtensions = networkLinkControlSimpleExtensions;
        this.networkLinkControlObjectExtensions = networkLinkControlObjectExtensions;
    }

    @Override
    public double getMinRefreshPeriod() {return this.minRefreshPeriod;}

    @Override
    public double getMaxSessionLength() {return this.maxSessionLength;}

    @Override
    public String getCookie() {return this.cookie;}

    @Override
    public String getMessage() {return this.message;}

    @Override
    public String getLinkName() {return this.linkName;}

    @Override
    public String getLinkDescription() {return this.linkDescription;}

    @Override
    public Snippet getLinkSnippet() {return this.linkSnippet;}

    @Override
    public String getExpire() {return this.expire;}

    @Override
    public Update getUpdate() {return this.update;}

    @Override
    public AbstractView getView() {return this.view;}

    @Override
    public List<SimpleType> getNetworkLinkControlSimpleExtensions() {return this.networkLinkControlSimpleExtensions;}

    @Override
    public List<AbstractObject> getNetworkLinkControlObjectExtensions() {return this.networkLinkControlObjectExtensions;}

}
