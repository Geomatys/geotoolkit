/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.skos.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGMLType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Concept", 
namespace = "http://www.w3.org/2004/02/skos/core#",
propOrder = {
    "hierarchyRoot",
    "hierarchyRootType",
    "externalID",
    "prefLabel",
    "altLabel",
    "definition",
    "description",
    "language",
    "rights",
    "issued",
    "type",
    "value",
    "label",
    "hasTopConcept",
    "hasVersion",
    "date",
    "contributor",
    "title",
    "subject",
    "creator",
    "broader",
    "related",
    "scopeNote",
    "changeNote",
    "name",
    "narrower",
    "narrowerTransitive",
    "modified",
    "example",
    "geometry"

})
public class Concept implements Serializable {
    
    @XmlAttribute(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String about;
    
    @XmlAttribute(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String resource;

    @XmlElement(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private Concept type;

    @XmlElement(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String value;

    @XmlElement(namespace="http://www.w3.org/2000/01/rdf-schema#")
    private String label;
    
    @XmlElement(namespace="http://semantic-web.at/ontologies/csw.owl#")
    private Boolean hierarchyRoot;
    
    @XmlElement(namespace="http://semantic-web.at/ontologies/csw.owl#")
    private Concept hierarchyRootType;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> hasTopConcept;
            
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String externalID;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String prefLabel;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<String> altLabel;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private Concept related;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String scopeNote;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String example;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> broader;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> narrower;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> narrowerTransitive;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String definition;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String changeNote;

    @XmlElement(namespace = "http://xmlns.com/foaf/0.1")
    private String name;
    
    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String subject;
    
    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String creator;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String date;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String description;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String language;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String rights;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String title;
    
    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String contributor;

    @XmlElement(namespace="http://purl.org/dc/terms")
    private String hasVersion;
    
    @XmlElement(namespace="http://purl.org/dc/terms")
    private String issued;

    @XmlElement(namespace="http://purl.org/dc/terms")
    private String modified;

    @XmlElement(namespace="http://www.opengis.net/gml")
    private List<AbstractGMLType> geometry;

    @XmlElement(namespace="http://www.geomatys.com/count")
    private Integer count;


    public Concept() {
        
    }

    public Concept(final String about) {
        this.about = about;
    }

    public Concept(final String about, final String prefLabel) {
        this.about     = about;
        this.prefLabel = prefLabel;
    }

    public Concept(final String about, final String externalID, final String prefLabel, final String altLabel, final String definition, final String date) {
        this.about      = about;
        this.altLabel   = Arrays.asList(altLabel);
        this.date       = date;
        this.definition = definition;
        this.externalID = externalID;
        this.prefLabel  = prefLabel;
    }

    public Concept(final String about, final String externalID, final String prefLabel, final List<String >altLabel, final String definition, final String date) {
        this.about      = about;
        this.altLabel   = altLabel;
        this.date       = date;
        this.definition = definition;
        this.externalID = externalID;
        this.prefLabel  = prefLabel;
    }

    public String getPropertyValue(final String property) {
        if (property != null) {
            if (property.equals("http://www.w3.org/2004/02/skos/core#definition") || property.equals("definition")) {
                return definition;
            }
            if (property.equals("http://www.w3.org/2004/02/skos/core#prefLabel") || property.equals("preferredLabel")) {
                return prefLabel;
            }
            if (property.equals("http://www.w3.org/2004/02/skos/core#scopeNote") || property.equals("scopeNote")) {
                return scopeNote;
            }
            if ((property.equals("http://www.w3.org/2004/02/skos/core#altLabel") || property.equals("nonPreferredLabels")) && altLabel!= null && altLabel.size() > 0) {
                return altLabel.get(0);
            }
            if (property.equals("http://www.w3.org/2004/02/skos/core#example") || property.equals("example")) {
                return example;
            }
        }
        return null;
    }

    public Map<String, String> getRelations() {
        Map<String, String> response = new HashMap<String, String>();
        if (narrower != null) {
            for (Concept naro : narrower) {
                response.put(naro.resource, "http://www.w3.org/2004/02/skos/core#narrower");
            }
        }
        if (narrowerTransitive != null) {
            for (Concept naro : narrowerTransitive) {
                response.put(naro.resource, "http://www.w3.org/2004/02/skos/core#narrowerTransitive");
            }
        }
        if (broader != null) {
            for (Concept bro : broader) {
                response.put(bro.resource, "http://www.w3.org/2004/02/skos/core#broader");
            }
        }
        if (related != null) {
            response.put(related.resource, "http://www.w3.org/2004/02/skos/core#related");
        }
        return response;
    }

    public List<String> getRelations(final String property) {
        final List<String> result = new ArrayList<String>();
        if ("http://www.w3.org/2004/02/skos/core#narrower".equals(property)) {
            if (narrower != null) {
                for (Concept c : narrower) {
                    result.add(c.resource);
                }
            }
        }
        if ("http://www.w3.org/2004/02/skos/core#narrowerTransitive".equals(property)) {
            if (narrowerTransitive != null) {
                for (Concept c : narrowerTransitive) {
                    result.add(c.resource);
                }
            }
        }
        if ("http://www.w3.org/2004/02/skos/core#broader".equals(property)) {
            if (broader != null) {
                for (Concept c : broader) {
                    result.add(c.resource);
                }
            }
        }
        if ("http://www.w3.org/2004/02/skos/core#related".equals(property)) {
            if (related != null) {
                result.add(related.resource);
            }
        }
        return result;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(final String about) {
        this.about = about;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(final String externalID) {
        this.externalID = externalID;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(final String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<String> getAltLabel() {
        if (altLabel == null) {
            altLabel = new ArrayList<String>();
        }
        return altLabel;
    }

    public void setAltLabel(final List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public void addAltLabel(final String altLabel) {
        if (this.altLabel == null) {
            this.altLabel = new ArrayList<String>();
        }
        this.altLabel.add(altLabel);
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * @return the rights
     */
    public String getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(final String rights) {
        this.rights = rights;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the issued
     */
    public String getIssued() {
        return issued;
    }

    /**
     * @param issued the issued to set
     */
    public void setIssued(final String issued) {
        this.issued = issued;
    }

    /**
     * @return the modified
     */
    public String getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(final String modified) {
        this.modified = modified;
    }

    /**
     * @return the type
     */
    public Concept getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final Concept type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(final String creator) {
        this.creator = creator;
    }

    /**
     * @return the broader
     */
    public List<Concept> getBroader() {
        if (broader == null) {
            broader = new ArrayList<Concept>();
        }
        return broader;
    }

    /**
     * @param broader the broader to set
     */
    public void setBroader(final List<Concept> broader) {
        this.broader = broader;
    }

    /**
     * @param broader the broader to add
     */
    public void addBroader(final Concept broader) {
        if (this.broader == null) {
            this.broader = new ArrayList<Concept>();
        }
        if (broader != null) {
            this.broader.add(broader);
        }
    }

    /**
     * @return the changeNote
     */
    public String getChangeNote() {
        return changeNote;
    }

    /**
     * @param changeNote the changeNote to set
     */
    public void setChangeNote(final String changeNote) {
        this.changeNote = changeNote;
    }

    /**
     * @return the narrower
     */
    public List<Concept> getNarrower() {
        return narrower;
    }

    /**
     * @param narrower the narrower to set
     */
    public void setNarrower(final List<Concept> narrower) {
        this.narrower = narrower;
    }

    /**
     * @param broader the broader to add
     */
    public void addNarrower(final Concept narrower) {
        if (this.narrower == null) {
            this.narrower = new ArrayList<Concept>();
        }
        if (narrower != null) {
            this.narrower.add(narrower);
        }
    }

    /**
     * @return the narrowerTransitive
     */
    public List<Concept> getNarrowerTransitive() {
        return narrowerTransitive;
    }

    /**
     * @param narrowerTransitive the narrowerTransitive to set
     */
    public void setNarrowerTransitive(List<Concept> narrowerTransitive) {
        this.narrowerTransitive = narrowerTransitive;
    }
    
    /**
     * @param broader the broader to add
     */
    public void addNarrowerTransitive(final Concept narrower) {
        if (this.narrowerTransitive == null) {
            this.narrowerTransitive = new ArrayList<Concept>();
        }
        if (narrower != null) {
            this.narrowerTransitive.add(narrower);
        }
    }
    
    /**
     * @return the related
     */
    public Concept getRelated() {
        return related;
    }

    /**
     * @param related the related to set
     */
    public void setRelated(final Concept related) {
        this.related = related;
    }

    /**
     * @return the scopeNote
     */
    public String getScopeNote() {
        return scopeNote;
    }

    /**
     * @param scopeNote the scopeNote to set
     */
    public void setScopeNote(final String scopeNote) {
        this.scopeNote = scopeNote;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the example
     */
    public String getExample() {
        return example;
    }

    /**
     * @param example the example to set
     */
    public void setExample(final String example) {
        this.example = example;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(final String resource) {
        this.resource = resource;
    }
    
    /**
     * @return the hierarchyRoot
     */
    public Boolean getHierarchyRoot() {
        return hierarchyRoot;
    }

    /**
     * @param hierarchyRoot the hierarchyRoot to set
     */
    public void setHierarchyRoot(Boolean hierarchyRoot) {
        this.hierarchyRoot = hierarchyRoot;
    }

    /**
     * @return the hierarchyRootType
     */
    public Concept getHierarchyRootType() {
        return hierarchyRootType;
    }

    /**
     * @param hierarchyRootType the hierarchyRootType to set
     */
    public void setHierarchyRootType(Concept hierarchyRootType) {
        this.hierarchyRootType = hierarchyRootType;
    }

    /**
     * @return the hasTopConcept
     */
    public List<Concept> getHasTopConcept() {
        return hasTopConcept;
    }

    /**
     * @param hasTopConcept the hasTopConcept to set
     */
    public void setHasTopConcept(final List<Concept> hasTopConcept) {
        this.hasTopConcept = hasTopConcept;
    }

    /**
     * @param broader the broader to add
     */
    public void addHasTopConcept(final Concept topConcept) {
        if (this.hasTopConcept == null) {
            this.hasTopConcept = new ArrayList<Concept>();
        }
        if (hasTopConcept != null) {
            this.hasTopConcept.add(topConcept);
        }
    }
    
    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the contributor
     */
    public String getContributor() {
        return contributor;
    }

    /**
     * @param contributor the contributor to set
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
     * @return the hasVersion
     */
    public String getHasVersion() {
        return hasVersion;
    }

    /**
     * @param hasVersion the hasVersion to set
     */
    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }
    
    /**
     * @return the geometry
     */
    public List<AbstractGMLType> getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(final List<AbstractGMLType> geometry) {
        this.geometry = geometry;
    }
    
    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Concept]:").append('\n');
        if (about != null)
            sb.append("about:").append(about).append('\n');
        if (altLabel != null) {
            sb.append("altLabel:").append('\n');
            for (String b : altLabel) {
                sb.append(b).append('\n');
            }
        }
        if (broader != null) {
            sb.append("broder:").append('\n');
            for (Concept b : broader) {
                sb.append(b).append('\n');
            }
        }
        if (changeNote != null)
            sb.append("changeNote:").append(changeNote).append('\n');
        if (creator != null)
            sb.append("creator:").append(creator).append('\n');
        if (date != null)
            sb.append("date:").append(date).append('\n');
        if (definition != null)
            sb.append("definition:").append(definition).append('\n');
        if (description != null)
            sb.append("description:").append(description).append('\n');
        if (externalID != null)
            sb.append("externalID:").append(externalID).append('\n');
        if (issued != null)
            sb.append("issued:").append(issued).append('\n');
        if (label != null)
            sb.append("label:").append(label).append('\n');
        if (language != null)
            sb.append("language:").append(language).append('\n');
        if (modified != null)
            sb.append("modified:").append(modified).append('\n');
        if (name != null)
            sb.append("name:").append(name).append('\n');
        if (narrower != null)
            sb.append("narrower:").append(narrower).append('\n');
        if (prefLabel != null)
            sb.append("prefLabel:").append(prefLabel).append('\n');
        if (related != null)
            sb.append("related:").append(related).append('\n');
        if (rights != null)
            sb.append("rights:").append(rights).append('\n');
        if (scopeNote != null)
            sb.append("scopeNote:").append(scopeNote).append('\n');
        if (title != null)
            sb.append("title:").append(title).append('\n');
        if (type != null)
            sb.append("type:").append(type).append('\n');
        if (value != null)
            sb.append("value:").append(value).append('\n');
        if (example != null)
            sb.append("example:").append(example).append('\n');
        if (geometry != null)
            sb.append("geometry:").append(geometry).append('\n');
        if (resource != null)
            sb.append("resource:").append(resource).append('\n');
        if (hierarchyRoot != null)
            sb.append("hierarchyRoot:").append(hierarchyRoot).append('\n');
        if (hierarchyRootType != null)
            sb.append("hierarchyRootType:").append(hierarchyRootType).append('\n');
        if (hasTopConcept != null) {
            sb.append("hasTopConcept:").append('\n');
            for (Concept b : hasTopConcept) {
                sb.append(b).append('\n');
            }
        }
        if (narrowerTransitive != null) {
            sb.append("narrowerTransitive:").append('\n');
            for (Concept b : narrowerTransitive) {
                sb.append(b).append('\n');
            }
        }
        if (subject != null)
            sb.append("subject:").append(hierarchyRootType).append('\n');
        if (contributor != null)
            sb.append("contributor:").append(contributor).append('\n');
        if (hasVersion != null)
            sb.append("hasVersion:").append(hasVersion).append('\n');
        if (count != null)
            sb.append("count:").append(count).append('\n');
        return sb.toString();
    }

    /*
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Concept) {
            final Concept that = (Concept) object;
            return Utilities.equals(this.about,              that.about)       &&
                   Utilities.equals(this.resource,           that.resource)    &&
                   Utilities.equals(this.hierarchyRoot,      that.hierarchyRoot)    &&
                   Utilities.equals(this.hierarchyRootType,  that.hierarchyRootType)    &&
                   Utilities.equals(this.hasTopConcept,      that.hasTopConcept)    &&
                   Utilities.equals(this.narrowerTransitive, that.narrowerTransitive)    &&
                   Utilities.equals(this.subject,            that.subject)    &&
                   Utilities.equals(this.contributor,        that.contributor)    &&
                   Utilities.equals(this.hasVersion,         that.hasVersion)    &&
                   Utilities.equals(this.altLabel,           that.altLabel)    &&
                   Utilities.equals(this.broader,            that.broader)     &&
                   Utilities.equals(this.changeNote,         that.changeNote)  &&
                   Utilities.equals(this.creator,            that.creator)     &&
                   Utilities.equals(this.date,               that.date)        &&
                   Utilities.equals(this.definition,         that.definition)  &&
                   Utilities.equals(this.description,        that.description) &&
                   Utilities.equals(this.externalID,         that.externalID)  &&
                   Utilities.equals(this.issued,             that.issued)      &&
                   Utilities.equals(this.label,              that.label)       &&
                   Utilities.equals(this.language,           that.language)    &&
                   Utilities.equals(this.modified,           that.modified)    &&
                   Utilities.equals(this.name,               that.name)        &&
                   Utilities.equals(this.narrower,           that.narrower)    &&
                   Utilities.equals(this.prefLabel,          that.prefLabel)   &&
                   Utilities.equals(this.related,            that.related)     &&
                   Utilities.equals(this.rights,             that.rights)      &&
                   Utilities.equals(this.scopeNote,          that.scopeNote)   &&
                   Utilities.equals(this.title,              that.title)       &&
                   Utilities.equals(this.type,               that.type)        &&
                   Utilities.equals(this.example,            that.example)     &&
                   Utilities.equals(this.geometry,           that.geometry)    &&
                   Utilities.equals(this.count,              that.count)       &&
                   Utilities.equals(this.value,              that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.about != null ? this.about.hashCode() : 0);
        hash = 47 * hash + (this.resource != null ? this.resource.hashCode() : 0);
        hash = 47 * hash + (this.externalID != null ? this.externalID.hashCode() : 0);
        return hash;
    }
}
