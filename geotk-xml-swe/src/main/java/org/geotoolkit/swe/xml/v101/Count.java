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
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractCount;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.apache.sis.util.ComparisonMode;


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
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0.1}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0.1}QualityPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0.1}SimpleComponentAttributeGroup"/>
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
@XmlRootElement(name = "Count")
public class Count extends AbstractDataComponentType implements AbstractCount {

    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    private Integer value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    /**
     * Empty constructor used by JAXB.
     */
    Count() {

    }

    /**
     * Clone a new Count.
     */
    public Count(final Count value) {
        this.value          = value.value;
        this.axisID         = value.axisID;
        this.constraint     = value.constraint;
        this.quality        = value.quality;
        this.referenceFrame = value.referenceFrame;
    }

    public Count(final AbstractCount q) {
        super(q);
        if (q != null) {
            this.axisID         = q.getAxisID();
            if (q.getConstraint() != null) {
                this.constraint = new AllowedValuesPropertyType(q.getConstraint());
            }
            if (q.getQuality() != null && q.getQuality().size() > 0) {
                this.quality = new ArrayList<QualityPropertyType>();
                for (AbstractQualityProperty qual : q.getQuality()) {
                    this.quality.add(new QualityPropertyType(qual));
                }
            }
            this.referenceFrame = q.getReferenceFrame();
            if (q.getValue() != null) {
                this.value = q.getValue().intValue();
            }
        }
    }

    /**
     * Build a new Count with only the value.
     */
    public Count(final int value) {
        this.value = value;
    }

    /**
     * Gets the value of the quality property.
     */
    @Override
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
    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    @Override
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Gets the value of the axisID property.
     */
    @Override
    public String getAxisID() {
        return axisID;
    }

    /**
     * @return the constraint
     */
    @Override
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof Count && super.equals(object, mode)) {
            final Count that = (Count) object;
            return Objects.equals(this.axisID, that.axisID) &&
                    Objects.equals(this.constraint, that.constraint) &&
                    Objects.equals(this.getQuality(), that.getQuality()) &&
                    Objects.equals(this.referenceFrame, that.referenceFrame) &&
                    Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 47 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 47 * hash + this.value;
        hash = 47 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 47 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (constraint != null) {
            sb.append("constraint:").append(constraint).append('\n');
        }
        if (quality != null) {
            sb.append("quality:").append(quality).append('\n');
        }
        sb.append("value:").append(value).append('\n');
        if (referenceFrame != null) {
            sb.append("referenceFrame:").append(referenceFrame).append('\n');
        }
        if (axisID != null) {
            sb.append("axisID:").append(axisID).append('\n');
        }
        return sb.toString();
    }
}
