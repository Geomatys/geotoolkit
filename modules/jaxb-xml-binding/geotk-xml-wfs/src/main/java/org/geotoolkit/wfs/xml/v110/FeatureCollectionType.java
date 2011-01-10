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
package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v311.AbstractFeatureCollectionType;
import org.geotoolkit.wfs.xml.WFSResponse;


/**
 * This type defines a container for the response to a GetFeature or GetFeatureWithLock request.
 * If the request is GetFeatureWithLock, the lockId attribute must be populated.
 * The lockId attribute can otherwise be safely ignored.
 *          
 * 
 * <p>Java class for FeatureCollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureCollectionType">
 *       &lt;attribute name="lockId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="numberOfFeatures" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureCollectionType")
@XmlRootElement(name = "FeatureCollection")
public class FeatureCollectionType extends AbstractFeatureCollectionType implements WFSResponse {

    @XmlAttribute
    private String lockId;
    @XmlAttribute
    private XMLGregorianCalendar timeStamp;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer numberOfFeatures;

    public FeatureCollectionType() {

    }

    public FeatureCollectionType(final Integer numberOfFeatures, final XMLGregorianCalendar timeStamp) {
        this.numberOfFeatures = numberOfFeatures;
        this.timeStamp        = timeStamp;
    }

    /**
     * Gets the value of the lockId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLockId() {
        return lockId;
    }

    /**
     * Sets the value of the lockId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLockId(final String value) {
        this.lockId = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(final XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the numberOfFeatures property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberOfFeatures() {
        return numberOfFeatures;
    }

    /**
     * Sets the value of the numberOfFeatures property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberOfFeatures(final Integer value) {
        this.numberOfFeatures = value;
    }

     @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (lockId != null) {
            s.append("lockId:").append(lockId).append('\n');
        }
        if (numberOfFeatures != null) {
            s.append("numberOfFeatures:").append(numberOfFeatures).append('\n');
        }
        if (timeStamp != null) {
            s.append("timeStamp:").append(timeStamp).append('\n');
        }
        return s.toString();
     }
}
