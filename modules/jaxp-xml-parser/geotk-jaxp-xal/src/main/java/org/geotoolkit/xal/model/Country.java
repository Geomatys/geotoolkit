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
 * <p>This interface maps Country element.</p>
 *
 * <p>Specification of a country.</p>
 *
 * <pre>
 * &lt;xs:element name="Country">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="CountryNameCode" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="CountryName" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element ref="AdministrativeArea"/>
 *              &lt;xs:element ref="Locality"/>
 *              &lt;xs:element ref="Thoroughfare"/>
 *          &lt;/xs:choice>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Country {

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
    List<CountryNameCode> getCountryNameCodes();

    /**
     * 
     * @param countryNameCodes
     */
    void setCountryNamesCodes(List<CountryNameCode> countryNameCodes);

    /**
     * <p>Specification of the name of a country.</p>
     *
     * @return
     */
    List<GenericTypedGrPostal> getCountryNames();

    /**
     * 
     * @param countryNames
     */
    void setCountryNames(List<GenericTypedGrPostal> countryNames);

    /*
     * === CHOICE ===
     */

    /**
     *
     * @return
     */
    AdministrativeArea getAdministrativeArea();

    /**
     * 
     * @param administrativeArea
     */
    void setAdministrativeArea(AdministrativeArea administrativeArea);

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
    Thoroughfare getThoroughfare();

    /**
     * 
     * @param thoroughfare
     */
    void setThoroughfare(Thoroughfare thoroughfare);
    /*
     * === END OF CHOICE ===
     */
}
