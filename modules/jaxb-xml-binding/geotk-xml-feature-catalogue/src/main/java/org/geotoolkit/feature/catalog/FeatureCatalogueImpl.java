

package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
import org.opengis.feature.catalog.DefinitionSource;
import org.opengis.feature.catalog.FeatureCatalogue;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.metadata.citation.ResponsibleParty;


/**
 * A feature catalogue contains its identification and contact information, and definition of some number of feature types with other information necessary for those definitions.
 * 
 * <p>Java class for FC_FeatureCatalogue_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_FeatureCatalogue_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gmx}AbstractCT_Catalogue_Type">
 *       &lt;sequence>
 *         &lt;element name="producer" type="{http://www.isotc211.org/2005/gmd}CI_ResponsibleParty_PropertyType"/>
 *         &lt;element name="functionalLanguage" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="featureType" type="{http://www.isotc211.org/2005/gfc}FC_FeatureType_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="definitionSource" type="{http://www.isotc211.org/2005/gfc}FC_DefinitionSource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="inheritanceRelation" type="{http://www.isotc211.org/2005/gfc}FC_InheritanceRelation_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "", propOrder = {
    "name",
    "scope",
    "fieldOfApplication",
    "versionNumber",
    "versionDate",
    "producer",
    "functionalLanguage",
    "featureType",
    "definitionSource"
})
@XmlRootElement( name = "FC_FeatureCatalogue")
public class FeatureCatalogueImpl implements  FeatureCatalogue, Referenceable {

    @XmlTransient
    private static final Logger LOGGER = Logger.getLogger("featureCatalogueImpl");
    
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private List<String> scope;
    private List<String> fieldOfApplication;
    @XmlElement(required = true)
    private String versionNumber;
    @XmlElement(required = true)
    private Date versionDate;
    private ResponsibleParty producer;
    private String functionalLanguage;
    @XmlElement(required = true)
    private List<FeatureType> featureType;
    private List<DefinitionSource> definitionSource;
    
    @XmlTransient
    private boolean isReference = false;
    @XmlTransient
    protected boolean rootElement = true;
    
     /**
     * An empty constructor used by JAXB
     */
    public FeatureCatalogueImpl() {
        
    }
    
    /**
     * Clone a FeatureCatalogue
     */
    public FeatureCatalogueImpl(final FeatureCatalogue feature) {
        if (feature != null) {
            this.featureType        = feature.getFeatureType();
            this.definitionSource   = feature.getDefinitionSource();
            this.fieldOfApplication = feature.getFieldOfApplication();
            this.functionalLanguage = feature.getFunctionalLanguage();
            this.name               = feature.getName();
            this.producer           = feature.getProducer();
            this.scope              = feature.getScope();
            this.versionDate        = feature.getVersionDate();
            this.versionNumber      = feature.getVersionNumber();
            this.id                 = feature.getId();
        }
    }
    
    /**
     * Build a new FeatureCatalogue
     */
    public FeatureCatalogueImpl(final String id, final String name, final List<String> scope, final ResponsibleParty producer, final Date versionDate,
            final String versionNumber, final List<FeatureType> types, final List<DefinitionSource> definitionSource, final List<String> fieldOfApplication,
            final String functionalLanguage) {
        this.id                 = id;
        this.name               = name;
        this.featureType        = types;
        this.definitionSource   = definitionSource;
        this.fieldOfApplication = fieldOfApplication;
        this.functionalLanguage = functionalLanguage;
        this.producer           = producer;
        this.scope              = scope;
        this.versionDate        = versionDate;
        this.versionNumber      = versionNumber;
        
    }
    

    /**
     * Return the identifier of the catalog
     */
    public String getId() {
        return this.id;
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
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     */
    public List<String> getScope() {
        if (scope == null) {
            scope = new ArrayList<String>();
        }
        return this.scope;
    }
    
    public void setScope(final String scope) {
        if (this.scope == null) {
            this.scope = new ArrayList<String>();
        }
        this.scope.add(scope);
    }
    
    public void setScope(final List<String> scope) {
        this.scope = scope;
    }
    

    /**
     * Gets the value of the fieldOfApplication property.
     */
    public List<String> getFieldOfApplication() {
        if (fieldOfApplication == null) {
            fieldOfApplication = new ArrayList<String>();
        }
        return this.fieldOfApplication;
    }
    
    public void setFieldOfApplication(final String fieldOfApplication) {
        if (this.fieldOfApplication == null) {
            this.fieldOfApplication = new ArrayList<String>();
        }
        this.fieldOfApplication.add(fieldOfApplication);
    }
    
    public void setFieldOfApplication(final List<String> fieldOfApplication) {
        this.fieldOfApplication = fieldOfApplication;
    }

    /**
     * Gets the value of the versionNumber property.
     * 
    */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the value of the versionNumber property.
     * 
     */
    public void setVersionNumber(final String value) {
        this.versionNumber = value;
    }

    /**
     * Gets the value of the versionDate property.
     * 
    */
    public Date getVersionDate() {
        return versionDate;
    }

    /**
     * Sets the value of the versionDate property.
     *     
     */
    public void setVersionDate(final Date value) {
        this.versionDate = value;
    }

    
    /**
     * Gets the value of the producer property.
     * 
     */
    public ResponsibleParty getProducer() {
        return producer;
    }

    /**
     * Sets the value of the producer property.
     */
    public void setProducer(final ResponsibleParty value) {
        this.producer = value;
    }

    /**
     * Gets the value of the functionalLanguage property.
     */
    public String getFunctionalLanguage() {
        return functionalLanguage;
    }

    /**
     * Sets the value of the functionalLanguage property.
     */
    public void setFunctionalLanguage(final String value) {
        this.functionalLanguage = value;
    }

    /**
     * Gets the value of the featureType property.
     */
    public List<FeatureType> getFeatureType() {
        if (featureType == null) {
            featureType = new ArrayList<FeatureType>();
        }
        return this.featureType;
    }
    
    
    public void setFeatureType(final FeatureType featureType) {
        if (this.featureType == null) {
            this.featureType = new ArrayList<FeatureType>();
        }
        this.featureType.add(featureType);
    }
    
    
    public void setFeatureType(final List<FeatureType> featureType) {
        this.featureType = featureType;
    }

    /**
     * Gets the value of the definitionSource property.
     */
    public List<DefinitionSource> getDefinitionSource() {
        if (definitionSource == null) {
            definitionSource = new ArrayList<DefinitionSource>();
        }
        return this.definitionSource;
    }
    
    public void setDefinitionSource(final DefinitionSource definitionSource) {
        if (this.definitionSource == null) {
            this.definitionSource = new ArrayList<DefinitionSource>();
        }
        this.definitionSource.add(definitionSource);
    }
    
    public void setDefinitionSource(final List<DefinitionSource> definitionSource) {
        this.definitionSource = definitionSource;
    }
    
    public void setReference(final boolean isReference) {
        this.isReference = isReference;
    }
    
    public boolean isReference() {
        return isReference;
    }
    
    public FeatureCatalogueImpl getReference() {
        FeatureCatalogueImpl result = new FeatureCatalogueImpl(this);
        result.setReference(true);
        return result;
    }
    
    
    private void beforeMarshal(final Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<String, Referenceable>());
        }
    }
    
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        if (id != null && !id.isEmpty()) {
            alreadySee.put(id, this);
        }
        rootElement = false;
        
        List<FeatureType> replacement = new ArrayList<FeatureType>();
        for (FeatureType f: getFeatureType()) {
            FeatureTypeImpl fi = (FeatureTypeImpl) f;
            
            if (alreadySee.get(fi.getId()) != null) {
                replacement.add(fi.getReference());
            } else {
                alreadySee = fi.beforeMarshal(alreadySee);
                replacement.add(fi);    
            }
        }
        featureType = replacement;
        return alreadySee;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[FeatureCatalogue]:").append('\n');
        s.append("id=").append(getId()).append('\n');
        if (isReference) {
            s.append("reference mode").append('\n');
        } else {
            s.append("name").append(name).append('\n');
       
            if (scope != null) {
                s.append("scopes:").append('\n');
                for (String sc: scope) {
                    s.append('\t').append(sc).append('\n');
                }
            }
        
            if (fieldOfApplication != null) {
                s.append("field of applications:").append('\n');
                for (String sc: fieldOfApplication) {
                    s.append('\t').append(sc).append('\n');
                }
            }
        
            if (versionNumber != null) {
                s.append("Version Number:").append(versionNumber).append('\n');
            }
        
            if (versionDate != null) {
                s.append("Version Date:").append(versionDate).append('\n');
            }
        
            if (producer != null) {
                s.append("Producer:").append(producer.toString()).append('\n');
            }
        
            if (functionalLanguage != null) {
                s.append("functional language:").append(functionalLanguage).append('\n');
            }
        
            if (featureType != null) {
                this.setReference(true);
                s.append("feature types:").append('\n');
                for (FeatureType sc: featureType) {
                    s.append('\t').append(sc).append('\n');
                }
                this.setReference(false);
            }
            if (definitionSource != null) {
                s.append("Definition sources:").append('\n');
                for (DefinitionSource sc: definitionSource) {
                    s.append('\t').append(sc.toString()).append('\n');
                }
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
        if (object instanceof FeatureCatalogueImpl) {
            final FeatureCatalogueImpl that = (FeatureCatalogueImpl) object;
            
            return Utilities.equals(this.definitionSource,   that.definitionSource)   &&
                   Utilities.equals(this.featureType,        that.featureType)        &&
                   Utilities.equals(this.fieldOfApplication, that.fieldOfApplication) &&
                   Utilities.equals(this.functionalLanguage, that.functionalLanguage) &&
                   Utilities.equals(this.getId(), that.getId())                 &&
                   Utilities.equals(this.name,               that.name)               &&
                   Utilities.equals(this.producer,           that.producer)           &&
                   Utilities.equals(this.scope,              that.scope)              &&
                   Utilities.equals(this.versionDate,        that.versionDate)        &&
                   Utilities.equals(this.versionNumber,      that.versionNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.id   != null ? this.id.hashCode()   : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
