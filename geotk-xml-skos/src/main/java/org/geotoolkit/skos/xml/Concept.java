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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.*;
import org.geotoolkit.gml.xml.v311.AbstractGMLType;

/**
 *
 * @author Guilhem Legal
 * @module
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
    "historyNote",
    "changeNote",
    "name",
    "narrower",
    "narrowerTransitive",
    "modified",
    "example",
    "inScheme",
    "geometry",
    "count"

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
    private List<Value> label;

    @XmlElement(namespace="http://semantic-web.at/ontologies/csw.owl#")
    private Boolean hierarchyRoot;

    @XmlElement(namespace="http://semantic-web.at/ontologies/csw.owl#")
    private Concept hierarchyRootType;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> hasTopConcept;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private String externalID;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> prefLabel;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> altLabel;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> related;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> scopeNote;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> historyNote;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> example;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> broader;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> narrower;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> narrowerTransitive;

    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Value> definition;

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
    private List<String> language;

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


    @XmlElement(namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> inScheme;

    public Concept() {

    }

    public Concept(final String about) {
        this.about = about;
    }

    /**
     * @deprecated use Concept(final {@link String} about, final {@link Value} prefLabel)
     */
    @Deprecated
    public Concept(final String about, final String prefLabel) {
        this.about     = about;
        this.prefLabel = new ArrayList<>();
        if (prefLabel != null) {
            this.prefLabel.add(new Value(prefLabel));
        }
    }

    public Concept(final String about, final Value prefLabel) {
        this.about     = about;
        this.prefLabel = new ArrayList<>();
        if (prefLabel != null) {
            this.prefLabel.add(prefLabel);
        }
    }

    /**
     * @deprecated use Concept(final {@link String} about, final {@link String} externalID, final {@link Value} prefLabel, final {@link Value} altLabel, final {@link Value} definition, final {@link String} date)
     */
    @Deprecated
    public Concept(final String about, final String externalID, final String prefLabel, final String altLabel, final String definition, final String date) {
        this.about      = about;
        this.altLabel = new ArrayList<>();
        if (altLabel != null) {
            this.altLabel.add(new Value(altLabel));
        }
        this.date       = date;
        this.definition = new ArrayList<>();
        if (definition != null) {
            this.definition.add(new Value(definition));
        }
        this.externalID = externalID;
        this.prefLabel = new ArrayList<>();
        if (prefLabel != null) {
            this.prefLabel.add(new Value(prefLabel));
        }
    }

    public Concept(final String about, final String externalID, final Value prefLabel, final Value altLabel, final Value definition, final String date) {
        this.about      = about;
        this.altLabel = new ArrayList<>();
        if (altLabel != null) {
            this.altLabel.add(altLabel);
        }
        this.date       = date;
        this.definition = new ArrayList<>();
        if (definition != null) {
            this.definition.add(definition);
        }
        this.externalID = externalID;
        this.prefLabel = new ArrayList<>();
        if (prefLabel != null) {
            this.prefLabel.add(prefLabel);
        }
    }

    public Concept(final String about, final String externalID, final Value prefLabel, final List<Value>altLabel, final Value definition, final String date) {
        this.about      = about;
        this.altLabel   = altLabel;
        this.date       = date;
        this.definition = new ArrayList<>();
        if (definition != null) {
            this.definition.add(definition);
        }
        this.externalID = externalID;
        this.prefLabel = new ArrayList<>();
        if (prefLabel != null) {
            this.prefLabel.add(prefLabel);
        }
    }

    public Concept(final String about, final String externalID, final List<Value> prefLabel, final List<Value>altLabel, final List<Value> definition, final String date) {
        this.about      = about;
        this.altLabel   = altLabel;
        this.date       = date;
        this.definition = definition;
        this.externalID = externalID;
        this.prefLabel  = prefLabel;
    }

    @Deprecated
    public Value getPropertyValue(final String property) {
        if (property != null) {
            if (property.equals("http://www.w3.org/2004/02/skos/core#definition") || property.equalsIgnoreCase("definition")) {
                if (definition != null && !definition.isEmpty()) {
                    return definition.get(0);
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#prefLabel") || property.equalsIgnoreCase("preferredLabel")) {
                if (prefLabel != null && !prefLabel.isEmpty()) {
                    return prefLabel.get(0);
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#scopeNote") || property.equalsIgnoreCase("scopeNote")) {
                if (scopeNote != null && !scopeNote.isEmpty()) {
                    return scopeNote.get(0);
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#altLabel") || property.equalsIgnoreCase("nonPreferredLabels")) {
                if (altLabel != null && !altLabel.isEmpty()) {
                    return altLabel.get(0);
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#example") || property.equalsIgnoreCase("example")) {
                if (example != null && !example.isEmpty()) {
                    return example.get(0);
                }
            }
        }
        return null;
    }

    public Value getPropertyValue(final String property, final String language) {
        if (property != null) {
            if (property.equals("http://www.w3.org/2004/02/skos/core#definition") || property.equalsIgnoreCase("definition")) {
                if (definition != null) {
                    for (Value v : definition) {
                        if (v.getLang() != null && v.getLang().equalsIgnoreCase(language)) {
                            return v;
                        }
                    }
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#prefLabel") || property.equalsIgnoreCase("preferredLabel")) {
                if (prefLabel != null) {
                    for (Value v : prefLabel) {
                        if (v.getLang() != null && v.getLang().equalsIgnoreCase(language)) {
                            return v;
                        }
                    }
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#scopeNote") || property.equalsIgnoreCase("scopeNote")) {
                if (scopeNote != null) {
                    for (Value v : scopeNote) {
                        if (v.getLang() != null && v.getLang().equalsIgnoreCase(language)) {
                            return v;
                        }
                    }
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#altLabel") || property.equalsIgnoreCase("nonPreferredLabels")) {
                if (altLabel != null) {
                    for (Value v : altLabel) {
                        if (v.getLang() != null && v.getLang().equalsIgnoreCase(language)) {
                            return v;
                        }
                    }
                }
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#example") || property.equalsIgnoreCase("example")) {
                if (example != null) {
                    for (Value v : example) {
                        if (v.getLang() != null && v.getLang().equalsIgnoreCase(language)) {
                            return v;
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<Value> getPropertyValues(final String property) {
        if (property != null) {
            if (property.equals("http://www.w3.org/2004/02/skos/core#definition") || property.equalsIgnoreCase("definition")) {
                return definition;
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#prefLabel") || property.equalsIgnoreCase("preferredLabel")) {
                return prefLabel;
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#scopeNote") || property.equalsIgnoreCase("scopeNote")) {
                return scopeNote;
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#altLabel") || property.equalsIgnoreCase("nonPreferredLabels")) {
                return altLabel;
            } else if (property.equals("http://www.w3.org/2004/02/skos/core#example") || property.equalsIgnoreCase("example")) {
                return example;
            }
        }
        return null;
    }

    public Map<String, String> getRelations() {
        Map<String, String> response = new HashMap<>();
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
            for (Concept rel : related) {
                response.put(rel.resource, "http://www.w3.org/2004/02/skos/core#related");
            }
        }
        return response;
    }

    public List<String> getRelations(final String property) {
        final List<String> result = new ArrayList<>();
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
                for (Concept c : related) {
                    result.add(c.resource);
                }
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

    public List<Value> getPrefLabel() {
        if (this.prefLabel == null) {
            this.prefLabel = new ArrayList<>();
        }
        return prefLabel;
    }

    public String getPrefLabel(final String language) {
        if (prefLabel != null) {
            for (Value v: prefLabel) {
                if (v.getLang() != null &&
                    v.getLang().equalsIgnoreCase(language)) {
                    return v.getValue();
                } else if (v.getLang() == null && language == null) {
                    return v.getValue();
                }
            }
        }
        return null;
    }

    @Deprecated
    public void setPrefLabel(final String prefLabel) {
        if (this.prefLabel == null) {
            this.prefLabel = new ArrayList<>();
        }
        this.prefLabel.add(new Value(prefLabel));
    }

    public void addPrefLabel(final Value prefLabel) {
        if (this.prefLabel == null) {
            this.prefLabel = new ArrayList<>();
        }
        this.prefLabel.add(prefLabel);
    }

    public void setPrefLabel(final List<Value> prefLabel) {
        this.prefLabel = prefLabel;
    }

    /**
     * @return the label
     */
    public List<Value> getLabel() {
        if (label == null) {
            this.label = new ArrayList<>();
        }
        return label;
    }

    public String getLabel(final String language) {
        if (label != null) {
            for (Value v: label) {
                if (v.getLang() != null &&
                    v.getLang().equalsIgnoreCase(language)) {
                    return v.getValue();
                } else if (v.getLang() == null && language == null) {
                    return v.getValue();
                }
            }
        }
        return null;
    }

    /**
     * @param label the label to set
     */
    @Deprecated
    public void setLabel(final String label) {
        if (this.label == null) {
            this.label = new ArrayList<>();
        }
        this.label.add(new Value(label));
    }

    public void addLabel(final Value label) {
        if (this.label == null) {
            this.label = new ArrayList<>();
        }
        this.label.add(label);
    }

    public void setLabel(final List<Value> label) {
        this.label = label;
    }

    public List<Value> getAltLabel() {
        if (altLabel == null) {
            altLabel = new ArrayList<>();
        }
        return altLabel;
    }

    public List<String> getAltLabel(final String language) {
        final List<String> response = new ArrayList<>();
        if (altLabel != null) {
            for (Value v: altLabel) {
                if (v.getLang() != null &&
                    v.getLang().equalsIgnoreCase(language)) {
                    response.add(v.getValue());
                } else if (v.getLang() == null && language == null) {
                    response.add(v.getValue());
                }
            }
        }
        return response;
    }

    public void setAltLabel(final List<Value> altLabel) {
        this.altLabel = altLabel;
    }

    @Deprecated
    public void addAltLabel(final String altLabel) {
        if (this.altLabel == null) {
            this.altLabel = new ArrayList<>();
        }
        this.altLabel.add(new Value(altLabel));
    }

    public void addAltLabel(final Value altLabel) {
        if (this.altLabel == null) {
            this.altLabel = new ArrayList<>();
        }
        this.altLabel.add(altLabel);
    }

    public List<Value> getDefinition() {
        if (this.definition == null) {
            this.definition = new ArrayList<>();
        }
        return definition;
    }

    public List<String> getDefinition(final String language) {
        final List<String> response = new ArrayList<>();
        if (definition != null) {
            for (Value v: definition) {
                if (v.getLang() != null &&
                    v.getLang().equalsIgnoreCase(language)) {
                    response.add(v.getValue());
                } else if (v.getLang() == null && language == null) {
                    response.add(v.getValue());
                }
            }
        }
        return response;
    }

    @Deprecated
    public void setDefinition(final String definition) {
        if (this.definition == null) {
            this.definition = new ArrayList<>();
        }
        this.definition.add(new Value(definition));
    }

    public void addDefinition(final Value definition) {
        if (this.definition == null) {
            this.definition = new ArrayList<>();
        }
        this.definition.add(definition);
    }

    public void setDefinition(final List<Value> definition) {
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
    public List<String> getLanguage() {
        return language;
    }

    public void addLanguage(final String language) {
        if (this.language == null) {
            this.language = new ArrayList<>();
        }
        this.language.add(language);
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final List<String> language) {
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

    public void setDefaultTypeIfNone() {
        if (this.type == null) {
            final Concept defaultType = new Concept();
            defaultType.setResource("http://www.w3.org/2004/02/skos/core#Concept");
            this.type = defaultType;
        }
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
            broader = new ArrayList<>();
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
            this.broader = new ArrayList<>();
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
        if (this.narrower == null) {
            this.narrower = new ArrayList<>();
        }
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
            this.narrower = new ArrayList<>();
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
            this.narrowerTransitive = new ArrayList<>();
        }
        if (narrower != null) {
            this.narrowerTransitive.add(narrower);
        }
    }

    /**
     * @return the related
     */
    public List<Concept> getRelated() {
        if (this.related == null) {
            this.related = new ArrayList<>();
        }
        return related;
    }

    /**
     * @param related the related to set
     */
    public void setRelated(final Concept related) {
        if (this.related == null) {
            this.related = new ArrayList<>();
        }
        this.related.add(related);
    }

    public void setRelated(final List<Concept> related) {
        this.related = related;
    }

     /**
     * @param broader the broader to add
     */
    public void addRelated(final Concept related) {
        if (this.related == null) {
            this.related = new ArrayList<>();
        }
        if (related != null) {
            this.related.add(related);
        }
    }

    /**
     * @return the scopeNote
     */
    public List<Value> getScopeNote() {
        if (this.scopeNote == null) {
            this.scopeNote = new ArrayList<>();
        }
        return scopeNote;
    }

    /**
     * @param scopeNote the scopeNote to set
     */
    @Deprecated
    public void setScopeNote(final String scopeNote) {
        if (this.scopeNote == null) {
            this.scopeNote = new ArrayList<>();
        }
        this.scopeNote.add(new Value(scopeNote));
    }

    public void addScopeNote(final Value scopeNote) {
        if (this.scopeNote == null) {
            this.scopeNote = new ArrayList<>();
        }
        this.scopeNote.add(scopeNote);
    }

    public void setScopeNote(final List<Value> scopeNote) {
        this.scopeNote = scopeNote;
    }

    /**
     * @return the scopeNote
     */
    public List<Value> getHistoryNote() {
        if (this.historyNote == null) {
            this.historyNote = new ArrayList<>();
        }
        return historyNote;
    }

    public void addHistoryNote(final Value historyNote) {
        if (this.historyNote == null) {
            this.historyNote = new ArrayList<>();
        }
        this.historyNote.add(historyNote);
    }

    public void setHistoryNote(final List<Value> historyNote) {
        this.historyNote = historyNote;
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
    public List<Value> getExample() {
        if (this.example == null) {
            this.example = new ArrayList<>();
        }
        return example;
    }

    /**
     * @param example the example to set
     */
    @Deprecated
    public void setExample(final String example) {
        if (this.example == null) {
            this.example = new ArrayList<>();
        }
        this.example.add(new Value(example));
    }

    public void addExample(final Value example) {
        if (this.example == null) {
            this.example = new ArrayList<>();
        }
        this.example.add(example);
    }

    public void setExample(final List<Value> example) {
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
        if (this.hasTopConcept == null) {
            this.hasTopConcept = new ArrayList<>();
        }
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
            this.hasTopConcept = new ArrayList<>();
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

    /**
     * @return the theme
     */
    public List<Concept> getInScheme() {
        if (this.inScheme == null) {
            this.inScheme = new ArrayList<>();
        }
        return inScheme;
    }

    /**
     * @param theme the theme to set
     */
    public void setInScheme(final List<Concept> inscheme) {
        this.inScheme = inscheme;
    }

    public void addInScheme(final Concept inScheme) {
        if (this.inScheme == null) {
            this.inScheme = new ArrayList<>();
        }
        if (inScheme != null) {
            this.inScheme.add(inScheme);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Concept]:").append('\n');
        if (about != null)
            sb.append("about:").append(about).append('\n');
        if (altLabel != null) {
            sb.append("altLabel:").append('\n');
            for (Value b : altLabel) {
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
        if (historyNote != null)
            sb.append("historyNote:").append(historyNote).append('\n');
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
        if (inScheme != null) {
            sb.append("EXT:theme:").append(inScheme).append('\n');
        }
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
            return Objects.equals(this.about,              that.about)       &&
                   Objects.equals(this.resource,           that.resource)    &&
                   Objects.equals(this.hierarchyRoot,      that.hierarchyRoot)    &&
                   Objects.equals(this.hierarchyRootType,  that.hierarchyRootType)    &&
                   Objects.equals(this.hasTopConcept,      that.hasTopConcept)    &&
                   Objects.equals(this.narrowerTransitive, that.narrowerTransitive)    &&
                   Objects.equals(this.subject,            that.subject)    &&
                   Objects.equals(this.contributor,        that.contributor)    &&
                   Objects.equals(this.hasVersion,         that.hasVersion)    &&
                   Objects.equals(this.altLabel,           that.altLabel)    &&
                   Objects.equals(this.broader,            that.broader)     &&
                   Objects.equals(this.changeNote,         that.changeNote)  &&
                   Objects.equals(this.creator,            that.creator)     &&
                   Objects.equals(this.date,               that.date)        &&
                   Objects.equals(this.definition,         that.definition)  &&
                   Objects.equals(this.description,        that.description) &&
                   Objects.equals(this.externalID,         that.externalID)  &&
                   Objects.equals(this.issued,             that.issued)      &&
                   Objects.equals(this.label,              that.label)       &&
                   Objects.equals(this.language,           that.language)    &&
                   Objects.equals(this.modified,           that.modified)    &&
                   Objects.equals(this.name,               that.name)        &&
                   Objects.equals(this.narrower,           that.narrower)    &&
                   Objects.equals(this.prefLabel,          that.prefLabel)   &&
                   Objects.equals(this.related,            that.related)     &&
                   Objects.equals(this.rights,             that.rights)      &&
                   Objects.equals(this.scopeNote,          that.scopeNote)   &&
                   Objects.equals(this.historyNote,        that.historyNote) &&
                   Objects.equals(this.title,              that.title)       &&
                   Objects.equals(this.type,               that.type)        &&
                   Objects.equals(this.example,            that.example)     &&
                   Objects.equals(this.geometry,           that.geometry)    &&
                   Objects.equals(this.count,              that.count)       &&
                   Objects.equals(this.inScheme,           that.inScheme)    &&
                   Objects.equals(this.value,              that.value);
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
