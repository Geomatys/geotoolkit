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
    double getMinRefreshPeriod();

    /**
     *
     * @return
     */
    double getMaxSessionLength();

    /**
     *
     * @return
     */
    String getCookie();

    /**
     *
     * @return
     */
    String getMessage();

    /**
     *
     * @return
     */
    String getLinkName();

    /**
     *
     * @return
     */
    Object getLinkDescription();

    /**
     *
     * @return
     */
    Snippet getLinkSnippet();

    /**
     *
     * @return
     */
    Calendar getExpires();

    /**
     *
     * @return
     */
    Update getUpdate();

    /**
     *
     * @return
     */
    AbstractView getView();

    /**
     *
     * @param minRefreshPeriod
     */
    void setMinRefreshPeriod(double minRefreshPeriod);

    /**
     *
     * @param maxSessionLength
     */
    void setMaxSessionLength(double maxSessionLength);

    /**
     *
     * @param cookie
     */
    void setCookie(String cookie);

    /**
     *
     * @param message
     */
    void setMessage(String message);

    /**
     *
     * @param linkName
     */
    void setLinkName(String linkName);

    /**
     *
     * @param linkDescription
     */
    void setLinkDescription(Object linkDescription);

    /**
     *
     * @param linkSnippet
     */
    void setLinkSnippet(Snippet linkSnippet);

    /**
     *
     * @param expires
     */
    void setExpires(Calendar expires);

    /**
     *
     * @param update
     */
    void setUpdate(Update update);

    /**
     *
     * @param abstractView
     */
    void setView(AbstractView abstractView);

    /**
     * 
     * @return
     */
    Extensions extensions();

}
