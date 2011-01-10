


package org.geotoolkit.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.PlatformNeutralServiceSpecification;
import org.opengis.service.PlatformSpecificServiceSpecification;
import org.opengis.service.ServiceType;


/**
 * <p>Java class for SV_PlatformNeutralServiceSpecification_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_PlatformNeutralServiceSpecification_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/srv}SV_ServiceSpecification_Type">
 *       &lt;sequence>
 *         &lt;element name="serviceType" type="{http://www.isotc211.org/2005/srv}SV_ServiceType_PropertyType"/>
 *         &lt;element name="implSpec" type="{http://www.isotc211.org/2005/srv}SV_PlatformSpecificServiceSpecification_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(name = "SV_PlatformNeutralServiceSpecification_Type", propOrder = {
    "serviceType",
    "implSpec"
})
@XmlSeeAlso({
    PlatformSpecificServiceSpecificationImpl.class
})
public class PlatformNeutralServiceSpecificationImpl extends ServiceSpecificationImpl implements PlatformNeutralServiceSpecification {

    private ServiceType serviceType;
    private PlatformSpecificServiceSpecification implSpec;

    /**
     * An empty constructor used by JAXB
     */
    public PlatformNeutralServiceSpecificationImpl() {
        
    }
    
    /**
     * Clone a PlatformSpecificServiceSpecification
     */
    public PlatformNeutralServiceSpecificationImpl(final PlatformNeutralServiceSpecification platform) {
        this.implSpec    = platform.getImplSpec();
        this.serviceType = platform.getServiceType();
    }
    
    /**
     * Gets the value of the serviceType property.
     */
    @XmlElement(required = true)
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     */
    public void setServiceType(final ServiceType value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the implSpec property.
     * 
     */
    @XmlElement(required = true)
    public PlatformSpecificServiceSpecification getImplSpec() {
        return implSpec;
    }

    /**
     * Sets the value of the implSpec property.
     * 
     */
    public void setImplSpec(final PlatformSpecificServiceSpecification value) {
        this.implSpec = value;
    }

}
