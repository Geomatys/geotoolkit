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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractBoolean;
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
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0}QualityPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "quality",
    "value"
})
@XmlRootElement(name = "Boolean")
public class BooleanType extends AbstractDataComponentType implements AbstractBoolean {

    private QualityPropertyType quality;
    private java.lang.Boolean value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    public BooleanType() {

    }

    public BooleanType(final String definition, final Boolean value) {
        super(definition);
        this.value = value;
    }

    /**
     * Build a new TimeType
     */
    public BooleanType(final AbstractBoolean bool) {
        super(bool);
        if (bool != null) {
            this.value  = bool.isValue();
            this.axisID = bool.getAxisID();
            this.referenceFrame = bool.getReferenceFrame();
        }

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
    public void setQuality(final QualityPropertyType value) {
        this.quality = value;
    }

    /**
     * Gets the value of the value property.
     */
    public java.lang.Boolean isValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final java.lang.Boolean value) {
        this.value = value;
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof BooleanType) {
            final BooleanType that = (BooleanType) object;

            return Objects.equals(this.axisID, that.axisID)   &&
                   Objects.equals(this.quality, that.quality) &&
                   Objects.equals(this.referenceFrame, that.referenceFrame) &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 71 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 71 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 71 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        return hash;
    }

}
