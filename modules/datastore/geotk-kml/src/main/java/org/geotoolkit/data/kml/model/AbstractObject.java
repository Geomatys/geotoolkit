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
 * <p>This interface maps an AbstractObjectGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractObjectGroup" type="kml:AbstractObjectType" abstract="true"/>
 *
 * &lt;complexType name="AbstractObjectType" abstract="true">
 *  &lt;sequence>
 *      &lt;element ref="kml:ObjectSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attributeGroup ref="kml:idAttributes"/>
 * &lt;/complexType>
 *
 * &lt;element name="ObjectSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface AbstractObject {

    /**
     *
     * @return The list of simple extensions.
     */
    Extensions extensions();

    /**
     *
     * @return The identification attributes.
     */
    IdAttributes getIdAttributes();

    /**
     *
     * @param idAttributes
     */
    void setIdAttributes(IdAttributes idAttributes);
}
