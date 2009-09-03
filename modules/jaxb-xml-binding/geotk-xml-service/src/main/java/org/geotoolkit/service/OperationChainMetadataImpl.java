


package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.OperationChainMetadata;
import org.opengis.service.OperationMetadata;


/**
 * <p>Java class for SV_OperationChainMetadata_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_OperationChainMetadata_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.isotc211.org/2005/srv}SV_OperationMetadata_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SV_OperationChainMetadata_Type", propOrder = {
    "name",
    "description",
    "operation"
})
public class OperationChainMetadataImpl implements OperationChainMetadata {

    @XmlElement(required = true)
    private String name;
    private String description;
    @XmlElement(required = true)
    private Collection<OperationMetadata> operation;

    public OperationChainMetadataImpl() {
        
    }
    
    public OperationChainMetadataImpl(String name, String description, List<OperationMetadata> operation) {
        this.name        = name;
        this.description = description;
        this.operation   = operation;
        
    }
    
    /**
     * Gets the value of the name property.
     * 
      */
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
    public Collection<OperationMetadata> getOperation() {
        if (operation == null) {
            operation = new ArrayList<OperationMetadata>();
        }
        return this.operation;
    }
    
    public void setOperation(Collection<OperationMetadata> operation) {
         this.operation = operation;
    }
    
    public void setOperation(OperationMetadata operation) {
        if (this.operation == null) {
            this.operation = new ArrayList<OperationMetadata>();
        }
        this.operation.add(operation);
    }

}
