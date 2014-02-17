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
package org.geotoolkit.csw.xml.v200;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.RequestStatus;
import org.apache.sis.util.logging.Logging;


/**
 * This element provides information about the status of the search request.
 *  
 * status    - status of the search
 * timestamp - The date and time when the result set was last updated (ISO 8601 format)
 *          
 * 
 * <p>Java class for RequestStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="status" use="required" type="{http://www.opengis.net/cat/csw}StatusType" />
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestStatusType")
public class RequestStatusType implements RequestStatus {

    private static final Logger LOGGER = Logging.getLogger(RequestStatusType.class);
    
    @XmlAttribute(required = true)
    private StatusType status;
    @XmlAttribute
    private XMLGregorianCalendar timestamp;

    private static final DatatypeFactory factory;
    static {
        DatatypeFactory candidate = null;
        try {
            candidate = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            LOGGER.severe("error at the dataType factory initialisation in request status");
        }
        factory = candidate;
    }
    
     /**
     * An empty constructor used by JAXB
     */
    RequestStatusType() {
        
    }
    
    /**
     * Build a new request statuc with the specified XML gregorian calendar.
     */
    public RequestStatusType(final XMLGregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Build a new request statuc with the specified .
     */
    public RequestStatusType(final long time) {
        final Date d = new Date(time);
        final GregorianCalendar cal = new  GregorianCalendar();
        cal.setTime(d);
        synchronized (factory) {
            this.timestamp = factory.newXMLGregorianCalendar(cal);
        }
    }
    /**
     * Gets the value of the status property.
     * 
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     */
    public void setStatus(final StatusType value) {
        this.status = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     */
    public void setTimestamp(final XMLGregorianCalendar value) {
        this.timestamp = value;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[requestStatusType]");
        if (status != null)
            s.append("status: ").append(status.value()).append('\n');
        
        if (timestamp != null)
            s.append("timeStamp: ").append(timestamp);
        return s.toString();
    }

}
