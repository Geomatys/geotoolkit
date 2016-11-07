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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.wps.xml.ExecuteResponse;
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
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}JobID" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}ExpirationDate" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}DataOutputType" maxOccurs="unbounded"/>
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
    "jobID",
    "expirationDate",
    "output"
})
@XmlRootElement(name = "Result")
public class Result implements ExecuteResponse, WPSResponse {

    @XmlElement(name = "JobID")
    protected String jobID;
    @XmlElement(name = "ExpirationDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expirationDate;
    @XmlElement(name = "Output", required = true)
    protected List<DataOutputType> output;

    public Result() {
        
    }
    
    public Result(List<DataOutputType> output, String jobID) {
        this.output = output;
        this.jobID = jobID;
    }
    /**
     * 
     * Include if required. A JobId is usually required for
     * a) asynchronous execution
     * b) the Dismiss operation extension, where the client is allowed to actively free server-side resources
     * 						
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
     * 
     * Identifier of the Process that was executed.
     * This Process identifier shall be as listed in the ProcessOfferings
     * section of the WPS Capabilities document. 
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
     * Gets the value of the output property.
     * 
     * @return Objects of the following type(s) are allowed in the list {@link DataOutputType }
     * 
     * 
     */
    public List<DataOutputType> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

    public void setOutput(List<DataOutputType> outputs) {
        this.output = outputs;
    }

    @Override
    public void setStatusLocation(String location) {
        // do nothing in this implementation
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (expirationDate != null) {
            sb.append("expirationDate:").append(expirationDate).append('\n');
        }
        if (jobID != null) {
            sb.append("jobID:").append(jobID).append('\n');
        }
        if (output != null) {
            sb.append("output:\n");
            for (DataOutputType out : output) {
                sb.append(out).append('\n');
            }
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
        if (object instanceof Result) {
            final Result that = (Result) object;
            return Objects.equals(this.expirationDate, that.expirationDate) &&
                   Objects.equals(this.jobID, that.jobID) &&
                   Objects.equals(this.output, that.output);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.jobID);
        hash = 73 * hash + Objects.hashCode(this.expirationDate);
        hash = 73 * hash + Objects.hashCode(this.output);
        return hash;
    }

}
