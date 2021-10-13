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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractService;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.explore;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.searchLayerByName;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.updateLayerURL;
import org.geotoolkit.wms.xml.WMSResponse;


/**
 * <p>Root element of a getCapabilities Document version 1.1.1.
 *
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WMT_MS_Capabilities", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMT_MS_Capabilities")
public class WMT_MS_Capabilities implements AbstractWMSCapabilities, WMSResponse {

    @XmlElement(name = "Service", required = true)
    private Service service;
    @XmlElement(name = "Capability", required = true)
    private Capability capability;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;

    /**
     * An empty constructor used by JAXB.
     */
    WMT_MS_Capabilities() {
    }

    /**
     * Build a new WMSCapabilities object.
     */
    public WMT_MS_Capabilities(final String version, final String updateSequence) {
        this(null, null, version, updateSequence);
    }

    /**
     * Build a new WMSCapabilities object.
     */
    public WMT_MS_Capabilities(final Service service, final Capability capability,
            final String version, final String updateSequence) {
        this.capability     = capability;
        this.service        = service;
        this.updateSequence = updateSequence;
        this.version        = version;
    }


    /**
     * Gets the value of the service property.
     *
     */
    @Override
    public Service getService() {
        return service;
    }

    public void setService(final AbstractService service) {
        if (service instanceof Service) {
            this.service = (Service) service;
        } else {
            throw new IllegalArgumentException("not the good version object, expected 1.1.1");
        }
    }

    /**
     * Gets the value of the capability property.
     *
     */
    @Override
    public Capability getCapability() {
        return capability;
    }

    public void setCapability(final AbstractCapability capability) {
        if (capability instanceof Capability) {
            this.capability = (Capability) capability;
        } else {
            throw new IllegalArgumentException("not the good version object, expected 1.1.1");
        }
    }

    @Override
    public void updateURL(final String url) {
        if (capability != null) {
            if (capability.getRequest() != null) {
                capability.getRequest().updateURL(url);
            }
            final Layer mainLayer = capability.getLayer();
            if (mainLayer != null) {
                updateLayerURL(url, mainLayer);
            }
        }
    }

    /**
     * Gets the value of the version property.
     *
     */
    @Override
    public String getVersion() {
        if (version == null) {
            return "1.1.1";
        } else {
            return version;
        }
    }

    /**
     * Gets the value of the updateSequence property.
     *
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Get a specific layer from the capabilities document.
     *
     */
    @Override
    public AbstractLayer getLayerFromName(final String name) {
        final AbstractLayer[] stack = getLayerStackFromName(name);
        if(stack != null){
            return stack[stack.length-1];
        }
        return null;
    }

    @Override
    public AbstractLayer[] getLayerStackFromName(final String name) {
        final List<AbstractLayer> stack = new ArrayList<AbstractLayer>();

        if(searchLayerByName(stack, getCapability().getLayer(), name)){
            return stack.toArray(new AbstractLayer[stack.size()]);
        }

        return null;
    }

    /**
     * List all layers recursivly.
     */
    @Override
    public List<AbstractLayer> getLayers() {
        final AbstractLayer layer = getCapability().getLayer();
        final List<AbstractLayer> layers = new ArrayList<AbstractLayer>();
        explore(layers, layer);
        return layers;
    }

    @Override
    public AbstractCapabilitiesCore applySections(Sections sections) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
