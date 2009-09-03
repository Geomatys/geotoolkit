

package org.geotoolkit.feature.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.ListedValue;


/**
 * Value for an enumerated feature attribute domain, including its codes and interpretation.
 * 
 * <p>Java class for FC_ListedValue_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_ListedValue_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="label" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl"/>
 *         &lt;element name="code" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl" minOccurs="0"/>
 *         &lt;element name="definition" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl" minOccurs="0"/>
 *         &lt;element name="definitionReference" type="{http://www.isotc211.org/2005/gfc}FC_DefinitionReference_Impl" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_ListedValue_Type", propOrder = {
    "label",
    "code",
    "definition",
    "definitionReference"
})
public class ListedValueImpl implements ListedValue {

    @XmlElement(required = true)
    private String label;
    private String code;
    private String definition;
    private DefinitionReference definitionReference;

    /**
     * An empty constructor used by JAXB
     */
    public ListedValueImpl() {
        
    }
    
    /**
     * Clone a Listed value
     */
    public ListedValueImpl(ListedValue feature) {
        if (feature != null) {
            this.definitionReference = feature.getDefinitionReference();
            this.code                = feature.getCode();
            this.definition          = feature.getDefinition();
            this.label               = feature.getLabel();
        }
    }
    
    /**
     * Clone a DefinitionReference
     */
    public ListedValueImpl(String code, String label, String definition, DefinitionReference definitionReference) {
       
        this.definitionReference = definitionReference;
        this.code                = code;
        this.definition          = definition;
        this.label               = label;
    }
    
    /**
     * Gets the value of the label property.
     * 
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the code property.
     * 
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the definition property.
     * 
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     */
    public void setDefinition(String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the definitionReference property.
     * 
     */
    public DefinitionReference getDefinitionReference() {
        return definitionReference;
    }

    /**
     * Sets the value of the definitionReference property.
     * 
     */
    public void setDefinitionReference(DefinitionReference value) {
        this.definitionReference = value;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[ListedValue]:").append('\n');
        if (label != null) {
            s.append("label: ").append(label).append('\n');
        }
        if (code!= null) {
            s.append("code: ").append(code).append('\n');
        }
        if (definition != null) {
            s.append("definition: ").append(definition).append('\n');
        }
        if (definitionReference != null) {
            s.append("definition reference: ").append('\n').append(definitionReference).append('\n');
        }
        return s.toString();
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ListedValueImpl) {
            final ListedValueImpl that = (ListedValueImpl) object;
            
           return Utilities.equals(this.code,                that.code)                &&
                   Utilities.equals(this.definition,          that.definition)          &&
                   Utilities.equals(this.definitionReference, that.definitionReference) &&
                   Utilities.equals(this.label,               that.label);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

}
