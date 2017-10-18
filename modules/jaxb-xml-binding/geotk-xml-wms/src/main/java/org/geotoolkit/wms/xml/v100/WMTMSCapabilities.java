/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMT_MS_Capabilities")
public class WMTMSCapabilities {

    @XmlAttribute(name = "version")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String version;
    @XmlAttribute(name = "updateSequence")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String updateSequence;
    @XmlElement(name = "Service", required = true)
    protected Service service;
    @XmlElement(name = "Capability", required = true)
    protected Capability capability;

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        if (version == null) {
            return "1.0.0";
        } else {
            return version;
        }
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété updateSequence.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUpdateSequence() {
        if (updateSequence == null) {
            return "0";
        } else {
            return updateSequence;
        }
    }

    /**
     * Définit la valeur de la propriété updateSequence.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUpdateSequence(String value) {
        this.updateSequence = value;
    }

    /**
     * Obtient la valeur de la propriété service.
     *
     * @return
     *     possible object is
     *     {@link Service }
     *
     */
    public Service getService() {
        return service;
    }

    /**
     * Définit la valeur de la propriété service.
     *
     * @param value
     *     allowed object is
     *     {@link Service }
     *
     */
    public void setService(Service value) {
        this.service = value;
    }

    /**
     * Obtient la valeur de la propriété capability.
     *
     * @return
     *     possible object is
     *     {@link Capability }
     *
     */
    public Capability getCapability() {
        return capability;
    }

    /**
     * Définit la valeur de la propriété capability.
     *
     * @param value
     *     allowed object is
     *     {@link Capability }
     *
     */
    public void setCapability(Capability value) {
        this.capability = value;
    }

}
