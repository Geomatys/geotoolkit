/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
import org.geotoolkit.util.Utilities;


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
    public SummaryRecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, List<BoundingBoxType> bboxes,
            List<SimpleLiteral> subject, SimpleLiteral format, SimpleLiteral modified, SimpleLiteral _abstract){
        
        this.identifier = new ArrayList<SimpleLiteral>();
        if (identifier == null)
            identifier = new SimpleLiteral();
        this.identifier.add(identifier);
        
        this.title = new ArrayList<SimpleLiteral>();
        if (title == null)
            title = new SimpleLiteral();
        this.title.add(title);
        
        this.type = type;
        
        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        for (BoundingBoxType bbox: bboxes) {
            if (bbox instanceof WGS84BoundingBoxType)
                this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
            else if (bbox != null)
                this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
        this.subject = subject;
        
        this.format = new ArrayList<SimpleLiteral>();
        if (format != null)
            this.format.add(format);
        
        this.modified = new ArrayList<SimpleLiteral>();
        if (modified != null)
            this.modified.add(modified);
        
        this._abstract = new ArrayList<SimpleLiteral>();
        if (_abstract != null)
            this._abstract.add(_abstract);
    }
    
    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, List<BoundingBoxType> bboxes,
            List<SimpleLiteral> subject, List<SimpleLiteral> formats, SimpleLiteral modified, List<SimpleLiteral> _abstract){
        
        this.identifier = new ArrayList<SimpleLiteral>();
        if (identifier == null)
            identifier = new SimpleLiteral();
        this.identifier.add(identifier);
        
        this.title = new ArrayList<SimpleLiteral>();
        if (title == null)
            title = new SimpleLiteral();
        this.title.add(title);
        
        this.type = type;
        
        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        for (BoundingBoxType bbox: bboxes) {
            if (bbox instanceof WGS84BoundingBoxType)
                this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
            else if (bbox != null)
                this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
        this.subject = subject;
        
        this.format = formats;
        
        this.modified = new ArrayList<SimpleLiteral>();
        if (modified != null)
            this.modified.add(modified);
        
        this._abstract = _abstract;
    }

    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(List<SimpleLiteral> identifier, List<SimpleLiteral> title, SimpleLiteral type, List<BoundingBoxType> bboxes,
            List<SimpleLiteral> subject, List<SimpleLiteral> format, List<SimpleLiteral> modified, List<SimpleLiteral> _abstract){

        this.identifier = identifier;
        this.title = title;
        this.type = type;
        this.modified = modified;
        this._abstract = _abstract;
        this.subject = subject;
        this.format = format;

        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        for (BoundingBoxType bbox: bboxes) {
            if (bbox instanceof WGS84BoundingBoxType)
                this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
            else if (bbox != null)
                this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
    }
    
    
    /**
     * Gets the value of the identifier property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getIdentifier() {
        if (identifier == null) {
            identifier = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(identifier);
    }

    /**
     * Gets the value of the title property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getTitle() {
        if (title == null) {
            title = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(title);
    }

    /**
     * Gets the value of the type property.
     */
    public SimpleLiteral getType() {
        return type;
    }

    /**
     * Gets the value of the subject property.
     * (unmodifiable) 
     */
    public List<SimpleLiteral> getSubject() {
        if (subject == null) {
            subject = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(subject);
    }

    /**
     * Gets the value of the format property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getFormat() {
        if (format == null) {
            format = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(format);
    }

    /**
     * Gets the value of the relation property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getRelation() {
        if (relation == null) {
            relation = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(relation);
    }

    /**
     * Gets the value of the modified property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getModified() {
        if (modified == null) {
            modified = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(modified);
    }

    /**
     * Gets the value of the abstract property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(_abstract);
    }

    /**
     * Gets the value of the spatial property.
     * (unmodifiable)
     */
    public List<SimpleLiteral> getSpatial() {
        if (spatial == null) {
            spatial = new ArrayList<SimpleLiteral>();
        }
        return Collections.unmodifiableList(spatial);
    }

    /**
     * Gets the value of the boundingBox property.
     * (unmodifiable)
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
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
                    if (!Utilities.equals(thisJB.getValue(), thatJB.getValue())) {
                        bbox = false;
                    }
                }
            }

            return Utilities.equals(this.type,       that.type)       &&
                   Utilities.equals(this.subject,    that.subject)    &&
                   Utilities.equals(this._abstract,  that._abstract)  &&
                   Utilities.equals(this.modified,   that.modified)   &&
                   Utilities.equals(this.subject,    that.subject)    &&
                   Utilities.equals(this.spatial,    that.spatial)    &&
                   Utilities.equals(this.identifier, that.identifier) &&
                   Utilities.equals(this.title,      that.title)      &&
                   Utilities.equals(this.relation,   that.relation)   &&
                   Utilities.equals(this.format,     that.format)     &&
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
