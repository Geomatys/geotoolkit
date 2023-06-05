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
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.Record;
import org.geotoolkit.csw.xml.Settable;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;


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
 * @module
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
    public RecordType(final SimpleLiteral identifier,
                      final SimpleLiteral title,
                      final SimpleLiteral type,
                      final List<SimpleLiteral> subject,
                      final SimpleLiteral format,
                      final SimpleLiteral modified,
                      final SimpleLiteral date,
                      final SimpleLiteral _abstract,
                      final List<BoundingBoxType> bboxes,
                      final SimpleLiteral creator,
                      final SimpleLiteral distributor,
                      final SimpleLiteral language,
                      final SimpleLiteral spatial,
                      final SimpleLiteral references) {

        super(identifier, title,type, subject, format, modified, date, _abstract, creator, distributor, language, spatial, references);

        this.boundingBox = new ArrayList<>();
        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
                } else {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }

    }

    /**
     * Build a new Record TODO add contributor, source , spatial, right, relation
     */
    public RecordType(final SimpleLiteral identifier,
                      final SimpleLiteral title,
                      final SimpleLiteral type,
                      final List<SimpleLiteral> subject,
                      final List<SimpleLiteral> formats,
                      final SimpleLiteral modified,
                      final SimpleLiteral date,
                      final List<SimpleLiteral> _abstract,
                      final List<BoundingBoxType> bboxes,
                      final List<SimpleLiteral> creator,
                      final SimpleLiteral distributor,
                      final SimpleLiteral language,
                      final SimpleLiteral spatial,
                      final SimpleLiteral references) {

        super(identifier, title,type, subject, formats, modified, date, _abstract, creator, distributor, language, spatial, references, null);

        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            this.boundingBox = new ArrayList<>();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
                } else {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }
    }

    /**
     * Gets the value of the anyText property.
     * (unmodifiable)
     */
    public List<EmptyType> getAnyText() {
        if (anyText == null) {
            anyText = new ArrayList<>();
        }
        return anyText;
    }

    /**
     * Gets the value of the boundingBox property.
     */
    @Override
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return boundingBox;
    }

    public void setBoundingBox(final BoundingBoxType bbox) {
        if (boundingBox == null) {
            this.boundingBox = new ArrayList<>();
        }
        final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
        if (bbox instanceof WGS84BoundingBoxType) {
            this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
        } else {
            this.boundingBox.add(owsFactory.createBoundingBox(bbox));
        }
    }

    public void setSimpleBoundingBox(final List<BoundingBoxType> bbox) {
        if (boundingBox == null) {
            this.boundingBox = new ArrayList<>();
        }
        if (bbox != null) {
            final org.geotoolkit.ows.xml.v100.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v100.ObjectFactory();
            for (BoundingBoxType b : bbox) {
                if (b instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)b));
                } else {
                    this.boundingBox.add(owsFactory.createBoundingBox(b));
                }
            }
        }
    }

    public void setBoundingBox(final List<JAXBElement<? extends BoundingBoxType>> bboxes) {
        this.boundingBox = bboxes;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (anyText != null && !anyText.isEmpty()) {
            s.append("anyText:");
            for (EmptyType e :anyText) {
                s.append(e.toString()).append('\n');
            }
        }

        if (boundingBox != null && !boundingBox.isEmpty()) {
            s.append("bounding box(").append(boundingBox.size()).append("): \n");
            for (JAXBElement<? extends BoundingBoxType> jb : boundingBox) {
                s.append(jb.getValue()).append('\n');
            }
        }

        return s.toString();
    }

    /**
     * Transform the recordType into a SummaryRecordType.
     */
    @Override
    public SummaryRecordType toSummary() {
        List<BoundingBoxType> bboxes = new ArrayList<>();
        for (JAXBElement<? extends BoundingBoxType> bb: getBoundingBox()) {
                bboxes.add(bb.getValue());
        }
        return new SummaryRecordType(getIdentifier(), getTitle(), getType(), bboxes, getSubject(), getFormat(), getModified(), getAbstract());
    }

    /**
     * Transform the recordType into a BriefRecordType.
     */
    @Override
    public BriefRecordType toBrief() {
        List<BoundingBoxType> bboxes = new ArrayList<>();
        for (JAXBElement<? extends BoundingBoxType> bb: getBoundingBox()) {
                bboxes.add(bb.getValue());
        }
        return new BriefRecordType(getIdentifier(), getTitle(), getType(), bboxes);
    }

    @Override
    public long[] getTemporalExtentRange() {
        return new long[0];
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
            List<BoundingBoxType> obj = new ArrayList<>();
            for (JAXBElement<? extends BoundingBoxType> jb: boundingBox) {
                obj.add(jb.getValue());
            }

            for (JAXBElement<? extends BoundingBoxType> jb: that.boundingBox) {
                if (!obj.contains(jb.getValue())) {
                    bbox = false;
                }
            }
            return  Objects.equals(this.anyText,   that.anyText)   &&
                    bbox;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
