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
        if (this.rights == null)
            this.rights = new ArrayList<SimpleLiteral>();
        this.rights.add(rights);
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
        StringBuilder s = new StringBuilder();
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
}
