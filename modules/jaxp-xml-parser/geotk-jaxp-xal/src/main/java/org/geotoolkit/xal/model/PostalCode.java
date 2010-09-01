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
package org.geotoolkit.xal.model;

import java.util.List;

/**
 * <p>This interface maps PostalCode element.</p>
 *
 * <p>PostalCode is the container element for either simple or complex (extended) postal codes.
 * Type: Area Code, Postcode, etc.</p>
 *
 * <pre>
 * &lt;xs:element name="PostalCode">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PostalCodeNumber" minOccurs="0" maxOccurs="unbounded">
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostalCodeNumberExtension" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostTown" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostalCode {

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Specification of a postcode.
     * The postcode is formatted according to country-specific rules.
     * Example: SW3 0A8-1A, 600074, 2067</p>
     *
     * @return
     */
    List<GenericTypedGrPostal> getPostalCodeNumbers();

    /**
     *
     * @return
     */
    List<PostalCodeNumberExtension> getPostalCodeNumberExtensions();

    /**
     * 
     * @return
     */
    PostTown getPostTown();

    /**
     * <p>Area Code, Postcode, Delivery code as in NZ, etc.</p>
     * @return
     */
    String getType();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @param postalCodeNumbers
     */
    void setPostalCodeNumbers(List<GenericTypedGrPostal> postalCodeNumbers);

    /**
     *
     * @param postalCodeNumberExtensions
     */
    void setPostalCodeNumberExtensions(List<PostalCodeNumberExtension> postalCodeNumberExtensions);

    /**
     *
     * @param postTown
     */
    void setPostTown(PostTown postTown);

    /**
     * 
     * @param type
     */
    void setType(String type);
}
