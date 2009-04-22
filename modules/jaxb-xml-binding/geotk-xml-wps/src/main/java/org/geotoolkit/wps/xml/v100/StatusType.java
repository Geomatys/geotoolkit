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

package org.geotoolkit.wps.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusType", propOrder = {
    "processAccepted",
    "processStarted",
    "processPaused",
    "processSucceeded",
    "processFailed"
})
public class StatusType {

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
    public void setProcessAccepted(String value) {
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
    public void setProcessStarted(ProcessStartedType value) {
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
    public void setProcessPaused(ProcessStartedType value) {
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
    public void setProcessSucceeded(String value) {
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
    public void setProcessFailed(ProcessFailedType value) {
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
    public void setCreationTime(XMLGregorianCalendar value) {
        this.creationTime = value;
    }

}
