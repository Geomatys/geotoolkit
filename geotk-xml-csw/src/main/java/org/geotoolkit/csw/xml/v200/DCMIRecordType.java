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
package org.geotoolkit.csw.xml.v200;

import java.util.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DCMIRecord;
import org.geotoolkit.dublincore.xml.AbstractSimpleLiteral;
import org.geotoolkit.dublincore.xml.v1.elements.SimpleLiteral;


/**
 *
 * This type encapsulates all of the standard DCMI metadata terms,
 * including the Dublin Core refinements; these terms may be mapped to the profile-specific information model.
 *
 *
 * <p>Java class for DCMIRecordType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DCMIRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.purl.org/dc/terms/}DCMI-terms"/>
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
    "distributor" ,
    "creator"     ,
    "modified"    ,
    "_abstract"   ,
    "references"  ,
    "spatial"     ,
    "dcElement"
})
@XmlSeeAlso({
    RecordType.class
})
@XmlRootElement(name="DCMIRecord")
public class DCMIRecordType extends AbstractRecordType implements DCMIRecord {



    @XmlElementRefs({
        @XmlElementRef(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "identifier", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> identifier;

    @XmlElementRefs({
        @XmlElementRef(name = "title", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "title", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> title;

    @XmlElementRefs({
        @XmlElementRef(name = "type", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "type", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> type;

     @XmlElementRefs({
        @XmlElementRef(name = "subject", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "subject", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> subject;

    @XmlElementRefs({
        @XmlElementRef(name = "format", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "format", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> format;

    @XmlElementRefs({
        @XmlElementRef(name = "language", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "language", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> language;

    @XmlElementRefs({
        @XmlElementRef(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "publisher", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> distributor;

    @XmlElementRefs({
        @XmlElementRef(name = "creator", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class),
        @XmlElementRef(name = "creator", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> creator;

    @XmlElementRefs({
        @XmlElementRef(name = "DC-element", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "DC-element", namespace = "http://purl.org/dc/elements/1.1/",     type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> dcElement;

    @XmlElementRefs({
        @XmlElementRef(name = "modified", namespace = "http://purl.org/dc/terms/",     type = JAXBElement.class),
        @XmlElementRef(name = "modified", namespace = "http://www.purl.org/dc/terms/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> modified;

    @XmlElementRefs({
        @XmlElementRef(name = "abstract", namespace = "http://purl.org/dc/terms/",     type = JAXBElement.class),
        @XmlElementRef(name = "abstract", namespace = "http://www.purl.org/dc/terms/", type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> _abstract;

    @XmlElementRefs({
        @XmlElementRef(name = "spatial", namespace = "http://purl.org/dc/terms/",     type = JAXBElement.class),
        @XmlElementRef(name = "spatial", namespace = "http://www.purl.org/dc/terms/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> spatial;

    @XmlElementRefs({
        @XmlElementRef(name = "references", namespace = "http://purl.org/dc/terms/",     type = JAXBElement.class),
        @XmlElementRef(name = "references", namespace = "http://www.purl.org/dc/terms/", type = JAXBElement.class)
    })
    private JAXBElement<SimpleLiteral> references;



     /**
     * An empty constructor used by JAXB
     */
    DCMIRecordType() {
        this.dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
    }


    public DCMIRecordType(final SimpleLiteral identifier, final SimpleLiteral title, final SimpleLiteral type,
            final List<SimpleLiteral> subjects, final SimpleLiteral format, final SimpleLiteral modified, final SimpleLiteral _abstract,
            final SimpleLiteral creator, final SimpleLiteral distributor, final SimpleLiteral language, final SimpleLiteral spatial,
            final SimpleLiteral references) {

        this.identifier = dublinFactory.createIdentifier(identifier);
        this.title      = dublinFactory.createTitle(title);
        this.type       = dublinFactory.createType(type);
        this.format     = Arrays.asList(dublinFactory.createFormat(format));

        this.dcElement  = new ArrayList<JAXBElement<SimpleLiteral>>();

        this.subject    = new ArrayList<JAXBElement<SimpleLiteral>>();
        for (SimpleLiteral sub: subjects) {
            this.subject.add(dublinFactory.createSubject(sub));
        }

        this.creator     = Arrays.asList(dublinFactory.createCreator(creator));

        this.distributor = dublinFactory.createPublisher(distributor);
        this.language    = dublinFactory.createLanguage(language);
        this.modified    = dublinTermFactory.createModified(modified);
        this._abstract   = Arrays.asList(dublinTermFactory.createAbstract(_abstract));
        this.spatial     = dublinTermFactory.createSpatial(spatial);
        this.references  = dublinTermFactory.createReferences(references);

    }

    /**
     * Gets the value of the dcElement property.
     * (unModifiable)
     */
    @Override
    public List<JAXBElement<SimpleLiteral>> getDCElement() {
        if (dcElement == null) {
            dcElement = new ArrayList<JAXBElement<SimpleLiteral>>();
        }
        return Collections.unmodifiableList(dcElement);
    }

    public void setIdentifier(final SimpleLiteral identifier) {
        this.identifier = dublinFactory.createIdentifier(identifier);
    }

    @Override
    public AbstractSimpleLiteral getIdentifier() {
        if (identifier != null) {
            return identifier.getValue();
        }
        return null;
    }

    @Override
    public String getIdentifierStringValue() {
        if (identifier != null && identifier.getValue() != null) {
            return identifier.getValue().getFirstValue();
        }
        return null;
    }

    public void setTitle(final SimpleLiteral title) {
        this.title = dublinFactory.createTitle(title);
    }

    @Override
    public AbstractSimpleLiteral getTitle() {
        if (title != null) {
            return title.getValue();
        }
        return null;
    }

    @Override
    public String getTitleStringValue() {
        if (title != null && title.getValue() != null) {
            return title.getValue().getFirstValue();
        }
        return null;
    }

    public void setType(final SimpleLiteral type) {
        this.type = dublinFactory.createType(type);
    }

    @Override
    public AbstractSimpleLiteral getType() {
        if (type != null) {
            return type.getValue();
        }
        return null;
    }

    @Override
    public String getTypeStringValue() {
        if (type != null && type.getValue() != null) {
            return type.getValue().getFirstValue();
        }
        return null;
    }

    public void setSubject(final List<SimpleLiteral> subjects) {
        this.subject = new ArrayList<JAXBElement<SimpleLiteral>>();
        for (SimpleLiteral sub: subjects) {
            this.subject.add(dublinFactory.createSubject(sub));
        }
    }

    public void setSubject(final SimpleLiteral subject) {
        if (this.subject == null) {
            this.subject = new ArrayList<JAXBElement<SimpleLiteral>>();
        }
        this.subject.add(dublinFactory.createSubject(subject));
    }

    @Override
    public List<AbstractSimpleLiteral> getSubject() {
        final List<AbstractSimpleLiteral> response = new ArrayList<AbstractSimpleLiteral>();
        if (subject != null) {
            for(JAXBElement<SimpleLiteral> jb: subject) {
                response.add(jb.getValue());
            }
        }
        return response;
    }

    public void setFormat(final SimpleLiteral format) {
        this.format = Arrays.asList(dublinFactory.createFormat(format));
    }

    public void setFormat(final List<SimpleLiteral> format) {
        this.format = new ArrayList<JAXBElement<SimpleLiteral>>();
        for (SimpleLiteral c : format) {
            this.format.add(dublinFactory.createFormat(c));
        }
    }

    @Override
    public List<? extends AbstractSimpleLiteral> getFormat() {
        final List<AbstractSimpleLiteral> response = new ArrayList<AbstractSimpleLiteral>();
        if (format != null) {
            for(JAXBElement<SimpleLiteral> jb: format) {
                response.add(jb.getValue());
            }
        }
        return response;
    }

    public void setModified(final SimpleLiteral modified) {
        this.modified = dublinTermFactory.createModified(modified);
    }

    @Override
    public AbstractSimpleLiteral getModified() {
        if (modified != null) {
            return modified.getValue();
        }
        return null;
    }

    public void setAbstract(final SimpleLiteral _abstract) {
        this._abstract = Arrays.asList(dublinTermFactory.createAbstract(_abstract));
    }

    public void setAbstract(final List<SimpleLiteral> _abstract) {
       this._abstract = new ArrayList<JAXBElement<SimpleLiteral>>();
       for (SimpleLiteral c : _abstract) {
            this._abstract.add(dublinTermFactory.createAbstract(c));
       }
    }

    @Override
    public List<AbstractSimpleLiteral> getAbstract() {
        final List<AbstractSimpleLiteral> response = new ArrayList<AbstractSimpleLiteral>();
        if (_abstract != null) {
            for(JAXBElement<SimpleLiteral> jb: _abstract) {
                response.add(jb.getValue());
            }
        }
        return response;
    }

    @Override
    public String getAbstractStringValue() {
        if (_abstract != null && !_abstract.isEmpty() && _abstract.get(0)!= null) {
            return _abstract.get(0).getValue().getFirstValue();
        }
        return null;
    }

    public void setCreator(final SimpleLiteral creator) {
        this.creator = Arrays.asList(dublinFactory.createCreator(creator));
    }

    public void setCreator(final List<SimpleLiteral> creator) {
        this.creator = new ArrayList<JAXBElement<SimpleLiteral>>();
        for (SimpleLiteral c : creator) {
            this.creator.add(dublinFactory.createCreator(c));
        }
    }

    @Override
    public List<AbstractSimpleLiteral> getCreator() {
        final List<AbstractSimpleLiteral> response = new ArrayList<AbstractSimpleLiteral>();
        if (creator != null) {
            for (JAXBElement<SimpleLiteral> jb : creator) {
                response.add(jb.getValue());
            }
        }
        return response;
    }

    @Override
    public String getCreatorStringValue() {
        if (creator != null && !creator.isEmpty() && creator.get(0).getValue() != null) {
            return creator.get(0).getValue().getFirstValue();
        }
        return null;
    }

    public void setDistributor(final SimpleLiteral distributor) {
        this.distributor = dublinFactory.createPublisher(distributor);
    }

    public AbstractSimpleLiteral getDistributor() {
        if (distributor != null) {
            return distributor.getValue();
        }
        return null;
    }

    public void setLanguage(final SimpleLiteral language) {
        this.language = dublinFactory.createLanguage(language);
    }

    @Override
    public AbstractSimpleLiteral getLanguage() {
        if (language != null) {
            return language.getValue();
        }
        return null;
    }

    public void setRelation(final SimpleLiteral relation) {
        this.dcElement.add(dublinFactory.createRelation(relation));
    }

    @Override
    public List<AbstractSimpleLiteral> getRelation() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("relation")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    public void setSource(final SimpleLiteral source) {
        this.dcElement.add(dublinFactory.createSource(source));
    }

    @Override
    public List<AbstractSimpleLiteral> getSource() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("source")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    public void setCoverage(final SimpleLiteral coverage) {
        this.dcElement.add(dublinFactory.createCoverage(coverage));
    }

    @Override
    public List<AbstractSimpleLiteral> getCoverage() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("coverage")) {
                result.add(jb.getValue());
            }

        }
        return result;
    }

    public void setDate(final SimpleLiteral date) {
        this.dcElement.add(dublinFactory.createDate(date));
    }

    @Override
    public AbstractSimpleLiteral getDate() {
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("date")) {
                return jb.getValue();
            }
        }
        return null;
    }

    @Override
    public String getDateStringValue() {
        final AbstractSimpleLiteral date = getDate();
        if (date != null) {
            return date.getFirstValue();
        }
        return null;
    }

    public void setRights(final SimpleLiteral rights) {
        this.dcElement.add(dublinFactory.createRights(rights));
    }

    @Override
    public List<AbstractSimpleLiteral> getRights() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("rights")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    public void setSpatial(final SimpleLiteral spatial) {
        this.spatial = dublinTermFactory.createSpatial(spatial);
    }

    @Override
    public AbstractSimpleLiteral getSpatial() {
        if (spatial != null) {
            return spatial.getValue();
        }
        return null;
    }

    public void setReferences(final SimpleLiteral references) {
        this.references = dublinTermFactory.createReferences(references);
    }

    @Override
    public AbstractSimpleLiteral getReferences() {
        if (references != null) {
            return references.getValue();
        }
        return null;
    }

    public void setPublisher(final SimpleLiteral publisher) {
        this.dcElement.add(dublinFactory.createPublisher(publisher));
    }

    @Override
    public List<AbstractSimpleLiteral> getPublisher() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("publisher")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    @Override
    public String getPublisherStringValue() {
        final List<AbstractSimpleLiteral> publisher = getContributor();
        if (publisher != null && !publisher.isEmpty()) {
            return publisher.get(0).getFirstValue();
        }
        return null;
    }

    public void setContributor(final SimpleLiteral contributor) {
        this.dcElement.add(dublinFactory.createContributor(contributor));
    }

    @Override
    public List<AbstractSimpleLiteral> getContributor() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("contributor")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    @Override
    public String getContributorStringValue() {
        final List<AbstractSimpleLiteral> contributor = getContributor();
        if (contributor != null && !contributor.isEmpty()) {
            return contributor.get(0).getFirstValue();
        }
        return null;
    }

    public void setDescription(final SimpleLiteral description) {
        this.dcElement.add(dublinFactory.createDescription(description));
    }

    @Override
    public List<AbstractSimpleLiteral> getDescription() {
        final List<AbstractSimpleLiteral> result = new ArrayList<AbstractSimpleLiteral>();
        for (JAXBElement<SimpleLiteral> jb: dcElement) {
            if (jb.getName().getLocalPart().equals("description")) {
                result.add(jb.getValue());
            }
        }
        return result;
    }

    @Override
    public String getDescriptionStringValue() {
        final List<AbstractSimpleLiteral> description = getContributor();
        if (description != null && !description.isEmpty()) {
            return description.get(0).getFirstValue();
        }
        return null;
    }

    @Override
    public List<String> getDescriptionStringValues() {
        final List<AbstractSimpleLiteral> description = getContributor();
        if (description != null && !description.isEmpty()) {
            return description.get(0).getContent();
        }
        return new ArrayList<String>();
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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getName()).append("]\n");
        if (identifier != null && identifier.getValue() != null) {
            s.append("identifier: ").append(identifier.getValue()).append('\n');
        }
        if (title != null && title.getValue() != null) {
            s.append("title: ").append(title.getValue()).append('\n');
        }
        if (type != null && type.getValue() != null) {
            s.append("type: ").append(type.getValue()).append('\n');
        }
        if (format != null){
            s.append("format: ").append('\n');
            for (JAXBElement<SimpleLiteral> sl: format) {
                s.append(sl.getValue()).append('\n');
            }
        }
        if (subject != null) {
            s.append("subjects: ").append('\n');
            for (JAXBElement<SimpleLiteral> sl: subject) {
                s.append(sl.getValue()).append('\n');
            }
        }
        if (dcElement != null) {
            for (JAXBElement<SimpleLiteral> jb: dcElement) {
                s.append("name=").append(jb.getName()).append(" value=").append(jb.getValue()).append('\n');
            }
        }
        if (language != null && language.getValue() != null) {
            s.append("language: ").append(language.getValue()).append('\n');
        }
        if (modified != null && modified.getValue() != null) {
            s.append("modified: ").append(modified).append('\n');
        }
        if (_abstract != null) {
            s.append("abstract: ").append('\n');
            for (JAXBElement<SimpleLiteral> sl: _abstract) {
                s.append(sl.getValue()).append('\n');
            }
        }
        if (spatial != null && spatial.getValue() != null) {
            s.append("spatial: ").append(spatial.getValue()).append('\n');
        }
        if (references != null && references.getValue() != null) {
            s.append("references: ").append(references.getValue()).append('\n');
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
            return Objects.equals(this._abstract,   that._abstract)   &&
                   Objects.equals(this.creator  ,   that.creator)     &&
                   Objects.equals(this.distributor, that.distributor) &&
                   Objects.equals(this.format,      that.format)      &&
                   Objects.equals(this.identifier,  that.identifier)  &&
                   Objects.equals(this.language,    that.language)    &&
                   Objects.equals(this.modified,    that.modified)    &&
                   Objects.equals(this.references,  that.references)  &&
                   Objects.equals(this.spatial,     that.spatial)     &&
                   Objects.equals(this.subject,     that.subject)     &&
                   Objects.equals(this.title,       that.title)       &&
                   Objects.equals(this.type,        that.type)        &&
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
