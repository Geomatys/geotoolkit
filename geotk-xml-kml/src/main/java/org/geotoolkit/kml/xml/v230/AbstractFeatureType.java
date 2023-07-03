/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

package org.geotoolkit.kml.xml.v230;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.atom.xml.AtomPersonConstruct;
import org.geotoolkit.atom.xml.Link;
import org.geotoolkit.xal.xml.v20.AddressDetails;


/**
 * <p>Classe Java pour AbstractFeatureType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}name" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}visibility" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}balloonVisibility" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}open" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2005/Atom}author" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2005/Atom}link" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}address" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressDetails" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}phoneNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractSnippetGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}description" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractViewGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractTimePrimitiveGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}styleUrl" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractStyleSelectorGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Region" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractExtendedDataGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractFeatureSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractFeatureObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractFeatureType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "name",
    "visibility",
    "balloonVisibility",
    "open",
    "author",
    "link",
    "address",
    "addressDetails",
    "phoneNumber",
    "abstractSnippetGroup",
    "description",
    "abstractViewGroup",
    "abstractTimePrimitiveGroup",
    "styleUrl",
    "abstractStyleSelectorGroup",
    "region",
    "abstractExtendedDataGroup",
    "abstractFeatureSimpleExtensionGroup",
    "abstractFeatureObjectExtensionGroup"
})
@XmlSeeAlso({
    TourType.class,
    PlacemarkType.class,
    NetworkLinkType.class,
    AbstractContainerType.class,
    AbstractOverlayType.class
})
public abstract class AbstractFeatureType
    extends AbstractObjectType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String name;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1")
    protected Boolean visibility;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "true")
    protected Boolean balloonVisibility;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "0")
    protected Boolean open;
    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    protected AtomPersonConstruct author;
    @XmlElement(namespace = "http://www.w3.org/2005/Atom")
    protected Link link;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String address;
    @XmlElement(name = "AddressDetails", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    protected AddressDetails addressDetails;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String phoneNumber;
    @XmlElementRef(name = "AbstractSnippetGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractSnippetGroup;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String description;
    @XmlElementRef(name = "AbstractViewGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractViewType> abstractViewGroup;
    @XmlElementRef(name = "AbstractTimePrimitiveGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractTimePrimitiveType> abstractTimePrimitiveGroup;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    @XmlSchemaType(name = "anyURI")
    protected String styleUrl;
    @XmlElementRef(name = "AbstractStyleSelectorGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected List<JAXBElement<? extends AbstractStyleSelectorType>> abstractStyleSelectorGroup;
    @XmlElement(name = "Region", namespace = "http://www.opengis.net/kml/2.2")
    protected RegionType region;
    @XmlElementRef(name = "AbstractExtendedDataGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractExtendedDataGroup;
    @XmlElement(name = "AbstractFeatureSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractFeatureSimpleExtensionGroup;
    @XmlElement(name = "AbstractFeatureObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractFeatureObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété visibility.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isVisibility() {
        return visibility;
    }

    /**
     * Définit la valeur de la propriété visibility.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setVisibility(Boolean value) {
        this.visibility = value;
    }

    /**
     * Obtient la valeur de la propriété balloonVisibility.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isBalloonVisibility() {
        return balloonVisibility;
    }

    /**
     * Définit la valeur de la propriété balloonVisibility.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setBalloonVisibility(Boolean value) {
        this.balloonVisibility = value;
    }

    /**
     * Obtient la valeur de la propriété open.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isOpen() {
        return open;
    }

    /**
     * Définit la valeur de la propriété open.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setOpen(Boolean value) {
        this.open = value;
    }

    /**
     * Obtient la valeur de la propriété author.
     *
     * @return
     *     possible object is
     *     {@link AtomPersonConstruct }
     *
     */
    public AtomPersonConstruct getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     *
     * @param value
     *     allowed object is
     *     {@link AtomPersonConstruct }
     *
     */
    public void setAuthor(AtomPersonConstruct value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété link.
     *
     * @return
     *     possible object is
     *     {@link Link }
     *
     */
    public Link getLink() {
        return link;
    }

    /**
     * Définit la valeur de la propriété link.
     *
     * @param value
     *     allowed object is
     *     {@link Link }
     *
     */
    public void setLink(Link value) {
        this.link = value;
    }

    /**
     * Obtient la valeur de la propriété address.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAddress() {
        return address;
    }

    /**
     * Définit la valeur de la propriété address.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Obtient la valeur de la propriété addressDetails.
     *
     * @return
     *     possible object is
     *     {@link AddressDetails }
     *
     */
    public AddressDetails getAddressDetails() {
        return addressDetails;
    }

    /**
     * Définit la valeur de la propriété addressDetails.
     *
     * @param value
     *     allowed object is
     *     {@link AddressDetails }
     *
     */
    public void setAddressDetails(AddressDetails value) {
        this.addressDetails = value;
    }

    /**
     * Obtient la valeur de la propriété phoneNumber.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Définit la valeur de la propriété phoneNumber.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Obtient la valeur de la propriété abstractSnippetGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *
     */
    public JAXBElement<?> getAbstractSnippetGroup() {
        return abstractSnippetGroup;
    }

    /**
     * Définit la valeur de la propriété abstractSnippetGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *
     */
    public void setAbstractSnippetGroup(JAXBElement<?> value) {
        this.abstractSnippetGroup = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété abstractViewGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractViewType> getAbstractViewGroup() {
        return abstractViewGroup;
    }

    /**
     * Définit la valeur de la propriété abstractViewGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CameraType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LookAtType }{@code >}
     *
     */
    public void setAbstractViewGroup(JAXBElement<? extends AbstractViewType> value) {
        this.abstractViewGroup = value;
    }

    /**
     * Obtient la valeur de la propriété abstractTimePrimitiveGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractTimePrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractTimePrimitiveType> getAbstractTimePrimitiveGroup() {
        return abstractTimePrimitiveGroup;
    }

    /**
     * Définit la valeur de la propriété abstractTimePrimitiveGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractTimePrimitiveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
     *
     */
    public void setAbstractTimePrimitiveGroup(JAXBElement<? extends AbstractTimePrimitiveType> value) {
        this.abstractTimePrimitiveGroup = value;
    }

    /**
     * Obtient la valeur de la propriété styleUrl.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStyleUrl() {
        return styleUrl;
    }

    /**
     * Définit la valeur de la propriété styleUrl.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStyleUrl(String value) {
        this.styleUrl = value;
    }

    /**
     * Gets the value of the abstractStyleSelectorGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractStyleSelectorGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractStyleSelectorGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     * {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends AbstractStyleSelectorType>> getAbstractStyleSelectorGroup() {
        if (abstractStyleSelectorGroup == null) {
            abstractStyleSelectorGroup = new ArrayList<JAXBElement<? extends AbstractStyleSelectorType>>();
        }
        return this.abstractStyleSelectorGroup;
    }

    /**
     * Obtient la valeur de la propriété region.
     *
     * @return
     *     possible object is
     *     {@link RegionType }
     *
     */
    public RegionType getRegion() {
        return region;
    }

    /**
     * Définit la valeur de la propriété region.
     *
     * @param value
     *     allowed object is
     *     {@link RegionType }
     *
     */
    public void setRegion(RegionType value) {
        this.region = value;
    }

    /**
     * Obtient la valeur de la propriété abstractExtendedDataGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExtendedDataType }{@code >}
     *
     */
    public JAXBElement<?> getAbstractExtendedDataGroup() {
        return abstractExtendedDataGroup;
    }

    /**
     * Définit la valeur de la propriété abstractExtendedDataGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExtendedDataType }{@code >}
     *
     */
    public void setAbstractExtendedDataGroup(JAXBElement<?> value) {
        this.abstractExtendedDataGroup = value;
    }

    /**
     * Gets the value of the abstractFeatureSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractFeatureSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractFeatureSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractFeatureSimpleExtensionGroup() {
        if (abstractFeatureSimpleExtensionGroup == null) {
            abstractFeatureSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractFeatureSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractFeatureObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractFeatureObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractFeatureObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractFeatureObjectExtensionGroup() {
        if (abstractFeatureObjectExtensionGroup == null) {
            abstractFeatureObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractFeatureObjectExtensionGroup;
    }

}
