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
 * <p>This interface maps Locality element.</p>
 *
 * <p>Locality is one level lower than adminisstrative area. Eg.: cities, reservations and any other built-up areas.</p>
 *
 * <pre>
 * &lt;xs:element name="Locality">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="LocalityName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element ref="PostBox"/>
 *              &lt;xs:element name="LargeMailUser" type="LargeMailUserType">...
 *              &lt;/xs:element>
 *              &lt;xs:element ref="PostOffice"/>
 *              &lt;xs:element name="PostalRoute" type="PostalRouteType">...
 *              &lt;/xs:element>
 *          &lt;/xs:choice>
 *          &lt;xs:element ref="Thoroughfare" minOccurs="0"/>
 *          &lt;xs:element ref="Premise" minOccurs="0"/>
 *          &lt;xs:element name="DependentLocality" type="DependentLocalityType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostalCode" minOccurs="0"/>
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
public interface Locality {

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
    List<GenericTypedGrPostal> getLocalityNames();

    /**
     *
     * @param localityNames
     */
    void setLocalityNames(List<GenericTypedGrPostal> localityNames);
    
    /*
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    PostBox getPostBox();

    /**
     *
     * @param postBox
     */
    void setPostBox(PostBox postBox);

    /**
     * <p>Specification of a large mail user address.
     * Examples of large mail users are postal companies,
     * companies in France with a cedex number,
     * hospitals and airports with their own post code.
     * Large mail user addresses do not have a street name
     * with premise name or premise number in countries like Netherlands.
     * But they have a POBox and street also in countries like France.</p>
     *
     * @return
     */
    LargeMailUser getLargeMailUser();

    /**
     *
     * @param largeMailUser
     */
    void setLargeMailUser(LargeMailUser largeMailUser);

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
     * <p>A Postal van is specific for a route as in Is`rael, Rural route.</p>
     *
     * @return
     */
    PostalRoute getPostalRoute();

    /**
     *
     * @param postalRoute
     */
    void setPostalRoute(PostalRoute postalRoute);
    
    /*
     * === END OF CHOICE ===
     */

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

    /**
     *
     * @return
     */
    Premise getPremise();

    /**
     *
     * @param premise
     */
    void setPremise(Premise premise);

    /**
     * <p>Dependent localities are Districts within cities/towns,
     * locality divisions, postal divisions of cities,
     * suburbs, etc. DependentLocality is a recursive element,
     * but no nesting deeper than two exists
     * (Locality-DependentLocality-DependentLocality).</p>
     * 
     * @return
     */
    DependentLocality getDependentLocality();

    /**
     *
     * @param dependentLocality
     */
    void setDependentLocality(DependentLocality dependentLocality);

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
     * === ATTRIBUTES: ===
     */
    
    /**
     * <p>Possible values not limited to: City, IndustrialEstate, etc.</p>
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
     * <p>Postal or Political - Sometimes locations must be distinguished between 
     * postal system, and physical locations as defined by a political system.</p>
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
