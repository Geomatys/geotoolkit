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
 * <p>This interface maps Placemark element.</p>
 *
 * <pre>
 * &lt;element name="Placemark" type="kml:PlacemarkType" substitutionGroup="kml:AbstractFeatureGroup"/>
 *
 * &lt;complexType name="PlacemarkType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractFeatureType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractGeometryGroup" minOccurs="0"/>
 *              &lt;element ref="kml:PlacemarkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PlacemarkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PlacemarkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PlacemarkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Placemark extends AbstractFeature {

    /**
     *
     * @return
     */
    public AbstractGeometry getAbstractGeometry();

    /**
     *
     * @param abstractGeometry
     */
    public void setAbstractGeometry(AbstractGeometry abstractGeometry);

}
