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
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;


/**
 *
 *             This type defines a summary representation of the common record
 *             format.  It extends AbstractRecordType to include the core
 *             properties.
 *
 *
 * <p>Classe Java pour SummaryRecordType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SummaryRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}AbstractRecordType">
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
    "boundingBox",
    "temporalExtent"
})
public class SummaryRecordType extends AbstractRecordType {

    @XmlElementRef(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    protected List<JAXBElement<SimpleLiteral>> identifier;
    @XmlElementRef(name = "title", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    protected List<JAXBElement<SimpleLiteral>> title;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    protected SimpleLiteral type;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    protected List<SimpleLiteral> subject;
    @XmlElementRef(name = "format", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false)
    protected List<JAXBElement<SimpleLiteral>> format;
    @XmlElementRef(name = "relation", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false)
    protected List<JAXBElement<SimpleLiteral>> relation;
    @XmlElement(namespace = "http://purl.org/dc/terms/")
    protected List<SimpleLiteral> modified;
    @XmlElement(name = "abstract", namespace = "http://purl.org/dc/terms/")
    protected List<SimpleLiteral> _abstract;
    @XmlElement(namespace = "http://purl.org/dc/terms/")
    protected List<SimpleLiteral> spatial;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElement(name = "TemporalExtent")
    protected List<TemporalExtentType> temporalExtent;

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

        org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory dcFactory = new org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory();
        if (identifier != null) {
            this.identifier = new ArrayList<>();
            this.identifier.add(dcFactory.createIdentifier(identifier));
        }

        if (title != null) {
            this.title = new ArrayList<>();
            this.title.add(dcFactory.createTitle(title));
        }

        this.type = type;

        this.boundingBox = new ArrayList<>();
        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v200.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
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
            this.format.add(dcFactory.createFormat(format));
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

        org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory dcFactory = new org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory();
        if (identifier != null) {
            this.identifier = new ArrayList<>();
            this.identifier.add(dcFactory.createIdentifier(identifier));
        }

        if (title != null) {
            this.title = new ArrayList<>();
            this.title.add(dcFactory.createTitle(title));
        }

        this.type = type;

        this.boundingBox = new ArrayList<>();
        if (bboxes != null) {
            final org.geotoolkit.ows.xml.v200.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType) bbox));
                } else if (bbox != null) {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
            }
        }
        this.subject = subject;

        if (formats != null) {
            this.format = new ArrayList<>();
            for (SimpleLiteral f : formats) {
                this.format.add(dcFactory.createFormat(f));
            }
        }

        if (modified != null) {
            this.modified = new ArrayList<>();
            this.modified.add(modified);
        }
        this._abstract = _abstract;
    }

    /**
     * Build a new Summary record TODO add relation and spatial
     */
    public SummaryRecordType(final List<SimpleLiteral> identifiers, final List<SimpleLiteral> titles, final SimpleLiteral type, final List<BoundingBoxType> bboxes,
            final List<SimpleLiteral> subject, final List<SimpleLiteral> formats, final List<SimpleLiteral> modified, final List<SimpleLiteral> _abstract){

        org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory dcFactory = new org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory();
        if (identifiers != null) {
            this.identifier = new ArrayList<>();
            for (SimpleLiteral f : identifiers) {
                this.identifier.add(dcFactory.createIdentifier(f));
            }
        }
        if (titles != null) {
            this.title = new ArrayList<>();
            for (SimpleLiteral f : titles) {
                this.title.add(dcFactory.createTitle(f));
            }
        }
        this.type = type;
        this.modified = modified;
        this._abstract = _abstract;
        this.subject = subject;
        if (formats != null) {
            this.format = new ArrayList<>();
            for (SimpleLiteral f : formats) {
                this.format.add(dcFactory.createFormat(f));
            }
        }

        if (bboxes != null) {
            this.boundingBox = new ArrayList<>();
            final org.geotoolkit.ows.xml.v200.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
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
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identifier property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentifier().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     *
     *
     */
    public List<JAXBElement<SimpleLiteral>> getIdentifier() {
        if (identifier == null) {
            identifier = new ArrayList<>();
        }
        return this.identifier;
    }

    /**
     * Gets the value of the title property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the title property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     *
     *
     */
    public List<JAXBElement<SimpleLiteral>> getTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        return this.title;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link SimpleLiteral }
     *
     */
    public SimpleLiteral getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link SimpleLiteral }
     *
     */
    public void setType(SimpleLiteral value) {
        this.type = value;
    }

    /**
     * Gets the value of the subject property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subject property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubject().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleLiteral }
     *
     *
     */
    public List<SimpleLiteral> getSubject() {
        if (subject == null) {
            subject = new ArrayList<>();
        }
        return this.subject;
    }

    /**
     * Gets the value of the format property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the format property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFormat().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     *
     *
     */
    public List<JAXBElement<SimpleLiteral>> getFormat() {
        if (format == null) {
            format = new ArrayList<>();
        }
        return this.format;
    }

    /**
     * Gets the value of the relation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     * {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}
     *
     *
     */
    public List<JAXBElement<SimpleLiteral>> getRelation() {
        if (relation == null) {
            relation = new ArrayList<>();
        }
        return this.relation;
    }

    /**
     * Gets the value of the modified property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modified property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModified().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleLiteral }
     *
     *
     */
    public List<SimpleLiteral> getModified() {
        if (modified == null) {
            modified = new ArrayList<>();
        }
        return this.modified;
    }

    /**
     * Gets the value of the abstract property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstract property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstract().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleLiteral }
     *
     *
     */
    public List<SimpleLiteral> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<>();
        }
        return this._abstract;
    }

    /**
     * Gets the value of the spatial property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spatial property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpatial().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleLiteral }
     *
     *
     */
    public List<SimpleLiteral> getSpatial() {
        if (spatial == null) {
            spatial = new ArrayList<>();
        }
        return this.spatial;
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

}
