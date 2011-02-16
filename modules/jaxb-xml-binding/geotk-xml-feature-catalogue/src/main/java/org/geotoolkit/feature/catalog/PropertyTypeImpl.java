


package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.gco.GO_GenericName;
import org.geotoolkit.util.Utilities;
import org.opengis.util.LocalName;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.PropertyType;
import org.geotoolkit.util.Multiplicity;

/**
 * Abstract class for feature properties.
 * 
 * <p>Java class for AbstractFC_PropertyType_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractFC_PropertyType_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="memberName" type="{http://www.isotc211.org/2005/gco}LocalName_PropertyType"/>
 *         &lt;element name="definition" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="cardinality" type="{http://www.isotc211.org/2005/gco}Multiplicity_PropertyType"/>
 *         &lt;element name="featureType" type="{http://www.isotc211.org/2005/gfc}FC_FeatureType_PropertyType"/>
 *         &lt;element name="constrainedBy" type="{http://www.isotc211.org/2005/gfc}FC_Constraint_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="definitionReference" type="{http://www.isotc211.org/2005/gfc}FC_DefinitionReference_PropertyType" minOccurs="0"/>
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
@XmlType(propOrder = {
    "memberName",
    "definition",
    "cardinality",
    "featureType",
    "constrainedBy",
    "definitionReference"
})
@XmlSeeAlso({
    FeatureAttributeImpl.class,
    FeatureOperationImpl.class,
    AssociationRoleImpl.class
})
@XmlRootElement(name= "FC_PropertyType")        
public class PropertyTypeImpl implements PropertyType, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    @XmlJavaTypeAdapter(GO_GenericName.class)
    @XmlElement(required = true)
    private LocalName memberName;
    private String definition;
    @XmlElement(required = true)
    private Multiplicity cardinality;
    @XmlElement(required = true)
    private FeatureType featureType;
    private List<Constraint> constrainedBy;
    private DefinitionReference definitionReference;
    
    @XmlTransient
    private boolean isReference = false;
    @XmlTransient
    protected boolean rootElement = true;

     /**
     * An empty constructor used by JAXB
     */
    public PropertyTypeImpl() {
        
    }
    
    /**
     * Clone a FeatureAttribute
     */
    public PropertyTypeImpl(final PropertyType feature) {
        if (feature != null) {
            this.id                  = feature.getId();
            this.cardinality         = feature.getCardinality();
            this.definitionReference = feature.getDefinitionReference();
            this.constrainedBy       = feature.getConstrainedBy();
            this.definition          = feature.getDefinition();
            this.featureType         = feature.getFeatureType();
            this.memberName          = feature.getMemberName(); 
        }
    }
    
    /**
     * Build a new PropertyTypeImpl
     */
    public PropertyTypeImpl(final String id, final LocalName memberName, final String definition, final Multiplicity cardinality, 
            final FeatureType featureType, final List<Constraint> constrainedBy, final DefinitionReference definitionReference) {
        this.id                  = id;
        this.cardinality         = cardinality;
        this.definitionReference = definitionReference;
        this.constrainedBy       = constrainedBy;
        this.definition          = definition;
        this.featureType         = featureType;
        this.memberName          = memberName; 
    }
    /**
     * Gets the value of the memberName property.
     * 
     */
    public LocalName getMemberName() {
        return memberName;
    }

    /**
     * Sets the value of the memberName property.
     * 
     */
    public void setMemberName(final LocalName value) {
        this.memberName = value;
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
    public void setDefinition(final String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the cardinality property.
     * 
     */
    public Multiplicity getCardinality() {
        return cardinality;
    }

    /**
     * Sets the value of the cardinality property.
     * 
    */
    public void setCardinality(final Multiplicity value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the featureType property.
     * 
     */
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     * Sets the value of the featureType property.
     * 
     */
    public void setFeatureType(final FeatureType value) {
        this.featureType = value;
    }

    /**
     * Gets the value of the constrainedBy property.
     */
    public List<Constraint> getConstrainedBy() {
        if (constrainedBy == null) {
            constrainedBy = new ArrayList<Constraint>();
        }
        return this.constrainedBy;
    }
    
    public void setConstrainedBy(final Constraint constrainedBy) {
        if (this.constrainedBy == null) {
            this.constrainedBy = new ArrayList<Constraint>();
        }
        this.constrainedBy.add(constrainedBy);
    }

    public void setConstrainedBy(final List<Constraint> constrainedBy) {
        this.constrainedBy = constrainedBy;
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
    public void setDefinitionReference(final DefinitionReference value) {
        this.definitionReference = value;
    }
    
    /**
     * set the catalogue in href mode
     */
    public void setReference(final boolean mode) {
        this.isReference = mode;
    }
    
     /**
     * get the current catalogue href mode
     */
    public boolean isReference() {
        return isReference;
    }
    
    public PropertyTypeImpl getReference() {
        PropertyTypeImpl result = new PropertyTypeImpl(this);
        result.setReference(true);
        return result;
    }

    public String getId() {
        return id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        if (id != null && !id.isEmpty()) {
            alreadySee.put(id, this);
        }
        rootElement = false;
        
        if (featureType != null) {
            if (alreadySee.get(featureType.getId()) != null) {
                featureType = ((FeatureTypeImpl)featureType).getReference();
            } else {
                alreadySee = ((FeatureTypeImpl)featureType).beforeMarshal(alreadySee);
            }
        }
        return alreadySee;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]:").append('\n');
        s.append("id: ").append(id).append('\n');
        if (memberName != null) {
            s.append("memberName: ").append(memberName).append('\n');
        }
        if (definition != null) {
            s.append("definition: ").append(definition).append('\n');
        }
        if (cardinality != null) {
            s.append("cardinality: ").append(cardinality).append('\n');
        }
        if (featureType != null) {
            s.append("featureType: ").append(featureType).append('\n');
        }
        if (definitionReference != null) {
            s.append("definitionReference: ").append(definitionReference).append('\n');
        }
        if (constrainedBy != null) {
            s.append("constrainedBy: ").append('\n');
            for (Constraint c: constrainedBy) {
                s.append(c).append('\n');
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
        if (object instanceof PropertyTypeImpl) {
            final PropertyTypeImpl that = (PropertyTypeImpl) object;
            
            return Utilities.equals(this.cardinality,         that.cardinality)         &&
                   Utilities.equals(this.constrainedBy,       that.constrainedBy)       &&
                   Utilities.equals(this.definition,          that.definition)          &&
                   Utilities.equals(this.definitionReference, that.definitionReference) &&
                   Utilities.equals(this.featureType,         that.featureType)         &&
                   Utilities.equals(this.id,                  that.id)                  &&
                   Utilities.equals(this.memberName,          that.memberName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.memberName != null ? this.memberName.hashCode() : 0);
        return hash;
    }

}
