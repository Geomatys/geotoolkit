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
package org.geotoolkit.sld.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}CoverageName"/>
 *         &lt;element ref="{http://www.opengis.net/sld}CoverageExtent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "coverageName",
    "coverageExtent"
})
@XmlRootElement(name = "CoverageConstraint")
public class CoverageConstraint {

    @XmlElement(name = "CoverageName", namespace = "http://www.opengis.net/se", required = true)
    protected String coverageName;
    @XmlElement(name = "CoverageExtent")
    protected CoverageExtent coverageExtent;

    /**
     * Gets the value of the coverageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageName() {
        return coverageName;
    }

    /**
     * Sets the value of the coverageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageName(String value) {
        this.coverageName = value;
    }

    /**
     * Gets the value of the coverageExtent property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageExtent }
     *     
     */
    public CoverageExtent getCoverageExtent() {
        return coverageExtent;
    }

    /**
     * Sets the value of the coverageExtent property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageExtent }
     *     
     */
    public void setCoverageExtent(CoverageExtent value) {
        this.coverageExtent = value;
    }

}
