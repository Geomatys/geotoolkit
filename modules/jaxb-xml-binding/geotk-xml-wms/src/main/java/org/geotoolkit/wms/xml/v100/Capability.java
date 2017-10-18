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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request",
    "exception",
    "vendorSpecificCapabilities",
    "layer"
})
@XmlRootElement(name = "Capability")
public class Capability {

    @XmlElement(name = "Request", required = true)
    protected Request request;
    @XmlElement(name = "Exception")
    protected Exception exception;
    @XmlElement(name = "VendorSpecificCapabilities")
    protected VendorSpecificCapabilities vendorSpecificCapabilities;
    @XmlElement(name = "Layer")
    protected Layer layer;

    /**
     * Obtient la valeur de la propriété request.
     *
     * @return
     *     possible object is
     *     {@link Request }
     *
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Définit la valeur de la propriété request.
     *
     * @param value
     *     allowed object is
     *     {@link Request }
     *
     */
    public void setRequest(Request value) {
        this.request = value;
    }

    /**
     * Obtient la valeur de la propriété exception.
     *
     * @return
     *     possible object is
     *     {@link Exception }
     *
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Définit la valeur de la propriété exception.
     *
     * @param value
     *     allowed object is
     *     {@link Exception }
     *
     */
    public void setException(Exception value) {
        this.exception = value;
    }

    /**
     * Obtient la valeur de la propriété vendorSpecificCapabilities.
     *
     * @return
     *     possible object is
     *     {@link VendorSpecificCapabilities }
     *
     */
    public VendorSpecificCapabilities getVendorSpecificCapabilities() {
        return vendorSpecificCapabilities;
    }

    /**
     * Définit la valeur de la propriété vendorSpecificCapabilities.
     *
     * @param value
     *     allowed object is
     *     {@link VendorSpecificCapabilities }
     *
     */
    public void setVendorSpecificCapabilities(VendorSpecificCapabilities value) {
        this.vendorSpecificCapabilities = value;
    }

    /**
     * Obtient la valeur de la propriété layer.
     *
     * @return
     *     possible object is
     *     {@link Layer }
     *
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Définit la valeur de la propriété layer.
     *
     * @param value
     *     allowed object is
     *     {@link Layer }
     *
     */
    public void setLayer(Layer value) {
        this.layer = value;
    }

}
