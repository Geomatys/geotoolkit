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
 * <p>This interface maps AbstractLatLonBoxType type.</p>
 *
 * <pre>
 * &lt;complexType name="AbstractLatLonBoxType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:north" minOccurs="0"/>
 *              &lt;element ref="kml:south" minOccurs="0"/>
 *              &lt;element ref="kml:east" minOccurs="0"/>
 *              &lt;element ref="kml:west" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractLatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractLatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractLatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractLatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractLatLonBox extends AbstractObject {

    /**
     *
     * @return the north angle.
     */
    double getNorth();

    /**
     *
     * @return the south angle.
     */
    double getSouth();

    /**
     *
     * @return the east angle.
     */
    double getEast();

    /**
     *
     * @return the west angle.
     */
    double getWest();

    /**
     *
     * @param north
     */
    void setNorth(double north);

    /**
     *
     * @param south
     */
    void setSouth(double south);

    /**
     *
     * @param east
     */
    void setEast(double east);

    /**
     *
     * @param west
     */
    void setWest(double west);

}
