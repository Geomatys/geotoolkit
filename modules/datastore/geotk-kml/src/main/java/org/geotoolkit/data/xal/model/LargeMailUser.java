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
 */
public interface LargeMailUser {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the large mail user. eg. Smith Ford International airport.</p>
     *
     * @return
     */
    public List<LargeMailUserName> getLargeMailUserNames();

    /**
     * <p>Specification of the identification number of a large mail user.
     * An example are the Cedex codes in France.</p>
     *
     * @return
     */
    public LargeMailUserIdentifier getLargeMailUserIdentifier();

    /**
     *
     * @return
     */
    public List<BuildingName> getBuildingNames();

    /**
     *
     * @return
     */
    public Department getDepartment();

    /**
     *
     * @return
     */
    public PostBox getPostBox();

    /**
     *
     * @return
     */
    public Thoroughfare getThoroughfare();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /**
     * 
     * @return
     */
    public String getType();
}
