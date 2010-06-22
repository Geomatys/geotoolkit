package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultNetworkLinkControl implements NetworkLinkControl {

    private final double minRefreshPeriod;
    private final double maxSessionLength;
    private final String cookie;
    private final String message;
    private final String linkName;
    private final String linkDescription;
    private final Snippet linkSnippet;
    private final String expires;
    private final Update update;
    private final AbstractView view;
    private final List<SimpleType> networkLinkControlSimpleExtensions;
    private final List<AbstractObject> networkLinkControlObjectExtensions;

    /**
     *
     * @param minRefreshPeriod
     * @param maxSessionLength
     * @param cookie
     * @param message
     * @param linkName
     * @param linkDescription
     * @param linkSnippet
     * @param expires
     * @param update
     * @param view
     * @param networkLinkControlSimpleExtensions
     * @param networkLinkControlObjectExtensions
     */
    public DefaultNetworkLinkControl(double minRefreshPeriod,
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
        this.expires = expire;
        this.update = update;
        this.view = view;
        this.networkLinkControlSimpleExtensions = (networkLinkControlSimpleExtensions == null) ? EMPTY_LIST : networkLinkControlSimpleExtensions;
        this.networkLinkControlObjectExtensions = (networkLinkControlObjectExtensions == null) ? EMPTY_LIST : networkLinkControlObjectExtensions;
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
    public String getExpires() {return this.expires;}

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
