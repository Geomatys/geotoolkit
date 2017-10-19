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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractRequest;


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
public class Capability implements AbstractCapability {

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
    @Override
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
    @Override
    public void setRequest(AbstractRequest value) {
        if (value instanceof Request) {
            this.request = (Request) value;
        } else throw new UnsupportedOperationException();
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
    @Override
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
    @Override
    public void setLayer(AbstractLayer value) {
        if (value instanceof AbstractLayer) {
        this.layer = (Layer) value;
        } else throw new UnsupportedOperationException();
    }

    /**
     * Gets the list of exception formats available.
     * @return
     */
    @Override
    public List<String> getExceptionFormats() {
        if (exception == null) {
            exception = new Exception();
        }
        return exception.getFormat().formats();
    }

    @Override
    public void setExceptionFormats(final List<String> formats) {
        if (formats != null) {
            this.exception = new Exception();
            final Format format = new Format();
            format.formats().addAll(formats);
            exception.setFormat(format);
        } else {
            this.exception = null;
        }
    }
}
