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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.SummaryRecord;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;


/**
 *  This type defines a summary representation of the common record format.
 * It extends AbstractRecordType to include the core properties.
 *
 *
 * <p>Java class for SummaryRecordType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SummaryRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}subject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}format" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}relation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/terms/}modified" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/terms/}abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://purl.org/dc/terms/}spatial" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SummaryRecordType", propOrder = {
    "identifier",
    "title",
    "type",
    "subject",
    "format",
    "relation",
    "modified",
    "_abstract",
    "spatial",
    "boundingBox"
})
@XmlRootElement(name = "SummaryRecord")
public class SummaryRecordType extends AbstractRecordType implements SummaryRecord {

    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> identifier;
    @XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> title;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral type;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> subject;
    @XmlElement(name = "format", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> format;
    @XmlElement(name = "relation", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> relation;
    @XmlElement(namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> modified;
    @XmlElement(name = "abstract", namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> _abstract;
    @XmlElement(namespace = "http://purl.org/dc/terms/")
    private List<SimpleLiteral> spatial;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;


    /**
     * An empty constructor used by JAXB
     */
    SummaryRecordType(){

    }

    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(SimpleLiteral identifier, SimpleLiteral title, final SimpleLiteral type, final List<BoundingBoxType> bboxes,
            final List<SimpleLiteral> subject, final SimpleLiteral format, final SimpleLiteral modified, final SimpleLiteral _abstract){

        if (identifier != null) {
            this.identifier = new ArrayList<>();
            this.identifier.add(identifier);
        }

        if (title != null) {
            this.title = new ArrayList<>();
            this.title.add(title);
        }

        this.type = type;

        this.boundingBox = new ArrayList<>();
        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType) bbox));
                } else if (bbox != null) {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }
        this.subject = subject;

        if (format != null) {
            this.format = new ArrayList<>();
            this.format.add(format);
        }

        if (modified != null) {
            this.modified = new ArrayList<>();
            this.modified.add(modified);
        }

        if (_abstract != null) {
            this._abstract = new ArrayList<>();
            this._abstract.add(_abstract);
        }
    }

    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(SimpleLiteral identifier, SimpleLiteral title, final SimpleLiteral type, final List<BoundingBoxType> bboxes,
            final List<SimpleLiteral> subject, final List<SimpleLiteral> formats, final SimpleLiteral modified, final List<SimpleLiteral> _abstract){

        if (identifier != null) {
            this.identifier = new ArrayList<>();
            this.identifier.add(identifier);
        }

        if (title != null) {
            this.title = new ArrayList<>();
            this.title.add(title);
        }

        this.type = type;

        this.boundingBox = new ArrayList<>();
        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType) bbox));
                } else if (bbox != null) {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }
        this.subject = subject;

        this.format = formats;

        if (modified != null) {
            this.modified = new ArrayList<>();
            this.modified.add(modified);
        }
        this._abstract = _abstract;
    }

    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(final List<SimpleLiteral> identifier, final List<SimpleLiteral> title, final SimpleLiteral type, final List<BoundingBoxType> bboxes,
            final List<SimpleLiteral> subject, final List<SimpleLiteral> format, final List<SimpleLiteral> modified, final List<SimpleLiteral> _abstract){

        this.identifier = identifier;
        this.title = title;
        this.type = type;
        this.modified = modified;
        this._abstract = _abstract;
        this.subject = subject;
        this.format = format;

        if (bboxes != null) {
            this.boundingBox = new ArrayList<>();
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType) bbox));
                } else if (bbox != null) {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }
    }


    /**
     * Gets the value of the identifier property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getIdentifier() {
        if (identifier == null) {
            identifier = new ArrayList<>();
        }
        return Collections.unmodifiableList(identifier);
    }

    @Override
    public String getIdentifierStringValue() {
        if (identifier != null && !identifier.isEmpty()) {
            return identifier.get(0).getFirstValue();
        }
        return null;
    }

    /**
     * Gets the value of the title property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        return Collections.unmodifiableList(title);
    }

    @Override
    public String getTitleStringValue() {
        if (title != null && !title.isEmpty()) {
            return title.get(0).getFirstValue();
        }
        return null;
    }

    /**
     * Gets the value of the type property.
     */
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
    /**
     * Gets the value of the subject property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getSubject() {
        if (subject == null) {
            subject = new ArrayList<>();
        }
        return Collections.unmodifiableList(subject);
    }


    @Override
    public List<String> getSubjectStringValues() {
        if (subject != null && !subject.isEmpty()) {
            return subject.get(0).getContent();
        }
        return new ArrayList<>();
    }
    /**
     * Gets the value of the format property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return Collections.unmodifiableList(format);
    }

    /**
     * Gets the value of the relation property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getRelation() {
        if (relation == null) {
            relation = new ArrayList<>();
        }
        return Collections.unmodifiableList(relation);
    }

    /**
     * Gets the value of the modified property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getModified() {
        if (modified == null) {
            modified = new ArrayList<>();
        }
        return Collections.unmodifiableList(modified);
    }

    @Override
    public String getModifiedStringValue() {
        if (modified != null && !modified.isEmpty()) {
            return modified.get(0).getFirstValue();
        }
        return null;
    }

    /**
     * Gets the value of the abstract property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<>();
        }
        return Collections.unmodifiableList(_abstract);
    }

    @Override
    public String getAbstractStringValue() {
        if (_abstract != null && !_abstract.isEmpty()) {
            return _abstract.get(0).getFirstValue();
        }
        return null;
    }

    /**
     * Gets the value of the spatial property.
     * (unmodifiable)
     */
    @Override
    public List<SimpleLiteral> getSpatial() {
        if (spatial == null) {
            spatial = new ArrayList<>();
        }
        return Collections.unmodifiableList(spatial);
    }

    /**
     * Gets the value of the boundingBox property.
     * (unmodifiable)
     */
    @Override
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return Collections.unmodifiableList(boundingBox);
    }


    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SummaryRecordType) {
            final SummaryRecordType that = (SummaryRecordType) object;

            boolean bbox = false;
            if (this.boundingBox == null && that.boundingBox == null ) {
                bbox = true;
            } else if (this.boundingBox != null && that.boundingBox != null && (this.boundingBox.size() == that.boundingBox.size())) {

                bbox = true;
                for (int i = 0; i < this.boundingBox.size(); i++) {
                    JAXBElement<? extends BoundingBoxType> thisJB = this.boundingBox.get(i);
                    JAXBElement<? extends BoundingBoxType> thatJB = that.boundingBox.get(i);
                    if (!Objects.equals(thisJB.getValue(), thatJB.getValue())) {
                        bbox = false;
                    }
                }
            }

            return Objects.equals(this.type,       that.type)       &&
                   Objects.equals(this.subject,    that.subject)    &&
                   Objects.equals(this._abstract,  that._abstract)  &&
                   Objects.equals(this.modified,   that.modified)   &&
                   Objects.equals(this.subject,    that.subject)    &&
                   Objects.equals(this.spatial,    that.spatial)    &&
                   Objects.equals(this.identifier, that.identifier) &&
                   Objects.equals(this.title,      that.title)      &&
                   Objects.equals(this.relation,   that.relation)   &&
                   Objects.equals(this.format,     that.format)     &&
                   bbox;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 11 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 11 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 11 * hash + (this.subject != null ? this.subject.hashCode() : 0);
        hash = 11 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 11 * hash + (this.relation != null ? this.relation.hashCode() : 0);
        hash = 11 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 11 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 11 * hash + (this.spatial != null ? this.spatial.hashCode() : 0);
        hash = 11 * hash + (this.boundingBox != null ? this.boundingBox.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[SummaryRecordType]").append('\n');

        if (identifier != null) {
            s.append("identifier: ").append('\n');
            for (SimpleLiteral jb : identifier) {
                s.append(jb).append('\n');
            }
        }
        if (title != null) {
            s.append("title: ").append('\n');
            for (SimpleLiteral jb : title) {
                s.append(jb).append('\n');
            }
        }
        if (type != null) {
            s.append("type: ").append(type).append('\n');
        }
        if (boundingBox != null) {
            s.append("bounding box: ").append('\n');
            for (JAXBElement<? extends BoundingBoxType> jb : boundingBox) {
                s.append(jb.getValue()).append('\n');
            }
        }
        if (_abstract != null) {
            s.append("abstract: ").append('\n');
            for (SimpleLiteral sl : _abstract) {
                s.append(sl).append('\n');
            }
        }
        if (subject != null) {
            s.append("subject: ").append('\n');
            for (SimpleLiteral sl : subject) {
                s.append(sl).append('\n');
            }
        }
        if (modified != null) {
            s.append("modified: ").append('\n');
            for (SimpleLiteral sl : modified) {
                s.append(sl).append('\n');
            }
        }
        if (spatial != null) {
            s.append("spatial: ").append('\n');
            for (SimpleLiteral sl : spatial) {
                s.append(sl).append('\n');
            }
        }
        if (format != null) {
            s.append("format: ").append('\n');
            for (SimpleLiteral jb : format) {
                s.append(jb).append('\n');
            }
        }
        if (relation != null) {
            s.append("relation: ").append('\n');
            for (SimpleLiteral jb : relation) {
                s.append(jb).append('\n');
            }
        }

        return s.toString();
    }

}
