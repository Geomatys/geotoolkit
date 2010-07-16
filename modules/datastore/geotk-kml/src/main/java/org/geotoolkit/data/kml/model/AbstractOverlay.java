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

import java.awt.Color;

/**
 * <p>This interface maps AbstractOverlayGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractOverlayGroup" type="kml:AbstractOverlayType" abstract="true" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="AbstractOverlayType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:color" minOccurs="0"/>
 *              &lt;element ref="kml:drawOrder" minOccurs="0"/>
 *              &lt;element ref="kml:Icon" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractOverlaySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractOverlayObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractOverlaySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractOverlayObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractOverlay extends AbstractFeature{

    /**
     *
     * @return the color.
     */
    public Color getColor();

    /**
     *
     * @return the drawOrder.
     */
    public int getDrawOrder();

    /**
     *
     * @return the icon link.
     */
    public Icon getIcon();

    /**
     *
     * @param color
     */
    public void setColor(Color color);

    /**
     *
     * @param drawOrder
     */
    public void setDrawOrder(int drawOrder);

    /**
     *
     * @param link
     */
    public void setIcon(Icon link);

}
