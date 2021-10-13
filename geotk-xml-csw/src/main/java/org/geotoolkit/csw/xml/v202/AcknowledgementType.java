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
import java.util.Objects;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.Acknowledgement;
import org.apache.sis.util.logging.Logging;


/**
 * This is a general acknowledgement response message for all requests that may be processed in an asynchronous manner.
 *
 * EchoedRequest - Echoes the submitted request message
 * RequestId     - identifier for polling purposes (if no response handler is available, or the URL scheme is unsupported)
 *
 * <p>Java class for AcknowledgementType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AcknowledgementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EchoedRequest" type="{http://www.opengis.net/cat/csw/2.0.2}EchoedRequestType"/>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="timeStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcknowledgementType", propOrder = {
    "echoedRequest",
    "requestId"
})
@XmlRootElement( name ="Acknowledgement" )
public class AcknowledgementType implements Acknowledgement {

    @XmlElement(name = "EchoedRequest", required = true)
    private EchoedRequestType echoedRequest;
    @XmlElement(name = "RequestId")
    @XmlSchemaType(name = "anyURI")
    private String requestId;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar timeStamp;

    /**
     * An empty constructor used by JAXB
     */
    public AcknowledgementType() {

    }

    /**
     * Build a new Anknowledgement message.
     */
    public AcknowledgementType(final String requestId, final EchoedRequestType echoedRequest, final Long timeStamp) {
        this.requestId     = requestId;
        this.echoedRequest = echoedRequest;
        if (timeStamp != null) {
            Date d = new Date(timeStamp);
            GregorianCalendar cal = new  GregorianCalendar();
            cal.setTime(d);
            try {
                DatatypeFactory factory = DatatypeFactory.newInstance();
                this.timeStamp = factory.newXMLGregorianCalendar(cal);
            } catch (DatatypeConfigurationException ex) {
                Logging.getLogger("org.geotoolkit.csw.xml.v202").log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Gets the value of the echoedRequest property.
     */
    public EchoedRequestType getEchoedRequest() {
        return echoedRequest;
    }

    /**
     * Gets the value of the requestId property.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the value of the timeStamp property.
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AcknowledgementType) {
            final AcknowledgementType that = (AcknowledgementType) object;
            return Objects.equals(this.echoedRequest, that.echoedRequest) &&
                   Objects.equals(this.requestId,     that.requestId)     &&
                   Objects.equals(this.timeStamp,     that.timeStamp);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.echoedRequest != null ? this.echoedRequest.hashCode() : 0);
        hash = 97 * hash + (this.requestId != null ? this.requestId.hashCode() : 0);
        hash = 97 * hash + (this.timeStamp != null ? this.timeStamp.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[AcknowledgementType]\n");

        if (echoedRequest != null) {
            s.append("echoedRequest: ").append(echoedRequest).append('\n');
        }
        if (requestId != null) {
            s.append("requestId: ").append(requestId).append('\n');
        }
        if (timeStamp != null) {
            s.append("timeStamp: ").append(timeStamp).append('\n');
        }
        return s.toString();
    }
}
