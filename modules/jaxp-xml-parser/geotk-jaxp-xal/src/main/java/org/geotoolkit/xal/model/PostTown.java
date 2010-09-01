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
 * <p>This interface maps PostTown element.</p>
 *
 * <p>A post town is not the same as a locality.
 * A post town can encompass a collection of (small) localities.
 * It can also be a subpart of a locality.
 * An actual post town in Norway is "Bergen".</p>
 *
 * <pre>
 * &lt;xs:element name="PostTown" minOccurs="0">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PostTownName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="PostTownSuffix" minOccurs="0">...
 *          &lt;/xs:element>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PostTown {

    /**
     * 
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the post town.</p>
     * 
     * @return
     */
    List<GenericTypedGrPostal> getPostTownNames();

    /**
     *
     * @return
     */
    PostTownSuffix getPostTownSuffix();

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
     * @param postTownNames
     */
    void setPostTownNames(List<GenericTypedGrPostal> postTownNames);

    /**
     *
     * @param postTownSuffix
     */
    void setPostTownSuffix(PostTownSuffix postTownSuffix);

    /**
     * 
     * @param type
     */
    void setType(String type);
}
