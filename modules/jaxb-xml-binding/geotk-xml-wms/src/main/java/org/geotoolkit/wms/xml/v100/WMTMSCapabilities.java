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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.explore;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.searchLayerByName;
import static org.geotoolkit.wms.xml.WMSBindingUtilities.updateLayerURL;
import org.geotoolkit.wms.xml.WMSResponse;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMT_MS_Capabilities")
public class WMTMSCapabilities implements AbstractWMSCapabilities, WMSResponse {

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
    @Override
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

    @Override
    public AbstractCapabilitiesCore applySections(Sections sections) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateURL(String url) {
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
}
