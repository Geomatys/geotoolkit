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
 * <p>This interface maps AddressLinesType type.</p>
 *
 * <p>Container for Address lines.</p>
 *
 * <pre>
 * &lt;xs:element name="AddressLines" type="AddressLinesType">...
 * &lt;/xs:element>
 *
 * &lt;xs:complexType name="AddressLinesType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" maxOccurs="unbounded"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:anyAttribute namespace="##other"/><!-- These attributes are not implemented -->
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AddressLines {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

}
