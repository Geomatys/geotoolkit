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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Envelope that includes also a temporal extent.
 * 
 * <p>Java class for EnvelopeWithTimePeriodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnvelopeWithTimePeriodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}EnvelopeType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}timePosition" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnvelopeWithTimePeriodType", propOrder = {
    "beginPosition",
    "endPosition"
})
public class EnvelopeWithTimePeriodType extends EnvelopeType {

    @XmlElement(required = true)
    private TimePositionType beginPosition;
    @XmlElement(required = true)
    private TimePositionType endPosition;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String frame;

    /**
     * Gets the value of the beginPosition property.
     */
    public TimePositionType getBeginPosition() {
        return beginPosition;
    }

    /**
     * Gets the value of the endPosition property.
     */
    public TimePositionType getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the value of the frame property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrame() {
        return frame;
    }

    /**
     * Sets the value of the frame property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrame(final String value) {
        this.frame = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnvelopeWithTimePeriodType other = (EnvelopeWithTimePeriodType) obj;
        if (this.beginPosition != other.beginPosition && (this.beginPosition == null || !this.beginPosition.equals(other.beginPosition))) {
            return false;
        }
        if (this.endPosition != other.endPosition && (this.endPosition == null || !this.endPosition.equals(other.endPosition))) {
            return false;
        }
        if ((this.frame == null) ? (other.frame != null) : !this.frame.equals(other.frame)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.beginPosition != null ? this.beginPosition.hashCode() : 0);
        hash = 23 * hash + (this.endPosition != null ? this.endPosition.hashCode() : 0);
        hash = 23 * hash + (this.frame != null ? this.frame.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        if (beginPosition != null) {
            s.append("beginPosition:").append(beginPosition).append('\n');
        }
        if (endPosition != null) {
            s.append("endPosition:").append(endPosition).append('\n');
        }
        if (frame != null) {
            s.append("frame:").append(frame).append('\n');
        }
        return s.toString();
    }

}
