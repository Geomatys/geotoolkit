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
 * <p>This interface maps ImagePyramid element.</p>
 *
 * <pre>
 * &lt;element name="ImagePyramid" type="kml:ImagePyramidType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ImagePyramidType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:tileSize" minOccurs="0"/>
 *              &lt;element ref="kml:maxWidth" minOccurs="0"/>
 *              &lt;element ref="kml:maxHeight" minOccurs="0"/>
 *              &lt;element ref="kml:gridOrigin" minOccurs="0"/>
 *              &lt;element ref="kml:ImagePyramidSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ImagePyramidObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ImagePyramidSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ImagePyramidObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module
 */
public interface ImagePyramid extends AbstractObject {

    /**
     *
     * @return
     */
    int getTitleSize();

    /**
     *
     * @return
     */
    int getMaxWidth();

    /**
     *
     * @return
     */
    int getMaxHeight();

    /**
     *
     * @return
     */
    GridOrigin getGridOrigin();

    /**
     *
     * @param titleSize
     */
    void setTitleSize(int titleSize);

    /**
     *
     * @param maxWidth
     */
    void setMaxWidth(int maxWidth);

    /**
     *
     * @param maxHeight
     */
    void setMaxHeight(int maxHeight);

    /**
     *
     * @param gridOrigin
     */
    void setGridOrigin(GridOrigin gridOrigin);

}
