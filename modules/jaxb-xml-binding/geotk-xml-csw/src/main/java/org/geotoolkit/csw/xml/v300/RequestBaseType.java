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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.Version;
import org.geotoolkit.csw.xml.AbstractCswRequest;
import org.geotoolkit.ows.xml.RequestBase;


/**
 *
 *             Base type for all request messages except GetCapabilities.
 *             The attributes identify the relevant service type and version.
 *
 *
 * <p>Classe Java pour RequestBaseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RequestBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="service" type="{http://www.opengis.net/ows/2.0}ServiceType" default="CSW" />
 *       &lt;attribute name="version" type="{http://www.opengis.net/ows/2.0}VersionType" default="3.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBaseType")
@XmlSeeAlso({
    GetRecordsType.class,
    HarvestType.class,
    UnHarvestType.class,
    GetDomainType.class,
    TransactionType.class,
    GetRecordByIdType.class
})
public abstract class RequestBaseType implements RequestBase, AbstractCswRequest {

    @XmlAttribute(name = "service")
    protected String service;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * An empty constructor used by JAXB
     */
    RequestBaseType() {

    }

    /**
     * Super contructor used by thi child classes
     *
     * @param service the name of the service (fixed to "CSW")
     * @param version the version of the service
     */
    protected RequestBaseType(final String service, final String version) {
        this.service = service;
        this.version = version;
    }

    protected RequestBaseType(final RequestBaseType other) {
        if (other != null) {
            this.service = other.service;
            this.version = other.version;
        }
    }

    /**
     * Obtient la valeur de la propriété service.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getService() {
        if (service == null) {
            return "CSW";
        } else {
            return service;
        }
    }

    /**
     * Définit la valeur de la propriété service.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public Version getVersion() {
        if (version != null) {
            return new Version(version);
        }
        return null;
    }


    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setVersion(String value) {
        this.version = value;
    }

}
