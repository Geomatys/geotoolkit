


package org.geotoolkit.feature.catalog;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.InheritanceRelation;


/**
 * FC_InheritanceRelation realizes GF_InheritanceRelation.  - [ocl] - FC_InheritanceRelation always assumes that its GF_InheritanceRelation::uniqueInstance is TRUE. - [/ocl]
 * 
 * <p>Java class for FC_InheritanceRelation_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_InheritanceRelation_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl"/>
 *         &lt;element name="uniqueInstance" type="{http://www.isotc211.org/2005/gco}Boolean_Impl"/>
 *         &lt;element name="subtype" type="{http://www.isotc211.org/2005/gfc}FC_FeatureType_Impl"/>
 *         &lt;element name="supertype" type="{http://www.isotc211.org/2005/gfc}FC_FeatureType_Impl"/>
 *         &lt;element name="featureCatalogue" type="{http://www.isotc211.org/2005/gfc}FC_FeatureCatalogue_Impl"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "description",
    "uniqueInstance",
    "subtype",
    "supertype"
})
@XmlRootElement(name = "FC_InheritanceRelation")        
public class InheritanceRelationImpl implements InheritanceRelation, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    
    private String name;
    @XmlElement(required = true)
    private String description;
    @XmlElement(required = true)
    private Boolean uniqueInstance;
    @XmlElement(required = true)
    private FeatureType subtype;
    @XmlElement(required = true)
    private FeatureType supertype;
    
    @XmlTransient
    private boolean isReference = false;
    @XmlTransient
    protected boolean rootElement = true;
    
    
    /**
     * An empty constructor used by JAXB
     */
    public InheritanceRelationImpl() {
        
    }
    
    /**
     * Clone a InheritanceRelation
     */
    public InheritanceRelationImpl(InheritanceRelation relation) {
        if (relation != null) {
            this.id             = relation.getId();
            this.description    = relation.getDescription();
            this.name           = relation.getName();
            this.subtype        = relation.getSubtype();
            this.supertype      = relation.getSupertype();
            this.uniqueInstance = relation.getUniqueInstance();
        }
    }
    
    /**
     * Clone a InheritanceRelation
     */
    public InheritanceRelationImpl(String name, String description ,Boolean uniqueInstance, FeatureType subtype, FeatureType supertype) {
        this.description    = description;
        this.name           = name;
        this.subtype        = subtype;
        this.supertype      = supertype;
        this.uniqueInstance = uniqueInstance;
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
     * Gets the value of the uniqueInstance property.
     * 
     */
    public Boolean getUniqueInstance() {
        return uniqueInstance;
    }

    /**
     * Sets the value of the uniqueInstance property.
     * 
     */
    public void setUniqueInstance(Boolean value) {
        this.uniqueInstance = value;
    }

    /**
     * Gets the value of the subtype property.
     */
    public FeatureType getSubtype() {
        return subtype;
    }

    /**
     * Sets the value of the subtype property.
     */
    public void setSubtype(FeatureType value) {
        this.subtype = value;
    }

    /**
     * Gets the value of the supertype property.
     * 
     */
    public FeatureType getSupertype() {
        return supertype;
    }

    /**
     * Sets the value of the supertype property.
     * 
     */
    public void setSupertype(FeatureType value) {
        this.supertype = value;
    }
    
    /**
     * Return the identifier of the relation
     */
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * set the feature in reference mode
     */
    public void setReference(boolean mode) {
        this.isReference = mode;
    }
    
     /**
     * get the current feature in reference mode
     */
    public boolean isReference() {
        return isReference;
    }
    
    public InheritanceRelationImpl getReference() {
        InheritanceRelationImpl result = new InheritanceRelationImpl(this);
        result.setReference(true);
        return result;
    }
    
    private void beforeMarshal(Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<String, Referenceable>());
        }
    }
    
     public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        alreadySee.put(id, this);
        rootElement = false;
        
        if (subtype != null) {
            if (alreadySee.get(subtype.getId()) != null) {
                subtype = ((FeatureTypeImpl)subtype).getReference();
            } else {
                alreadySee = ((FeatureTypeImpl)subtype).beforeMarshal(alreadySee);
            }
        }
        
        if (supertype != null) {
            if (alreadySee.get(supertype.getId()) != null) {
                supertype = ((FeatureTypeImpl)supertype).getReference();
            } else {
               alreadySee = ((FeatureTypeImpl)supertype).beforeMarshal(alreadySee);
            }
        }
        return alreadySee;
     }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[InheritanceRelation]:").append('\n');
        if (isReference) {
            s.append("referenceMode").append('\n');
        } else {
            if (name != null) {
                s.append("name: ").append(name).append('\n');
            }
            if (description!= null) {
                s.append("description: ").append(description).append('\n');
            }
            if (uniqueInstance != null) {
                s.append("unique instance: ").append(uniqueInstance).append('\n');
            }
            if (subtype!= null) {
                setReference(true);
                s.append("subType: ").append(subtype).append('\n');
                setReference(false);
            }
            if (supertype != null) {
                setReference(true);
                s.append("supertype: ").append(supertype).append('\n');
                setReference(false);
            }
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
        if (object instanceof InheritanceRelationImpl) {
            final InheritanceRelationImpl that = (InheritanceRelationImpl) object;
            
            return Utilities.equals(this.description,    that.description) &&
                   Utilities.equals(this.id,             that.id)          &&
                   Utilities.equals(this.name,           that.name)        &&
                   Utilities.equals(this.subtype,        that.subtype)     &&
                   Utilities.equals(this.supertype,      that.supertype)   &&
                   Utilities.equals(this.uniqueInstance, that.uniqueInstance);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id   != null ? this.id.hashCode()   : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
