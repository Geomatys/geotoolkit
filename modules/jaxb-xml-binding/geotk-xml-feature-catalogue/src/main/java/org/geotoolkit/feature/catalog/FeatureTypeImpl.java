


package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.resources.jaxb.feature.catalog.FeatureCatalogueAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.util.LocalName;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.FeatureCatalogue;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.InheritanceRelation;
import org.opengis.feature.catalog.PropertyType;



/**
 * Class of real world phenomena with common properties -  - [ocl] - name realizes GF_FeatureType::typeName; - isAbstract realizes GF_FeatureType::isAbstract; - constrainedBy realizes GF_FeatureType::constrainedBy - [/ocl]
 * 
 * <p>Java class for FC_FeatureType_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_FeatureType_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="typeName" type="{http://www.isotc211.org/2005/gco}LocalName_Impl"/>
 *         &lt;element name="definition" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.isotc211.org/2005/gco}CharacterString_Impl" minOccurs="0"/>
 *         &lt;element name="isAbstract" type="{http://www.isotc211.org/2005/gco}Boolean_Impl"/>
 *         &lt;element name="aliases" type="{http://www.isotc211.org/2005/gco}LocalName_Impl" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inheritsFrom" type="{http://www.isotc211.org/2005/gfc}FC_InheritanceRelation_Impl" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inheritsTo" type="{http://www.isotc211.org/2005/gfc}FC_InheritanceRelation_Impl" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="featureCatalogue" type="{http://www.isotc211.org/2005/gfc}FC_FeatureCatalogue_Impl"/>
 *         &lt;element name="carrierOfCharacteristics" type="{http://www.isotc211.org/2005/gfc}FC_Impl_Impl" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="constrainedBy" type="{http://www.isotc211.org/2005/gfc}FC_Constraint_Impl" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="definitionReference" type="{http://www.isotc211.org/2005/gfc}FC_DefinitionReference_Impl" minOccurs="0"/>
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
@XmlType(name = "FC_FeatureType_Type", propOrder = {
    "typeName",
    "definition",
    "code",
    "isAbstract",
    "aliases",
    "inheritsFrom",
    "inheritsTo",
    "featureCatalogue",
    "carrierOfCharacteristics",
    "constrainedBy",
    "definitionReference"
})
@XmlSeeAlso({
    FeatureAssociationImpl.class
})
public class FeatureTypeImpl implements FeatureType, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    
    @XmlElement(required = true)
    private LocalName typeName;
    private String definition;
    private String code;
    @XmlElement(required = true)
    private Boolean isAbstract;
    private List<LocalName> aliases;
    private List<InheritanceRelation> inheritsFrom;
    private List<InheritanceRelation> inheritsTo;
    
    @XmlJavaTypeAdapter(FeatureCatalogueAdapter.class)
    private FeatureCatalogue featureCatalogue;
    private List<PropertyType> carrierOfCharacteristics;
    private List<Constraint> constrainedBy;
    private DefinitionReference definitionReference;
    
    @XmlTransient
    private boolean isReference = false;
    @XmlTransient
    protected boolean rootElement = true;
     
    /**
     * An empty constructor used by JAXB
     */
    public FeatureTypeImpl() {
        
    }
    
    /**
     * Clone a FeatureType
     */
    public FeatureTypeImpl(FeatureType feature) {
        if (feature != null) {
            this.aliases                  = feature.getAliases();
            this.carrierOfCharacteristics = feature.getCarrierOfCharacteristics();
            this.code                     = feature.getCode();
            this.id                       = "ftype-" + feature.getCode();
            this.constrainedBy            = feature.getConstrainedBy();
            this.definition               = feature.getDefinition();
            this.definitionReference      = feature.getDefinitionReference();
            this.featureCatalogue         = feature.getFeatureCatalogue();
            this.inheritsFrom             = feature.getInheritsFrom();
            this.inheritsTo               = feature.getInheritsTo();
            this.isAbstract               = feature.getIsAbstract();
            this.typeName                 = feature.getTypeName();
        }
    }
    
    /**
     * Build a new Feature type
     */
    public FeatureTypeImpl(LocalName typeName, String definition, String code, Boolean isAbstract, List<LocalName> aliases,
            FeatureCatalogue catalogue, List<PropertyType> carrierOfCharacteristics) {
        this.id                       = "ftype-" + code;
        this.aliases                  = aliases;
        this.carrierOfCharacteristics = carrierOfCharacteristics;
        this.code                     = code;
        this.definition               = definition;
        this.featureCatalogue         = catalogue;
        this.isAbstract               = isAbstract;
        this.typeName                 = typeName;
    }
    
    /**
     * Gets the value of the typeName property.
     */
    public LocalName getTypeName() {
        return typeName;
    }

    /**
     * Sets the value of the typeName property.
     */
    public void setTypeName(LocalName value) {
        this.typeName = value;
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
     */
    public void setDefinition(String value) {
        this.definition = value;
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
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the isAbstract property.
     */
    public Boolean getIsAbstract() {
        return isAbstract;
    }

    /**
     * Sets the value of the isAbstract property.
     */
    public void setIsAbstract(Boolean value) {
        this.isAbstract = value;
    }

    /**
     * Gets the value of the aliases property.
     * 
     */
    public List<LocalName> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<LocalName>();
        }
        return this.aliases;
    }
    
    public void setAliases(LocalName alias) {
        if (aliases == null) {
            aliases = new ArrayList<LocalName>();
        }
        this.aliases.add(alias);
    }
    
    public void setAliases(List<LocalName> aliases) {
        this.aliases = aliases;
    }

    /**
     * Gets the value of the inheritsFrom property.
     */
    public List<InheritanceRelation> getInheritsFrom() {
        if (inheritsFrom == null) {
            inheritsFrom = new ArrayList<InheritanceRelation>();
        }
        return this.inheritsFrom;
    }
    
    public void setInheritsFrom(InheritanceRelation inheritsFrom) {
        if (this.inheritsFrom == null) {
            this.inheritsFrom = new ArrayList<InheritanceRelation>();
        }
        this.inheritsFrom.add(inheritsFrom);
    }
    
    public void setInheritsFrom(List<InheritanceRelation> inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    /**
     * Gets the value of the inheritsTo property.
     */
    public List<InheritanceRelation> getInheritsTo() {
        if (inheritsTo == null) {
            inheritsTo = new ArrayList<InheritanceRelation>();
        }
        return this.inheritsTo;
    }
    
    public void setInheritsTo(InheritanceRelation inheritsTo) {
        if (this.inheritsTo == null) {
            this.inheritsTo = new ArrayList<InheritanceRelation>();
        }
        this.inheritsTo.add(inheritsTo);
    }
    
    public void setInheritsTo(List<InheritanceRelation> inheritsTo) {
        this.inheritsTo = inheritsTo;
    }

    /**
     * Gets the value of the featureCatalogue property.
     */
    public FeatureCatalogue getFeatureCatalogue() {
        return featureCatalogue;
    }

    /**
     * Sets the value of the featureCatalogue property.
     */
    public void setFeatureCatalogue(FeatureCatalogue value) {
        this.featureCatalogue = value;
    }

    /**
     * Gets the value of the carrierOfCharacteristics property.
     * 
     */
    public List<PropertyType> getCarrierOfCharacteristics() {
        if (carrierOfCharacteristics == null) {
            carrierOfCharacteristics = new ArrayList<PropertyType>();
        }
        return this.carrierOfCharacteristics;
    }
    
    public void setCarrierOfCharacteristics(PropertyType carrierOfCharacteristics) {
        if (this.carrierOfCharacteristics == null) {
            this.carrierOfCharacteristics = new ArrayList<PropertyType>();
        }
        this.carrierOfCharacteristics.add(carrierOfCharacteristics);
    }
    
    public void setCarrierOfCharacteristics(List<PropertyType> carrierOfCharacteristics) {
        this.carrierOfCharacteristics = carrierOfCharacteristics;
    }

    /**
     * Gets the value of the constrainedBy property.
     * 
     */
    public List<Constraint> getConstrainedBy() {
        if (constrainedBy == null) {
            constrainedBy = new ArrayList<Constraint>();
        }
        return this.constrainedBy;
    }
    
    public void setConstrainedBy(Constraint constrainedBy) {
        if (this.constrainedBy == null) {
            this.constrainedBy = new ArrayList<Constraint>();
        }
        this.constrainedBy.add(constrainedBy);
    }
    
    public void setConstrainedBy(List<Constraint> constrainedBy) {
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
    public void setDefinitionReference(DefinitionReference value) {
        this.definitionReference = value;
    }
    
    public String getId() {
        return id;
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
    
    public FeatureTypeImpl getReference() {
        FeatureTypeImpl reference = new FeatureTypeImpl(this);
        reference.setReference(true);
        return reference;
    }
    
    /**
     * This java object contains cycle. this cycle cannot be handle by JAXB.
     * We must create reference mark in the xml.
     * @param marshaller
     */
    private void beforeMarshal(Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<String, Referenceable>());
        }
    }
    
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        alreadySee.put(id, this);
        rootElement = false;
        
        List<InheritanceRelation> fromReplacement = new ArrayList<InheritanceRelation>();
        for (InheritanceRelation in: getInheritsFrom()) {
            InheritanceRelationImpl inf = (InheritanceRelationImpl) in;
            
            if (alreadySee.get(inf.getId()) != null) {
                fromReplacement.add(inf.getReference());
            } else {
                alreadySee = inf.beforeMarshal(alreadySee);
                fromReplacement.add(inf);    
            }
        }
        inheritsFrom = fromReplacement;
        
        List<InheritanceRelation> toReplacement = new ArrayList<InheritanceRelation>();
        for (InheritanceRelation in: getInheritsTo()) {
            InheritanceRelationImpl inf = (InheritanceRelationImpl) in;
            
            if (alreadySee.get(inf.getId()) != null) {
                toReplacement.add(inf.getReference());
            } else {
                alreadySee = inf.beforeMarshal(alreadySee);
                toReplacement.add(inf);    
            }
        }
        inheritsTo = toReplacement;
        
        if (featureCatalogue != null) {
            if (alreadySee.get(featureCatalogue.getId()) != null) {
                featureCatalogue = ((FeatureCatalogueImpl)featureCatalogue).getReference();
            } else {
                alreadySee = ((FeatureCatalogueImpl)featureCatalogue).beforeMarshal(alreadySee);
            }
        }
        
        List<PropertyType> replacement = new ArrayList<PropertyType>();
        for (PropertyType f: getCarrierOfCharacteristics()) {
            PropertyTypeImpl p = (PropertyTypeImpl) f;
            
            if (alreadySee.get(p.getId()) != null) {
                replacement.add(p.getReference());
            } else {
                alreadySee = p.beforeMarshal(alreadySee);
                replacement.add(p);    
            }
        }
        carrierOfCharacteristics = replacement;
        return alreadySee;
    }
    
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]:").append('\n');
        s.append("id: ").append(id).append('\n');
        if (isReference) {
            s.append("reference mode").append('\n');
        } else {
            if (typeName != null) {
                s.append("typeName: ").append(typeName).append('\n');
            }
            if (definition != null) {
                s.append("definition: ").append(definition).append('\n');
            }
            if (code != null) {
                s.append("code: ").append(code).append('\n');
            }
            if (isAbstract != null) {
                s.append("isAbstract: ").append(isAbstract).append('\n');
            }
            if (aliases != null) {
                s.append("aliases: ").append('\n');
                for (LocalName l: aliases) {
                    s.append(l).append('\n');
                }
            }
            if (inheritsFrom != null) {
                s.append("inherits from: ").append('\n');
                for (InheritanceRelation l: inheritsFrom) {
                    s.append(l).append('\n');
                }
            }
            if (inheritsTo != null) {
                s.append("inherits to: ").append('\n');
                for (InheritanceRelation l: inheritsTo) {
                    s.append(l).append('\n');
                }
            }
            if (featureCatalogue != null) {
                s.append("featureCatalogue: ").append(featureCatalogue).append('\n');
            }
            if (carrierOfCharacteristics != null) {
                setReference(true);
                s.append("carrier of characteristics: ").append('\n');
                for (PropertyType l: carrierOfCharacteristics) {
                    s.append(l).append('\n');
                }
                setReference(false);
            }
            if (constrainedBy != null) {
                s.append("constained by: ").append('\n');
                for (Constraint l: constrainedBy) {
                    s.append(l).append('\n');
                }
            }
            if (definitionReference != null) {
                s.append("definition reference: ").append(definitionReference).append('\n');
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
        if (object instanceof FeatureTypeImpl) {
            final FeatureTypeImpl that = (FeatureTypeImpl) object;
            
             //to avoid infinite cycle
             boolean carrier = false;
             carrier = Utilities.equals(this.getCarrierOfCharacteristics().size(), that.getCarrierOfCharacteristics().size());
             if (carrier) {
                 for (int i = 0; i < this.getCarrierOfCharacteristics().size(); i++) {
                     if (!this.getCarrierOfCharacteristics().get(i).getId().equals(that.getCarrierOfCharacteristics().get(i).getId())) {
                         carrier = false;
                     }
                 }
             } else {
                 return false;
             }
             
                     
             //to avoid infinite cycle
             boolean catalogue = Utilities.equals(this.featureCatalogue.getId(), that.featureCatalogue.getId());
             
             //to avoid infinite cycle
             boolean inherits = Utilities.equals(this.getInheritsFrom().size(), that.getInheritsFrom().size()) && 
                                Utilities.equals(this.getInheritsTo().size(),   that.getInheritsTo().size()); 
            
            return Utilities.equals(this.aliases,                  that.aliases)                  &&
                   Utilities.equals(this.code,                     that.code)                     &&
                   Utilities.equals(this.constrainedBy,            that.constrainedBy)            &&
                   Utilities.equals(this.definition,               that.definition)               &&
                   Utilities.equals(this.definitionReference,      that.definitionReference)      &&
                   Utilities.equals(this.id,                       that.id)                       &&
                   Utilities.equals(this.isAbstract,               that.isAbstract)               &&
                   Utilities.equals(this.typeName,                 that.typeName)                 &&
                   inherits && catalogue && carrier;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id       != null ? this.id.hashCode()       : 0);
        hash = 61 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 61 * hash + (this.code     != null ? this.code.hashCode()     : 0);
        return hash;
    }
}
