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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour ListStyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ListStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractSubStyleType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractListItemType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}bgColor" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ItemIcon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}maxSnippetLines" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ListStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ListStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ListStyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractListItemType",
    "bgColor",
    "itemIcon",
    "maxSnippetLines",
    "listStyleSimpleExtensionGroup",
    "listStyleObjectExtensionGroup"
})
public class ListStyleType
    extends AbstractSubStyleType
{

    @XmlElementRef(name = "abstractListItemType", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractListItemType;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", type = String.class, defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] bgColor;
    @XmlElement(name = "ItemIcon", namespace = "http://www.opengis.net/kml/2.2")
    protected List<ItemIconType> itemIcon;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "2")
    protected Integer maxSnippetLines;
    @XmlElement(name = "ListStyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> listStyleSimpleExtensionGroup;
    @XmlElement(name = "ListStyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> listStyleObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractListItemType.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ListItemTypeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractListItemType() {
        return abstractListItemType;
    }

    /**
     * Définit la valeur de la propriété abstractListItemType.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ListItemTypeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractListItemType(JAXBElement<?> value) {
        this.abstractListItemType = value;
    }

    /**
     * Obtient la valeur de la propriété bgColor.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getBgColor() {
        return bgColor;
    }

    /**
     * Définit la valeur de la propriété bgColor.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBgColor(byte[] value) {
        this.bgColor = value;
    }

    /**
     * Gets the value of the itemIcon property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemIcon property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemIcon().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemIconType }
     *
     *
     */
    public List<ItemIconType> getItemIcon() {
        if (itemIcon == null) {
            itemIcon = new ArrayList<ItemIconType>();
        }
        return this.itemIcon;
    }

    /**
     * Obtient la valeur de la propriété maxSnippetLines.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxSnippetLines() {
        return maxSnippetLines;
    }

    /**
     * Définit la valeur de la propriété maxSnippetLines.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxSnippetLines(Integer value) {
        this.maxSnippetLines = value;
    }

    /**
     * Gets the value of the listStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getListStyleSimpleExtensionGroup() {
        if (listStyleSimpleExtensionGroup == null) {
            listStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.listStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the listStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getListStyleObjectExtensionGroup() {
        if (listStyleObjectExtensionGroup == null) {
            listStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.listStyleObjectExtensionGroup;
    }

}
