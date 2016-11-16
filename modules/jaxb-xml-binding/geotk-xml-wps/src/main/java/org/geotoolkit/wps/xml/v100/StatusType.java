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
package org.geotoolkit.wps.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import org.geotoolkit.wps.xml.StatusInfo;


/**
 * Description of the status of process execution. 
 * 
 * <p>Java class for StatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ProcessAccepted" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessStarted" type="{http://www.opengis.net/wps/1.0.0}ProcessStartedType"/>
 *         &lt;element name="ProcessPaused" type="{http://www.opengis.net/wps/1.0.0}ProcessStartedType"/>
 *         &lt;element name="ProcessSucceeded" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessFailed" type="{http://www.opengis.net/wps/1.0.0}ProcessFailedType"/>
 *       &lt;/choice>
 *       &lt;attribute name="creationTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", propOrder = {
    "processAccepted",
    "processStarted",
    "processPaused",
    "processSucceeded",
    "processFailed"
})
public class StatusType implements StatusInfo {

    @XmlElement(name = "ProcessAccepted")
    protected String processAccepted;
    @XmlElement(name = "ProcessStarted")
    protected ProcessStartedType processStarted;
    @XmlElement(name = "ProcessPaused")
    protected ProcessStartedType processPaused;
    @XmlElement(name = "ProcessSucceeded")
    protected String processSucceeded;
    @XmlElement(name = "ProcessFailed")
    protected ProcessFailedType processFailed;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar creationTime;

    public StatusType() {
        
    }
    
    public StatusType(XMLGregorianCalendar creationTime, String processAccepted, String processSucceeded) {
        this.creationTime = creationTime;
        this.processAccepted = processAccepted;
        this.processSucceeded = processSucceeded;
    }
    
    public StatusType(XMLGregorianCalendar creationTime, ProcessStartedType processStarted, ProcessStartedType processPaused) {
        this.creationTime = creationTime;
        this.processStarted = processStarted;
        this.processPaused = processPaused;
    }
    
    public StatusType(XMLGregorianCalendar creationTime, ProcessFailedType processFailed) {
        this.creationTime = creationTime;
        this.processFailed = processFailed;
    }
    
    public StatusType(XMLGregorianCalendar creationTime, ExceptionReport processFailed) {
        this.creationTime = creationTime;
        if (processFailed != null) {
            this.processFailed = new ProcessFailedType(processFailed);
        }
    }
    
    /**
     * Gets the value of the processAccepted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessAccepted() {
        return processAccepted;
    }

    /**
     * Sets the value of the processAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessAccepted(final String value) {
        this.processAccepted = value;
    }

    /**
     * Gets the value of the processStarted property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessStartedType }
     *     
     */
    public ProcessStartedType getProcessStarted() {
        return processStarted;
    }

    /**
     * Sets the value of the processStarted property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessStartedType }
     *     
     */
    public void setProcessStarted(final ProcessStartedType value) {
        this.processStarted = value;
    }

    /**
     * Gets the value of the processPaused property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessStartedType }
     *     
     */
    public ProcessStartedType getProcessPaused() {
        return processPaused;
    }

    /**
     * Sets the value of the processPaused property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessStartedType }
     *     
     */
    public void setProcessPaused(final ProcessStartedType value) {
        this.processPaused = value;
    }

    /**
     * Gets the value of the processSucceeded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessSucceeded() {
        return processSucceeded;
    }

    /**
     * Sets the value of the processSucceeded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessSucceeded(final String value) {
        this.processSucceeded = value;
    }

    /**
     * Gets the value of the processFailed property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessFailedType }
     *     
     */
    public ProcessFailedType getProcessFailed() {
        return processFailed;
    }

    /**
     * Sets the value of the processFailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessFailedType }
     *     
     */
    public void setProcessFailed(final ProcessFailedType value) {
        this.processFailed = value;
    }

    /**
     * Gets the value of the creationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationTime(final XMLGregorianCalendar value) {
        this.creationTime = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (creationTime != null) {
            sb.append("creationTime:").append(creationTime).append('\n');
        }
        if (processAccepted != null) {
            sb.append("processAccepted:").append(processAccepted).append('\n');
        }
        if (processFailed != null) {
            sb.append("processFailed:").append(processFailed).append('\n');
        }
        if (processPaused != null) {
            sb.append("processPaused:").append(processPaused).append('\n');
        }
        if (processStarted != null) {
            sb.append("processStarted:").append(processStarted).append('\n');
        }
        if (processSucceeded != null) {
            sb.append("processSucceeded:").append(processSucceeded).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof StatusType) {
            final StatusType that = (StatusType) object;
            return Objects.equals(this.creationTime, that.creationTime) &&
                   Objects.equals(this.processAccepted, that.processAccepted) &&
                   Objects.equals(this.processFailed, that.processFailed) &&
                   Objects.equals(this.processPaused, that.processPaused) &&
                   Objects.equals(this.processStarted, that.processStarted) &&
                   Objects.equals(this.processSucceeded, that.processSucceeded);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.processAccepted);
        hash = 59 * hash + Objects.hashCode(this.processStarted);
        hash = 59 * hash + Objects.hashCode(this.processPaused);
        hash = 59 * hash + Objects.hashCode(this.processSucceeded);
        hash = 59 * hash + Objects.hashCode(this.processFailed);
        hash = 59 * hash + Objects.hashCode(this.creationTime);
        return hash;
    }
}
