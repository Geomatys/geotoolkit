/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultNetworkLinkControl implements NetworkLinkControl {

    private Extensions extensions = new Extensions();
    private double minRefreshPeriod;
    private double maxSessionLength;
    private String cookie;
    private String message;
    private String linkName;
    private Object linkDescription;
    private Snippet linkSnippet;
    private Calendar expires;
    private Update update;
    private AbstractView view;

    /**
     *
     */
    public DefaultNetworkLinkControl() {
        this.minRefreshPeriod = DEF_MIN_REFRESH_PERIOD;
        this.maxSessionLength = DEF_MAX_SESSION_LENGTH;
    }

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
    public DefaultNetworkLinkControl(double minRefreshPeriod,
            double maxSessionLength, String cookie, String message,
            String linkName, Object linkDescription,
            Snippet linkSnippet, Calendar expire, Update update, AbstractView view,
            List<SimpleTypeContainer> networkLinkControlSimpleExtensions,
            List<Object> networkLinkControlObjectExtensions) {
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
        if (networkLinkControlSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.NETWORK_LINK_CONTROL).addAll(networkLinkControlSimpleExtensions);
        }
        if (networkLinkControlObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.NETWORK_LINK_CONTROL).addAll(networkLinkControlObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMinRefreshPeriod() {
        return this.minRefreshPeriod;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getMaxSessionLength() {
        return this.maxSessionLength;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCookie() {
        return this.cookie;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getLinkName() {
        return this.linkName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Object getLinkDescription() {
        return this.linkDescription;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Snippet getLinkSnippet() {
        return this.linkSnippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getExpires() {
        return this.expires;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Update getUpdate() {
        return this.update;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractView getView() {
        return this.view;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMinRefreshPeriod(double minRefreshPeriod) {
        this.minRefreshPeriod = minRefreshPeriod;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMaxSessionLength(double maxSessionLength) {
        this.maxSessionLength = maxSessionLength;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinkDescription(Object linkDescription) {
        this.linkDescription = linkDescription;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLinkSnippet(Snippet linkSnippet) {
        this.linkSnippet = linkSnippet;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExpires(Calendar expires) {
        this.expires = expires;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setUpdate(Update update) {
        this.update = update;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setView(AbstractView abstractView) {
        this.view = abstractView;
    }

    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
