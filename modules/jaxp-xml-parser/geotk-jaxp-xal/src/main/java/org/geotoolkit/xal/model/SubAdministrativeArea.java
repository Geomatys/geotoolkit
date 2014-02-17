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
 * <p>This interface maps SubAdministrativeArea element.</p>
 *
 * <p>Specification of a sub-administrative area.
 * An example of a sub-administrative areas is a county.
 * There are two places where the name of an administrative
 * area can be specified and in this case,
 * one becomes sub-administrative area.</p>
 *
 * <pre>
 * &lt;xs:element name="SubAdministrativeArea" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="SubAdministrativeAreaName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element ref="Locality"/>
 *              &lt;xs:element ref="PostOffice"/>
 *              &lt;xs:element ref="PostalCode"/>
 *          &lt;/xs:choice>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="UsageType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="Indicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface SubAdministrativeArea {

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getSubAdministrativeAreaNames();

    /**
     *
     * @param subAdministrativeAreaNames
     */
    void setSubAdministrativeAreaNames(List<GenericTypedGrPostal> subAdministrativeAreaNames);

    /*
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    Locality getLocality();

    /**
     *
     * @param locality
     */
    void setLocality(Locality locality);

    /**
     *
     * @return
     */
    PostOffice getPostOffice();

    /**
     *
     * @param postOffice
     */
    void setPostOffice(PostOffice postOffice);

    /**
     * 
     * @return
     */
    PostalCode getPostalCode();

    /**
     *
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /*
     * === END OF CHOICE ===
     *
     * === ATTRIBUTES: ===
     */

    /**
     * <p>Province or State or County or Kanton, etc.</p>
     *
     * @return
     */
    String getType();

    /**
     *
     * @param type
     */
    void setType(String type);

    /**
     * <p>Postal or Political - 
     * Sometimes locations must be distinguished between postal system, 
     * and physical locations as defined by a political system.</p>
     *
     * @return
     */
    String getUsageType();

    /**
     *
     * @param usageType
     */
    void setUsageType(String usageType);

    /**
     * <p>Erode (Dist) where (Dist) is the Indicator.</p>
     *
     * @return
     */
    String getIndicator();

    /**
     * 
     * @param indicator
     */
    void setIndicator(String indicator);

}
