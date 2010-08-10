/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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
package org.geotoolkit.ogc.xml.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Version;


/**
 * Reports an exception.
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ServiceException" type="{http://www.opengis.net/ogc}ServiceExceptionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.3.0"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @author Guilhem Legal
 * @author Martin Desruisseaux
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"serviceExceptions"})
@XmlRootElement(name = "ServiceExceptionReport")
public final class ServiceExceptionReport {
    /**
     * A list of details that explain the reason for the failure.
     */
    @XmlElement(name = "ServiceException")
    private final List<ServiceExceptionType> serviceExceptions = new ArrayList<ServiceExceptionType>(4);

    /**
     * The version of the web service in which the error occured.
     */
    @XmlAttribute
    private String version;

    /**
     * An empty constructor used by JAXB.
     */
    ServiceExceptionReport() {
    }

    /**
     * Build a new exception report.
     *
     * @param version The version of the web service in which the error occured.
     * @param details The reasons for the failure.
     */
    public ServiceExceptionReport(final Version version, final ServiceExceptionType... details) {
        for (final ServiceExceptionType element : details) {
            serviceExceptions.add(element);
        }
        this.version = (version != null) ? version.toString() : null;
    }

    /**
     * Return the details that explain the reason for the failure.
     */
    public List<ServiceExceptionType> getServiceExceptions() {
        return Collections.unmodifiableList(serviceExceptions);
    }

    /**
     * Return the version number
     */
    public String getVersion() {
        return version;
    }
}
