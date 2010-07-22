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
 *
 * <p>This interface maps BalloonStyle elements.</p>
 *
 * <pre>
 * &lt;element name="BalloonStyle" type="kml:BalloonStyleType" substitutionGroup="kml:AbstractSubStyleGroup"/>
 *
 * &lt;complexType name="BalloonStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractSubStyleType">
 *          &lt;sequence>
 *              &lt;choice>
 *                  &lt;annotation>
 *                      &lt;documentation>color deprecated in 2.1&lt;/documentation>
 *                  &lt;/annotation>
 *                  &lt;element ref="kml:color" minOccurs="0"/>
 *                  &lt;element ref="kml:bgColor" minOccurs="0"/>
 *              &lt;/choice>
 *              &lt;element ref="kml:textColor" minOccurs="0"/>
 *              &lt;element ref="kml:text" minOccurs="0"/>
 *              &lt;element ref="kml:displayMode" minOccurs="0"/>
 *              &lt;element ref="kml:BalloonStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:BalloonStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="BalloonStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="BalloonStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface BalloonStyle extends AbstractSubStyle {

    /**
     *
     * @return the background color.
     */
    Color getBgColor();

    /**
     *
     * @return the text color.
     */
    Color getTextColor();

    /**
     *
     * @return the text content.
     */
    Object getText();

    /**
     *
     * @return the display mode
     */
    DisplayMode getDisplayMode();
    
    /**
     *
     * @param bgColor
     */
    void setBgColor(Color bgColor);

    /**
     *
     * @param textColor
     */
    void setTextColor(Color textColor);

    /**
     *
     * @param text
     */
    void setText(Object text);

    /**
     *
     * @param displayMode
     */
    void setDisplayMode(DisplayMode displayMode);

}
