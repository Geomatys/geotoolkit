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
 * <p>This interface maps NetworkLink element.</p>
 *
 * <pre>
 * &lt;element name="NetworkLink" type="kml:NetworkLinkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="NetworkLinkType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:refreshVisibility" minOccurs="0"/>
 *              &lt;element ref="kml:flyToView" minOccurs="0"/>
 *              &lt;choice>
 *                  &lt;annotation>
 *                      &lt;documentation>Url deprecated in 2.2</documentation>
 *                  &lt;/annotation>
 *                  &lt;element ref="kml:Url" minOccurs="0"/>
 *                  &lt;element ref="kml:Link" minOccurs="0"/>
 *              &lt;/choice>
 *              &lt;element ref="kml:NetworkLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:NetworkLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="NetworkLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="NetworkLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface NetworkLink extends AbstractFeature {

    /**
     *
     * @return
     */
    public boolean getRefreshVisibility();

    /**
     *
     * @return
     */
    public boolean getFlyToView();

    /**
     *
     * @return
     */
    public Link getLink();

    /**
     *
     * @param refreshVisibility
     */
    public void setRefreshVisibility(boolean refreshVisibility);

    /**
     *
     * @param flyToView
     */
    public void setFlyToView(boolean flyToView);

    /**
     *
     * @param link
     */
    public void setLink(Link link);

}
