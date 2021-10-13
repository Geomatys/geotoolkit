/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.WPSResponse;


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
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}JobID"/>
 *         &lt;element name="Status">
 *           &lt;simpleType>
 *             &lt;union>
 *               &lt;simpleType>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                   &lt;enumeration value="Succeeded"/>
 *                   &lt;enumeration value="Failed"/>
 *                   &lt;enumeration value="Accepted"/>
 *                   &lt;enumeration value="Running"/>
 *                 &lt;/restriction>
 *               &lt;/simpleType>
 *               &lt;simpleType>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                 &lt;/restriction>
 *               &lt;/simpleType>
 *             &lt;/union>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}ExpirationDate" minOccurs="0"/>
 *         &lt;element name="EstimatedCompletion" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="NextPoll" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="PercentCompleted" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "", propOrder = {
    "jobID",
    "status",
    "expirationDate",
    "creationTime",
    "estimatedCompletion",
    "nextPoll",
    "percentCompleted"
  //  "message"
})
@XmlRootElement(name = "StatusInfo")
public class StatusInfo extends DocumentBase implements WPSResponse{

    @XmlElement(name = "JobID", required = true)
    protected String jobID;
    @XmlElement(name = "Status", required = true)
    protected Status status;
    @XmlElement(name = "ExpirationDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expirationDate;
    @XmlElement(name = "EstimatedCompletion")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar estimatedCompletion;
    @XmlElement(name = "NextPoll")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar nextPoll;
    @XmlElement(name = "PercentCompleted")
    protected Integer percentCompleted;

    /**
     * NON-STANDARD FIELD: added for compatibility with WPS-RESTfull bindings.
     * Also, it's provide a way to get more detailed information about process
     * execution.
     */
    //@XmlElement(name="message", required = false)
    protected String message;

    public StatusInfo() {

    }

    public StatusInfo(Status status, String jobId) {
        this.status = status;
        this.jobID = jobId;
    }

    public StatusInfo(Status status, Integer percentCompleted, String jobId) {
        this.status = status;
        this.percentCompleted = percentCompleted;
        this.jobID = jobId;
    }

    /**
     * Gets the value of the jobID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJobID(String value) {
        this.jobID = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(Status value) {
        this.status = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Gets the value of the expirationDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the value of the expirationDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setExpirationDate(XMLGregorianCalendar value) {
        this.expirationDate = value;
    }

    /**
     * Gets the value of the estimatedCompletion property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEstimatedCompletion() {
        return estimatedCompletion;
    }

    /**
     * Sets the value of the estimatedCompletion property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setEstimatedCompletion(XMLGregorianCalendar value) {
        this.estimatedCompletion = value;
    }

    /**
     * Gets the value of the nextPoll property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getNextPoll() {
        return nextPoll;
    }

    /**
     * Sets the value of the nextPoll property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setNextPoll(XMLGregorianCalendar value) {
        this.nextPoll = value;
    }

    /**
     * Gets the value of the percentCompleted property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getPercentCompleted() {
        return percentCompleted;
    }

    /**
     * Sets the value of the percentCompleted property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setPercentCompleted(Integer value) {
        this.percentCompleted = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (jobID != null) {
            sb.append("jobID:").append(jobID).append('\n');
        }
        if (estimatedCompletion != null) {
            sb.append("estimatedCompletion:").append(estimatedCompletion).append('\n');
        }
        if (expirationDate != null) {
            sb.append("expirationDate:").append(expirationDate).append('\n');
        }
        if (nextPoll != null) {
            sb.append("nextPoll:").append(nextPoll).append('\n');
        }
        if (percentCompleted != null) {
            sb.append("percentCompleted:").append(percentCompleted).append('\n');
        }
        if (status != null) {
            sb.append("status:").append(status).append('\n');
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
        if (object instanceof StatusInfo) {
            final StatusInfo that = (StatusInfo) object;
            return Objects.equals(this.estimatedCompletion, that.estimatedCompletion) &&
                   Objects.equals(this.expirationDate, that.expirationDate) &&
                   Objects.equals(this.jobID, that.jobID) &&
                   Objects.equals(this.nextPoll, that.nextPoll) &&
                   Objects.equals(this.percentCompleted, that.percentCompleted) &&
                   Objects.equals(this.creationTime, that.creationTime) &&
                   Objects.equals(this.status, that.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.jobID);
        hash = 79 * hash + Objects.hashCode(this.status);
        hash = 79 * hash + Objects.hashCode(this.expirationDate);
        hash = 79 * hash + Objects.hashCode(this.estimatedCompletion);
        hash = 79 * hash + Objects.hashCode(this.nextPoll);
        hash = 79 * hash + Objects.hashCode(this.percentCompleted);
        hash = 79 * hash + Objects.hashCode(this.creationTime);
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////
    private XMLGregorianCalendar creationTime;

    @Deprecated
    public StatusInfo(Status status, XMLGregorianCalendar creationTime, Integer percentCompleted, String msg, String jobId) {
        this.status = status;
        this.percentCompleted = percentCompleted;
        this.jobID = jobId;
        this.creationTime = creationTime;
        this.message = msg;
    }

    @Deprecated
    public StatusInfo(Status status, XMLGregorianCalendar creationTime, String msg, String jobId) {
        this.status = status;
        this.jobID = jobId;
        this.creationTime = creationTime;
        this.message = msg;
    }

    /**
     *
     * @deprecated WPS 1 retro-compatibility purpose. Avoid if possible.
     */
    @XmlAttribute(name = "creationTime", namespace = WPSMarshallerPool.OWS_2_0_NAMESPACE, required = true)
    @XmlJavaTypeAdapter(FilterV1.XMLGregorianCalendar.class)
    @Deprecated
    public XMLGregorianCalendar getCreationTime() {
        return creationTime;
    }

    public void setTitle(XMLGregorianCalendar creationTime) {
        this.creationTime = creationTime;
    }

}
