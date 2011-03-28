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
 * <p>This interface maps BoundaryType type used by :</p>
 * <ul>
 * <li>outerBoundaryIs element;</li>
 * <li>innerBoundaryIs element.</li>
 * </ul>
 *
 * <pre>
 * &lt;element name="outerBoundaryIs" type="kml:BoundaryType"/>
 * &lt;element name="innerBoundaryIs" type="kml:BoundaryType"/>
 *
 * &lt;complexType name="BoundaryType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:LinearRing" minOccurs="0"/>
 *      &lt;element ref="kml:BoundarySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:BoundaryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 *
 * &lt;element name="BoundarySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="BoundaryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Boundary {

    /**
     *
     * @return the linear ring geometry.
     */
    LinearRing getLinearRing();

    /**
     * 
     * @param linearRing
     */
    void setLinearRing(LinearRing linearRing);

    /**
     * 
     * @return
     */
    Extensions extensions();
}
