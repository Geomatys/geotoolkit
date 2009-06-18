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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Concept", 
namespace = "http://www.w3.org/2004/02/skos/core#",
propOrder = {
    "externalID",
    "prefLabel",
    "altLabel",
    "definition",
    "date",
    "description",
    "language",
    "rights",
    "title",
    "issued",
    "type",
    "value",
    "label",
    "creator",
    "broader",
    "related",
    "scopeNote",
    "changeNote",
    "name",
    "narrower",
    "modified"

})
public class Concept {
    
    @XmlAttribute(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String about;

    @XmlElement(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String type;

    @XmlElement(namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String value;

    @XmlElement(namespace="http://www.w3.org/2000/01/rdf-schema#")
    private String label;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String externalID;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String prefLabel;
    
    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<String> altLabel;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String related;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String scopeNote;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String example;

    @XmlElement(namespace = "http://xmlns.com/foaf/0.1")
    private String name;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<String> broader;

    @XmlTransient
    private List<Concept> broaderConcept;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<String> narrower;

    @XmlTransient
    private List<Concept> narrowerConcept;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String definition;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String changeNote;

    @XmlElement(namespace="http://purl.org/dc/elements/1.1/")
    private String creator;

    @XmlTransient
    private Concept creatorConcept;

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

    @XmlElement(namespace="http://purl.org/dc/terms")
    private String issued;

    @XmlElement(namespace="http://purl.org/dc/terms")
    private String modified;

    



    public Concept() {
        
    }

    public Concept(String about) {
        this.about = about;
    }

    public Concept(String about, String prefLabel) {
        this.about     = about;
        this.prefLabel = prefLabel;
    }

    public Concept(String about, String externalID, String prefLabel, String altLabel, String definition, String date) {
        this.about      = about;
        this.altLabel   = Arrays.asList(altLabel);
        this.date       = date;
        this.definition = definition;
        this.externalID = externalID;
        this.prefLabel  = prefLabel;
    }

    public Concept(String about, String externalID, String prefLabel, List<String >altLabel, String definition, String date) {
        this.about      = about;
        this.altLabel   = altLabel;
        this.date       = date;
        this.definition = definition;
        this.externalID = externalID;
        this.prefLabel  = prefLabel;
    }

    public String getPropertyValue(String property) {
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
            for (String naro : narrower)
                response.put(naro, "http://www.w3.org/2004/02/skos/core#narrower");
        }
        if (broader != null) {
            for (String bro : broader)
                response.put(bro, "http://www.w3.org/2004/02/skos/core#broader");
        }

        if (related != null) {
            response.put(related, "http://www.w3.org/2004/02/skos/core#related");
        }
        return response;
    }

    public List<String> getRelations(String property) {
        if ("http://www.w3.org/2004/02/skos/core#narrower".equals(property)) {
            return narrower;
        }
        if ("http://www.w3.org/2004/02/skos/core#broader".equals(property)) {
            return broader;
        }

        if ("http://www.w3.org/2004/02/skos/core#related".equals(property)) {
            return Arrays.asList(related);
        }
        return null;
    }
    
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<String> getAltLabel() {
        if (altLabel == null) {
            altLabel = new ArrayList<String>();
        }
        return altLabel;
    }

    public void setAltLabel(List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public void addAltLabel(String altLabel) {
        if (this.altLabel == null) {
            this.altLabel = new ArrayList<String>();
        }
        this.altLabel.add(altLabel);
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
    public void setDescription(String description) {
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
    public void setLanguage(String language) {
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
    public void setRights(String rights) {
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
    public void setTitle(String title) {
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
    public void setIssued(String issued) {
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
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
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
    public void setValue(String value) {
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
    public void setLabel(String label) {
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
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the broader
     */
    public List<String> getBroader() {
        if (broader == null) {
            broader = new ArrayList<String>();
        }
        return broader;
    }

    /**
     * @param broader the broader to set
     */
    public void setBroader(List<String> broader) {
        this.broader = broader;
    }

    /**
     * @param broader the broader to add
     */
    public void addBroader(String broader) {
        if (this.broader == null) {
            this.broader = new ArrayList<String>();
        }
        if (broader != null)
            this.broader.add(broader);
    }

    /**
     * @return the broaderConcept
     */
    public List<Concept> getBroaderConcept() {
        return broaderConcept;
    }

    /**
     * @param broaderConcept the broaderConcept to set
     */
    public void setBroaderConcept(List<Concept> broaderConcept) {
        this.broaderConcept = broaderConcept;
    }

    /**
     * @return the creatorConcept
     */
    public Concept getCreatorConcept() {
        return creatorConcept;
    }

    /**
     * @param creatorConcept the creatorConcept to set
     */
    public void setCreatorConcept(Concept creatorConcept) {
        this.creatorConcept = creatorConcept;
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
    public void setChangeNote(String changeNote) {
        this.changeNote = changeNote;
    }

    /**
     * @return the narrower
     */
    public List<String> getNarrower() {
        return narrower;
    }

    /**
     * @param narrower the narrower to set
     */
    public void setNarrower(List<String> narrower) {
        this.narrower = narrower;
    }

    /**
     * @param broader the broader to add
     */
    public void addNarrower(String narrower) {
        if (this.narrower == null) {
            this.narrower = new ArrayList<String>();
        }
        if (narrower != null)
            this.narrower.add(narrower);
    }

    /**
     * @return the narrowerConcept
     */
    public List<Concept> getNarrowerConcept() {
        return narrowerConcept;
    }

    /**
     * @param narrowerConcept the narrowerConcept to set
     */
    public void setNarrowerConcept(List<Concept> narrowerConcept) {
        this.narrowerConcept = narrowerConcept;
    }

    /**
     * @return the related
     */
    public String getRelated() {
        return related;
    }

    /**
     * @param related the related to set
     */
    public void setRelated(String related) {
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
    public void setScopeNote(String scopeNote) {
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
    public void setName(String name) {
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
    public void setExample(String example) {
        this.example = example;
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
            for (String b : broader) {
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

            return Utilities.equals(this.about,       that.about)       &&
                   Utilities.equals(this.altLabel,    that.altLabel)    &&
                   Utilities.equals(this.broader,     that.broader)     &&
                   Utilities.equals(this.changeNote,  that.changeNote)  &&
                   Utilities.equals(this.creator,     that.creator)     &&
                   Utilities.equals(this.date,        that.date)        &&
                   Utilities.equals(this.definition,  that.definition)  &&
                   Utilities.equals(this.description, that.description) &&
                   Utilities.equals(this.externalID,  that.externalID)  &&
                   Utilities.equals(this.issued,      that.issued)      &&
                   Utilities.equals(this.label,       that.label)       &&
                   Utilities.equals(this.language,    that.language)    &&
                   Utilities.equals(this.modified,    that.modified)    &&
                   Utilities.equals(this.name,        that.name)        &&
                   Utilities.equals(this.narrower,    that.narrower)    &&
                   Utilities.equals(this.prefLabel,   that.prefLabel)   &&
                   Utilities.equals(this.related,     that.related)     &&
                   Utilities.equals(this.rights,      that.rights)      &&
                   Utilities.equals(this.scopeNote,   that.scopeNote)   &&
                   Utilities.equals(this.title,       that.title)       &&
                   Utilities.equals(this.type,        that.type)        &&
                   Utilities.equals(this.example,     that.example)     &&
                   Utilities.equals(this.value,       that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.about != null ? this.about.hashCode() : 0);
        hash = 47 * hash + (this.externalID != null ? this.externalID.hashCode() : 0);
        return hash;
    }

    
}
