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
package org.geotoolkit.wms.xml.v130;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;


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
 *         &lt;element ref="{http://www.opengis.net/wms}Request"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Exception"/>
 *         &lt;element ref="{http://www.opengis.net/wms}_ExtendedCapabilities" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Layer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request",
    "exception",
    "extendedCapabilities",
    "layer"
})
@XmlRootElement(name = "Capability")
public class Capability extends AbstractCapability {

    @XmlElement(name = "Request", required = true)
    private Request request;
    @XmlElement(name = "Exception", required = true)
    private Exception exception;
    @XmlElementRef(name = "_ExtendedCapabilities", namespace = "http://www.opengis.net/wms", type = JAXBElement.class)
    protected List<JAXBElement<?>> extendedCapabilities;
    @XmlElement(name = "Layer")
    private Layer layer;

     /**
     * An empty constructor used by JAXB.
     */
     Capability() {
     }

    /**
     * Build a new capability object.
     */
    public Capability(final Request request, final Exception exception, final Layer layer,
            final JAXBElement<?>... extendedCapabilities) {
        this.request   = request;
        this.exception = exception;
        this.layer     = layer;
        if (extendedCapabilities != null && extendedCapabilities.length != 0) {
            for (final JAXBElement<?> element : extendedCapabilities) {
                this.extendedCapabilities.add(element);
            }
        }
    }
    /**
     * Gets the value of the request property.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Gets the value of the exception property.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Gets the value of the extendedCapabilities property.
     */
    public List<JAXBElement<?>> getExtendedCapabilities() {
        return Collections.unmodifiableList(extendedCapabilities);
    }

    /**
     * Gets the value of the layer property.
     */
    public Layer getLayer() {
        return layer;
    }
    
    /**
     * Gets the value of the layer property.
     */
    public void setLayer(AbstractLayer layer) {
        if (layer instanceof Layer) {
            this.layer = (Layer) layer;
        } else {
            throw new IllegalArgumentException("not good version of layer. expected 1.3.0");
        }
    }


}
