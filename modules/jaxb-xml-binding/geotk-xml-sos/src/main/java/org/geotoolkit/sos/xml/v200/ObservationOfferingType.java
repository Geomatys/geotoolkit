/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.EnvelopeWithTimePeriodType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.sos.xml.ObservationOffering;
import org.geotoolkit.swes.xml.v200.AbstractOfferingType;


/**
 * <p>Java class for ObservationOfferingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObservationOfferingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractOfferingType">
 *       &lt;sequence>
 *         &lt;element name="observedArea" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/gml/3.2}Envelope"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="phenomenonTime" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/gml/3.2}TimePeriod"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resultTime" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/gml/3.2}TimePeriod"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="responseFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observationType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="featureOfInterestType" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationOfferingType", propOrder = {
    "observedArea",
    "phenomenonTime",
    "resultTime",
    "responseFormat",
    "observationType",
    "featureOfInterestType"
})
public class ObservationOfferingType extends AbstractOfferingType implements ObservationOffering {

    private ObservationOfferingType.ObservedArea observedArea;
    private ObservationOfferingType.PhenomenonTime phenomenonTime;
    private ObservationOfferingType.ResultTime resultTime;
    @XmlSchemaType(name = "anyURI")
    private List<String> responseFormat;
    @XmlSchemaType(name = "anyURI")
    private List<String> observationType;
    @XmlSchemaType(name = "anyURI")
    private List<String> featureOfInterestType;

    public ObservationOfferingType() {
        
    }
    
    /**
     *  Build a new offering.
     */ 
    public ObservationOfferingType(final String id, final String name, final String description, final EnvelopeType observedArea, 
            final TimePeriodType phenomenonTime, final String procedure, final List<String> observedProperty, final List<String> featureOfInterest,
            final List<String> responseFormat, final List<String> resultModel) {
        
        super(id, name, description, procedure, observedProperty, featureOfInterest);
        this.responseFormat    = responseFormat;
        this.observationType   = resultModel;
        if (phenomenonTime != null) {
            this.phenomenonTime    = new PhenomenonTime();
        }
    }

    
    /**
     * Gets the value of the observedArea property.
     * 
     * @return
     *     possible object is
     *     {@link ObservationOfferingType.ObservedArea }
     *     
     */
    public ObservationOfferingType.ObservedArea getObservedArea() {
        return observedArea;
    }

    /**
     * Sets the value of the observedArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservationOfferingType.ObservedArea }
     *     
     */
    public void setObservedArea(ObservationOfferingType.ObservedArea value) {
        this.observedArea = value;
    }

    /**
     * Gets the value of the phenomenonTime property.
     * 
     * @return
     *     possible object is
     *     {@link ObservationOfferingType.PhenomenonTime }
     *     
     */
    public ObservationOfferingType.PhenomenonTime getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * Sets the value of the phenomenonTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservationOfferingType.PhenomenonTime }
     *     
     */
    public void setPhenomenonTime(ObservationOfferingType.PhenomenonTime value) {
        this.phenomenonTime = value;
    }

    /**
     * Gets the value of the resultTime property.
     * 
     * @return
     *     possible object is
     *     {@link ObservationOfferingType.ResultTime }
     *     
     */
    public ObservationOfferingType.ResultTime getResultTime() {
        return resultTime;
    }

    /**
     * Sets the value of the resultTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservationOfferingType.ResultTime }
     *     
     */
    public void setResultTime(ObservationOfferingType.ResultTime value) {
        this.resultTime = value;
    }

    /**
     * Gets the value of the responseFormat property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getResponseFormat() {
        if (responseFormat == null) {
            responseFormat = new ArrayList<String>();
        }
        return this.responseFormat;
    }

    /**
     * Gets the value of the observationType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getObservationType() {
        if (observationType == null) {
            observationType = new ArrayList<String>();
        }
        return this.observationType;
    }

    /**
     * Gets the value of the featureOfInterestType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getFeatureOfInterestType() {
        if (featureOfInterestType == null) {
            featureOfInterestType = new ArrayList<String>();
        }
        return this.featureOfInterestType;
    }

    @Override
    public List<String> getObservedProperties() {
        return getObservableProperty();
    }
    
    /**
     * compatibility with SOS 1.0.0
     */ 
    @Override
    public List<String> getProcedures() {
        if (getProcedure() != null) {
            return Arrays.asList(getProcedure());
        }
        return new ArrayList<String>();
    }
    
    /**
     * compatibility with SOS 1.0.0
     */ 
    @Override
    public List<String> getSrsName() {
        return new ArrayList<String>();
    }

    /**
     * compatibility with SOS 1.0.0
     */ 
    @Override
    public List<String> getFeatureOfInterestIds() {
        return new ArrayList<String>();
    }

    /**
     * compatibility with SOS 1.0.0
     */
    @Override
    public List<QName> getResultModel() {
        return new ArrayList<QName>();
    }

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/gml/3.2}Envelope"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "envelope"
    })
    public static class ObservedArea {

        @XmlElementRef(name = "Envelope", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
        private JAXBElement<? extends EnvelopeType> envelope;

        /**
         * Gets the value of the envelope property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link EnvelopeWithTimePeriodType }{@code >}
         *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
         *     
         */
        public JAXBElement<? extends EnvelopeType> getEnvelope() {
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

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/gml/3.2}TimePeriod"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "timePeriod"
    })
    public static class PhenomenonTime {

        @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml/3.2", required = true)
        private TimePeriodType timePeriod;

        public PhenomenonTime() {
        
        }
        
        public PhenomenonTime(final TimePeriodType timePeriod) {
            this.timePeriod = timePeriod;
        }
        
        /**
         * Gets the value of the timePeriod property.
         * 
         * @return
         *     possible object is
         *     {@link TimePeriodType }
         *     
         */
        public TimePeriodType getTimePeriod() {
            return timePeriod;
        }

        /**
         * Sets the value of the timePeriod property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimePeriodType }
         *     
         */
        public void setTimePeriod(TimePeriodType value) {
            this.timePeriod = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/gml/3.2}TimePeriod"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "timePeriod"
    })
    public static class ResultTime {

        @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml/3.2", required = true)
        private TimePeriodType timePeriod;

        /**
         * Gets the value of the timePeriod property.
         * 
         * @return
         *     possible object is
         *     {@link TimePeriodType }
         *     
         */
        public TimePeriodType getTimePeriod() {
            return timePeriod;
        }

        /**
         * Sets the value of the timePeriod property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimePeriodType }
         *     
         */
        public void setTimePeriod(TimePeriodType value) {
            this.timePeriod = value;
        }

    }

}
