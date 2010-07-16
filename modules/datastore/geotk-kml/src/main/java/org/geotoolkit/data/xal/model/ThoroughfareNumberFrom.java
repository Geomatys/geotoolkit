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
package org.geotoolkit.data.xal.model;

import java.util.List;

/**
 * <p>This interface maps ThoroughfareNumberFrom element.</p>
 *
 * <p>Starting number in the range.</p>
 *
 * <pre>
 * &lt;xs:element name="ThoroughfareNumberFrom">
 *  &lt;xs:complexType mixed="true">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element ref="ThoroughfareNumber" maxOccurs="unbounded"/>
 *      &lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ThoroughfareNumberFrom {

    /**
     * <p>The content list elements have to be AddressLine,ThoroughfareNumberPrefix,
     * ThoroughfareNumber or ThoroughfareNumberSuffix instances.</p>
     *
     * @return
     */
    public List<Object> getContent();

    /**
     * 
     * @return
     */
    public GrPostal getGrPostal();
}
