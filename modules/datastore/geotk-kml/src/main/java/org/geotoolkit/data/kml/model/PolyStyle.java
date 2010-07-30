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
 * <p>This interface maps PointStyle element.</p>
 *
 * <pre>
 * &lt;element name="PolyStyle" type="kml:PolyStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="PolyStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:fill" minOccurs="0"/>
 *              &lt;element ref="kml:outline" minOccurs="0"/>
 *              &lt;element ref="kml:PolyStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PolyStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PolyStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PolyStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PolyStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    boolean getFill();

    /**
     *
     * @return
     */
    boolean getOutline();

    /**
     *
     * @param fill
     */
    void setFill(boolean fill);

    /**
     *
     * @param outline
     */
    void setOutline(boolean outline);

}
