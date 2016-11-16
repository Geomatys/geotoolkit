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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v100.MeasurementType;
import org.geotoolkit.observation.xml.v100.ObservationType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="AssignedSensorId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element ref="{http://www.opengis.net/om/1.0}Observation"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertObservation", propOrder = {
    "assignedSensorId",
    "observation"
})
@XmlRootElement(name = "InsertObservation")
public class InsertObservation extends RequestBaseType implements org.geotoolkit.sos.xml.InsertObservation {

    @XmlElement(name = "AssignedSensorId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String assignedSensorId;
    @XmlElements({
       @XmlElement(name = "Measurement", namespace = "http://www.opengis.net/om/1.0", type = MeasurementType.class),
       @XmlElement(name = "Observation", namespace = "http://www.opengis.net/om/1.0", type = ObservationType.class)
       
    })
    private ObservationType observation;

    /**
     * An empty constructor used by jaxB
     */
    InsertObservation(){}
    
    /**
     * Build a new InsertObservation request
     * 
     * @param version the version of the SOS interfaces.
     * @param observation The observation to insert in the database.
     */
    public InsertObservation(final String version, final String assignedSensorId, final ObservationType observation) {
        super(version);
        this.assignedSensorId = assignedSensorId;
        this.observation = observation;
        
    }
    
    /**
     * Gets the value of the insertId property.
     * 
     */
    @Override
    public String getAssignedSensorId() {
        return assignedSensorId;
    }

    /**
     * The observation to be inserted to the SOS.
     * 
     */
    public ObservationType getObservation() {
        return observation;
    }

    @Override
    public List<ObservationType> getObservations() {
        return Arrays.asList(observation);
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InsertObservation && super.equals(object)) {
            final InsertObservation that = (InsertObservation) object;
            return Objects.equals(this.assignedSensorId, that.assignedSensorId) &&
                   Objects.equals(this.observation,      that.observation);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.assignedSensorId != null ? this.assignedSensorId.hashCode() : 0);
        hash = 31 * hash + (this.observation != null ? this.observation.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[Insert Observation]\n");
        s.append("assigned sensor id=").append(assignedSensorId).append('\n');
        if (observation != null) {
            s.append("observation:\n").append(observation).append('\n');
        }
        return s.toString();
    }

    @Override
    public List<String> getOffering() {
        return null;
    }

    @Override
    public String getResponseFormat() {
        return "text/xml";
    }
}
