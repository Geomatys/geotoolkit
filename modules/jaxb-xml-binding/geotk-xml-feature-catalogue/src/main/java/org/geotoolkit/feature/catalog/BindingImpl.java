
package org.geotoolkit.feature.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.Binding;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_Binding_Type")
public class BindingImpl implements Binding {

    private String description;
    
    /**
     * An empty constructor used by JAXB
     */
    public BindingImpl() {
        
    }
    
    /**
     * Clone a Binding
     */
    public BindingImpl(final Binding feature) {
        if (feature != null)
            this.description = feature.getDescription();
        
    }
    public String getDescription() {
        return description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "[Binding]: description: " + description;  
    }
    
     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BindingImpl) {
            final BindingImpl that = (BindingImpl) object;
            
            return Utilities.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

}
