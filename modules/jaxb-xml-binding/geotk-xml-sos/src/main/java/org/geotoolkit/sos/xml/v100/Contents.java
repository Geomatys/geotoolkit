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
package org.geotoolkit.sos.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="ObservationOfferingList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ObservationOffering" type="{http://www.opengis.net/sos/1.0}ObservationOfferingType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "observationOfferingList"
})
@XmlRootElement(name = "Contents")
public class Contents {

    @XmlElement(name = "ObservationOfferingList", required = true)
    private Contents.ObservationOfferingList observationOfferingList;

    /**
     * Empty constructor used by JAXB
     */
    Contents() {
        
    }
    
    /**
     * Build a new Contents
     */
    public Contents(ObservationOfferingList observationOfferingList) {
        this.observationOfferingList = observationOfferingList;
    }
    
    /**
     * Return the value of the observationOfferingList property.
     */
    public ObservationOfferingList getObservationOfferingList() {
        return observationOfferingList;
    }

    /**
     * Verify if this entry is identical to�the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Contents) {
            final Contents that = (Contents) object;
            return Utilities.equals(this.observationOfferingList, that.observationOfferingList);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.observationOfferingList != null ? this.observationOfferingList.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class: Contents :").append(observationOfferingList.toString());
        return s.toString();
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
     *         &lt;element name="ObservationOffering" type="{http://www.opengis.net/sos/1.0}ObservationOfferingType" maxOccurs="unbounded"/>
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
        "observationOffering"
    })
    public static class ObservationOfferingList {

        @XmlElement(name = "ObservationOffering", required = true)
        private List<ObservationOfferingEntry> observationOffering;

        /**
         * Empty constructor used by JAXB
         */
        ObservationOfferingList(){
            
        }
        
        /**
         * Build a new Observation offering list.
         */
        public ObservationOfferingList(List<ObservationOfferingEntry> observationOffering){
            this.observationOffering = observationOffering;
        }
        
        /**
         * Return the list of observation Offering.
         */
        public List<ObservationOfferingEntry> getObservationOffering() {
            if (observationOffering == null){
                observationOffering = new ArrayList<ObservationOfferingEntry>();
            }
            return Collections.unmodifiableList(observationOffering);
        }
        
         /**
          * Verify if this entry is identical to the specified object.
          */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof ObservationOfferingList) {
                final ObservationOfferingList that = (ObservationOfferingList) object;
                return Utilities.equals(this.observationOffering, that.observationOffering);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.observationOffering != null ? this.observationOffering.hashCode() : 0);
            return hash;
        }
        
        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (ObservationOfferingEntry o:observationOffering) {
                s.append(o.toString()).append('\n');
            }
            return s.toString();
        }

    }
}
