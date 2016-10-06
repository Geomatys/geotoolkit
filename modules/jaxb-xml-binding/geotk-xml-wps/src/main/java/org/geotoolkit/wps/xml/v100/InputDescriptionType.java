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
package org.geotoolkit.wps.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.xml.DataDescription;
import org.geotoolkit.wps.xml.InputDescription;


/**
 * Description of an input to a process. 
 * 
 * In this use, the DescriptionType shall describe this process input. 
 * 
 * <p>Java class for InputDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/wps/1.0.0}InputFormChoice"/>
 *       &lt;/sequence>
 *       &lt;attribute name="minOccurs" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="maxOccurs" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputDescriptionType", propOrder = {
    "complexData",
    "literalData",
    "boundingBoxData"
})
public class InputDescriptionType extends DescriptionType implements InputDescription {

    @XmlElement(name = "ComplexData", namespace = "")
    protected SupportedComplexDataInputType complexData;
    @XmlElement(name = "LiteralData", namespace = "")
    protected LiteralInputType literalData;
    @XmlElement(name = "BoundingBoxData", namespace = "")
    protected SupportedCRSsType boundingBoxData;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected Integer minOccurs;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected Integer maxOccurs;

    public InputDescriptionType() {
        
    }
    
    public InputDescriptionType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, 
            Integer minOccur, Integer maxOccur, DataDescription dataDescription) {
        super(identifier, title, _abstract);
        this.minOccurs = minOccur;
        this.maxOccurs = maxOccur;
        if (dataDescription instanceof SupportedComplexDataInputType) {
            this.complexData = (SupportedComplexDataInputType) dataDescription;
        } else if (dataDescription instanceof LiteralInputType) {
            this.literalData = (LiteralInputType) dataDescription;
        } else if (dataDescription instanceof SupportedCRSsType) {
            this.boundingBoxData = (SupportedCRSsType) dataDescription;
        } else if (dataDescription != null) {
            throw new IllegalArgumentException("unecpected data description type:" + dataDescription.getClass().getName());
        }
    }
    
    /**
     * Gets the value of the complexData property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedComplexDataInputType }
     *     
     */
    public SupportedComplexDataInputType getComplexData() {
        return complexData;
    }

    /**
     * Sets the value of the complexData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedComplexDataInputType }
     *     
     */
    public void setComplexData(final SupportedComplexDataInputType value) {
        this.complexData = value;
    }

    /**
     * Gets the value of the literalData property.
     * 
     * @return
     *     possible object is
     *     {@link LiteralInputType }
     *     
     */
    public LiteralInputType getLiteralData() {
        return literalData;
    }

    /**
     * Sets the value of the literalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiteralInputType }
     *     
     */
    public void setLiteralData(final LiteralInputType value) {
        this.literalData = value;
    }

    /**
     * Gets the value of the boundingBoxData property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCRSsType }
     *     
     */
    public SupportedCRSsType getBoundingBoxData() {
        return boundingBoxData;
    }

    /**
     * Sets the value of the boundingBoxData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCRSsType }
     *     
     */
    public void setBoundingBoxData(final SupportedCRSsType value) {
        this.boundingBoxData = value;
    }

    /**
     * Gets the value of the minOccurs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the value of the minOccurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMinOccurs(final Integer value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the value of the maxOccurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxOccurs(final Integer value) {
        this.maxOccurs = value;
    }

}
