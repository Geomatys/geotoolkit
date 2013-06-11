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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractTime;
import org.apache.sis.util.ComparisonMode;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Time", propOrder = {
    "uom",
    "value"
})
public class TimeType extends AbstractDataComponentType implements AbstractTime {

    private UomPropertyType uom;
    //private AllowedTimesPropertyType constraint;
    //private QualityPropertyType quality;
    @XmlList
    private List<String> value;
    @XmlAttribute
    private String localFrame;
    @XmlAttribute
    private String referenceFrame;
    @XmlAttribute
    private String referenceTime;

    /**
     * A empty contructor used by JAXB
     */
    public TimeType() {
        
    }

    public TimeType(final AbstractTime time) {
        super(time);
        if (time != null) {
           if (time.getUom() != null) {
                this.uom = new UomPropertyType(time.getUom());
            }
            this.referenceFrame = time.getReferenceFrame();
            this.referenceTime  = time.getReferenceTime();
            this.localFrame     = time.getLocalFrame();
            List<String> times  = time.getValue();
            if (times.size() > 0) {
                this.value      =  times;
            }
        }

    }
    
    /**
     * Build a new TimeType
     */
    public TimeType(final String definition, final String uomCode, final String uomHref) {
        super(null, definition, null);
        if (uomCode != null || uomHref != null) {
            this.uom = new UomPropertyType(uomCode, uomHref);
        }
    }
    
    public TimeType(final String definition, final UomPropertyType uom) {
        super(null, definition, null);
        this.uom = uom;
    }

    /**
     * Build a new TimeType
     */
    public TimeType(final String definition) {
        super(null, definition, null);
    }
    
    /**
     * Gets the value of the uom property.
     */
    @Override
    public UomPropertyType getUom() {
        return uom;
    }

    /**
     * Gets the value of the value property.
     * 
     */
    @Override
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the localFrame property.
     */
    @Override
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    @Override
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Gets the value of the referenceTime property.
     */
    @Override
    public String getReferenceTime() {
        return referenceTime;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof TimeType && super.equals(object, mode)) {
            final TimeType that = (TimeType) object;

            boolean valueEqorEmpty = Objects.equals(this.value, that.value);
            if (!valueEqorEmpty) {
                if (this.value == null && (that.value != null && that.value.isEmpty()) ||
                    that.value == null && (this.value != null && this.value.isEmpty())) {
                    valueEqorEmpty = true;
                }
            }
            return Objects.equals(this.localFrame,     that.localFrame)     &&
                   Objects.equals(this.referenceFrame, that.referenceFrame) &&
                   Objects.equals(this.referenceTime,  that.referenceTime)  &&
                   Objects.equals(this.uom,            that.uom)            &&
                   valueEqorEmpty;
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 61 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 61 * hash + (this.localFrame != null ? this.localFrame.hashCode() : 0);
        hash = 61 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 61 * hash + (this.referenceTime != null ? this.referenceTime.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        if (localFrame != null) {
            s.append("localFrame:").append(localFrame).append('\n');
        }
        if (referenceFrame != null) {
            s.append("referenceFrame:").append(referenceFrame).append('\n');
        }
        if (referenceTime != null) {
            s.append("referenceTime:").append(referenceTime).append('\n');
        }
        if (value != null) {
            s.append("value:").append('\n');
            for (String ss:value){
                s.append(ss).append('\n');
            }
        }
        if (uom != null) {
            s.append("uom: ").append(uom.toString());
        }
        return s.toString();
    }
    
}
