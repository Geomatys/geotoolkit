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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.swes.xml.v200.ExtensibleResponseType;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;


/**
 * <p>Java class for GetObservationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetObservationResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *       &lt;sequence>
 *         &lt;element name="observationData" maxOccurs="unbounded" minOccurs="0">
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
@XmlType(name = "GetObservationResponseType", propOrder = {
    "observationData"
})
@XmlRootElement(name="GetObservationResponse")
public class GetObservationResponseType extends ExtensibleResponseType implements ObservationCollection {

    private List<GetObservationResponseType.ObservationData> observationData;

    public GetObservationResponseType() {
        
    }
    
    public GetObservationResponseType(final List<OMObservationType> observations) {
        if (observations != null) {
            this.observationData = new ArrayList<ObservationData>();
            for (OMObservationType observation : observations) {
                this.observationData.add(new ObservationData(observation));
            }
        }
    }
    
    /**
     * Gets the value of the observationData property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GetObservationResponseType.ObservationData }
     * 
     */
    public List<GetObservationResponseType.ObservationData> getObservationData() {
        if (observationData == null) {
            observationData = new ArrayList<GetObservationResponseType.ObservationData>();
        }
        return this.observationData;
    }

    @Override
    public List<Observation> getMember() {
        final List<Observation> observations = new ArrayList<Observation>();
        if (observationData != null) {
            for (ObservationData data : observationData) {
                observations.add(data.omObservation);
            }
        } 
        return observations;
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
    public static class ObservationData {

        @XmlElement(name = "OM_Observation", namespace = "http://www.opengis.net/om/2.0", required = true)
        private OMObservationType omObservation;

        public ObservationData() {
            
        }
        
        public ObservationData(final OMObservationType omObservation) {
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
