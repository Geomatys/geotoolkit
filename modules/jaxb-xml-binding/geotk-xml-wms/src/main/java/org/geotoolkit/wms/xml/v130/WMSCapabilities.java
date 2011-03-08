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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractCapability;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractService;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;


/**
 * <p>Root element of a getCapabilities Document version 1.3.0.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Service"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Capability"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.3.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WMS_Capabilities", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMS_Capabilities", namespace = "http://www.opengis.net/wms")
public class WMSCapabilities extends AbstractWMSCapabilities {

    @XmlElement(name = "Service", required = true)
    private Service service;
    @XmlElement(name = "Capability", required = true)
    private Capability capability;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;
    
    @XmlAttribute(namespace = "http://www.w3.org/2001/XMLSchema-instance")
    private String schemaLocation = "http://www.opengis.net/wms http://schemas.opengis.net/wms/1.3.0/capabilities_1_3_0.xsd";

    /**
     * An empty constructor used by JAXB.
     */
    WMSCapabilities() {
    }

    /**
     * Build a new WMSCapabilities object.
     */
    public WMSCapabilities(final Service service, final Capability capability, 
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
        if (service instanceof Service)
            this.service = (Service) service;
        else
            throw new IllegalArgumentException("not the good version object, expected 1.3.0"); 
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
            throw new IllegalArgumentException("not the good version object, expected 1.3.0");
        }
    }

    /**
     * Gets the value of the version property.
     * 
     */
    @Override
    public String getVersion() {
        if (version == null) {
            return "1.3.0";
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

    /**
     * @return true if it founds the layer
     */
    private static boolean searchLayerByName(final List<AbstractLayer> stack, final Layer candidate, final String name){
        if(candidate == null || name == null){
            return false;
        }

        //add current layer in the stack
        stack.add(candidate);

        if(name.equals(candidate.getName())){
            return true;
        }

        //search it's children
        final List<Layer> layers = candidate.getLayer();
        if(layers != null){
            for(Layer layer : layers){
                if(searchLayerByName(stack, layer, name)){
                    return true;
                }
            }
        }

        //we didn't find the searched layer in this layer, remove it from the stack
        stack.remove(stack.size()-1);
        return false;
    }

    @Override
    public AbstractLayer[] getLayerStackFromName(final String name) {
        final List<AbstractLayer> stack = new ArrayList<AbstractLayer>();

        if(searchLayerByName(stack, getCapability().getLayer(), name)){
            return stack.toArray(new AbstractLayer[stack.size()]);
        }

        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[WMSCapabilities]\n");
        if (service != null) {
            sb.append("service:").append(service).append("\n");
        }
        if (capability != null) {
            sb.append("capability:").append(capability).append("\n");
        }
        if (schemaLocation != null) {
            sb.append("schemaLocation:").append(schemaLocation).append("\n");
        }
        if (updateSequence != null) {
            sb.append("updateSequence:").append(updateSequence).append("\n");
        }
        if (version != null) {
            sb.append("version:").append(version).append("\n");
        }
        return sb.toString();
    }

}
