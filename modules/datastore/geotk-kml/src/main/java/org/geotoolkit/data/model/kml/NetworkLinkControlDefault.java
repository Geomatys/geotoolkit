package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
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

    /**
     *
     * @param minRefreshPeriod
     * @param maxSessionLength
     * @param cookie
     * @param message
     * @param linkName
     * @param linkDescription
     * @param linkSnippet
     * @param expire
     * @param update
     * @param view
     * @param networkLinkControlSimpleExtensions
     * @param networkLinkControlObjectExtensions
     */
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinRefreshPeriod() {return this.minRefreshPeriod;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxSessionLength() {return this.maxSessionLength;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCookie() {return this.cookie;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getMessage() {return this.message;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getLinkName() {return this.linkName;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getLinkDescription() {return this.linkDescription;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Snippet getLinkSnippet() {return this.linkSnippet;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getExpire() {return this.expire;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Update getUpdate() {return this.update;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractView getView() {return this.view;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getNetworkLinkControlSimpleExtensions() {return this.networkLinkControlSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getNetworkLinkControlObjectExtensions() {return this.networkLinkControlObjectExtensions;}

}
