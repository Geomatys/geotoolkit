/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.csw.xml.v200;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DistributedSearch;


/**
 * 
 * Governs the behaviour of a distributed search.
 * 
 * hopCount     - The maximum number of message hops before the search is terminated. 
 *                Each catalogue decrements this when the request is received, and does not forward it if hopCount=0.
 *          
 * 
 * <p>Java class for DistributedSearchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DistributedSearchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="hopCount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="2" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistributedSearchType")
public class DistributedSearchType implements DistributedSearch {

    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger hopCount;

    /**
     * Gets the value of the hopCount property.
     * 
     */
    public BigInteger getHopCount() {
        if (hopCount == null) {
            return new BigInteger("2");
        } else {
            return hopCount;
        }
    }

    /**
     * Sets the value of the hopCount property.
     * 
     */
    public void setHopCount(final BigInteger value) {
        this.hopCount = value;
    }

}
