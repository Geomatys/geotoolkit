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
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.csw.xml.RequestStatus;


/**
 *
 *             This element provides information about the status of the
 *             search request.
 *
 *             status    - status of the search
 *             timestamp - the date and time when the result set was modified
 *                         (ISO 8601 format: YYYY-MM-DDThh:mm:ss[+|-]hh:mm).
 *
 *
 * <p>Classe Java pour RequestStatusType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
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

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.csw.xml.v300");

    @XmlAttribute(name = "timestamp")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;

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
     * Obtient la valeur de la propriété timestamp.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    @Override
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Définit la valeur de la propriété timestamp.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    @Override
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
