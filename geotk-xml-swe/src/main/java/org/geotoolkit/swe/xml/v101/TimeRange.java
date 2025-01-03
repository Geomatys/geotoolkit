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
package org.geotoolkit.swe.xml.v101;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractTimeRange;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="uom" type="{http://www.opengis.net/swe/1.0.1}UomPropertyType" minOccurs="0"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0.1}AllowedTimesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0.1}QualityPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/1.0.1}timePair" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0.1}TRSAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "TimeRange")
public class TimeRange extends AbstractDataComponentType implements AbstractTimeRange {

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

    public TimeRange(final List<String> value) {
        this.value = value;
    }

    public TimeRange(final AbstractTimeRange q) {
        super(q);
        if (q != null) {
           /* if (q.getConstraint() != null) {
                this.constraint = new AllowedValuesPropertyType(q.getConstraint());
            }*/
            if (q.getQuality() != null && !q.getQuality().isEmpty()) {
                this.quality = new QualityPropertyType(q.getQuality().get(0));
            }
            this.referenceFrame = q.getReferenceFrame();
            this.referenceTime  = q.getReferenceTime();
            this.localFrame     = q.getLocalFrame();
            this.value          = q.getValue();
            if (q.getUom() != null) {
                this.uom = new UomPropertyType(q.getUom());
            }
        }
    }

    /**
     * Gets the value of the quality property.
     *
     */
    @Override
    public List<QualityPropertyType> getQuality() {
        if (this.quality != null) {
            return Arrays.asList(this.quality);
        }
        return null;
    }

    public void setQuality(final QualityPropertyType quality) {
        this.quality = quality;
    }

    /**
     * Gets the value of the uom property.
     */
    @Override
    public UomPropertyType getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     */
    public void setUom(final UomPropertyType value) {
        this.uom = value;
    }

    /**
     * Gets the value of the value property.
     */
    @Override
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the value property.
     */
    public void setValue(final List<String> value) {
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     */
    public void setValue(final String value) {
        if (this.value == null) {
            this.value = new ArrayList<String>();
        }
        this.value.add(value);
    }

    /**
     * Gets the value of the referenceTime property.
     */
    @Override
    public String getReferenceTime() {
        return referenceTime;
    }

    /**
     * Sets the value of the referenceTime property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReferenceTime(final String value) {
        this.referenceTime = value;
    }

    /**
     * Gets the value of the referenceFrame property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReferenceFrame(final String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the localFrame property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocalFrame(final String value) {
        this.localFrame = value;
    }

    /**
     * @return the constraint
     */
    public AllowedTimesPropertyType getConstraint() {
        return constraint;
    }

}
