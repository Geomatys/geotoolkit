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
 * <p>This interface maps Region element.</p>
 *
 * <pre>
 * &lt;element name="Region" type="kml:RegionType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="RegionType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:LatLonAltBox" minOccurs="0"/>
 *              &lt;element ref="kml:Lod" minOccurs="0"/>
 *              &lt;element ref="kml:RegionSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:RegionObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="RegionSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="RegionObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module
 */
public interface Region extends AbstractObject{

    /**
     *
     * @return
     */
    LatLonAltBox getLatLonAltBox();

    /**
     *
     * @return
     */
    Lod getLod();

    /**
     *
     * @param latLonAltBox
     */
    void setLatLonAltBox(LatLonAltBox latLonAltBox);

    /**
     *
     * @param lod
     */
    void setLod(Lod lod);

}
