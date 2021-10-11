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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.ows.xml.v200.WGS84BoundingBoxType;

/**
 *
 *             This type defines a brief representation of the common record
 *             format.  It extends AbstractRecordType to include only the
 *             dc:identifier and dc:type properties.
 *
 *
 * <p>Classe Java pour BriefRecordType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="BriefRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BriefRecordType", propOrder = {
    "identifier",
    "title",
    "type",
    "boundingBox"
})
public class BriefRecordType extends AbstractRecordType {

    @XmlElementRef(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    protected List<JAXBElement<SimpleLiteral>> identifier;
    @XmlElementRef(name = "title", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class)
    protected List<JAXBElement<SimpleLiteral>> title;
    @XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
    protected SimpleLiteral type;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends BoundingBoxType>> boundingBox;

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
    public BriefRecordType(SimpleLiteral identifier, SimpleLiteral title, final SimpleLiteral type, final List<BoundingBoxType> bboxes) {

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

        if (bboxes != null) {
            this.boundingBox = new ArrayList<>();
            final org.geotoolkit.ows.xml.v200.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
                } else if (bbox != null) {
                    this.boundingBox.add(owsFactory.createBoundingBox(bbox));
                }
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
    public BriefRecordType(final List<SimpleLiteral> identifiers, final List<SimpleLiteral> titles, final SimpleLiteral type, final List<BoundingBoxType> bboxes) {
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
        this.type       = type;

        if (bboxes != null) {
            this.boundingBox = new ArrayList<>();
            final org.geotoolkit.ows.xml.v200.ObjectFactory owsFactory = new org.geotoolkit.ows.xml.v200.ObjectFactory();
            for (BoundingBoxType bbox: bboxes) {
                if (bbox instanceof WGS84BoundingBoxType) {
                    this.boundingBox.add(owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox));
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
     */
    public SimpleLiteral getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     */
    public void setType(SimpleLiteral value) {
        this.type = value;
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
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return this.boundingBox;
    }
}
