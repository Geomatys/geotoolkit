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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="value" type="{http://www.opengis.net/swe/1.0}timePositionType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0}TRSAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "Time")
public class TimeType extends AbstractDataComponentType implements AbstractTime {

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

    public TimeType() {

    }

    public TimeType(URI definition, UomPropertyType uom) {
        super(definition);
        this.uom = uom;
    }

    public TimeType(AbstractTime time) {
        super(time);
        if (time != null) {
            if (time.getUom() != null) {
                this.uom = new UomPropertyType(time.getUom());
            }
            this.referenceFrame = time.getReferenceFrame();
            this.referenceTime  = time.getReferenceTime();
            this.localFrame     = time.getLocalFrame();
            this.value          = time.getValue();
        }

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
     * 
     */
    public AllowedTimesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     *     
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof TimeType) {
            final TimeType that = (TimeType) object;

            return Utilities.equals(this.referenceTime, that.referenceTime)   &&
                   Utilities.equals(this.quality, that.quality) &&
                   Utilities.equals(this.localFrame, that.localFrame) &&
                   Utilities.equals(this.referenceFrame, that.referenceFrame) &&
                   Utilities.equals(this.uom, that.uom) &&
                   Utilities.equals(this.getValue(), that.getValue()) &&
                   Utilities.equals(this.constraint,  that.constraint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 53 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 53 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 53 * hash + (this.referenceTime != null ? this.referenceTime.hashCode() : 0);
        hash = 53 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 53 * hash + (this.localFrame != null ? this.localFrame.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (uom != null) {
            s.append("uom:").append(uom).append('\n');
        }
        if (constraint != null) {
            s.append("constraint:").append(constraint).append('\n');
        }
        if (quality != null) {
            s.append("quality:").append(quality).append('\n');
        }
        if (value != null) {
            s.append("value:").append(value).append('\n');
        }
        if (referenceTime != null) {
            s.append("referenceTime:").append(referenceTime).append('\n');
        }
        if (referenceFrame != null) {
            s.append("referenceFrame:").append(referenceFrame).append('\n');
        }
        if (localFrame != null) {
            s.append("localFrame").append(localFrame).append('\n');
        }
        return s.toString();
    }

}
