/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.Record;
import org.geotoolkit.csw.xml.Settable;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType;


/**
 *
 *             This type extends DCMIRecordType to add ows:BoundingBox;
 *             it may be used to specify a spatial envelope for the
 *             catalogued resource.
 *
 *
 * <p>Classe Java pour RecordType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}DCMIRecordType">
 *       &lt;sequence>
 *         &lt;element name="AnyText" type="{http://www.opengis.net/cat/csw/3.0}EmptyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TemporalExtent" type="{http://www.opengis.net/cat/csw/3.0}TemporalExtentType" maxOccurs="unbounded" minOccurs="0"/>
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
    "boundingBox",
    "temporalExtent"
})
@XmlRootElement(name = "Record")
public class RecordType extends DCMIRecordType implements Record, Settable {

    @XmlElement(name = "AnyText")
    protected List<EmptyType> anyText;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElement(name = "TemporalExtent")
    protected List<TemporalExtentType> temporalExtent;

    /**
     * Gets the value of the anyText property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anyText property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnyText().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EmptyType }
     *
     *
     */
    public List<EmptyType> getAnyText() {
        if (anyText == null) {
            anyText = new ArrayList<>();
        }
        return this.anyText;
    }

    /**
     * Gets the value of the boundingBox property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boundingBox property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoundingBox().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     * {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *
     *
     */
    @Override
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return this.boundingBox;
    }

    /**
     * Gets the value of the temporalExtent property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the temporalExtent property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTemporalExtent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TemporalExtentType }
     *
     *
     */
    public List<TemporalExtentType> getTemporalExtent() {
        if (temporalExtent == null) {
            temporalExtent = new ArrayList<>();
        }
        return this.temporalExtent;
    }

    /**
     * Transform the recordType into a SummaryRecordType.
     *
     * @return
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
     *
     * @return
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
        if (temporalExtent != null && !temporalExtent.isEmpty()) {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            for (TemporalExtentType tex : temporalExtent) {
                if (tex.begin != null) {
                    long time = tex.begin.value.toGregorianCalendar().getTimeInMillis();
                    max = Math.max(time, max);
                    min = Math.min(time, min);
                    time = tex.end.value.toGregorianCalendar().getTimeInMillis();
                    max = Math.max(time, max);
                    min = Math.min(time, min);
                }
            }
            return new long[]{min, max};
        }
        return new long[0];
    }
}
