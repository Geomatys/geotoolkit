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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.util.Utilities;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Quantity", propOrder = {
    "uom",
    "value"
})
public class QuantityType extends AbstractDataComponentEntry implements Quantity {

    protected UomPropertyType uom;
   // protected AllowedValuesPropertyType constraint;
    //protected List<QualityPropertyType> quality;
    protected Double value;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String axisID;
    @XmlAttribute
    protected String referenceFrame;

    /**
     * A empty contructor used by JAXB
     */
    public QuantityType() {
        
    }
    
    /**
     * Build a new QuantityType
     */
    public QuantityType(String definition, String uomCode, String uomHref) {
        super(null, definition, false);
        this.uom = new UomPropertyType(uomCode, uomHref);
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
    public String getReferenceFrame() {
        return referenceFrame;
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
               Utilities.equals(this.value,          that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 23 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        hash = 23 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
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
        return s.toString();
    }
}
