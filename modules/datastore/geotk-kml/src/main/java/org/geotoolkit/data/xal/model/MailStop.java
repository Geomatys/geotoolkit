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
 * <p>This interface maps MailStopType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="MailStopType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="MailStopName" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="MailStopNumber" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface MailStop {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the the Mail Stop. eg. MSP, MS, etc.</p>
     *
     * @return
     */
    public List<GenericTypedGrPostal> getMailStopNames();

    /**
     * <p>Number of the Mail stop. eg. 123 in MS 123.</p>
     * 
     * @return
     */
    public MailStopNumber getMailStopNumber();

    /**
     *
     * @return
     */
    public String getType();

}
