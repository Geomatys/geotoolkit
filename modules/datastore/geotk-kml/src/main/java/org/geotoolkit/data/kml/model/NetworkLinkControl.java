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

/**
 * <p>This interface maps NetworkLinkControl element.</p>
 *
 * <pre>
 * &lt;element name="NetworkLinkControl" type="kml:NetworkLinkControlType"/>
 *
 * &lt;complexType name="NetworkLinkControlType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:minRefreshPeriod" minOccurs="0"/>
 *      &lt;element ref="kml:maxSessionLength" minOccurs="0"/>
 *      &lt;element ref="kml:cookie" minOccurs="0"/>
 *      &lt;element ref="kml:message" minOccurs="0"/>
 *      &lt;element ref="kml:linkName" minOccurs="0"/>
 *      &lt;element ref="kml:linkDescription" minOccurs="0"/>
 *      &lt;element ref="kml:linkSnippet" minOccurs="0"/>
 *      &lt;element ref="kml:expires" minOccurs="0"/>
 *      &lt;element ref="kml:Update" minOccurs="0"/>
 *      &lt;element ref="kml:AbstractViewGroup" minOccurs="0"/>
 *      &lt;element ref="kml:NetworkLinkControlSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:NetworkLinkControlObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 *
 * &lt;element name="NetworkLinkControlSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="NetworkLinkControlObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface NetworkLinkControl {

    /**
     *
     * @return
     */
    public double getMinRefreshPeriod();

    /**
     *
     * @return
     */
    public double getMaxSessionLength();

    /**
     *
     * @return
     */
    public String getCookie();

    /**
     *
     * @return
     */
    public String getMessage();

    /**
     *
     * @return
     */
    public String getLinkName();

    /**
     *
     * @return
     */
    public Object getLinkDescription();

    /**
     *
     * @return
     */
    public Snippet getLinkSnippet();

    /**
     *
     * @return
     */
    public Calendar getExpires();

    /**
     *
     * @return
     */
    public Update getUpdate();

    /**
     *
     * @return
     */
    public AbstractView getView();

    /**
     *
     * @param minRefreshPeriod
     */
    public void setMinRefreshPeriod(double minRefreshPeriod);

    /**
     *
     * @param maxSessionLength
     */
    public void setMaxSessionLength(double maxSessionLength);

    /**
     *
     * @param cookie
     */
    public void setCookie(String cookie);

    /**
     *
     * @param message
     */
    public void setMessage(String message);

    /**
     *
     * @param linkName
     */
    public void setLinkName(String linkName);

    /**
     *
     * @param linkDescription
     */
    public void setLinkDescription(Object linkDescription);

    /**
     *
     * @param linkSnippet
     */
    public void setLinkSnippet(Snippet linkSnippet);

    /**
     *
     * @param expires
     */
    public void setExpires(Calendar expires);

    /**
     *
     * @param update
     */
    public void setUpdate(Update update);

    /**
     *
     * @param abstractView
     */
    public void setView(AbstractView abstractView);

    /**
     * 
     * @return
     */
    public Extensions extensions();

}
