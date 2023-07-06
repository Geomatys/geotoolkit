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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.ows.xml.v200.ExceptionReport;
import org.geotoolkit.wps.xml.v200.StatusInfo;


/**
 * Description of the status of process execution.
 *
 * <p>Java class for LegacyStatus complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LegacyStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ProcessAccepted" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessStarted" type="{http://www.opengis.net/wps/1.0.0}ProcessStarted"/>
 *         &lt;element name="ProcessPaused" type="{http://www.opengis.net/wps/1.0.0}ProcessStarted"/>
 *         &lt;element name="ProcessSucceeded" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessFailed" type="{http://www.opengis.net/wps/1.0.0}ProcessFailed"/>
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
public class LegacyStatus {

    @XmlElement(name = "ProcessAccepted")
    protected String processAccepted;
    @XmlElement(name = "ProcessStarted")
    protected ProcessStarted processStarted;
    @XmlElement(name = "ProcessPaused")
    protected ProcessStarted processPaused;
    @XmlElement(name = "ProcessSucceeded")
    protected String processSucceeded;
    @XmlElement(name = "ProcessFailed")
    protected ProcessFailed processFailed;
    @XmlAttribute(required = true)
    protected XMLGregorianCalendar creationTime;

    public LegacyStatus() {

    }

    public LegacyStatus(XMLGregorianCalendar creationTime, String processAccepted, String processSucceeded) {
        this.creationTime = creationTime;
        this.processAccepted = processAccepted;
        this.processSucceeded = processSucceeded;
    }

    public LegacyStatus(XMLGregorianCalendar creationTime, ProcessStarted processStarted, ProcessStarted processPaused) {
        this.creationTime = creationTime;
        this.processStarted = processStarted;
        this.processPaused = processPaused;
    }

    public LegacyStatus(XMLGregorianCalendar creationTime, ProcessFailed processFailed) {
        this.creationTime = creationTime;
        this.processFailed = processFailed;
    }

    public LegacyStatus(XMLGregorianCalendar creationTime, ExceptionReport processFailed) {
        this.creationTime = creationTime;
        if (processFailed != null) {
            this.processFailed = new ProcessFailed(processFailed);
        }
    }

    public LegacyStatus(StatusInfo status) {
        this.creationTime = status.getCreationTime();
        if (status.getStatus() != null) {
            switch (status.getStatus().name()) {
                case "Accepted":
                    this.processAccepted = status.getMessage();break;
                case "Running":
                    this.processStarted = new ProcessStarted(status.getMessage(), status.getPercentCompleted());break;
                case "Failed":
                    this.processFailed = new ProcessFailed(null);break; // impossible to get the exception report back
                case "Succeeded":
                    this.processSucceeded = status.getMessage();break;
                case "Dismissed":
                    this.processFailed = new ProcessFailed(null);break;
                case "Started":
                    this.processStarted = new ProcessStarted(status.getMessage(), status.getPercentCompleted());break;
                case "Paused":
                    this.processPaused = new ProcessStarted(status.getMessage(), status.getPercentCompleted());break;
            }
        }
    }

    public Integer getPercentCompleted() {
        if (processStarted != null) {
            return processStarted.getPercentCompleted();
        } else if (processPaused != null) {
            return processPaused.getPercentCompleted();
        }
        return 0;
    }

    public String getMessage() {
        if (processStarted != null) {
            return processStarted.getValue();
        } else if (processPaused != null) {
            return processPaused.getValue();
        } else if (processFailed != null) {
            return processFailed.getExceptionReport().toString();
        } else if (processAccepted != null) {
            return processAccepted;
        } else if (processSucceeded != null) {
            return processSucceeded;
        }
        return null;
    }

    public String getStatus() {
//        if (processStarted != null) {
//            return STATUS_RUNNING;
//        } else if (processPaused != null) {
//            return STATUS_PAUSED;
//        } else if (processFailed != null) {
//            return STATUS_FAILED;
//        } else if (processAccepted != null) {
//            return STATUS_ACCEPTED;
//        } else if (processSucceeded != null) {
//            return STATUS_SUCCEEDED;
//        }
        return null;
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
     *     {@link ProcessStarted }
     *
     */
    public ProcessStarted getProcessStarted() {
        return processStarted;
    }

    /**
     * Sets the value of the processStarted property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessStarted }
     *
     */
    public void setProcessStarted(final ProcessStarted value) {
        this.processStarted = value;
    }

    /**
     * Gets the value of the processPaused property.
     *
     * @return
     *     possible object is
     *     {@link ProcessStarted }
     *
     */
    public ProcessStarted getProcessPaused() {
        return processPaused;
    }

    /**
     * Sets the value of the processPaused property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessStarted }
     *
     */
    public void setProcessPaused(final ProcessStarted value) {
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
     *     {@link ProcessFailed }
     *
     */
    public ProcessFailed getProcessFailed() {
        return processFailed;
    }

    /**
     * Sets the value of the processFailed property.
     *
     * @param value
     *     allowed object is
     *     {@link ProcessFailed }
     *
     */
    public void setProcessFailed(final ProcessFailed value) {
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
        if (object instanceof LegacyStatus) {
            final LegacyStatus that = (LegacyStatus) object;
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
