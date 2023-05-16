/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.Acknowledgement;


/**
 *
 *             This is a general acknowledgement response message for all requests
 *             that may be processed in an asynchronous manner.
 *             EchoedRequest - Echoes the submitted request message
 *             RequestId     - identifier for polling purposes (if no response
 *                             handler is available, or the URL scheme is
 *                             unsupported)
 *
 *
 * <p>Classe Java pour AcknowledgementType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AcknowledgementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EchoedRequest" type="{http://www.opengis.net/cat/csw/3.0}EchoedRequestType"/>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="timeStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcknowledgementType", propOrder = {
    "echoedRequest",
    "requestId"
})
public class AcknowledgementType implements Acknowledgement {

    @XmlElement(name = "EchoedRequest", required = true)
    protected EchoedRequestType echoedRequest;
    @XmlElement(name = "RequestId")
    @XmlSchemaType(name = "anyURI")
    protected String requestId;
    @XmlAttribute(name = "timeStamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;

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
                Logger.getLogger("org.geotoolkit.csw.xml.v300").log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Obtient la valeur de la propriété echoedRequest.
     */
    public EchoedRequestType getEchoedRequest() {
        return echoedRequest;
    }

    /**
     * Définit la valeur de la propriété echoedRequest.
     */
    public void setEchoedRequest(EchoedRequestType value) {
        this.echoedRequest = value;
    }

    /**
     * Obtient la valeur de la propriété requestId.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Définit la valeur de la propriété requestId.
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Obtient la valeur de la propriété timeStamp.
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Définit la valeur de la propriété timeStamp.
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }
}
