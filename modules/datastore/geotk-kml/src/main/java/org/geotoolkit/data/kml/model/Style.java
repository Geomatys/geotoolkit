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
 * <p>This interface maps Style element.</p>
 *
 * <pre>
 * &lt;element name="Style" type="kml:StyleType" substitutionGroup="kml:AbstractStyleSelectorGroup"/>
 *
 * &lt;complexType name="StyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractStyleSelectorType">
 *          &lt;sequence>
 *              &lt;element ref="kml:IconStyle" minOccurs="0"/>
 *              &lt;element ref="kml:LabelStyle" minOccurs="0"/>
 *              &lt;element ref="kml:LineStyle" minOccurs="0"/>
 *              &lt;element ref="kml:PolyStyle" minOccurs="0"/>
 *              &lt;element ref="kml:BalloonStyle" minOccurs="0"/>
 *              &lt;element ref="kml:ListStyle" minOccurs="0"/>
 *              &lt;element ref="kml:StyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:StyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="StyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="StyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Style extends AbstractStyleSelector {

    /**
     *
     * @return
     */
    IconStyle getIconStyle();

    /**
     *
     * @return
     */
    LabelStyle getLabelStyle();

    /**
     *
     * @return
     */
    LineStyle getLineStyle();

    /**
     *
     * @return
     */
    PolyStyle getPolyStyle();

    /**
     *
     * @return
     */
    BalloonStyle getBalloonStyle();

    /**
     *
     * @return
     */
    ListStyle getListStyle();
    
    /**
     *
     * @param iconStyle
     */
    void setIconStyle(IconStyle iconStyle);

    /**
     *
     * @param labelStyle
     */
    void setLabelStyle(LabelStyle labelStyle);

    /**
     *
     * @param lineStyle
     */
    void setLineStyle(LineStyle lineStyle);

    /**
     *
     * @param polyStyle
     */
    void setPolyStyle(PolyStyle polyStyle);

    /**
     *
     * @param baloonStyle
     */
    void setBalloonStyle(BalloonStyle baloonStyle);

    /**
     *
     * @param listStyle
     */
    void setListStyle(ListStyle listStyle);

}
