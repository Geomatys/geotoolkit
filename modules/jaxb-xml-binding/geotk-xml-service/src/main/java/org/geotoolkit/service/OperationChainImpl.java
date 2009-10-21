


package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Operation;
import org.opengis.service.OperationChain;


/**
 * <p>Java class for SV_OperationChain_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_OperationChain_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.isotc211.org/2005/srv}SV_Operation_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(name = "SV_OperationChain_Type", propOrder = {
    "name",
    "description",
    "operation"
})
public class OperationChainImpl implements OperationChain {

    private String name;
    private String description;
    private Collection<Operation> operation;

    public OperationChainImpl() {
        
    }
    
    public OperationChainImpl(String name, String description, List<Operation> operation) {
        this.name        = name;
        this.description = description;
        this.operation   = operation;
    }
    /**
     * Gets the value of the name property.
     * 
     */
    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     */
    @XmlElement
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
    */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     */
    @XmlElement(required = true)
    public Collection<Operation> getOperation() {
        if (operation == null) {
            operation = new ArrayList<Operation>();
        }
        return this.operation;
    }
    
    public void setOperation(Collection<Operation> operation) {
         this.operation = operation;
    }
    
     public void setOperation(Operation operation) {
        if (this.operation == null) {
            this.operation = new ArrayList<Operation>();
        }
         this.operation.add(operation);
     }

}
