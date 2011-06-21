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
package org.geotoolkit.wms.xml.v111;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractRequest;


/**
 * <p>Java class for anonymous complex type.
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request",
    "exception",
    "vendorSpecificCapabilities",
    "userDefinedSymbolization",
    "layer"
})
@XmlRootElement(name = "Capability")
public class Capability extends AbstractCapability {

    @XmlElement(name = "Request", required = true)
    private Request request;
    @XmlElement(name = "Exception", required = true)
    private Exception exception;
    @XmlElement(name = "VendorSpecificCapabilities")
    private VendorSpecificCapabilities vendorSpecificCapabilities;
    @XmlElement(name = "UserDefinedSymbolization")
    private UserDefinedSymbolization userDefinedSymbolization;
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
    public Capability(final Request request, final Exception exception, final Layer layer, final UserDefinedSymbolization userDefinedSymbolization) {
        this.request   = request;
        this.exception = exception;
        this.layer     = layer;
        this.userDefinedSymbolization = userDefinedSymbolization;
    }
    /**
     * Gets the value of the request property.
     */
    @Override
    public Request getRequest() {
        return request;
    }

    public void setRequest(final AbstractRequest request) {
        if (request instanceof Request) {
            this.request = (Request)request;
        } else {
            throw new IllegalArgumentException("not good version of request. expected 1.1.1");
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

    public void setExceptionFormats(final List<String> formats) {
        if (formats != null) {
            this.exception = new Exception(formats.toArray(new String[formats.size()]));
        } else {
            this.exception = null;
        }
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
    public void setLayer(final AbstractLayer layer) {
        if (layer instanceof Layer) {
            this.layer = (Layer) layer;
        } else {
            throw new IllegalArgumentException("not good version of layer. expected 1.1.1");
        }
    }

    public VendorSpecificCapabilities getVendorSpecificCapabilities() {
        return vendorSpecificCapabilities;
    }
    
    public void setVendorSpecificCapabilities(VendorSpecificCapabilities vc) {
        this.vendorSpecificCapabilities = vc;
    }

    public UserDefinedSymbolization getUserDefinedSymbolization() {
        return userDefinedSymbolization;
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

            
            return Utilities.equals(this.exception, that.exception) &&
                   Utilities.equals(this.layer,     that.layer)     &&
                   Utilities.equals(this.request,   that.request)   &&
                   Utilities.equals(this.userDefinedSymbolization,   that.userDefinedSymbolization)   &&
                   Utilities.equals(this.vendorSpecificCapabilities,   that.vendorSpecificCapabilities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.request != null ? this.request.hashCode() : 0);
        hash = 41 * hash + (this.exception != null ? this.exception.hashCode() : 0);
        hash = 41 * hash + (this.vendorSpecificCapabilities != null ? this.vendorSpecificCapabilities.hashCode() : 0);
        hash = 41 * hash + (this.userDefinedSymbolization != null ? this.userDefinedSymbolization.hashCode() : 0);
        hash = 41 * hash + (this.layer != null ? this.layer.hashCode() : 0);
        return hash;
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
        if (vendorSpecificCapabilities != null) {
           s.append("vendorSpecificCapabilities:").append(vendorSpecificCapabilities).append('\n');
        }
        return s.toString();
    }
}
