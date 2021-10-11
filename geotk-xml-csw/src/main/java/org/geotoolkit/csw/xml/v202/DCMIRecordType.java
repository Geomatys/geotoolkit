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

import java.util.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DCMIRecord;
import org.geotoolkit.dublincore.xml.AbstractSimpleLiteral;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;


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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DCMIRecordType", propOrder = {
    "identifier"  ,
    "title"       ,
    "type"        ,
    "subject"     ,
    "format"      ,
    "language"    ,
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
    "instructionalMethod",
    "accrualMethod",
    "accrualPeriodicity",
    "accrualPolicy",
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
    private List<SimpleLiteral> description;

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

    @XmlElement(name = "instructionalMethod", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> instructionalMethod;

    @XmlElement(name = "accrualMethod", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> accrualMethod;

    @XmlElement(name = "accrualPeriodicity", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> accrualPeriodicity;

    @XmlElement(name = "accrualPolicy", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> accrualPolicy;
    /**
     * An empty constructor used by JAXB
     */
    DCMIRecordType() {
        this.dcElement = new ArrayList<>();
    }


    public DCMIRecordType(final SimpleLiteral identifier, final SimpleLiteral title, final SimpleLiteral type,
            final List<SimpleLiteral> subjects, final SimpleLiteral format, final SimpleLiteral modified, final SimpleLiteral date, final SimpleLiteral _abstract,
            final SimpleLiteral creator, final SimpleLiteral publisher, final SimpleLiteral language, final SimpleLiteral spatial,
            final SimpleLiteral references) {

        this.identifier = identifier;
        this.title      = title;
        this.type       = type;
        if (format != null) {
            this.format = new ArrayList<>();
            this.format.add(format);
        }
        this.date      = date;
        this.dcElement = new ArrayList<>();

        this.subject     = subjects;
        if (creator != null) {
            this.creator = new ArrayList<>();
            this.creator.add(creator);
        }
        if (publisher != null) {
            this.publisher = new ArrayList<>();
            this.publisher.add(publisher);
        }
        this.language    = language;
        this.modified    = modified;
        if (_abstract != null) {
            this._abstract = new ArrayList<>();
            this._abstract.add(_abstract);
        }
        this.spatial     = spatial;
        this.references  = references;

    }

    public DCMIRecordType(final SimpleLiteral identifier, final SimpleLiteral title, final SimpleLiteral type,
            final List<SimpleLiteral> subjects, final List<SimpleLiteral> format, final SimpleLiteral modified, final SimpleLiteral date, final List<SimpleLiteral> _abstract,
            final List<SimpleLiteral> creator, final SimpleLiteral publisher, final SimpleLiteral language, final SimpleLiteral spatial,
            final SimpleLiteral references, final List<SimpleLiteral> relation) {

        this.identifier = identifier;
        this.title      = title;
        this.type       = type;
        this.format     = format;
        this.date       = date;

        this.dcElement = new ArrayList<>();

        this.subject     = subjects;
        this.creator     = creator;
        if (publisher != null) {
            this.publisher = Arrays.asList(publisher);
        }
        this.language    = language;
        this.modified    = modified;
        this._abstract   = _abstract;
        this.spatial     = spatial;
        this.references  = references;
        this.relation    = relation;

    }

    /**
     * Gets the value of the dcElement property.
     */
    @Override
    public List<JAXBElement<SimpleLiteral>> getDCElement() {
        if (dcElement == null) {
            dcElement = new ArrayList<>();
        }
        return dcElement;
    }

    public void setIdentifier(final SimpleLiteral identifier) {
        this.identifier = identifier;
    }

    @Override
    public SimpleLiteral getIdentifier() {
        return identifier;
    }

    @Override
    public String getIdentifierStringValue() {
        if (identifier != null) {
            return identifier.getFirstValue();
        }
        return null;
    }

    public void setTitle(final SimpleLiteral title) {
        this.title = title;
    }

    @Override
    public SimpleLiteral getTitle() {
        return title;
    }

    @Override
    public String getTitleStringValue() {
        if (title != null) {
            return title.getFirstValue();
        }
        return null;
    }

    public void setType(final SimpleLiteral type) {
        this.type = type;
    }

    @Override
    public SimpleLiteral getType() {
        return type;
    }

    @Override
    public String getTypeStringValue() {
        if (type != null) {
            return type.getFirstValue();
        }
        return null;
    }

    public void setSubject(final List<SimpleLiteral> subjects) {
        this.subject = subjects;
    }

    public void setSubject(final SimpleLiteral subject) {
        if (this.subject == null) {
            this.subject = new ArrayList<>();
        }
        this.subject.add(subject);
    }

    @Override
    public List<SimpleLiteral> getSubject() {
        if (subject == null) {
            subject = new ArrayList<>();
        }
        return subject;
    }

    public void setFormat(final SimpleLiteral format) {
        this.format = Arrays.asList(format);
    }

    public void setFormat(final List<SimpleLiteral> format) {
        this.format = format;
    }

    @Override
    public List<SimpleLiteral> getFormat() {
        return format;
    }

    public void setModified(final SimpleLiteral modified) {
        this.modified = modified;
    }

    @Override
    public SimpleLiteral getModified() {
        return modified;
    }

    public void setDate(final SimpleLiteral date) {
        this.date = date;
    }

    @Override
    public SimpleLiteral getDate() {
        return date;
    }

    @Override
    public String getDateStringValue() {
        if (date != null) {
            return date.getFirstValue();
        }
        return null;
    }

    public void setAbstract(final SimpleLiteral _abstract) {
        this._abstract = Arrays.asList(_abstract);
    }

    public void setAbstract(final List<SimpleLiteral> _abstract) {
        this._abstract =_abstract;
    }

    @Override
    public List<SimpleLiteral> getAbstract() {
        return _abstract;
    }

    @Override
    public String getAbstractStringValue() {
        if (_abstract != null && !_abstract.isEmpty()) {
            return _abstract.get(0).getFirstValue();
        }
        return null;
    }

    public void setCreator(final SimpleLiteral creator) {
        this.creator = Arrays.asList(creator);
    }

    public void setCreator(final List<SimpleLiteral> creator) {
        this.creator = creator;
    }

    @Override
    public List<SimpleLiteral> getCreator() {
        return creator;
    }

    @Override
    public String getCreatorStringValue() {
        if (creator != null && !creator.isEmpty()) {
            return creator.get(0).getFirstValue();
        }
        return null;
    }

    public void setLanguage(final SimpleLiteral language) {
        this.language = language;
    }

    @Override
    public SimpleLiteral getLanguage() {
        return language;
    }

    public void setRelation(final SimpleLiteral relation) {
        if (this.relation == null) {
            this.relation = new ArrayList<>();
        }
        this.relation.add(relation);
    }

    public void setRelation(final List<SimpleLiteral> relation) {
        this.relation = relation;
    }

    @Override
    public List<SimpleLiteral> getRelation() {
        return relation;
    }

    public void setSource(final SimpleLiteral source) {
        if (this.source == null) {
            this.source = new ArrayList<>();
        }
        this.source.add(source);
    }

    public void setSource(final List<SimpleLiteral> source) {
        this.source = source;
    }

    @Override
    public List<SimpleLiteral> getSource() {
       return source;
    }

    public void setCoverage(final SimpleLiteral coverage) {
        if (this.coverage == null) {
            this.coverage = new ArrayList<>();
        }
        this.coverage.add(coverage);
    }

    public void setCoverage(final List<SimpleLiteral> coverage) {
        this.coverage = coverage;
    }

    @Override
    public List<SimpleLiteral> getCoverage() {
        return coverage;
    }

    public void setRights(final SimpleLiteral rights) {
        if (this.rights == null) {
            this.rights = new ArrayList<>();
        }
        if (rights != null) {
            this.rights.add(rights);
        }
    }

    public void setRights(final List<SimpleLiteral> rights) {
        this.rights = rights;
    }

    @Override
    public List<SimpleLiteral> getRights() {
        return rights;
    }

    public void setSpatial(final SimpleLiteral spatial) {
        this.spatial = spatial;
    }

    @Override
    public SimpleLiteral getSpatial() {
         return spatial;
    }

    public void setReferences(final SimpleLiteral references) {
        this.references = references;
    }

    @Override
    public SimpleLiteral getReferences() {
        return references;
    }

    public void setPublisher(final List<SimpleLiteral> publisher) {
        this.publisher = publisher;
    }

    public void setPublisher(final SimpleLiteral publisher) {
        if (this.publisher == null) {
            this.publisher = new ArrayList<>();
        }
        this.publisher.add(publisher);
    }

    @Override
    public List<SimpleLiteral> getPublisher() {
        return publisher;
    }

    @Override
    public String getPublisherStringValue() {
        if (publisher != null && !publisher.isEmpty()) {
            return publisher.get(0).getFirstValue();
        }
        return null;
    }

    public void setContributor(final List<SimpleLiteral> contributor) {
        this.contributor = contributor;
    }

    public void setContributor(final SimpleLiteral contributor) {
        if (this.contributor == null) {
            this.contributor = new ArrayList<>();
        }
        this.contributor.add(contributor);
    }

    @Override
    public List<SimpleLiteral> getContributor() {
        return contributor;
    }

    @Override
    public String getContributorStringValue() {
        if (contributor != null && !contributor.isEmpty()) {
            return contributor.get(0).getFirstValue();
        }
        return null;
    }

    public void setDescription(final List<SimpleLiteral> description) {
        this.description = description;
    }

    public void setDescription(final SimpleLiteral description) {
        if (this.description == null) {
            this.description = new ArrayList<>();
        }
        this.description.add(description);
    }

    @Override
    public List<SimpleLiteral> getDescription() {
        return description;
    }

    @Override
    public String getDescriptionStringValue() {
        if (description != null && !description.isEmpty()) {
            return description.get(0).getFirstValue();
        }
        return null;
    }

    @Override
    public List<String> getDescriptionStringValues() {
        if (description != null && !description.isEmpty()) {
            return description.get(0).getContent();
        }
        return new ArrayList<>();
    }

    public void setTemporal(final SimpleLiteral temporal) {
        this.temporal = temporal;
    }

    public SimpleLiteral getTemporal() {
        return temporal;
    }

    /**
     * if the attribute have not been fill by JAXB we search in DCelement
     */
    public SimpleLiteral getAttributeFromDCelement(final String name) {
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
            s.append("subjects:\n");
            for (SimpleLiteral sl: subject) {
                s.append(sl).append('\n');
            }
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
            s.append("description:\n");
            for (SimpleLiteral sl: description) {
                s.append(sl).append('\n');
            }
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
            s.append("spatialontribu: ").append(spatial).append('\n');
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
            List<SimpleLiteral> obj = new ArrayList<>();
            for (JAXBElement<SimpleLiteral> jb: dcElement) {
                obj.add(jb.getValue());
            }

            for (JAXBElement<SimpleLiteral> jb: that.dcElement) {
                if (!obj.contains(jb.getValue())) {
                    dcelement = false;
                }
            }
            return Objects.equals(this._abstract,   that._abstract)   &&
                   Objects.equals(this.creator  ,   that.creator)     &&
                   Objects.equals(this.contributor, that.contributor) &&
                   Objects.equals(this.coverage,    that.coverage)    &&
                   Objects.equals(this.date,        that.date)        &&
                   Objects.equals(this.description, that.description) &&
                   Objects.equals(this.format,      that.format)      &&
                   Objects.equals(this.identifier,  that.identifier)  &&
                   Objects.equals(this.language,    that.language)    &&
                   Objects.equals(this.modified,    that.modified)    &&
                   Objects.equals(this.publisher,   that.publisher)   &&
                   Objects.equals(this.references,  that.references)  &&
                   Objects.equals(this.relation,    that.relation)    &&
                   Objects.equals(this.rights,      that.rights)      &&
                   Objects.equals(this.source,      that.source)      &&
                   Objects.equals(this.spatial,     that.spatial)     &&
                   Objects.equals(this.subject,     that.subject)     &&
                   Objects.equals(this.title,       that.title)       &&
                   Objects.equals(this.type,        that.type)        &&
                   Objects.equals(this.temporal,    that.temporal)    &&
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
    public void setMediator(final List<SimpleLiteral> mediator) {
        this.mediator = mediator;
    }

    /**
     * @param mediator the mediator to set
     */
    public void setMediator(final SimpleLiteral mediator) {
        if (this.mediator == null) {
            this.mediator = new ArrayList<>();
        }
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
    public void setIsRequiredBy(final List<SimpleLiteral> isRequiredBy) {
        this.isRequiredBy = isRequiredBy;
    }

    /**
     * @param isRequiredBy the isRequiredBy to set
     */
    public void setIsRequiredBy(final SimpleLiteral isRequiredBy) {
        if (this.isRequiredBy == null) {
            this.isRequiredBy = new ArrayList<>();
        }
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
    public void setAudience(final List<SimpleLiteral> audience) {
        this.audience = audience;
    }

    /**
     * @param audience the audience to set
     */
    public void setAudience(final SimpleLiteral audience) {
        if (this.audience == null) {
            this.audience = new ArrayList<>();
        }
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
    public void setExtent(final List<SimpleLiteral> extent) {
        this.extent = extent;
    }

    /**
     * @param extent the extent to set
     */
    public void setExtent(final SimpleLiteral extent) {
        if (this.extent == null) {
            this.extent = new ArrayList<>();
        }
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
    public void setTableOfContents(final List<SimpleLiteral> tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    /**
     * @param tableOfContents the tableOfContents to set
     */
    public void setTableOfContents(final SimpleLiteral tableOfContents) {
        if (this.tableOfContents == null) {
            this.tableOfContents = new ArrayList<>();
        }
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
    public void setHasVersion(final List<SimpleLiteral> hasVersion) {
        this.hasVersion = hasVersion;
    }

    /**
     * @param hasVersion the hasVersion to set
     */
    public void setHasVersion(final SimpleLiteral hasVersion) {
        if (this.hasVersion == null) {
            this.hasVersion = new ArrayList<>();
        }
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
    public void setRequires(final List<SimpleLiteral> requires) {
        this.requires = requires;
    }

    /**
     * @param requires the requires to set
     */
    public void setRequires(final SimpleLiteral requires) {
        if (this.requires == null) {
            this.requires = new ArrayList<>();
        }
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
    public void setDateSubmitted(final List<SimpleLiteral> dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * @param dateSubmitted the dateSubmitted to set
     */
    public void setDateSubmitted(final SimpleLiteral dateSubmitted) {
        if (this.dateSubmitted == null) {
            this.dateSubmitted = new ArrayList<>();
        }
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
    public void setLicense(final List<SimpleLiteral> license) {
        this.license = license;
    }

    /**
     * @param license the license to set
     */
    public void setLicense(final SimpleLiteral license) {
        if (this.license == null) {
            this.license = new ArrayList<>();
        }
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
    public void setConformsTo(final List<SimpleLiteral> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * @param conformsTo the conformsTo to set
     */
    public void setConformsTo(final SimpleLiteral conformsTo) {
        if (this.conformsTo == null) {
            this.conformsTo = new ArrayList<>();
        }
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
    public void setBibliographicCitation(final List<SimpleLiteral> bibliographicCitation) {
        this.bibliographicCitation = bibliographicCitation;
    }

    /**
     * @param bibliographicCitation the bibliographicCitation to set
     */
    public void setBibliographicCitation(final SimpleLiteral bibliographicCitation) {
        if (this.bibliographicCitation == null) {
            this.bibliographicCitation = new ArrayList<>();
        }
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
    public void setIsVersionOf(final List<SimpleLiteral> isVersionOf) {
        this.isVersionOf = isVersionOf;
    }

    /**
     * @param isVersionOf the isVersionOf to set
     */
    public void setIsVersionOf(final SimpleLiteral isVersionOf) {
        if (this.isVersionOf == null) {
            this.isVersionOf = new ArrayList<>();
        }
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
    public void setAvailable(final List<SimpleLiteral> available) {
        this.available = available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(final SimpleLiteral available) {
        if (this.available == null) {
            this.available = new ArrayList<>();
        }
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
    public void setAccessRights(final List<SimpleLiteral> accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * @param accesRights the accesRights to set
     */
    public void setAccessRights(final SimpleLiteral accessRights) {
        if (this.accessRights == null) {
            this.accessRights = new ArrayList<>();
        }
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
    public void setIsPartOf(final List<SimpleLiteral> isPartOf) {
        this.isPartOf = isPartOf;
    }

    /**
     * @param isPartOf the isPartOf to set
     */
    public void setIsPartOf(final SimpleLiteral isPartOf) {
        if (this.isPartOf == null) {
            this.isPartOf = new ArrayList<>();
        }
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
    public void setValid(final List<SimpleLiteral> valid) {
        this.valid = valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(final SimpleLiteral valid) {
        if (this.valid == null) {
            this.valid = new ArrayList<>();
        }
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
    public void setEducationLevel(final List<SimpleLiteral> educationLevel) {
        this.educationLevel = educationLevel;
    }

    /**
     * @param eductionLevel the eductionLevel to set
     */
    public void setEducationLevel(final SimpleLiteral educationLevel) {
        if (this.educationLevel == null) {
            this.educationLevel = new ArrayList<>();
        }
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
    public void setReplaces(final List<SimpleLiteral> replaces) {
        this.replaces = replaces;
    }

    /**
     * @param replaces the replaces to set
     */
    public void setReplaces(final SimpleLiteral replaces) {
        if (this.replaces == null) {
            this.replaces = new ArrayList<>();
        }
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
    public void setIssued(final List<SimpleLiteral> issued) {
        this.issued = issued;
    }

    /**
     * @param issued the issued to set
     */
    public void setIssued(final SimpleLiteral issued) {
        if (this.issued == null) {
            this.issued = new ArrayList<>();
        }
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
    public void setCreated(final List<SimpleLiteral> created) {
        this.created = created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final SimpleLiteral created) {
        if (this.created == null) {
            this.created = new ArrayList<>();
        }
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
    public void setHasPart(final List<SimpleLiteral> hasPart) {
        this.hasPart = hasPart;
    }

    /**
     * @param hasPart the hasPart to set
     */
    public void setHasPart(final SimpleLiteral hasPart) {
        if (this.hasPart == null) {
            this.hasPart = new ArrayList<>();
        }
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
    public void setProvenance(final List<SimpleLiteral> provenance) {
        this.provenance = provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(final SimpleLiteral provenance) {
        if (this.provenance == null) {
            this.provenance = new ArrayList<>();
        }
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
    public void setIsReplacedBy(final List<SimpleLiteral> isReplacedBy) {
        this.isReplacedBy = isReplacedBy;
    }

    /**
     * @param isReplacedBy the isReplacedBy to set
     */
    public void setIsReplacedBy(final SimpleLiteral isReplacedBy) {
        if (this.isReplacedBy == null) {
            this.isReplacedBy = new ArrayList<>();
        }
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
    public void setAlternative(final List<SimpleLiteral> alternative) {
        this.alternative = alternative;
    }

    /**
     * @param alternative the alternative to set
     */
    public void setAlternative(final SimpleLiteral alternative) {
        if (this.alternative == null) {
            this.alternative = new ArrayList<>();
        }
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
    public void setRightsHolder(final List<SimpleLiteral> rightsHolder) {
        this.rightsHolder = rightsHolder;
    }

    /**
     * @param rightsHolder the rightsHolder to set
     */
    public void setRightsHolder(final SimpleLiteral rightsHolder) {
        if (this.rightsHolder == null) {
            this.rightsHolder = new ArrayList<>();
        }
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
    public void setDateCopyrighted(final List<SimpleLiteral> dateCopyrighted) {
        this.dateCopyrighted = dateCopyrighted;
    }

    /**
     * @param dateCopyrighted the dateCopyrighted to set
     */
    public void setDateCopyrighted(final SimpleLiteral dateCopyrighted) {
        if (this.dateCopyrighted == null) {
            this.dateCopyrighted = new ArrayList<>();
        }
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
    public void setMedium(final List<SimpleLiteral> medium) {
        this.medium = medium;
    }

    /**
     * @param medium the medium to set
     */
    public void setMedium(final SimpleLiteral medium) {
        if (this.medium == null) {
            this.medium = new ArrayList<>();
        }
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
    public void setDateAccepted(final List<SimpleLiteral> dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    /**
     * @param dateAccepted the dateAccepted to set
     */
    public void setDateAccepted(final SimpleLiteral dateAccepted) {
        if (this.dateAccepted == null) {
            this.dateAccepted = new ArrayList<>();
        }
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
    public void setIsFormatOf(final List<SimpleLiteral> isFormatOf) {
        this.isFormatOf = isFormatOf;
    }

    /**
     * @param isFormatOf the isFormatOf to set
     */
    public void setIsFormatOf(final SimpleLiteral isFormatOf) {
        if (this.isFormatOf == null) {
            this.isFormatOf = new ArrayList<>();
        }
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
    public void setHasFormat(final List<SimpleLiteral> hasFormat) {
        this.hasFormat = hasFormat;
    }

    /**
     * @param hasFormat the hasFormat to set
     */
    public void setHasFormat(final SimpleLiteral hasFormat) {
        if (this.hasFormat == null) {
            this.hasFormat = new ArrayList<>();
        }
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
    public void setIsReferencedBy(final List<SimpleLiteral> isReferencedBy) {
        this.isReferencedBy = isReferencedBy;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setIsReferencedBy(final SimpleLiteral isReferencedBy) {
        if (this.isReferencedBy == null) {
            this.isReferencedBy = new ArrayList<>();
        }
        this.isReferencedBy.add(isReferencedBy);
    }

    /**
     * @return the instructionalMethod
     */
    public List<SimpleLiteral> getInstructionalMethod() {
        return instructionalMethod;
    }

    /**
     * @param instructionalMethod the instructionalMethod to set
     */
    public void setInstructionalMethod(final List<SimpleLiteral> instructionalMethod) {
        this.instructionalMethod = instructionalMethod;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setInstructionalMethod(final SimpleLiteral instructionalMethod) {
        if (this.instructionalMethod == null) {
            this.instructionalMethod = new ArrayList<>();
        }
        this.instructionalMethod.add(instructionalMethod);
    }

    /**
     * @return the accrualMethod
     */
    public List<SimpleLiteral> getAccrualMethod() {
        return accrualMethod;
    }

    /**
     * @param accrualMethod the accrualMethod to set
     */
    public void setAccrualMethod(final List<SimpleLiteral> accrualMethod) {
        this.accrualMethod = accrualMethod;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setAccrualMethod(final SimpleLiteral instructionalMethod) {
        if (this.accrualMethod == null) {
            this.accrualMethod = new ArrayList<>();
        }
        this.accrualMethod.add(instructionalMethod);
    }

    /**
     * @return the accrualPeriodicity
     */
    public List<SimpleLiteral> getAccrualPeriodicity() {
        return accrualPeriodicity;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setAccrualPeriodicity(final SimpleLiteral instructionalPeriodicity) {
        if (this.accrualPeriodicity == null) {
            this.accrualPeriodicity = new ArrayList<>();
        }
        this.accrualPeriodicity.add(instructionalPeriodicity);
    }

    /**
     * @param accrualPeriodicity the accrualPeriodicity to set
     */
    public void setAccrualPeriodicity(final List<SimpleLiteral> accrualPeriodicity) {
        this.accrualPeriodicity = accrualPeriodicity;
    }

    /**
     * @return the accrualPolicy
     */
    public List<SimpleLiteral> getAccrualPolicy() {
        return accrualPolicy;
    }

    /**
     * @param accrualPolicy the accrualPolicy to set
     */
    public void setAccrualPolicy(final List<SimpleLiteral> accrualPolicy) {
        this.accrualPolicy = accrualPolicy;
    }

    /**
     * @param isReferencedBy the isReferencedBy to set
     */
    public void setAccrualPolicy(final SimpleLiteral instructionalPolicy) {
        if (this.accrualPolicy == null) {
            this.accrualPolicy = new ArrayList<>();
        }
        this.accrualPolicy.add(instructionalPolicy);
    }

    @Override
    public AbstractSimpleLiteral getDCProperty(final String property) {
        if (dcElement != null) {
            for (JAXBElement<SimpleLiteral> s : dcElement) {
                if (s.getValue() != null) {
                    if (s.getName().getLocalPart().equals(property)) {
                        return s.getValue();
                    }
                }
            }
        }
        return null;
    }
}
