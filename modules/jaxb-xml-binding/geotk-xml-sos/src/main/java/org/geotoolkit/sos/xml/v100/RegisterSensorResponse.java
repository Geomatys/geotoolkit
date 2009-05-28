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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="AssignedSensorId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterSensorResponse", propOrder = {
    "assignedSensorId"
})
@XmlRootElement(name = "RegisterSensorResponse")
public class RegisterSensorResponse {

    @XmlElement(name = "AssignedSensorId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String assignedSensorId;

    /**
     * An empty constructor used by JAXB.
     */
    RegisterSensorResponse () {}
    
    /**
     * Build a new response with the specified sensor ID.
     *
     * @param assignedSensorId The id of the sensor whitch have been inserted previously. 
     */
     public RegisterSensorResponse (String assignedSensorId) {
         this.assignedSensorId = assignedSensorId;
     }
     
    /**
     * Gets the value of the assignedSensorId property.
     */
    public String getAssignedSensorId() {
        return assignedSensorId;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RegisterSensorResponse) {
            final RegisterSensorResponse that = (RegisterSensorResponse) object;
            return Utilities.equals(this.assignedSensorId, that.assignedSensorId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = assignedSensorId.hashCode();
        return hash;
    }
    
    @Override
    public String toString() {
        return "RegisterSensorResponse: assignedSensorId=" + assignedSensorId;
    }
}
