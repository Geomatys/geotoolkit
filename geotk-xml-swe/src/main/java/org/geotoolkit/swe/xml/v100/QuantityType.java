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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.apache.sis.util.ComparisonMode;


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
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0}QualityPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
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
    "uom",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "Quantity")
public class QuantityType extends AbstractDataComponentType implements Quantity {

    private UomPropertyType uom;
    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    private Double value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    public QuantityType() {

    }

    public QuantityType(final Quantity q) {
        super(q);
        if (q != null) {
            this.axisID = q.getAxisID();
            this.referenceFrame = q.getReferenceFrame();
            this.value = q.getValue();
            if (q.getUom() != null) {
                this.uom = new UomPropertyType(q.getUom());
            }
            if (q.getConstraint() != null) {
                this.constraint = new AllowedValuesPropertyType(q.getConstraint());
            }
            if (q.getQuality() != null) {
                this.quality = new ArrayList<QualityPropertyType>();
                for (AbstractQualityProperty qual : q.getQuality()) {
                    this.quality.add(new QualityPropertyType(qual));
                }
            }
        }
    }

    public QuantityType(final String definition, final UomPropertyType uom, final Double value) {
        super(definition);
        this.uom   = uom;
        this.value = value;
    }

    public QuantityType(final String axisID, final String definition, final UomPropertyType uom, final Double value) {
        super(definition);
        this.axisID = axisID;
        this.uom    = uom;
        this.value  = value;
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
     * Gets the value of the constraint property.
     */
    @Override
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
    public Double getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final Double value) {
        this.value = value;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    @Override
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
    @Override
    public String getAxisID() {
        return axisID;
    }

    /**
     * Sets the value of the axisID property.
     */
    @Override
    public void setAxisID(final String value) {
        this.axisID = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof QuantityType && super.equals(object, mode)) {
            final QuantityType that = (QuantityType) object;

            return Objects.equals(this.axisID, that.axisID)   &&
                   Objects.equals(this.getQuality(), that.getQuality()) &&
                   Objects.equals(this.referenceFrame, that.referenceFrame) &&
                   Objects.equals(this.uom, that.uom) &&
                   Objects.equals(this.value, that.value) &&
                   Objects.equals(this.constraint,  that.constraint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 67 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 67 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 67 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 67 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 67 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (uom != null) {
            s.append("uom = ").append(uom).append('\n');
        }
        if (constraint != null) {
            s.append(" constraint = ").append(constraint).append('\n');
        }
        if (quality != null) {
            s.append(" quality = ").append(quality).append('\n');
        }
        if (value != null) {
            s.append(" value = ").append(value).append('\n');
        }
        if (referenceFrame != null) {
            s.append(" referenceFrame = ").append(referenceFrame).append('\n');
        }
        if (axisID != null) {
            s.append(" axisID = ").append(axisID).append('\n');
        }
        return s.toString();
    }

}
