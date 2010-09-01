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
 * <p>This interface maps PostOffice element.</p>
 *
 * <p>Specification of a post office. Examples are a rural post office where
 * post is delivered and a post office containing post office boxes.</p>
 *
 * <pre>
 * &lt;xs:element name="PostOffice">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:choice>
 *              &lt;xs:element name="PostOfficeName" minOccurs="0" maxOccurs="unbounded">...
 *              &lt;/xs:element>
 *              &lt;xs:element name="PostOfficeNumber" minOccurs="0">...
 *              &lt;/xs:element>
 *          &lt;/xs:choice>
 *          &lt;xs:element name="PostalRoute" type="PostalRouteType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostBox" minOccurs="0"/>
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
public interface PostOffice {

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

    /*
     * === CHOICE: ===
     */

    /**
     * <p>Specification of the name of the post office.
     * This can be a rural postoffice where post is delivered
     * or a post office containing post office boxes.</p>
     *
     * @return
     */
    List<GenericTypedGrPostal> getPostOfficeNames();

    /**
     *
     * @param postOfficeNames
     */
    void setPostOfficeNames(List<GenericTypedGrPostal> postOfficeNames);

    /**
     * <p>Specification of the number of the postoffice. Common in rural postoffices.</p>
     *
     * @return
     */
    PostOfficeNumber getPostOfficeNumber();

    /**
     *
     * @param postOfficeNumber
     */
    void setPostOfficeNumber(PostOfficeNumber postOfficeNumber);

    /*
     * === END OF CHOICE ===
     */

    /**
     * <p>A Postal van is specific for a route as in Israel, Rural route.</p>
     *
     * @return
     */
    PostalRoute getPostalRoute();

    /**
     *
     * @param postalRoute
     */
    void setPostalRoute(PostalRoute postalRoute);

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
    PostalCode getPostalCode();

    /**
     *
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /*
     * === ATTRIBUTES
     */

    /**
     * <p>Could be a Mobile Postoffice Van as in Isreal.</p>
     * @return
     */
    String getType();

    /**
     *
     * @param type
     */
    void setType(String type);

    /**
     * <p>eg. Kottivakkam (P.O) here (P.O) is the Indicator.</p>
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
