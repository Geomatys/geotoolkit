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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.apache.sis.util.ComparisonMode;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quantity", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
public class QuantityType extends AbstractDataComponentType implements Quantity {

    private UomPropertyType uom;
    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    private Double value;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String axisID;
    @XmlAttribute
    private String referenceFrame;

    /**
     * A empty constructor used by JAXB
     */
    public QuantityType() {

    }

    public QuantityType(final Quantity q) {
        super(q);
        if (q != null) {
            this.axisID         = q.getAxisID();
            this.referenceFrame = q.getReferenceFrame();
            this.value          = q.getValue();
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

    /**
     * Build a new QuantityType
     */
    public QuantityType(final String definition, final String uomCode, final String uomHref) {
        super(null, definition, null);
        this.uom = new UomPropertyType(uomCode, uomHref);
    }

    /**
     * Build a new QuantityType
     */
    public QuantityType(final String definition, final String uomCode) {
        super(null, definition, null);
        this.uom = new UomPropertyType(uomCode, null);
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
     * Invoked through Java reflection.
     */
    public void setUom(UomPropertyType uom) {
        this.uom = uom;
    }

    /**
     * Gets the value of the value property.
     */
    @Override
    public Double getValue() {
        return value;
    }

    /**
     * Invoked through Java reflection.
     */
    public void setValue(Double value) {
        this.value = value;
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
     * Gets the value of the referenceFrame property.
     */
    @Override
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * @return the constraint
     */
    @Override
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(final AllowedValuesPropertyType constraint) {
        this.constraint = constraint;
    }

    /**
     * @return the quality
     */
    @Override
    public List<QualityPropertyType> getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(final List<QualityPropertyType> quality) {
        this.quality = quality;
    }

    public void setQuality(final QualityPropertyType quality) {
        if (quality != null) {
            if (this.quality == null) {
                this.quality = new ArrayList<QualityPropertyType>();
            }
            this.quality.add(quality);
        }
    }


    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof QuantityType && super.equals(object, mode)) {
        final QuantityType that = (QuantityType) object;
        return Objects.equals(this.axisID,     that.axisID)     &&
               Objects.equals(this.referenceFrame, that.referenceFrame) &&
               Objects.equals(this.uom,            that.uom)            &&
               Objects.equals(this.quality,        that.quality)        &&
               Objects.equals(this.constraint,     that.constraint)     &&
               Objects.equals(this.value,          that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 79 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 79 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 79 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        hash = 79 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[QuantityType]").append('\n').append("super:").append(super.toString()).append('\n');
        if (axisID != null) {
            s.append("axisId:").append(axisID).append('\n');
        }
        if (referenceFrame != null) {
            s.append("referenceFrame:").append(referenceFrame).append('\n');
        }
        if (value != null) {
            s.append("value:").append(value).append('\n');
        }
        if (uom != null) {
            s.append("uom: ").append(uom.toString());
        }
        if (quality != null) {
            s.append("quality: ").append('\n');
            for (QualityPropertyType qp: quality) {
                s.append(qp).append('\n');
            }
        }
        if (constraint != null) {
            s.append("constraint: ").append(constraint.toString());
        }
        return s.toString();
    }
}
