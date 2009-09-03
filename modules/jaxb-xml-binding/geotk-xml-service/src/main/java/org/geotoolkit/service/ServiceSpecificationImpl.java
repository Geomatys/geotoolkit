


package org.geotoolkit.service;

import org.opengis.service.OperationModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Interface;
import org.opengis.service.PlatformNeutralServiceSpecification;
import org.opengis.service.ServiceSpecification;


/**
 * <p>Java class for SV_ServiceSpecification_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_ServiceSpecification_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="opModel" type="{http://www.isotc211.org/2005/srv}SV_OperationModel_PropertyType"/>
 *         &lt;element name="typeSpec" type="{http://www.isotc211.org/2005/srv}SV_PlatformNeutralServiceSpecification_PropertyType"/>
 *         &lt;element name="theSV_Interface" type="{http://www.isotc211.org/2005/srv}SV_Interface_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlType(name = "SV_ServiceSpecification_Type", propOrder = {
    "name",
    "opModel",
    "typeSpec",
    "theSVInterface"
})
@XmlSeeAlso({
    PlatformNeutralServiceSpecificationImpl.class
})
public class ServiceSpecificationImpl implements ServiceSpecification {

    /**
     * The name of the service
     */
    private String name;
    private OperationModel opModel;
    private PlatformNeutralServiceSpecification typeSpec;
    private Collection<Interface> theSVInterface;

    public ServiceSpecificationImpl() {
        
    }
    
    /**
     * Gets the name of the service.
    */
    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the service.
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the opModel property.
     */
    @XmlElement(required = true)
    public OperationModel getOpModel() {
        return opModel;
    }

    /**
     * Sets the value of the opModel property.
     */
    public void setOpModel(OperationModel value) {
        this.opModel = value;
    }

    /**
     * Gets the value of the typeSpec property.
     */
    @XmlElement(required = true)
    public PlatformNeutralServiceSpecification getTypeSpec() {
        return typeSpec;
    }

    /**
     * Sets the value of the typeSpec property.
     */
    public void setTypeSpec(PlatformNeutralServiceSpecification value) {
        this.typeSpec = value;
    }

    /**
     * Gets the value of the theSVInterface property.
     */
    @XmlElement(name = "theSV_Interface", required = true)
    public Collection<Interface> getTheSVInterface() {
        if (theSVInterface == null) {
            theSVInterface = new ArrayList<Interface>();
        }
        return this.theSVInterface;
    }
    
    public void setTheSVInterface(Collection<Interface> theSVInterface) {
         this.theSVInterface = theSVInterface;
    }
    
    public void setTheSVInterface(Interface theSVInterface) {
        if (this.theSVInterface == null) {
            this.theSVInterface = new ArrayList<Interface>();
        }
        this.theSVInterface.add(theSVInterface);
    }

}
