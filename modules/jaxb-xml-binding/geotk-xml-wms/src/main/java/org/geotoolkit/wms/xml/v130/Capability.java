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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.inspire.xml.vs.ExtendedCapabilitiesType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractRequest;


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
 * @module pending
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
            this.extendedCapabilities = new ArrayList<JAXBElement<?>>();
            for (final JAXBElement<?> element : extendedCapabilities) {
                this.extendedCapabilities.add(element);
            }
        }
    }
    /**
     * Gets the value of the request property.
     */
    @Override
    public Request getRequest() {
        return request;
    }

    public void setRequest(AbstractRequest request) {
        if (request instanceof Request) {
            this.request = (Request)request;
        } else {
            throw new IllegalArgumentException("not good version of request. expected 1.3.0");
        }
    }

    /**
     * Gets the value of the exception property.
     */
    public Exception getException() {
        return exception;
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
        return exception.getFormat();
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setExceptionFormats(List<String> formats) {
        if (formats == null) {
            this.exception = new Exception(formats.toArray(new String[formats.size()]));
        } else {
            this.exception = null;
        }
    }

    /**
     * Gets the value of the extendedCapabilities property.
     */
    public List<JAXBElement<?>> getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Gets the value of the INSPIRE extendedCapabilities property.
     */
    public ExtendedCapabilitiesType getInspireExtendedCapabilities() {
        if (extendedCapabilities != null) {
            for (JAXBElement<?> jb : extendedCapabilities) {
                if (jb.getValue() instanceof ExtendedCapabilitiesType) {
                    return (ExtendedCapabilitiesType) jb.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Gets the value of the layer property.
     */
    @Override
    public Layer getLayer() {
        return layer;
    }
    
    /**
     * Gets the value of the layer property.
     */
    @Override
    public void setLayer(AbstractLayer layer) {
        if (layer instanceof Layer) {
            this.layer = (Layer) layer;
        } else {
            throw new IllegalArgumentException("not good version of layer. expected 1.3.0");
        }
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[Capability]\n");
        if (request != null) {
            s.append("request:").append(request).append('\n');
        }
        if (layer != null) {
            s.append("layer:").append(layer).append('\n');
        }
        if (exception != null) {
            s.append("exception:").append(exception).append('\n');
        }
        if (extendedCapabilities != null) {
            for (JAXBElement<?> ext: extendedCapabilities) {
                s.append("extension:").append(ext.getValue()).append('\n');
            }
            s.append('\n');
        }
        return s.toString();
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Capability) {
            final Capability that = (Capability) object;

            boolean ext = true;
            if (this.extendedCapabilities != null && that.extendedCapabilities != null &&
                this.extendedCapabilities.size() == that.extendedCapabilities.size()) {
                for (int i = 0; i < this.extendedCapabilities.size(); i++) {
                    if (!Utilities.equals(this.extendedCapabilities.get(i).getValue(), that.extendedCapabilities.get(i).getValue())) {
                        ext = false;
                        break;
                    }
                }

            } else if (this.extendedCapabilities == null && that.extendedCapabilities == null) {
                ext = true;
            } else  {
                ext = false;
            }
            return Utilities.equals(this.exception, that.exception) &&
                   Utilities.equals(this.layer,     that.layer)     &&
                   Utilities.equals(this.request,   that.request)   &&
                   ext;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.request != null ? this.request.hashCode() : 0);
        hash = 61 * hash + (this.exception != null ? this.exception.hashCode() : 0);
        hash = 61 * hash + (this.extendedCapabilities != null ? this.extendedCapabilities.hashCode() : 0);
        hash = 61 * hash + (this.layer != null ? this.layer.hashCode() : 0);
        return hash;
    }

}
