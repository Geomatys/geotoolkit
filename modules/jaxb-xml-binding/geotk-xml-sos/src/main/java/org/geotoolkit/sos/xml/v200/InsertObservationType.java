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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.swes.xml.v200.ExtensibleRequestType;


/**
 * <p>Java class for InsertObservationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="observation" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/om/2.0}OM_Observation"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertObservationType", propOrder = {
    "offering",
    "observation"
})
public class InsertObservationType extends ExtensibleRequestType implements org.geotoolkit.sos.xml.InsertObservation {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> offering;
    @XmlElement(required = true)
    private List<InsertObservationType.Observation> observation;

    public InsertObservationType() {
        
    }
    
    public InsertObservationType(final String version, final List<String> offering, final List<OMObservationType> observations) {
        super(version, "SOS");
        this.observation = new ArrayList<Observation>();
        this.offering = offering;
        for (OMObservationType om : observations) {
            this.observation.add(new Observation(om));
        }
    }
    
    /**
     * Gets the value of the offering property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getOffering() {
        if (offering == null) {
            offering = new ArrayList<String>();
        }
        return this.offering;
    }

    /**
     * Gets the value of the observation property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link InsertObservationType.Observation }
     * 
     */
    public List<InsertObservationType.Observation> getObservation() {
        if (observation == null) {
            observation = new ArrayList<InsertObservationType.Observation>();
        }
        return this.observation;
    }

    @Override
    public String getAssignedSensorId() {
        return null;
    }

    @Override
    public List<OMObservationType> getObservations() {
        final List<OMObservationType> result = new ArrayList<OMObservationType>();
        for (InsertObservationType.Observation obs : observation) {
            result.add(obs.omObservation);
        }
        return result;
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
     *         &lt;element ref="{http://www.opengis.net/om/2.0}OM_Observation"/>
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
        "omObservation"
    })
    public static class Observation {

        @XmlElement(name = "OM_Observation", namespace = "http://www.opengis.net/om/2.0", required = true)
        private OMObservationType omObservation;

        public Observation() {
            
        }
        
        public Observation(final OMObservationType omObservation) {
            this.omObservation = omObservation;
        }
        
        /**
         * Gets the value of the omObservation property.
         * 
         * @return
         *     possible object is
         *     {@link OMObservationType }
         *     
         */
        public OMObservationType getOMObservation() {
            return omObservation;
        }

        /**
         * Sets the value of the omObservation property.
         * 
         * @param value
         *     allowed object is
         *     {@link OMObservationType }
         *     
         */
        public void setOMObservation(OMObservationType value) {
            this.omObservation = value;
        }

    }

}
