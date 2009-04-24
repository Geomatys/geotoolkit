/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="uom" type="{http://www.opengis.net/swe/1.0}UomPropertyType" minOccurs="0"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0}AllowedTimesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0}QualityPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/1.0}timePair" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0}TRSAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "TimeRange")
public class TimeRange extends AbstractDataComponentType {

    private UomPropertyType uom;
    private AllowedTimesPropertyType constraint;
    private QualityPropertyType quality;
    @XmlList
    private List<String> value;
    @XmlAttribute
    private String referenceTime;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String localFrame;

    public TimeRange() {

    }

    public TimeRange(List<String> value) {
        this.value = value;
    }

    /**
     * Gets the value of the uom property.
     */
    public UomPropertyType getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     */
    public void setUom(UomPropertyType value) {
        this.uom = value;
    }

    /**
     * Gets the value of the constraint property.
     */
    public AllowedTimesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     */
    public void setConstraint(AllowedTimesPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the quality property.
     */
    public QualityPropertyType getQuality() {
        return quality;
    }

    /**
     * Sets the value of the quality property.
     */
    public void setQuality(QualityPropertyType value) {
        this.quality = value;
    }

    /**
     * Gets the value of the value property.
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the referenceTime property.
     */
    public String getReferenceTime() {
        return referenceTime;
    }

    /**
     * Sets the value of the referenceTime property.
     */
    public void setReferenceTime(String value) {
        this.referenceTime = value;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the localFrame property.
     */
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     */
    public void setLocalFrame(String value) {
        this.localFrame = value;
    }

}
