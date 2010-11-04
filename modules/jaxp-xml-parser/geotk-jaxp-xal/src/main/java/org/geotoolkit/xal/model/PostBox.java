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
 * @module pending
 */
public interface PostBox {

    /**
     * 
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    PostBoxNumber getPostBoxNumber();

    /**
     *
     * @return
     */
    PostBoxNumberPrefix getPostBoxNumberPrefix();

    /**
     *
     * @return
     */
    PostBoxNumberSuffix getPostBoxNumberSuffix();

    /**
     *
     * @return
     */
    PostBoxNumberExtension getPostBoxNumberExtension();

    /**
     *
     * @return
     */
    Firm getFirm();

    /**
     *
     * @return
     */
    PostalCode getPostalCode();

    /**
     * <p>Possible values are, not limited to: POBox and Freepost.</p>
     *
     * @return
     */
    String getType();

    /**
     * <p>LOCKED BAG NO:1234 where the Indicator is NO: and Type is LOCKED BAG.</p>
     *
     * @return
     */
    String getIndicator();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @param postBoxNumber
     */
    void setPostBoxNumber(PostBoxNumber postBoxNumber);

    /**
     *
     * @param postBoxNumberPrefix
     */
    void setPostBoxNumberPrefix(PostBoxNumberPrefix postBoxNumberPrefix);

    /**
     *
     * @param postBoxNumberSuffix
     */
    void setPostBoxNumberSuffix(PostBoxNumberSuffix postBoxNumberSuffix);

    /**
     *
     * @param postBoxNumberExtension
     */
    void setPostBoxNumberExtension(PostBoxNumberExtension postBoxNumberExtension);

    /**
     *
     * @param firm
     */
    void setFirm(Firm firm);

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

    /**
     * 
     * @param indicator
     */
    void setIndicator(String indicator);

}
