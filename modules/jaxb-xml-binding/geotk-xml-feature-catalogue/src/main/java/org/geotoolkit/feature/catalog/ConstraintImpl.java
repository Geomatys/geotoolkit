

package org.geotoolkit.feature.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.Constraint;


/**
 * A class for defining constraints for types.
 * 
 * <p>Java class for FC_Constraint_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_Constraint_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_Constraint_Type", propOrder = {
    "description"
})
@XmlRootElement(name = "FC_Constraint")
public class ConstraintImpl implements Constraint {

    @XmlElement(required = true)
    private String description;

    /**
     * An empty constructor used by JAXB
     */
    public ConstraintImpl() {
        
    }
    
    /**
     * Clone a Constraint
     */
    public ConstraintImpl(final Constraint feature) {
        if (feature != null) {
            this.description = feature.getDescription();
        }
        
    }
    
    /**
     * Build a new Constraint with the specified description
     */
    public ConstraintImpl(final String description) {
        this.description = description;
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
    public void setDescription(final String value) {
        this.description = value;
    }
    
    @Override
    public String toString() {
        return "[Constraint]: description: " + description;  
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ConstraintImpl) {
            final ConstraintImpl that = (ConstraintImpl) object;
            
            return Utilities.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

}
