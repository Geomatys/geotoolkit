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
 * <p>This interface maps PostalRouteType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="PostalRouteType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:choice>
 *          &lt;xs:element name="PostalRouteName" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostalRouteNumber">...
 *          &lt;/xs:element>
 *      &lt;/xs:choice>
 *      &lt;xs:element ref="PostBox" minOccurs="0"/>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface PostalRoute {

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
     * <p>Name of the Postal Route.</p>
     *
     * @return
     */
    List<GenericTypedGrPostal> getPostalRouteNames();

    /**
     *
     * @param postalRouteNames
     */
    void setPostalRouteNames(List<GenericTypedGrPostal> postalRouteNames);

    /**
     * <p>Number of the Postal Route.</p>
     *
     * @return
     */
    PostalRouteNumber getPostalRouteNumber();

    /**
     *
     * @param postalRouteNumber
     */
    void setPostalRouteNumber(PostalRouteNumber postalRouteNumber);

    /*
     * === END OF CHOICE ===
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

    /*
     * === ATTRIBUTES ===
     */

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
