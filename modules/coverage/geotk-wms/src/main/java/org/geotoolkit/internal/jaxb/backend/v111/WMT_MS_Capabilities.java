package org.geotoolkit.internal.jaxb.backend.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractLayer;
import org.geotoolkit.internal.jaxb.backend.AbstractService;
import org.geotoolkit.internal.jaxb.backend.AbstractWMSCapabilities;


/**
 * <p>Root element of a getCapabilities Document version 1.1.1.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "capability"
})
@XmlRootElement(name = "WMT_MS_Capabilities")
public class WMT_MS_Capabilities extends AbstractWMSCapabilities {

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
    public Service getService() {
        return service;
    }
    
    public void setService(AbstractService service) {
        if (service instanceof Service)
            this.service = (Service) service;
        else
            throw new IllegalArgumentException("not the good version object, expected 1.1.1"); 
    }

    /**
     * Gets the value of the capability property.
     * 
     */
    public Capability getCapability() {
        return capability;
    }

    /**
     * Gets the value of the version property.
     * 
     */
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
    public String getUpdateSequence() {
        return updateSequence;
    }
    
    /**
     * Get a specific layer from the capabilities document.
     * 
     */
    public AbstractLayer getLayerFromName(String name) {
        for( Layer layer : getCapability().getLayer().getLayer()){
            if(layer.getName().equals(name)){
                return (AbstractLayer) layer; 
            }
        }        
        return null;
    }
   
    
}