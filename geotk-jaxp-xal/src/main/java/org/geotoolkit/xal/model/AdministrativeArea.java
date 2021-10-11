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
 * <p>This interface maps AdministrativeArea element.</p>
 *
 * <p>Examples of administrative areas are provinces counties, special regions (such as "Rijnmond"), etc.</p>
 *
 * <pre>
 * &lt;xs:element name="AdministrativeArea">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="AdministrativeAreaName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SubAdministrativeArea" minOccurs="0">...
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
 * @module
 */
public interface AdministrativeArea {

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
    List<GenericTypedGrPostal> getAdministrativeAreaNames();

    /**
     *
     * @param administrativeAreaNames
     */
    void setAdministrativeAreaNames(List<GenericTypedGrPostal> administrativeAreaNames);

    /**
     *
     * @return
     */
    SubAdministrativeArea getSubAdministrativeArea();

    /**
     *
     * @param subAdministrativeArea
     */
    void setSubAdministrativeArea(SubAdministrativeArea subAdministrativeArea);

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
     * <p>ostal or Political - Sometimes locations must be distinguished between postal system,
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
     * @param Indicator
     */
    void setIndicator(String Indicator);
}
