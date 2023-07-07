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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ItemIconType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ItemIconType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractState" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}href" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ItemIconSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}ItemIconObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ItemIconType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractState",
    "href",
    "itemIconSimpleExtensionGroup",
    "itemIconObjectExtensionGroup"
})
public class ItemIconType
    extends AbstractObjectType
{

    @XmlElementRef(name = "abstractState", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractState;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String href;
    @XmlElement(name = "ItemIconSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> itemIconSimpleExtensionGroup;
    @XmlElement(name = "ItemIconObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> itemIconObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractState.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link List }{@code <}{@link ItemIconStateEnumType }{@code >}{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *
     */
    public JAXBElement<?> getAbstractState() {
        return abstractState;
    }

    /**
     * Définit la valeur de la propriété abstractState.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link List }{@code <}{@link ItemIconStateEnumType }{@code >}{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *
     */
    public void setAbstractState(JAXBElement<?> value) {
        this.abstractState = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the itemIconSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemIconSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemIconSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getItemIconSimpleExtensionGroup() {
        if (itemIconSimpleExtensionGroup == null) {
            itemIconSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.itemIconSimpleExtensionGroup;
    }

    /**
     * Gets the value of the itemIconObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemIconObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItemIconObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getItemIconObjectExtensionGroup() {
        if (itemIconObjectExtensionGroup == null) {
            itemIconObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.itemIconObjectExtensionGroup;
    }

}
