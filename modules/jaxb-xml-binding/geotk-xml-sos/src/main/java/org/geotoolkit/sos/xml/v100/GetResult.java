/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sos.xml.v100;

import java.util.ArrayList;
import java.util.List;
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
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="ObservationTemplateId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="eventTime" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ogc}temporalOps"/>
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
@XmlType(name = "GetResult", propOrder = {
    "observationTemplateId",
    "eventTime"
})
@XmlRootElement(name = "GetResult")
public class GetResult extends RequestBaseType {

    @XmlElement(name = "ObservationTemplateId", required = true)
    @XmlSchemaType(name = "anyURI")
    private String observationTemplateId;
    private List<EventTime> eventTime;

    /**
     * An empty constructor used by jaxB
     */
     GetResult(){}
     
    /**
     * Build a new request GetResult.
     */
     public GetResult(String observationTemplateId, List<EventTime> eventTime){
        this.eventTime             = eventTime;
        this.observationTemplateId = observationTemplateId;
     }
     
    /**
     * Gets the value of the observationTemplateId property.
     * 
    */
    public String getObservationTemplateId() {
        return observationTemplateId;
    }

    /**
     * Gets the value of the eventTime property.
     * (unmodifable) 
     */
    public List<EventTime> getEventTime() {
        if (eventTime == null){
            eventTime = new ArrayList<EventTime>();
        }
        return eventTime;
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetResult && super.equals(object)) {
            final GetResult that = (GetResult) object;
            return Utilities.equals(this.eventTime, that.eventTime) &&
                   Utilities.equals(this.observationTemplateId,   that.observationTemplateId);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.observationTemplateId != null ? this.observationTemplateId.hashCode() : 0);
        hash = 37 * hash + (this.eventTime != null ? this.eventTime.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("GetResult:");
        s.append('\n').append("observation template id=").append(observationTemplateId).append('\n');
        for (EventTime et:eventTime) {
            s.append(et.toString()).append('\n');
        }
        return  s.toString();
    }
}
