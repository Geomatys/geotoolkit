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
 * <p>This interface maps iconStyle element.</p>
 *
 * <pre>
 * &lt;element name="IconStyle" type="kml:IconStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="IconStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:scale" minOccurs="0"/>
 *              &lt;element ref="kml:heading" minOccurs="0"/>
 *              &lt;element name="Icon" type="kml:BasicLinkType" minOccurs="0"/>
 *              &lt;element ref="kml:hotSpot" minOccurs="0"/>
 *              &lt;element ref="kml:IconStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:IconStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="IconStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="IconStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface IconStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    double getScale();

    /**
     *
     * @return
     */
    double getHeading();
    
    /**
     * 
     * @return
     */
    BasicLink getIcon();

    /**
     * 
     * @return
     */
    Vec2 getHotSpot();

    /**
     *
     * @param scale
     */
    void setScale(double scale);

    /**
     *
     * @param heading
     */
    void setHeading(double heading);

    /**
     *
     * @param icon
     */
    void setIcon(BasicLink icon);

    /**
     *
     * @param hotSpot
     */
    void setHotSpot(Vec2 hotSpot);

}
