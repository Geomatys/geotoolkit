/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.EnvelopeWithTimePeriod;


/**
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
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}Envelope"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}Null"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="nilReason" type="{http://www.opengis.net/gml/3.2}NilReasonType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingShapeType", propOrder = {
    "envelope",
    "_null"
})
public class BoundingShapeType implements BoundingShape {

    @XmlElementRef(name = "Envelope", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<? extends EnvelopeType> envelope;
    @XmlList
    @XmlElement(name = "Null")
    private List<String> _null;
    @XmlAttribute
    private List<String> nilReason;

    public BoundingShapeType() {
    }

    public BoundingShapeType(final String nul) {
        this._null = new ArrayList<>();
        this._null.add(nul);
    }

    public BoundingShapeType(final BoundingShape that) {
        if (that != null) {
            if (that.getEnvelope() != null) {
                final ObjectFactory factory = new ObjectFactory();
                if (that.getEnvelope() instanceof EnvelopeWithTimePeriod) {
                    this.envelope = factory.createEnvelopeWithTimePeriod(new EnvelopeWithTimePeriodType((EnvelopeWithTimePeriod)that.getEnvelope()));
                } else if (that.getEnvelope() instanceof Envelope) {
                    this.envelope = factory.createEnvelope(new EnvelopeType(that.getEnvelope()));
                }
            }
           if (that.getNull() != null) {
               this._null = new ArrayList<>(that.getNull());
           }
           if (that.getNilReason() != null) {
               this.nilReason = new ArrayList<>(that.getNilReason());
           }
        }
    }

    public BoundingShapeType(final EnvelopeType envelope) {
        final ObjectFactory factory = new ObjectFactory();
        if (envelope instanceof EnvelopeWithTimePeriodType) {
            this.envelope = factory.createEnvelopeWithTimePeriod((EnvelopeWithTimePeriodType)envelope);
        } else if (envelope instanceof EnvelopeType) {
            this.envelope = factory.createEnvelope(envelope);
        }
        if (envelope == null) {
            this._null = new ArrayList<>();
            this._null.add("not_bounded");
        }

    }

    public BoundingShapeType(final org.opengis.geometry.Envelope envelope) {
        if (envelope == null) {
            this._null = new ArrayList<>();
            this._null.add("not_bounded");
        } else {
            final ObjectFactory factory = new ObjectFactory();
            final EnvelopeType gmlEnv = new EnvelopeType(envelope);
            this.envelope = factory.createEnvelope(gmlEnv);
        }
    }

    @Override
    public EnvelopeType getEnvelope() {
        if (envelope != null) {
            return envelope.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the envelope property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EnvelopeWithTimePeriodType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
     *
     */
    public JAXBElement<? extends EnvelopeType> getjbEnvelope() {
        return envelope;
    }

    /**
     * Sets the value of the envelope property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EnvelopeWithTimePeriodType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
     *
     */
    public void setEnvelope(JAXBElement<? extends EnvelopeType> value) {
        this.envelope = ((JAXBElement<? extends EnvelopeType> ) value);
    }

    /**
     * Gets the value of the null property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
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
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    @Override
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<>();
        }
        return this.nilReason;
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
                   Objects.equals(this.getEnvelope(),          that.getEnvelope())          &&
                   Objects.equals(this.getNilReason(),         that.getNilReason());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.envelope != null ? this.envelope.hashCode() : 0);
        hash = 47 * hash + (this._null != null ? this._null.hashCode() : 0);
        hash = 47 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[BoundingshapeEntry]").append('\n');
        if (envelope != null) {
            s.append("envelope:").append(envelope.getValue());
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
