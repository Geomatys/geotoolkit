


package org.geotoolkit.service;

import org.opengis.service.DCPList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.PlatformNeutralServiceSpecification;
import org.opengis.service.PlatformSpecificServiceSpecification;
import org.opengis.service.Service;


/**
 * <p>Java class for SV_PlatformSpecificServiceSpecification_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_PlatformSpecificServiceSpecification_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/srv}SV_PlatformNeutralServiceSpecification_Type">
 *       &lt;sequence>
 *         &lt;element name="DCP" type="{http://www.isotc211.org/2005/srv}DCPList_PropertyType"/>
 *         &lt;element name="implementation" type="{http://www.isotc211.org/2005/srv}SV_Service_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(name = "SV_PlatformSpecificServiceSpecification_Type", propOrder = {
    "DCP",
    "implementation"
})
@XmlRootElement(name="SV_PlatformSpecificServiceSpecification")
public class PlatformSpecificServiceSpecificationImpl extends PlatformNeutralServiceSpecificationImpl implements PlatformSpecificServiceSpecification {

   
    private DCPList dcp;
    private Collection<Service> implementation;

    /**
     * An empty constructor used by JAXB
     */
    public PlatformSpecificServiceSpecificationImpl() {
        
    }
    
    /**
     * Clone a PlatformSpecificServiceSpecification
     */
    public PlatformSpecificServiceSpecificationImpl(final PlatformSpecificServiceSpecification platform) {
        super((PlatformNeutralServiceSpecification)platform);
        this.implementation = platform.getImplementation();
        this.dcp            = platform.getDCP();
    }
    
    /**
     * Gets the value of the dcp property.
     */
    @XmlElement(name = "DCP", required = true)
    public DCPList getDCP() {
        return dcp;
    }

    /**
     * Sets the value of the dcp property.
     * 
     */
    public void setDCP(final DCPList value) {
        this.dcp = value;
    }

    /**
     * Gets the value of the implementation property.
     * 
     */
    @XmlElement(required = true)
    public Collection<Service> getImplementation() {
        if (implementation == null) {
            implementation = new ArrayList<Service>();
        }
        return this.implementation;
    }
    
    public void setImplementation(final Collection<Service> implementation) {
         this.implementation = implementation;
    }
    
    public void setImplementation(final Service implementation) {
        if (this.implementation == null) {
            this.implementation = new ArrayList<Service>();
        }
        this.implementation.add(implementation);
     }

}
