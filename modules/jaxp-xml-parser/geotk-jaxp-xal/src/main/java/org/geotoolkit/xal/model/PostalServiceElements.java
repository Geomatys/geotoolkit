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
 * <p>This interface maps PostalServiceElements element.</p>
 *
 * <p>Postal authorities use specific postal service data to expedient delivery of mail.</p>
 *
 * <pre>
 * &lt;xs:element name="PostalServiceElements" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element name="AddressIdentifier" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="EndorsementLineCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="KeyLineCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="Barcode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SortingCode" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLatitude" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLatitudeDirection" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLongitude" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="AddressLongitudeDirection" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SupplementaryPostalServiceData" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface PostalServiceElements {

    /**
     *
     * @return
     */
    List<AddressIdentifier> getAddressIdentifiers();

    /**
     * <p>Directly affects postal service distribution.</p>
     *
     * @return
     */
    GenericTypedGrPostal getEndorsementLineCode();

    /**
     * <p>Required for some postal services</p>
     *
     * @return
     */
    GenericTypedGrPostal getKeyLineCode();

    /**
     * <p>Required for some postal services.</p>
     *
     * @return
     */
    GenericTypedGrPostal getBarcode();

    /**
     *
     * @return
     */
    SortingCode getSortingCode();

    /**
     * <p>Latitude of delivery address.</p>
     *
     * @return
     */
    GenericTypedGrPostal getAddressLatitude();

    /**
     * <p>Latitude direction of delivery address;N = North and S = South.</p>
     *
     * @return
     */
    GenericTypedGrPostal getAddressLatitudeDirection();

    /**
     * <p>Longtitude of delivery address.</p>
     *
     * @return
     */
    GenericTypedGrPostal getAddressLongitude();

    /**
     * <p>Longtitude direction of delivery address;N=North and S=South.</p>
     *
     * @return
     */
    GenericTypedGrPostal getAddressLongitudeDirection();

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getSupplementaryPostalServiceData();

    /**
     * <p>Specific to postal service.</p>
     *
     * @return
     */
    String getType();

    /**
     *
     * @param addressIdentifiers
     */
    void setAddressIdentifiers(List<AddressIdentifier> addressIdentifiers);

    /**
     *
     * @param endorsementLineCode
     */
    void setEndorsementLineCode(GenericTypedGrPostal endorsementLineCode);

    /**
     *
     * @param keyLineCode
     */
    void setKeyLineCode(GenericTypedGrPostal keyLineCode);

    /**
     *
     * @param barcode
     */
    void setBarcode(GenericTypedGrPostal barcode);

    /**
     *
     * @param sortingCode
     */
    void setSortingCode(SortingCode sortingCode);

    /**
     *
     * @param addressLatitude
     */
    void setAddressLatitude(GenericTypedGrPostal addressLatitude);

    /**
     *
     * @param addressLatitudeDirection
     */
    void setAddressLatitudeDirection(GenericTypedGrPostal addressLatitudeDirection);

    /**
     *
     * @param addressLongitude
     */
    void setAddressLongitude(GenericTypedGrPostal addressLongitude);

    /**
     *
     * @param addressLongitudeDirection
     */
    void setAddressLongitudeDirection(GenericTypedGrPostal addressLongitudeDirection);

    /**
     *
     * @param supplementaryPostalServiceData
     */
    void setSupplementaryPostalServiceData(List<GenericTypedGrPostal> supplementaryPostalServiceData);

    /**
     *
     * @param type
     */
    void setType(String type);
}
