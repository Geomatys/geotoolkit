


package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.PlatformSpecificServiceSpecification;
import org.opengis.service.Port;
import org.opengis.service.Service;


/**
 * <p>Java class for SV_Service_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_Service_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="specification" type="{http://www.isotc211.org/2005/srv}SV_PlatformSpecificServiceSpecification_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="theSV_Port" type="{http://www.isotc211.org/2005/srv}SV_Port_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(propOrder = {
    "specification",
    "theSVPort"
})
@XmlRootElement(name = "SV_Service")
public class ServiceImpl implements Service {

    private Collection<PlatformSpecificServiceSpecification> specification;
    private Collection<Port> theSVPort;

     /**
     * An empty constructor used by JAXB
     */
    public ServiceImpl() {
        
    }
    
    /**
     * Clone an interface
     */
    public ServiceImpl(final Service service) {
      this.specification = service.getSpecification();
      this.theSVPort     = service.getTheSVPort();
    }
    
    /**
     * Gets the value of the specification property.
     * 
    */
    @XmlElement(required = true)
    public Collection<PlatformSpecificServiceSpecification> getSpecification() {
        if (specification == null) {
            specification = new ArrayList<PlatformSpecificServiceSpecification>();
        }
        return this.specification;
    }
    
    public void setSpecification(final Collection<PlatformSpecificServiceSpecification> specification) {
         this.specification = specification;
    }
    
    public void setSpecification(final PlatformSpecificServiceSpecification specification) {
        if (this.specification == null) {
            this.specification = new ArrayList<PlatformSpecificServiceSpecification>();
        }
         this.specification.add(specification);
    }

    /**
     * Gets the value of the theSVPort property.
     * 
     */
    @XmlElement(name = "theSV_Port")
    public Collection<Port> getTheSVPort() {
        if (theSVPort == null) {
            theSVPort = new ArrayList<Port>();
        }
        return this.theSVPort;
    }
    
    public void setTheSVPort(final Collection<Port> theSVPort) {
         this.theSVPort = theSVPort;
    }
    
    public void setTheSVPort(final Port theSVPort) {
        if (this.theSVPort == null) {
            this.theSVPort = new ArrayList<Port>();
        }
        this.theSVPort.add(theSVPort);
     }
    

}
