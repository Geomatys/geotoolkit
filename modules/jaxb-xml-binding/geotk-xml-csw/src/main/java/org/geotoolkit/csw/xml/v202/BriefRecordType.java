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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * This type defines a brief representation of the common record
 * format.  It extends AbstractRecordType to include only the
 * dc:identifier and dc:type properties.
 *          
 * 
 * <p>Java class for BriefRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BriefRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "BriefRecordType", propOrder = {
    "identifier",
    "title",
    "type",
    "boundingBox"
})
@XmlRootElement(name = "BriefRecord")
public class BriefRecordType extends AbstractRecordType {

    @XmlElement(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> identifier;
    @XmlElement(name = "title", namespace = "http://purl.org/dc/elements/1.1/")
    private List<SimpleLiteral> title;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    private SimpleLiteral type;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;

    /**
     * An empty constructor used by JAXB
     */
    BriefRecordType() {
    }
    
    /**
     * Build a new brief record.
     * 
     * @param identifier
     * @param title
     * @param type
     * @param bbox
     */
    public BriefRecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, List<BoundingBoxType> bboxes) {
        
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
        if (bboxes != null) {
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType)
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
                else if (bbox != null)
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
            }
        }
    }

    /**
     * Build a new brief record (full possibility).
     *
     * @param identifier
     * @param title
     * @param type
     * @param bbox
     */
    public BriefRecordType(List<SimpleLiteral> identifier, List<SimpleLiteral> title, SimpleLiteral type, List<BoundingBoxType> bboxes) {

        this.identifier = identifier;
        this.title      = title;
        this.type       = type;

        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        if (bboxes != null) {
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType)
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
                else if (bbox != null)
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
            }
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
     * Gets the value of the boundingBox property.
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
        if (object instanceof BriefRecordType) {
            final BriefRecordType that = (BriefRecordType) object;

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
                   Utilities.equals(this.identifier, that.identifier) &&
                   Utilities.equals(this.title,      that.title)      &&
                   bbox;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 71 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 71 * hash + (this.boundingBox != null ? this.boundingBox.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[BriefRecordType]").append('\n');

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
        return s.toString();
    }

}
