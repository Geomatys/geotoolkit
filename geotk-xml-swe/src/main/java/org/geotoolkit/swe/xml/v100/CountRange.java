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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractCountRange;
import org.geotoolkit.swe.xml.AbstractQualityProperty;


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
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0}QualityPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/1.0}countPair" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0}SimpleComponentAttributeGroup"/>
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
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "CountRange")
public class CountRange extends AbstractDataComponentType implements AbstractCountRange {

    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    @XmlList
    private List<Integer> value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    public CountRange() {

    }

    public CountRange(final AbstractCountRange q) {
        super(q);
        if (q != null) {
            this.axisID         = q.getAxisID();
            if (q.getConstraint() != null) {
                this.constraint = new AllowedValuesPropertyType(q.getConstraint());
            }
            if (q.getQuality() != null) {
                this.quality = new ArrayList<QualityPropertyType>();
                for (AbstractQualityProperty qual : q.getQuality()) {
                    this.quality.add(new QualityPropertyType(qual));
                }
            }
            this.referenceFrame = q.getReferenceFrame();
            this.value = q.getValue();
        }
    }

    /**
     * Gets the value of the constraint property.
     */
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
    */
    public void setConstraint(final AllowedValuesPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the quality property.
     *
     */
    public List<QualityPropertyType> getQuality() {
        if (quality == null) {
            quality = new ArrayList<QualityPropertyType>();
        }
        return this.quality;
    }

    public void setQuality(final QualityPropertyType quality) {
        if (quality != null) {
            if (this.quality == null) {
                this.quality = new ArrayList<QualityPropertyType>();
            }
            this.quality.add(quality);
        }
    }

    public void setQuality(final List<QualityPropertyType> quality) {
        this.quality = quality;
    }

    /**
     * Gets the value of the value property.
     */
    public List<Integer> getValue() {
        if (value == null) {
            value = new ArrayList<Integer>();
        }
        return this.value;
    }

    /**
     * Gets the value of the value property.
     *
     */
    public void setValue(final List<Integer> value) {
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     *
     */
    public void setValue(final Integer value) {
        if (this.value == null) {
            this.value = new ArrayList<Integer>();
        }
        this.value.add(value);
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
    public void setReferenceFrame(final String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the axisID property.
     */
    public String getAxisID() {
        return axisID;
    }

    /**
     * Sets the value of the axisID property.
     */
    public void setAxisID(final String value) {
        this.axisID = value;
    }

}
