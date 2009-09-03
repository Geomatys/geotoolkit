

package org.geotoolkit.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.CoupledResource;
import org.opengis.util.ScopedName;


/**
 * <p>Java class for SV_CoupledResource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_CoupledResource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="operationName" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="identifier" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element ref="{http://www.isotc211.org/2005/gco}ScopedName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlType(name = "SV_CoupledResource_Type", propOrder = {
    "operationName",
    "identifier",
    "scopedName"
})
public class CoupledResourceImpl implements CoupledResource {

    private String operationName;
    private String identifier;
    private ScopedName scopedName;

    /**
     * An empty constructor used by JAXB
     */
    public CoupledResourceImpl() {
        
    }
    
    /**
     * Clone an coupled resource.
     */
    public CoupledResourceImpl(CoupledResource resource) {
        this.identifier    = resource.getIdentifier();
        this.operationName = resource.getOperationName();
        this.scopedName    = resource.getScopedName();
    }
    
    /**
     * Gets the value of the operationName property.
     * 
     */
    @XmlElement(name = "operationName", required = true)
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
    */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the identifier property.
     * 
    */
    @XmlElement(name = "identifier", required = true)
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the scopedName property.
     * 
    */
    @XmlElement(name = "scopedName", namespace = "http://www.isotc211.org/2005/gco")
    public ScopedName getScopedName() {
        return scopedName;
    }

    /**
     * Sets the value of the scopedName property.
     * 
    */
    public void setScopedName(ScopedName value) {
        this.scopedName = value;
    }

}
