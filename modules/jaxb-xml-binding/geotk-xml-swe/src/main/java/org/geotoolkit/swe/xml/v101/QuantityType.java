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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.util.Utilities;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quantity", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
public class QuantityType extends AbstractDataComponentEntry implements Quantity {

    private UomPropertyType uom;
    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    private Double value;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String axisID;
    @XmlAttribute
    private URI referenceFrame;

    /**
     * A empty contructor used by JAXB
     */
    public QuantityType() {
        
    }

    public QuantityType(Quantity q) {
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
    public QuantityType(String definition, String uomCode, String uomHref) {
        super(null, definition, null);
        this.uom = new UomPropertyType(uomCode, uomHref);
    }

    /**
     * Build a new QuantityType
     */
    public QuantityType(String definition, String uomCode) {
        super(null, definition, null);
        this.uom = new UomPropertyType(uomCode, null);
    }


    public QuantityType(String definition, UomPropertyType uom, Double value) {
        super(definition);
        this.uom   = uom;
        this.value = value;
    }

    public QuantityType(String axisID, String definition, UomPropertyType uom, Double value) {
        super(definition);
        this.axisID = axisID;
        this.uom    = uom;
        this.value  = value;
    }
    
    /**
     * Gets the value of the uom property.
    */
    public UomPropertyType getUom() {
        return uom;
    }

    /**
     * Gets the value of the value property.
     */
    public Double getValue() {
        return value;
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
    public void setAxisID(String value) {
        this.axisID = value;
    }
    
    /**
     * Gets the value of the referenceFrame property.
     */
    public URI getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * @return the constraint
     */
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * @param constraint the constraint to set
     */
    public void setConstraint(AllowedValuesPropertyType constraint) {
        this.constraint = constraint;
    }

    /**
     * @return the quality
     */
    public List<QualityPropertyType> getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(List<QualityPropertyType> quality) {
        this.quality = quality;
    }

    public void setQuality(QualityPropertyType quality) {
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QuantityType && super.equals(object)) {
        final QuantityType that = (QuantityType) object;
        return Utilities.equals(this.axisID,     that.axisID)     &&
               Utilities.equals(this.referenceFrame, that.referenceFrame) &&
               Utilities.equals(this.uom,            that.uom)            &&
               Utilities.equals(this.quality,        that.quality)        &&
               Utilities.equals(this.constraint,     that.constraint)     &&
               Utilities.equals(this.value,          that.value);
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
        if (axisID != null)
            s.append("axisId:").append(axisID).append('\n');
        if (referenceFrame != null)
            s.append("referenceFrame:").append(referenceFrame).append('\n');
        if (value != null)
            s.append("value:").append(value).append('\n');
        
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
