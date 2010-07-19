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
 *
 * <p>This interface maps PostBox element.</p>
 *
 * <p>Specification of a postbox like mail delivery point.
 * Only a single postbox number can be specified.
 * Examples of postboxes are POBox, free mail numbers, etc.</p>
 *
 * <pre>
 * &lt;xs:element name="PostBox">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PostBoxNumber">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostBoxNumberPrefix" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostBoxNumberSuffix" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostBoxNumberExtension" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="Firm" type="FirmType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
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
public interface PostBox {

    /**
     * 
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public PostBoxNumber getPostBoxNumber();

    /**
     *
     * @return
     */
    public PostBoxNumberPrefix getPostBoxNumberPrefix();

    /**
     *
     * @return
     */
    public PostBoxNumberSuffix getPostBoxNumberSuffix();

    /**
     *
     * @return
     */
    public PostBoxNumberExtension getPostBoxNumberExtension();

    /**
     *
     * @return
     */
    public Firm getFirm();

    /**
     *
     * @return
     */
    public PostalCode getPostalCode();

    /**
     * <p>Possible values are, not limited to: POBox and Freepost.</p>
     *
     * @return
     */
    public String getType();

    /**
     * <p>LOCKED BAG NO:1234 where the Indicator is NO: and Type is LOCKED BAG.</p>
     *
     * @return
     */
    public String getIndicator();

}
