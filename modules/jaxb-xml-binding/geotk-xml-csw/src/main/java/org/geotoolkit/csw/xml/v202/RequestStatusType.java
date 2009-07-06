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
package org.geotoolkit.csw.xml.v202;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.RequestStatus;
import org.geotoolkit.util.logging.Logging;


/**
 * 
 * This element provides information about the status of the search request.
 * 
 * status    - status of the search
 * timestamp - the date and time when the result set was modified 
 *             (ISO 8601 format: YYYY-MM-DDThh:mm:ss[+|-]hh:mm).
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
 *       &lt;attribute name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestStatusType")
public class RequestStatusType implements RequestStatus {

    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar timestamp;
    
    @XmlTransient
    private Logger logger = Logging.getLogger(RequestStatusType.class);

    /**
     * An empty constructor used by JAXB
     */
    RequestStatusType() {
        
    }
    
    /**
     * Build a new request statuc with the specified XML gregorian calendar.
     */
    public RequestStatusType(XMLGregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Build a new request statuc with the specified .
     */
    public RequestStatusType(long time) {
        Date d = new Date(time);
        GregorianCalendar cal = new  GregorianCalendar();
        cal.setTime(d);
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            this.timestamp = factory.newXMLGregorianCalendar(cal);
        } catch(DatatypeConfigurationException ex) {
            logger.severe("error at the timestamp initialisation in request status");
        }
    }
    
    /**
     * Gets the value of the timestamp property.
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp property with a XMLGregorianCalendar value.
     * @param value
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[requestStatusType]");
        if (timestamp != null)
            s.append("timeStamp:").append(timestamp);
        return s.toString();
    }

}
