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

/**
 * <p>This interface maps LinkType type.</p>
 *
 * <pre>
 * &lt;complexType name="LinkType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:BasicLinkType">
 *          &lt;sequence>
 *              &lt;element ref="kml:refreshMode" minOccurs="0"/>
 *              &lt;element ref="kml:refreshInterval" minOccurs="0"/>
 *              &lt;element ref="kml:viewRefreshMode" minOccurs="0"/>
 *              &lt;element ref="kml:viewRefreshTime" minOccurs="0"/>
 *              &lt;element ref="kml:viewBoundScale" minOccurs="0"/>
 *              &lt;element ref="kml:viewFormat" minOccurs="0"/>
 *              &lt;element ref="kml:httpQuery" minOccurs="0"/>
 *              &lt;element ref="kml:LinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Link extends AbstractObject {

    /**
     *
     * @return
     */
    String getHref();

    /**
     *
     * @return
     */
    RefreshMode getRefreshMode();

    /**
     *
     * @return
     */
    double getRefreshInterval();

    /**
     *
     * @return
     */
    ViewRefreshMode getViewRefreshMode();

    /**
     *
     * @return
     */
    double getViewRefreshTime();

    /**
     *
     * @return
     */
    double getViewBoundScale();

    /**
     *
     * @return
     */
    String getViewFormat();

    /**
     *
     * @return
     */
    String getHttpQuery();

    /**
     *
     * @param href
     */
    void setHref(String href);

    /**
     *
     * @param refreshMode
     */
    void setRefreshMode(RefreshMode refreshMode);

    /**
     *
     * @param refreshInterval
     */
    void setRefreshInterval(double refreshInterval);

    /**
     *
     * @param viewRefreshMode
     */
    void setViewRefreshMode(ViewRefreshMode viewRefreshMode);

    /**
     *
     * @param viewRefreshTime
     */
    void setViewRefreshTime(double viewRefreshTime);

    /**
     *
     * @return
     */
    void setViewBoundScale(double viewBoundScale);

    /**
     *
     * @param viewFormat
     */
    void setViewFormat(String viewFormat);

    /**
     *
     * @param httpQuery
     */
    void setHttpQuery(String httpQuery);
}
