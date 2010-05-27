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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DCMIRecord;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.util.Utilities;


/**
 * 
 * This type encapsulates all of the standard DCMI metadata terms,
 * including the Dublin Core refinements; these terms may be mapped
 * to the profile-specific information model.
 *          
 * 
 * <p>Java class for DCMIRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DCMIRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;group ref="{http://purl.org/dc/terms/}DCMI-terms"/>
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
@XmlType(name = "DCMIRecordType", propOrder = {
    "identifier"  ,
    "title"       ,
    "type"        ,
    "subject"     ,
    "format"      ,
    "language"    ,
    "distributor" ,
    "creator"     ,
    "modified"    ,
    "date"        ,    
    "_abstract"   ,
    "references"  ,
    "spatial"     ,
    "relation"    ,
    "rights"      ,
    "source"      ,
    "coverage"    ,
    "publisher"   ,
    "contributor" ,
    "description" ,
    "temporal"    ,
    "mediator",
    "isRequiredBy",
    "audience",
    "extent",
    "tableOfContents",
    "hasVersion",
    "requires",
    "dateSubmitted",
    "license",
    "conformsTo",
    "bibliographicCitation",
    "isVersionOf",
    "available",
    "accessRights",
    "isPartOf",
    "valid",
    "educationLevel",
    "replaces",
    "issued",
    "created",
    "hasPart",
    "provenance",
    "isReplacedBy",
    "alternative",
    "rightsHolder",
    "dateCopyrighted",
    "medium",
    "dateAccepted",
    "isFormatOf",
    "hasFormat",
    "isReferencedBy",
    "dcElement"   
})
@XmlSeeAlso({
    RecordType.class
})
@XmlRootElement(name="DCMIRecord") 
public class DCMIRecordType extends AbstractRecordType implements DCMIRecord {

    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral identifier;
    
    @XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral title;
    
    @XmlElement(name = "type", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral type;
    
    @XmlElement(name = "subject", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> subject;
    
    @XmlElement(name = "format", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> format;
    
    @XmlElement(name = "language", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral language;
    
    @XmlElement(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral distributor;
    
    @XmlElement(name = "creator", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> creator;
    
    @XmlElementRef(name = "DC-element", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    private List<JAXBElement<SimpleLiteral>> dcElement;

    @XmlElement(name = "modified", namespace = "http://purl.org/dc/terms/")
    private SimpleLiteral modified;
    
    @XmlElement(name = "date", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral date;
    
    @XmlElement(name = "abstract", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> _abstract;
    
    @XmlElement(name = "spatial", namespace = "http://purl.org/dc/terms/")
    private SimpleLiteral spatial;
    
    @XmlElement(name = "references", namespace = "http://purl.org/dc/terms/")
    private SimpleLiteral references;

    @XmlElement(name = "relation", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> relation;

    @XmlElement(name = "rights", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> rights;

    @XmlElement(name = "source", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> source;

    @XmlElement(name = "coverage", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> coverage;

    @XmlElement(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> publisher;

    @XmlElement(name = "contributor", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> contributor;

    @XmlElement(name = "description", namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral description;

    @XmlElement(name = "temporal", namespace = "http://purl.org/dc/terms/")
    private SimpleLiteral temporal;

    @XmlElement(name = "mediator", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> mediator;

    @XmlElement(name = "isRequiredBy", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isRequiredBy;

    @XmlElement(name = "audience", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> audience;

    @XmlElement(name = "extent", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> extent;

    @XmlElement(name = "tableOfContents", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> tableOfContents;

    @XmlElement(name = "hasVersion", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> hasVersion;

    @XmlElement(name = "requires", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> requires;

    @XmlElement(name = "dateSubmitted", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> dateSubmitted;

    @XmlElement(name = "license", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> license;

    @XmlElement(name = "conformsTo", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> conformsTo;

    @XmlElement(name = "bibliographicCitation", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> bibliographicCitation;

    @XmlElement(name = "isVersionOf", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isVersionOf;

    @XmlElement(name = "available", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> available;

    @XmlElement(name = "accessRights", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> accessRights;

    @XmlElement(name = "isPartOf", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isPartOf;

    @XmlElement(name = "valid", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> valid;

    @XmlElement(name = "educationLevel", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> educationLevel;

    @XmlElement(name = "replaces", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> replaces;

    @XmlElement(name = "issued", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> issued;

    @XmlElement(name = "created", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> created;

    @XmlElement(name = "hasPart", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> hasPart;

    @XmlElement(name = "provenance", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> provenance;

    @XmlElement(name = "isReplacedBy", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isReplacedBy;

    @XmlElement(name = "alternative", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> alternative;

    @XmlElement(name = "rightsHolder", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> rightsHolder;

    @XmlElement(name = "dateCopyrighted", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> dateCopyrighted;

    @XmlElement(name = "medium", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> medium;

    @XmlElement(name = "dateAccepted", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> dateAccepted;

    @XmlElement(name = "isFormatOf", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isFormatOf;

    @XmlElement(name = "hasFormat", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> hasFormat;

    @XmlElement(name = "isReferencedBy", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> isReferencedBy;

    
    /**
     * An empty constructor used by JAXB
     */
    DCMIRecordType() {
        this.dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
    }
        
    
    public DCMIRecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, 
            List<SimpleLiteral> subjects, SimpleLiteral format, SimpleLiteral modified, SimpleLiteral date, SimpleLiteral _abstract,
            SimpleLiteral creator, SimpleLiteral distributor, SimpleLiteral language, SimpleLiteral spatial, 
            SimpleLiteral references) {
        
        this.identifier = identifier;
        this.title      = title;
        this.type       = type;
        if (format != null) {
            this.format     = Arrays.asList(format);
        }
        this.date       = date;
        
        this.dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
        
        this.subject     = subjects;
        if (creator != null) {
            this.creator     = Arrays.asList(creator);
        }
        this.distributor = distributor;
        this.language    = language;
        this.modified    = modified;
        if (_abstract != null) {
            this._abstract   = Arrays.asList(_abstract);
        }
        this.spatial     = spatial;
        this.references  = references;
        
    }
    
    public DCMIRecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, 
            List<SimpleLiteral> subjects, List<SimpleLiteral> format, SimpleLiteral modified, SimpleLiteral date, List<SimpleLiteral> _abstract,
            List<SimpleLiteral> creator, SimpleLiteral distributor, SimpleLiteral language, SimpleLiteral spatial, 
            SimpleLiteral references, List<SimpleLiteral> relation) {
        
        this.identifier = identifier;
        this.title      = title;
        this.type       = type;
        this.format     = format;
        this.date       = date;
        
        this.dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
        
        this.subject     = subjects;
        this.creator     = creator;
        this.distributor = distributor;
        this.language    = language;
        this.modified    = modified;
        this._abstract   = _abstract;
        this.spatial     = spatial;
        this.references  = references;
        this.relation    = relation;
        
    }
    
    /**
     * Gets the value of the dcElement property.
     * (unModifiable)
     */
    public List<JAXBElement<SimpleLiteral>> getDCElement() {
        if (dcElement == null) {
            dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
        }
        return Collections.unmodifiableList(dcElement);
    }
    
    public void setIdentifier(SimpleLiteral identifier) {
        this.identifier = identifier;
    }
    
    public SimpleLiteral getIdentifier() {
        return identifier;
    }
    
    public void setTitle(SimpleLiteral title) {
        this.title = title;
    }
    
    public SimpleLiteral getTitle() {
        return title;
    }
    
    public void setType(SimpleLiteral type) {
        this.type = type;
    }
    
    public SimpleLiteral getType() {
        return type;
    }
    
    public void setSubject(List<SimpleLiteral> subjects) {
        this.subject = subjects;
    }
    
    public void setSubject(SimpleLiteral subject) {
        if (this.subject == null) {
            this.subject = new ArrayList<SimpleLiteral>();
        }
        this.subject.add(subject);
    }
    
    public List<SimpleLiteral> getSubject() {
        if (subject == null) {
            subject = new ArrayList<SimpleLiteral>();
        }
        return subject;
    }
    
    public void setFormat(SimpleLiteral format) {
        this.format = Arrays.asList(format);
    }
    
    public void setFormat(List<SimpleLiteral> format) {
        this.format = format;
    }
    
    public List<SimpleLiteral> getFormat() {
        return format;
    }
    
    public void setModified(SimpleLiteral modified) {
        this.modified = modified;
    }
    
    public SimpleLiteral getModified() {
        return modified;
    }
    
    public void setDate(SimpleLiteral date) {
        this.date = date;
    }
    
    public SimpleLiteral getDate() {
        return date;
    }
    
    public void setAbstract(SimpleLiteral _abstract) {
        this._abstract = Arrays.asList(_abstract);
    }
    
    public void setAbstract(List<SimpleLiteral> _abstract) {
        this._abstract =_abstract;
    }
    
    public List<SimpleLiteral> getAbstract() {
        return _abstract;
    }
    
    public void setCreator(SimpleLiteral creator) {
        this.creator = Arrays.asList(creator);
    }
    
    public void setCreator(List<SimpleLiteral> creator) {
        this.creator = creator;
    }
    
    public List<SimpleLiteral> getCreator() {
        return creator;
    }
    
    public void setDistributor(SimpleLiteral distributor) {
        this.distributor = distributor;
    }
    
    public SimpleLiteral getDistributor() {
        return distributor;
    }
    
    public void setLanguage(SimpleLiteral language) {
        this.language = language;
    }
    
    public SimpleLiteral getLanguage() {
        return language;
    }
    
    public void setRelation(SimpleLiteral relation) {
        if (this.relation == null)
            this.relation = new ArrayList<SimpleLiteral>();
        this.relation.add(relation);
    }

    public void setRelation(List<SimpleLiteral> relation) {
        this.relation = relation;
    }
    
    public List<SimpleLiteral> getRelation() {
        return relation;
    }
    
    public void setSource(SimpleLiteral source) {
        if (this.source == null)
            this.source = new ArrayList<SimpleLiteral>();
        this.source.add(source);
    }

    public void setSource(List<SimpleLiteral> source) {
        this.source = source;
    }
    
    public List<SimpleLiteral> getSource() {
       return source;
    }
    
    public void setCoverage(SimpleLiteral coverage) {
        if (this.coverage == null)
            this.coverage = new ArrayList<SimpleLiteral>();
        this.coverage.add(coverage);
    }

    public void setCoverage(List<SimpleLiteral> coverage) {
        this.coverage = coverage;
    }
    
    public List<SimpleLiteral> getCoverage() {
        return coverage;
    }
    
    public void setRights(SimpleLiteral rights) {
        if (this.rights == null) {
            this.rights = new ArrayList<SimpleLiteral>();
        }
        if (rights != null) {
            this.rights.add(rights);
        }
    }

    public void setRights(List<SimpleLiteral> rights) {
        this.rights = rights;
    }
    
    public List<SimpleLiteral> getRights() {
        return rights;
    }
    
    public void setSpatial(SimpleLiteral spatial) {
        this.spatial = spatial;
    }
    
    public SimpleLiteral getSpatial() {
         return spatial;
    }
    
    public void setReferences(SimpleLiteral references) {
        this.references = references;
    }
    
    public SimpleLiteral getReferences() {
        return references;
    }
    
    public void setPublisher(List<SimpleLiteral> publisher) {
        this.publisher = publisher;
    }

    public void setPublisher(SimpleLiteral publisher) {
        if (this.publisher == null)
            this.publisher = new ArrayList<SimpleLiteral>();
        this.publisher.add(publisher);
    }
    
    public List<SimpleLiteral> getPublisher() {
        return publisher;
    }
    
    public void setContributor(List<SimpleLiteral> contributor) {
        this.contributor = contributor;
    }

    public void setContributor(SimpleLiteral contributor) {
        if (this.contributor == null)
            this.contributor = new ArrayList<SimpleLiteral>();
        this.contributor.add(contributor);
    }
    
    public List<SimpleLiteral> getContributor() {
        return contributor;
    }
    
    public void setDescription(SimpleLiteral description) {
        this.description = description;
    }
    
    public SimpleLiteral getDescription() {
        return description;
    }

    public void setTemporal(SimpleLiteral temporal) {
        this.temporal = temporal;
    }

    public SimpleLiteral getTemporal() {
        return temporal;
    }
    
    /**
     * if the attribute have not been fill by JAXB we search in DCelement
     */
    public SimpleLiteral getAttributeFromDCelement(String name) {
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals(name)) {
                return jb.getValue();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']');
        if (identifier != null) {
            s.append("identifier: ").append(identifier).append('\n');
        }
        if (title != null) {
            s.append("title: ").append(title).append('\n');
        }
        if (type != null) {
            s.append("type: ").append(type).append('\n');
        }
        if (format != null) {
            s.append("format: ").append(format).append('\n');
        }
        if (subject != null) {
            s.append("subjects: ").append('\n');
            for (SimpleLiteral sl: subject) {
                s.append(sl).append('\n');
            }
        }
        if (distributor != null) {
            s.append("distributor: ").append(distributor).append('\n');
        }
        if (date != null) {
            s.append("date: ").append(date).append('\n');
        }
        if (contributor != null) {
            s.append("contributor: ").append(contributor).append('\n');
        }
        if (coverage != null) {
            s.append("coverage: ").append(coverage).append('\n');
        }
        if (creator != null) {
            s.append("creator: ").append(creator).append('\n');
        }
        if (description != null) {
            s.append("description: ").append(description).append('\n');
        }
        if (language != null) {
            s.append("language: ").append(language).append('\n');
        }
        if (modified != null) {
            s.append("modified: ").append(modified).append('\n');
        }
        if (_abstract != null) {
            s.append("abstract: ").append(_abstract).append('\n');
        }
        if (source != null) {
            s.append("source: ").append(source).append('\n');
        }
        if (spatial != null) {
            s.append("spatial: ").append(spatial).append('\n');
        }
        if (references != null) {
            s.append("references: ").append(references).append('\n');
        }
        if (relation != null) {
            s.append("relation: ").append(relation).append('\n');
        }
        if (rights != null) {
            s.append("rights: ").append(rights).append('\n');
        }
        if (temporal != null) {
            s.append("temporal: ").append(temporal).append('\n');
        }
        if (dcElement != null) {
            for (JAXBElement<SimpleLiteral> jb: dcElement) {
                s.append("name=").append(jb.getName()).append(" value=").append(jb.getValue().toString()).append('\n');
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
        if (object instanceof DCMIRecordType) {
            final DCMIRecordType that = (DCMIRecordType) object;

            boolean dcelement = this.dcElement.size() == that.dcElement.size();
        
            //we verify that the two list contains the same object
            List<SimpleLiteral> obj = new ArrayList<SimpleLiteral>();
            for (JAXBElement<SimpleLiteral> jb: dcElement) {
                obj.add(jb.getValue());
            }
        
            for (JAXBElement<SimpleLiteral> jb: that.dcElement) {
                if (!obj.contains(jb.getValue())) {
                    dcelement = false;
                }
            }
            return Utilities.equals(this._abstract,   that._abstract)   &&
                   Utilities.equals(this.creator  ,   that.creator)     &&
                   Utilities.equals(this.contributor, that.contributor) &&
                   Utilities.equals(this.coverage,    that.coverage)    &&
                   Utilities.equals(this.date,        that.date)        &&
                   Utilities.equals(this.description, that.description) &&
                   Utilities.equals(this.distributor, that.distributor) &&
                   Utilities.equals(this.format,      that.format)      &&
                   Utilities.equals(this.identifier,  that.identifier)  &&
                   Utilities.equals(this.language,    that.language)    &&
                   Utilities.equals(this.modified,    that.modified)    &&
                   Utilities.equals(this.publisher,   that.publisher)   &&
                   Utilities.equals(this.references,  that.references)  &&
                   Utilities.equals(this.relation,    that.relation)    &&
                   Utilities.equals(this.rights,      that.rights)      &&
                   Utilities.equals(this.source,      that.source)      &&
                   Utilities.equals(this.spatial,     that.spatial)     &&
                   Utilities.equals(this.subject,     that.subject)     &&
                   Utilities.equals(this.title,       that.title)       &&
                   Utilities.equals(this.type,        that.type)        &&
                   Utilities.equals(this.temporal,    that.temporal)    &&
                   dcelement;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }

    /**
     * @return the mediator
     */
    public List<SimpleLiteral> getMediator() {
        return mediator;
    }

    /**
     * @param mediator the mediator to set
     */
    public void setMediator(List<SimpleLiteral> mediator) {
        this.mediator = mediator;
    }

    /**
     * @param mediator the mediator to set
     */
    public void setMediator(SimpleLiteral mediator) {
         if (this.mediator == null)
            this.mediator = new ArrayList<SimpleLiteral>();
        this.mediator.add(mediator);
    }

    /**
     * @return the isRequiredBy
     */
    public List<SimpleLiteral> getIsRequiredBy() {
        return isRequiredBy;
    }

    /**
     * @param isRequiredBy the isRequiredBy to set
     */
    public void setIsRequiredBy(List<SimpleLiteral> isRequiredBy) {
        this.isRequiredBy = isRequiredBy;
    }

    /**
     * @param isRequiredBy the isRequiredBy to set
     */
    public void setIsRequiredBy(SimpleLiteral isRequiredBy) {
         if (this.isRequiredBy == null)
            this.isRequiredBy = new ArrayList<SimpleLiteral>();
        this.isRequiredBy.add(isRequiredBy);
    }

    /**
     * @return the audience
     */
    public List<SimpleLiteral> getAudience() {
        return audience;
    }

    /**
     * @param audience the audience to set
     */
    public void setAudience(List<SimpleLiteral> audience) {
        this.audience = audience;
    }

    /**
     * @param audience the audience to set
     */
    public void setAudience(SimpleLiteral audience) {
         if (this.audience == null)
            this.audience = new ArrayList<SimpleLiteral>();
        this.audience.add(audience);
    }

    /**
     * @return the extent
     */
    public List<SimpleLiteral> getExtent() {
        return extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(List<SimpleLiteral> extent) {
        this.extent = extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(SimpleLiteral extent) {
         if (this.extent == null)
            this.extent = new ArrayList<SimpleLiteral>();
        this.extent.add(extent);
    }

    /**
     * @return the tableOfContents
     */
    public List<SimpleLiteral> getTableOfContents() {
        return tableOfContents;
    }

    /**
     * @param tableOfContents the tableOfContents to set
     */
    public void setTableOfContents(List<SimpleLiteral> tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    /**
     * @param tableOfContents the tableOfContents to set
     */
    public void setTableOfContents(SimpleLiteral tableOfContents) {
         if (this.tableOfContents == null)
            this.tableOfContents = new ArrayList<SimpleLiteral>();
        this.tableOfContents.add(tableOfContents);
    }

    /**
     * @return the hasVersion
     */
    public List<SimpleLiteral> getHasVersion() {
        return hasVersion;
    }

    /**
     * @param hasVersion the hasVersion to set
     */
    public void setHasVersion(List<SimpleLiteral> hasVersion) {
        this.hasVersion = hasVersion;
    }

    /**
     * @param hasVersion the hasVersion to set
     */
    public void setHasVersion(SimpleLiteral hasVersion) {
         if (this.hasVersion == null)
            this.hasVersion = new ArrayList<SimpleLiteral>();
        this.hasVersion.add(hasVersion);
    }

    /**
     * @return the requires
     */
    public List<SimpleLiteral> getRequires() {
        return requires;
    }

    /**
     * @param requires the requires to set
     */
    public void setRequires(List<SimpleLiteral> requires) {
        this.requires = requires;
    }

    /**
     * @param requires the requires to set
     */
    public void setRequires(SimpleLiteral requires) {
         if (this.requires == null)
            this.requires = new ArrayList<SimpleLiteral>();
        this.requires.add(requires);
    }

    /**
     * @return the dateSubmitted
     */
    public List<SimpleLiteral> getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * @param dateSubmitted the dateSubmitted to set
     */
    public void setDateSubmitted(List<SimpleLiteral> dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * @param dateSubmitted the dateSubmitted to set
     */
    public void setDateSubmitted(SimpleLiteral dateSubmitted) {
         if (this.dateSubmitted == null)
            this.dateSubmitted = new ArrayList<SimpleLiteral>();
        this.dateSubmitted.add(dateSubmitted);
    }

    /**
     * @return the license
     */
    public List<SimpleLiteral> getLicense() {
        return license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(List<SimpleLiteral> license) {
        this.license = license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(SimpleLiteral license) {
         if (this.license == null)
            this.license = new ArrayList<SimpleLiteral>();
        this.license.add(license);
    }

    /**
     * @return the conformsTo
     */
    public List<SimpleLiteral> getConformsTo() {
        return conformsTo;
    }

    /**
     * @param conformsTo the conformsTo to set
     */
    public void setConformsTo(List<SimpleLiteral> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * @param conformsTo the conformsTo to set
     */
    public void setConformsTo(SimpleLiteral conformsTo) {
         if (this.conformsTo == null)
            this.conformsTo = new ArrayList<SimpleLiteral>();
        this.conformsTo.add(conformsTo);
    }

    /**
     * @return the bibliographicCitation
     */
    public List<SimpleLiteral> getBibliographicCitation() {
        return bibliographicCitation;
    }

    /**
     * @param bibliographicCitation the bibliographicCitation to set
     */
    public void setBibliographicCitation(List<SimpleLiteral> bibliographicCitation) {
        this.bibliographicCitation = bibliographicCitation;
    }

    /**
     * @param bibliographicCitation the bibliographicCitation to set
     */
    public void setBibliographicCitation(SimpleLiteral bibliographicCitation) {
         if (this.bibliographicCitation == null)
            this.bibliographicCitation = new ArrayList<SimpleLiteral>();
        this.bibliographicCitation.add(bibliographicCitation);
    }

    /**
     * @return the isVersionOf
     */
    public List<SimpleLiteral> getIsVersionOf() {
        return isVersionOf;
    }

    /**
     * @param isVersionOf the isVersionOf to set
     */
    public void setIsVersionOf(List<SimpleLiteral> isVersionOf) {
        this.isVersionOf = isVersionOf;
    }

    /**
     * @param isVersionOf the isVersionOf to set
     */
    public void setIsVersionOf(SimpleLiteral isVersionOf) {
         if (this.isVersionOf == null)
            this.isVersionOf = new ArrayList<SimpleLiteral>();
        this.isVersionOf.add(isVersionOf);
    }

    /**
     * @return the available
     */
    public List<SimpleLiteral> getAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(List<SimpleLiteral> available) {
        this.available = available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(SimpleLiteral available) {
         if (this.available == null)
            this.available = new ArrayList<SimpleLiteral>();
        this.available.add(available);
    }

    /**
     * @return the accessRights
     */
    public List<SimpleLiteral> getAccessRights() {
        return accessRights;
    }

    /**
     * @param accessRights the accessRights to set
     */
    public void setAccessRights(List<SimpleLiteral> accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * @param accesRights the accesRights to set
     */
    public void setAccessRights(SimpleLiteral accessRights) {
         if (this.accessRights == null)
            this.accessRights = new ArrayList<SimpleLiteral>();
        this.accessRights.add(accessRights);
    }

    /**
     * @return the isPartOf
     */
    public List<SimpleLiteral> getIsPartOf() {
        return isPartOf;
    }

    /**
     * @param isPartOf the isPartOf to set
     */
    public void setIsPartOf(List<SimpleLiteral> isPartOf) {
        this.isPartOf = isPartOf;
    }

    /**
     * @param isPartOf the isPartOf to set
     */
    public void setIsPartOf(SimpleLiteral isPartOf) {
         if (this.isPartOf == null)
            this.isPartOf = new ArrayList<SimpleLiteral>();
        this.isPartOf.add(isPartOf);
    }

    /**
     * @return the valid
     */
    public List<SimpleLiteral> getValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(List<SimpleLiteral> valid) {
        this.valid = valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(SimpleLiteral valid) {
         if (this.valid == null)
            this.valid = new ArrayList<SimpleLiteral>();
        this.valid.add(valid);
    }

    /**
     * @return the educationLevel
     */
    public List<SimpleLiteral> getEducationLevel() {
        return educationLevel;
    }

    /**
     * @param educationLevel the educationLevel to set
     */
    public void setEducationLevel(List<SimpleLiteral> educationLevel) {
        this.educationLevel = educationLevel;
    }

    /**
     * @param eductionLevel the eductionLevel to set
     */
    public void setEducationLevel(SimpleLiteral educationLevel) {
         if (this.educationLevel == null)
            this.educationLevel = new ArrayList<SimpleLiteral>();
        this.educationLevel.add(educationLevel);
    }

    /**
     * @return the replaces
     */
    public List<SimpleLiteral> getReplaces() {
        return replaces;
    }

    /**
     * @param replaces the replaces to set
     */
    public void setReplaces(List<SimpleLiteral> replaces) {
        this.replaces = replaces;
    }

    /**
     * @param replaces the replaces to set
     */
    public void setReplaces(SimpleLiteral replaces) {
         if (this.replaces == null)
            this.replaces = new ArrayList<SimpleLiteral>();
        this.replaces.add(replaces);
    }

    /**
     * @return the issued
     */
    public List<SimpleLiteral> getIssued() {
        return issued;
    }

    /**
     * @param issued the issued to set
     */
    public void setIssued(List<SimpleLiteral> issued) {
        this.issued = issued;
    }

    /**
     * @param issued the issued to set
     */
    public void setIssued(SimpleLiteral issued) {
         if (this.issued == null)
            this.issued = new ArrayList<SimpleLiteral>();
        this.issued.add(issued);
    }

    /**
     * @return the created
     */
    public List<SimpleLiteral> getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(List<SimpleLiteral> created) {
        this.created = created;
    }

    /**
     * @param created the created to set
     */
    public void setcreated(SimpleLiteral created) {
         if (this.created == null)
            this.created = new ArrayList<SimpleLiteral>();
        this.created.add(created);
    }

    /**
     * @return the hasPart
     */
    public List<SimpleLiteral> getHasPart() {
        return hasPart;
    }

    /**
     * @param hasPart the hasPart to set
     */
    public void setHasPart(List<SimpleLiteral> hasPart) {
        this.hasPart = hasPart;
    }

    /**
     * @param hasPart the hasPart to set
     */
    public void setHasPart(SimpleLiteral hasPart) {
         if (this.hasPart == null)
            this.hasPart = new ArrayList<SimpleLiteral>();
        this.hasPart.add(hasPart);
    }

    /**
     * @return the provenance
     */
    public List<SimpleLiteral> getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(List<SimpleLiteral> provenance) {
        this.provenance = provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(SimpleLiteral provenance) {
         if (this.provenance == null)
            this.provenance = new ArrayList<SimpleLiteral>();
        this.provenance.add(provenance);
    }

    /**
     * @return the isReplacedBy
     */
    public List<SimpleLiteral> getIsReplacedBy() {
        return isReplacedBy;
    }

    /**
     * @param isReplacedBy the isReplacedBy to set
     */
    public void setIsReplacedBy(List<SimpleLiteral> isReplacedBy) {
        this.isReplacedBy = isReplacedBy;
    }

    /**
     * @param isReplacedBy the isReplacedBy to set
     */
    public void setIsReplacedBy(SimpleLiteral isReplacedBy) {
         if (this.isReplacedBy == null)
            this.isReplacedBy = new ArrayList<SimpleLiteral>();
        this.isReplacedBy.add(isReplacedBy);
    }

    /**
     * @return the alternative
     */
    public List<SimpleLiteral> getAlternative() {
        return alternative;
    }

    /**
     * @param alternative the alternative to set
     */
    public void setAlternative(List<SimpleLiteral> alternative) {
        this.alternative = alternative;
    }

    /**
     * @param alternative the alternative to set
     */
    public void setAlternative(SimpleLiteral alternative) {
         if (this.alternative == null)
            this.alternative = new ArrayList<SimpleLiteral>();
        this.alternative.add(alternative);
    }

    /**
     * @return the rightsHolder
     */
    public List<SimpleLiteral> getRightsHolder() {
        return rightsHolder;
    }

    /**
     * @param rightsHolder the rightsHolder to set
     */
    public void setRightsHolder(List<SimpleLiteral> rightsHolder) {
        this.rightsHolder = rightsHolder;
    }

    /**
     * @param rightsHolder the rightsHolder to set
     */
    public void setRightsHolder(SimpleLiteral rightsHolder) {
         if (this.rightsHolder == null)
            this.rightsHolder = new ArrayList<SimpleLiteral>();
        this.rightsHolder.add(rightsHolder);
    }

    /**
     * @return the dateCopyrighted
     */
    public List<SimpleLiteral> getDateCopyrighted() {
        return dateCopyrighted;
    }

    /**
     * @param dateCopyrighted the dateCopyrighted to set
     */
    public void setDateCopyrighted(List<SimpleLiteral> dateCopyrighted) {
        this.dateCopyrighted = dateCopyrighted;
    }

    /**
     * @param dateCopyrighted the dateCopyrighted to set
     */
    public void setDateCopyrighted(SimpleLiteral dateCopyrighted) {
         if (this.dateCopyrighted == null)
            this.dateCopyrighted = new ArrayList<SimpleLiteral>();
        this.dateCopyrighted.add(dateCopyrighted);
    }

    /**
     * @return the medium
     */
    public List<SimpleLiteral> getMedium() {
        return medium;
    }

    /**
     * @param medium the medium to set
     */
    public void setMedium(List<SimpleLiteral> medium) {
        this.medium = medium;
    }

    /**
     * @param medium the medium to set
     */
    public void setMedium(SimpleLiteral medium) {
         if (this.medium == null)
            this.medium = new ArrayList<SimpleLiteral>();
        this.medium.add(medium);
    }

    /**
     * @return the dateAccepted
     */
    public List<SimpleLiteral> getDateAccepted() {
        return dateAccepted;
    }

    /**
     * @param dateAccepted the dateAccepted to set
     */
    public void setDateAccepted(List<SimpleLiteral> dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    /**
     * @param dateAccepted the dateAccepted to set
     */
    public void setdateAccepted(SimpleLiteral dateAccepted) {
         if (this.dateAccepted == null)
            this.dateAccepted = new ArrayList<SimpleLiteral>();
        this.dateAccepted.add(dateAccepted);
    }

    /**
     * @return the isFormatOf
     */
    public List<SimpleLiteral> getIsFormatOf() {
        return isFormatOf;
    }

    /**
     * @param isFormatOf the isFormatOf to set
     */
    public void setIsFormatOf(List<SimpleLiteral> isFormatOf) {
        this.isFormatOf = isFormatOf;
    }

    /**
     * @param isFormatOf the isFormatOf to set
     */
    public void setIsFormatOf(SimpleLiteral isFormatOf) {
         if (this.isFormatOf == null)
            this.isFormatOf = new ArrayList<SimpleLiteral>();
        this.isFormatOf.add(isFormatOf);
    }

    /**
     * @return the hasFormat
     */
    public List<SimpleLiteral> getHasFormat() {
        return hasFormat;
    }

    /**
     * @param hasFormat the hasFormat to set
     */
    public void setHasFormat(List<SimpleLiteral> hasFormat) {
        this.hasFormat = hasFormat;
    }

    /**
     * @param hasFormat the hasFormat to set
     */
    public void setHasFormat(SimpleLiteral hasFormat) {
         if (this.hasFormat == null)
            this.hasFormat = new ArrayList<SimpleLiteral>();
        this.hasFormat.add(hasFormat);
    }

    /**
     * @return the isReferencedBy
     */
    public List<SimpleLiteral> getIsReferencedBy() {
        return isReferencedBy;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setIsReferencedBy(List<SimpleLiteral> isReferencedBy) {
        this.isReferencedBy = isReferencedBy;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setIsReferencedBy(SimpleLiteral isReferencedBy) {
         if (this.isReferencedBy == null)
            this.isReferencedBy = new ArrayList<SimpleLiteral>();
        this.isReferencedBy.add(isReferencedBy);
    }
}
