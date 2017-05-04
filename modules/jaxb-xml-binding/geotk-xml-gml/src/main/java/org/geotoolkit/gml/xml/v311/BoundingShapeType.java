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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.EnvelopeWithTimePeriod;


/**
 * Bounding shape.
 *
 * <p>Java class for BoundingShapeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BoundingShapeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *           &lt;element ref="{http://www.opengis.net/gml}Null"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingShapeType", propOrder = {
    "envelope",
    "envelopeWithTimePeriod",
    "_null"
})
public class BoundingShapeType implements BoundingShape {

    @XmlElement(name = "Envelope")
    private EnvelopeType envelope;
    @XmlElement(name = "EnvelopeWithTimePeriod")
    private EnvelopeWithTimePeriodType envelopeWithTimePeriod;
    @XmlList
    @XmlElement(name = "Null")
    private List<String> _null;
    @XmlAttribute
    private List<String> nilReason;

    BoundingShapeType() {}

     public BoundingShapeType(final BoundingShape that) {
        if (that != null) {
            if (that.getEnvelope() != null) {
                if (that.getEnvelope() instanceof EnvelopeWithTimePeriod) {
                    this.envelopeWithTimePeriod = new EnvelopeWithTimePeriodType((EnvelopeWithTimePeriod)that.getEnvelope());
                } else if (that.getEnvelope() instanceof Envelope) {
                    this.envelope = new EnvelopeType(that.getEnvelope());
                }
            }
           if (that.getNull() != null) {
               this._null = new ArrayList<String>(that.getNull());
           }
           if (that.getNilReason() != null) {
               this.nilReason = new ArrayList<String>(that.getNilReason());
           }
        }
    }

    public BoundingShapeType(final EnvelopeType envelope) {
        this.envelope = envelope;
        if (envelope == null) {
            this._null = new ArrayList<String>();
            this._null.add("not_bounded");
        }

    }

    public BoundingShapeType(final String nul) {
        this._null = new ArrayList<String>();
        this._null.add(nul);
    }

    /**
     * Gets the value of the envelope property.
     */
    @Override
    public EnvelopeType getEnvelope() {
        if (envelope != null) {
            return envelope;
        } else if (envelopeWithTimePeriod != null) {
            return envelopeWithTimePeriod;
        }
        return null;
    }

    /**
     * Sets the value of the envelope property.
     */
    public void setEnvelope(final EnvelopeType value) {
        this.envelope = value;
    }

    /**
     * Gets the value of the null property.
     */
    @Override
    public List<String> getNull() {
        if (_null == null) {
            _null = new ArrayList<String>();
        }
        return this._null;
    }

    /**
     * Gets the value of the nilReason property.
     *
     */
    @Override
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return nilReason;
    }

     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BoundingShapeType) {

            final BoundingShapeType that = (BoundingShapeType) object;

            return Objects.equals(this.getNull(),              that.getNull())              &&
                   Objects.equals(this.envelope,               that.envelope)               &&
                   Objects.equals(this.envelopeWithTimePeriod, that.envelopeWithTimePeriod) &&
                   Objects.equals(this.getNilReason(),         that.getNilReason());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.envelope != null ? this.envelope.hashCode() : 0);
        hash = 47 * hash + (this.envelopeWithTimePeriod != null ? this.envelopeWithTimePeriod.hashCode() : 0);
        hash = 47 * hash + (this._null != null ? this._null.hashCode() : 0);
        hash = 47 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[BoundingshapeEntry]").append('\n');
        if (envelope != null) {
            s.append("envelope:").append(envelope.toString());
        }
        if (envelopeWithTimePeriod != null) {
            s.append("envelopeWithTimePeriod:").append(envelopeWithTimePeriod.toString());
        }
        if (_null != null) {
            s.append("_null: ");
            for (String ss: _null) {
                s.append("       ").append(ss).append('\n');
            }
        }
        if (nilReason != null) {
            s.append("nilReason:").append('\n');
            for (String ss: nilReason) {
                s.append(ss).append('\n');
            }
        }
        return s.toString();
    }

}
