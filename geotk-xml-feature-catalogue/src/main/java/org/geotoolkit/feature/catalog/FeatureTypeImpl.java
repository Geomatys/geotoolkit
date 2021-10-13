/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.Marshaller;
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
import org.apache.sis.internal.jaxb.gco.GO_GenericName;
import org.apache.sis.metadata.AbstractMetadata;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.jaxb.feature.catalog.FeatureCatalogueAdapter;
import org.opengis.util.LocalName;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.FeatureAttribute;
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
 * @module
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
@XmlRootElement(name = "FC_FeatureType")
public class FeatureTypeImpl extends AbstractMetadata implements FeatureType, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    @XmlJavaTypeAdapter(GO_GenericName.class)
    @XmlElement(required = true)
    private LocalName typeName;
    private String definition;
    private String code;
    @XmlElement(required = true)
    private Boolean isAbstract;

    @XmlJavaTypeAdapter(GO_GenericName.class)
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
    public FeatureTypeImpl(final FeatureType feature) {
        if (feature != null) {
            this.aliases                  = feature.getAliases();
            this.carrierOfCharacteristics = feature.getCarrierOfCharacteristics();
            this.code                     = feature.getCode();
            this.id                       = feature.getId();
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
    public FeatureTypeImpl(final String id, final LocalName typeName, final String definition, final String code, final Boolean isAbstract, final List<LocalName> aliases,
            final FeatureCatalogue catalogue, final List<PropertyType> carrierOfCharacteristics) {
        if (id != null) {
            this.id                   = id;
        } else {
            this.id                   = "ftype-" + code;
        }
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
    @Override
    public LocalName getTypeName() {
        return typeName;
    }

    /**
     * Sets the value of the typeName property.
     */
    public void setTypeName(final LocalName value) {
        this.typeName = value;
    }

    /**
     * Gets the value of the definition property.
     *
     */
    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     */
    public void setDefinition(final String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the code property.
     *
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     */
    public void setCode(final String value) {
        this.code = value;
    }

    /**
     * Gets the value of the isAbstract property.
     */
    @Override
    public Boolean getIsAbstract() {
        return isAbstract;
    }

    /**
     * Sets the value of the isAbstract property.
     */
    public void setIsAbstract(final Boolean value) {
        this.isAbstract = value;
    }

    /**
     * Gets the value of the aliases property.
     *
     */
    @Override
    public List<LocalName> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<>();
        }
        return aliases;
    }

    public void setAliases(final LocalName alias) {
        if (aliases == null) {
            aliases = new ArrayList<>();
        }
        this.aliases.add(alias);
    }

    public void setAliases(final List<LocalName> aliases) {
        this.aliases = aliases;
    }

    /**
     * Gets the value of the inheritsFrom property.
     */
    @Override
    public List<InheritanceRelation> getInheritsFrom() {
        if (inheritsFrom == null) {
            inheritsFrom = new ArrayList<>();
        }
        return this.inheritsFrom;
    }

    public void setInheritsFrom(final InheritanceRelation inheritsFrom) {
        if (this.inheritsFrom == null) {
            this.inheritsFrom = new ArrayList<>();
        }
        this.inheritsFrom.add(inheritsFrom);
    }

    public void setInheritsFrom(final List<InheritanceRelation> inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    /**
     * Gets the value of the inheritsTo property.
     */
    @Override
    public List<InheritanceRelation> getInheritsTo() {
        if (inheritsTo == null) {
            inheritsTo = new ArrayList<>();
        }
        return this.inheritsTo;
    }

    public void setInheritsTo(final InheritanceRelation inheritsTo) {
        if (this.inheritsTo == null) {
            this.inheritsTo = new ArrayList<>();
        }
        this.inheritsTo.add(inheritsTo);
    }

    public void setInheritsTo(final List<InheritanceRelation> inheritsTo) {
        this.inheritsTo = inheritsTo;
    }

    /**
     * Gets the value of the featureCatalogue property.
     */
    @Override
    public FeatureCatalogue getFeatureCatalogue() {
        return featureCatalogue;
    }

    /**
     * Sets the value of the featureCatalogue property.
     */
    public void setFeatureCatalogue(final FeatureCatalogue value) {
        this.featureCatalogue = value;
    }

    /**
     * Gets the value of the carrierOfCharacteristics property.
     *
     */
    @Override
    public List<PropertyType> getCarrierOfCharacteristics() {
        if (carrierOfCharacteristics == null) {
            carrierOfCharacteristics = new ArrayList<>();
        }
        return this.carrierOfCharacteristics;
    }

    public void setCarrierOfCharacteristics(final PropertyType carrierOfCharacteristics) {
        if (this.carrierOfCharacteristics == null) {
            this.carrierOfCharacteristics = new ArrayList<>();
        }
        this.carrierOfCharacteristics.add(carrierOfCharacteristics);
    }

    public void setCarrierOfCharacteristics(final FeatureAttribute carrierOfCharacteristics) {
        if (this.carrierOfCharacteristics == null) {
            this.carrierOfCharacteristics = new ArrayList<>();
        }
        this.carrierOfCharacteristics.add(carrierOfCharacteristics);
    }

    public void setCarrierOfCharacteristics(final List<PropertyType> carrierOfCharacteristics) {
        this.carrierOfCharacteristics = carrierOfCharacteristics;
    }

    /**
     * Gets the value of the constrainedBy property.
     *
     */
    @Override
    public List<Constraint> getConstrainedBy() {
        if (constrainedBy == null) {
            constrainedBy = new ArrayList<>();
        }
        return this.constrainedBy;
    }

    public void setConstrainedBy(final Constraint constrainedBy) {
        if (this.constrainedBy == null) {
            this.constrainedBy = new ArrayList<>();
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
    @Override
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

    @Override
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * set the feature in reference mode
     */
    public void setReference(final boolean mode) {
        this.isReference = mode;
    }

     /**
     * get the current feature in reference mode
     */
    @Override
    public boolean isReference() {
        return isReference;
    }

    @Override
    public FeatureTypeImpl getReferenceableObject() {
        FeatureTypeImpl reference = new FeatureTypeImpl(this);
        reference.setReference(true);
        return reference;
    }

    /**
     * This java object contains cycle. this cycle cannot be handle by JAXB.
     * We must create reference mark in the xml.
     * @param marshaller
     */
    private void beforeMarshal(final Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<>());
        }
    }

    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        if (id != null && !id.isEmpty()) {
            alreadySee.put(id, this);
        }
        rootElement = false;

        List<InheritanceRelation> fromReplacement = new ArrayList<>();
        for (InheritanceRelation in: getInheritsFrom()) {
            InheritanceRelationImpl inf = (InheritanceRelationImpl) in;

            if (alreadySee.get(inf.getId()) != null) {
                fromReplacement.add(inf.getReferenceableObject());
            } else {
                alreadySee = inf.beforeMarshal(alreadySee);
                fromReplacement.add(inf);
            }
        }
        inheritsFrom = fromReplacement;

        List<InheritanceRelation> toReplacement = new ArrayList<>();
        for (InheritanceRelation in: getInheritsTo()) {
            InheritanceRelationImpl inf = (InheritanceRelationImpl) in;

            if (alreadySee.get(inf.getId()) != null) {
                toReplacement.add(inf.getReferenceableObject());
            } else {
                alreadySee = inf.beforeMarshal(alreadySee);
                toReplacement.add(inf);
            }
        }
        inheritsTo = toReplacement;

        if (featureCatalogue != null) {
            if (alreadySee.get(featureCatalogue.getId()) != null) {
                featureCatalogue = ((FeatureCatalogueImpl)featureCatalogue).getReferenceableObject();
            } else {
                alreadySee = ((FeatureCatalogueImpl)featureCatalogue).beforeMarshal(alreadySee);
            }
        }

        List<PropertyType> replacement = new ArrayList<>();
        for (PropertyType f: getCarrierOfCharacteristics()) {
            PropertyTypeImpl p = (PropertyTypeImpl) f;

            if (alreadySee.get(p.getId()) != null) {
                replacement.add(p.getReferenceableObject());
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
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeatureTypeImpl) {
            final FeatureTypeImpl that = (FeatureTypeImpl) object;

             //to avoid infinite cycle
             boolean carrier = false;
             carrier = Objects.equals(this.getCarrierOfCharacteristics().size(), that.getCarrierOfCharacteristics().size());
             if (carrier) {
                 for (int i = 0; i < this.getCarrierOfCharacteristics().size(); i++) {
                     final String thisId = this.getCarrierOfCharacteristics().get(i).getId();
                     final String thatId = that.getCarrierOfCharacteristics().get(i).getId();
                     // if the ids are null we try the name
                     if (thisId == null && thatId == null) {
                         final LocalName thisName = this.getCarrierOfCharacteristics().get(i).getMemberName();
                         final LocalName thatName = that.getCarrierOfCharacteristics().get(i).getMemberName();
                         carrier = Objects.equals(thisName, thatName);

                     } else {
                         carrier = Objects.equals(thisId, thatId);
                     }
                 }
             } else {
                 return false;
             }


             //to avoid infinite cycle
             boolean catalogue = false;
             if (this.featureCatalogue != null && that.featureCatalogue != null) {
                catalogue = Objects.equals(this.featureCatalogue.getId(), that.featureCatalogue.getId());
             } else if (this.featureCatalogue == null && that.featureCatalogue == null){
                 catalogue = true;
             }

             //to avoid infinite cycle
             boolean inherits = Objects.equals(this.getInheritsFrom().size(), that.getInheritsFrom().size()) &&
                                Objects.equals(this.getInheritsTo().size(),   that.getInheritsTo().size());

            return Objects.equals(this.aliases,                  that.aliases)                  &&
                   Objects.equals(this.code,                     that.code)                     &&
                   Objects.equals(this.constrainedBy,            that.constrainedBy)            &&
                   Objects.equals(this.definition,               that.definition)               &&
                   Objects.equals(this.definitionReference,      that.definitionReference)      &&
                   Objects.equals(this.id,                       that.id)                       &&
                   Objects.equals(this.isAbstract,               that.isAbstract)               &&
                   Objects.equals(this.typeName,                 that.typeName)                 &&
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

    @Override
    public MetadataStandard getStandard() {
        return FeatureCatalogueStandard.ISO_19110;
    }
}
