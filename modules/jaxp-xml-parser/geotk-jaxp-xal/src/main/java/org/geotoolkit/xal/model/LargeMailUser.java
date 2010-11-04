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
 * <p>This interface maps LargeMailUserType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="LargeMailUserType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="LargeMailUserName" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="LargeMailUserIdentifier" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="BuildingName" type="BuildingNameType" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="Department" minOccurs="0"/>
 *      &lt;xs:element ref="PostBox" minOccurs="0"/>
 *      &lt;xs:element ref="Thoroughfare" minOccurs="0"/>
 *      &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attribute name="Type" type="xs:string"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface LargeMailUser {

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
     * <p>Name of the large mail user. eg. Smith Ford International airport.</p>
     *
     * @return
     */
    List<LargeMailUserName> getLargeMailUserNames();

    /**
     *
     * @param largeMailUserNames
     */
    void setLargeMailUserNames(List<LargeMailUserName> largeMailUserNames);

    /**
     * <p>Specification of the identification number of a large mail user.
     * An example are the Cedex codes in France.</p>
     *
     * @return
     */
    LargeMailUserIdentifier getLargeMailUserIdentifier();

    /**
     *
     * @param largeMailUserIdentifier
     */
    void setLargeMailUserIdentifier(LargeMailUserIdentifier largeMailUserIdentifier);

    /**
     *
     * @return
     */
    List<BuildingName> getBuildingNames();

    /**
     *
     * @param buildingNames
     */
    void setBuildingNames(List<BuildingName> buildingNames);

    /**
     *
     * @return
     */
    Department getDepartment();

    /**
     *
     * @param department
     */
    void setDepartment(Department department);

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
    PostalCode getPostalCode();

    /**
     * 
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /**
     * 
     * @return
     */
    String getType();

    /**
     *
     * @param type
     */
    void setType(String type);
}
