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
import org.geotoolkit.csw.xml.Record;
import org.geotoolkit.csw.xml.Settable;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * This type extends DCMIRecordType to add ows:BoundingBox;
 * it may be used to specify a spatial envelope for the
 * catalogued resource.
 *          
 * 
 * <p>Java class for RecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}DCMIRecordType">
 *       &lt;sequence>
 *         &lt;element name="AnyText" type="{http://www.opengis.net/cat/csw/2.0.2}EmptyType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "RecordType", propOrder = {
    "anyText",
    "boundingBox"
})
@XmlRootElement(name = "Record")
public class RecordType extends DCMIRecordType implements Record, Settable {

    @XmlElement(name = "AnyText")
    private List<EmptyType> anyText;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;

    /**
     * An empty constructor used by JAXB
     */
    public RecordType() {
        
    }
    
    /**
     * Build a new Record TODO add contributor, source , spatial, right, relation
     */
    public RecordType(SimpleLiteral identifier, 
                      SimpleLiteral title, 
                      SimpleLiteral type, 
                      List<SimpleLiteral> subject, 
                      SimpleLiteral format, 
                      SimpleLiteral modified,
                      SimpleLiteral date,
                      SimpleLiteral _abstract,
                      List<BoundingBoxType> bboxes, 
                      SimpleLiteral creator, 
                      SimpleLiteral distributor, 
                      SimpleLiteral language, 
                      SimpleLiteral spatial,
                      SimpleLiteral references) {
        
        super(identifier, title,type, subject, format, modified, date, _abstract, creator, distributor, language, spatial, references);
        
        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        for (BoundingBoxType bbox: bboxes) {
            if (bbox instanceof WGS84BoundingBoxType)
                this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
            else
                this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
        
    }
    
    /**
     * Build a new Record TODO add contributor, source , spatial, right, relation
     */
    public RecordType(SimpleLiteral identifier, 
                      SimpleLiteral title, 
                      SimpleLiteral type, 
                      List<SimpleLiteral> subject, 
                      List<SimpleLiteral> formats, 
                      SimpleLiteral modified,
                      SimpleLiteral date,
                      List<SimpleLiteral> _abstract,
                      List<BoundingBoxType> bboxes, 
                      List<SimpleLiteral> creator, 
                      SimpleLiteral distributor, 
                      SimpleLiteral language, 
                      SimpleLiteral spatial,
                      SimpleLiteral references) {
        
        super(identifier, title,type, subject, formats, modified, date, _abstract, creator, distributor, language, spatial, references, null);
        
        this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        for (BoundingBoxType bbox: bboxes) {
            if (bbox instanceof WGS84BoundingBoxType)
                this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
            else
                this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
        
    }
    
    /**
     * Gets the value of the anyText property.
     * (unmodifiable)
     */
    public List<EmptyType> getAnyText() {
        if (anyText == null) {
            anyText = new ArrayList<EmptyType>();
        }
        return Collections.unmodifiableList(anyText);
    }

    /**
     * Gets the value of the boundingBox property.
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        }
        return boundingBox;
    }
    
    public void setBoundingBox(BoundingBoxType bbox) {
        if (boundingBox == null) {
            this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        }
        if (bbox instanceof WGS84BoundingBoxType) {
            this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
        } else {
            this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
    }

    public void setBoundingBox(List<BoundingBoxType> bbox) {
        if (boundingBox == null) {
            this.boundingBox = new ArrayList<JAXBElement<? extends BoundingBoxType>>();
        }
        if (bbox != null) {
            for (BoundingBoxType b : bbox) {
                if (b instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)b));
                } else {
                    this.boundingBox.add(owsFactory.createBoundingBox(b));
                }
            }
        }
    }
    
    public void setJBBoundingBox(List<JAXBElement<? extends BoundingBoxType>> bboxes) {
        this.boundingBox = bboxes;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (anyText != null && anyText.size() != 0) {
            s.append("anyText:");
            for (EmptyType e :anyText) {
                s.append(e.toString()).append('\n');
            }
        }
        
        if (boundingBox != null && boundingBox.size() != 0) {
            s.append("bounding boxes:");
            for (JAXBElement<? extends BoundingBoxType> bb: boundingBox) {
                BoundingBoxType bbox = bb.getValue();
                if (bbox != null)
                    s.append(bbox.toString()).append('\n');
            }
        }
        
        return s.toString();
    }
    
    /**
     * Transform the recordType into a SummaryRecordType.
     * 
     * @return
     */
    public SummaryRecordType toSummary() {
        List<BoundingBoxType> bboxes = new ArrayList<BoundingBoxType>();
        for (JAXBElement<? extends BoundingBoxType> bb: getBoundingBox()) {
                bboxes.add(bb.getValue());
        }
        return new SummaryRecordType(getIdentifier(), getTitle(), getType(), bboxes, getSubject(), getFormat(), getModified(), getAbstract());
    }
    
    /**
     * Transform the recordType into a BriefRecordType.
     * 
     * @return
     */
    public BriefRecordType toBrief() {
        List<BoundingBoxType> bboxes = new ArrayList<BoundingBoxType>();
        for (JAXBElement<? extends BoundingBoxType> bb: getBoundingBox()) {
                bboxes.add(bb.getValue());
        }
        return new BriefRecordType(getIdentifier(), getTitle(), getType(), bboxes);
    }
    
     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RecordType && super.equals(object)) {
            final RecordType that = (RecordType) object;
            
            boolean bbox = this.getBoundingBox().size() == that.getBoundingBox().size();
            
            //we verify that the two list contains the same object
            List<BoundingBoxType> obj = new ArrayList<BoundingBoxType>();
            for (JAXBElement<? extends BoundingBoxType> jb: boundingBox) {
                obj.add(jb.getValue());
            }
        
            for (JAXBElement<? extends BoundingBoxType> jb: that.boundingBox) {
                if (!obj.contains(jb.getValue())) {
                    bbox = false;
                }
            }
            return  Utilities.equals(this.anyText,   that.anyText)   &&
                    bbox;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
