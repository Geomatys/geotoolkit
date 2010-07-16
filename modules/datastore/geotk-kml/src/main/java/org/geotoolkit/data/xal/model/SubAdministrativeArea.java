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
 */
public interface SubAdministrativeArea {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getSubAdministrativeAreaNames();

    /*
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    public Locality getLocality();

    /**
     *
     * @return
     */
    public PostOffice getPostOffice();

    /**
     * 
     * @return
     */
    public PostalCode getPostalCode();

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
    public String getType();

    /**
     * <p>Postal or Political - 
     * Sometimes locations must be distinguished between postal system, 
     * and physical locations as defined by a political system.</p>
     *
     * @return
     */
    public String getUsageType();

    /**
     * <p>Erode (Dist) where (Dist) is the Indicator.</p>
     *
     * @return
     */
    public String getIndicator();

}
