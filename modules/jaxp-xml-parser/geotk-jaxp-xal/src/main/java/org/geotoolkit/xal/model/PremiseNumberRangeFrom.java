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
 * <p>Start number details of the premise number range.</p>;
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberRangeFrom">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumber" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberRangeFrom {

    /**
     *
     * @return
     */
    public List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @return
     */
    public List<PremiseNumberPrefix> getPremiseNumberPrefixes();

    /**
     *
     * @return
     */
    public List<PremiseNumber> getPremiseNumbers();

    /**
     * 
     * @return
     */
    public List<PremiseNumberSuffix> getPremiseNumberSuffixes();
}
