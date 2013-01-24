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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
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
        this._null = new ArrayList<String>();
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
               this._null = new ArrayList<String>(that.getNull());
           }
           if (that.getNilReason() != null) {
               this.nilReason = new ArrayList<String>(that.getNilReason());
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
            this._null = new ArrayList<String>();
            this._null.add("not_bounded");
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
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

}
