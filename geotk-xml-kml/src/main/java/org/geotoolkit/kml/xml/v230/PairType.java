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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PairType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PairType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractKey" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}styleUrl" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractStyleSelectorGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PairSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PairObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PairType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractKey",
    "styleUrl",
    "abstractStyleSelectorGroup",
    "pairSimpleExtensionGroup",
    "pairObjectExtensionGroup"
})
public class PairType
    extends AbstractObjectType
{

    @XmlElementRef(name = "abstractKey", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractKey;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    @XmlSchemaType(name = "anyURI")
    protected String styleUrl;
    @XmlElementRef(name = "AbstractStyleSelectorGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractStyleSelectorType> abstractStyleSelectorGroup;
    @XmlElement(name = "PairSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> pairSimpleExtensionGroup;
    @XmlElement(name = "PairObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> pairObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractKey.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link StyleStateEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractKey() {
        return abstractKey;
    }

    /**
     * Définit la valeur de la propriété abstractKey.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link StyleStateEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractKey(JAXBElement<?> value) {
        this.abstractKey = value;
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
     * Obtient la valeur de la propriété abstractStyleSelectorGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractStyleSelectorType> getAbstractStyleSelectorGroup() {
        return abstractStyleSelectorGroup;
    }

    /**
     * Définit la valeur de la propriété abstractStyleSelectorGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
     *     {@link JAXBElement }{@code <}{@link StyleType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}
     *
     */
    public void setAbstractStyleSelectorGroup(JAXBElement<? extends AbstractStyleSelectorType> value) {
        this.abstractStyleSelectorGroup = value;
    }

    /**
     * Gets the value of the pairSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pairSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPairSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPairSimpleExtensionGroup() {
        if (pairSimpleExtensionGroup == null) {
            pairSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.pairSimpleExtensionGroup;
    }

    /**
     * Gets the value of the pairObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pairObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPairObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPairObjectExtensionGroup() {
        if (pairObjectExtensionGroup == null) {
            pairObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.pairObjectExtensionGroup;
    }

}
