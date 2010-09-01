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
 * <p>This interface maps FirmType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="FirmType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="FirmName" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="Department" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Firm {

    /**
     * 
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the firm.</p>
     *
     * @return
     */
    List<GenericTypedGrPostal> getFirmNames();

    /**
     *
     * @return
     */
    List<Department> getDepartments();

    /**
     * <p>A MailStop is where the the mail is delivered to within a premise/subpremise/firm or a facility.</p>
     *
     * @return
     */
    MailStop getMailStop();

    /**
     *
     * @return
     */
    PostalCode getPostalCode();

    /**
     *
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
     * @param firmNames
     */
    void setFirmNames(List<GenericTypedGrPostal> firmNames);

    /**
     *
     * @param departments
     */
    void setDepartments(List<Department> departments);

    /**
     *
     * @param mailStop
     */
    void setMailStop(MailStop mailStop);

    /**
     *
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /**
     * 
     * @param type
     */
    void setType(String type);
}