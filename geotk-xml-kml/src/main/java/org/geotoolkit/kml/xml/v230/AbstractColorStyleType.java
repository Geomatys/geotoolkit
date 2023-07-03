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
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour AbstractColorStyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractColorStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractSubStyleType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}color" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}abstractColorMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractColorStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractColorStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AbstractColorStyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "color",
    "abstractColorMode",
    "abstractColorStyleSimpleExtensionGroup",
    "abstractColorStyleObjectExtensionGroup"
})
@XmlSeeAlso({
    LineStyleType.class,
    LabelStyleType.class,
    PolyStyleType.class,
    IconStyleType.class
})
public abstract class AbstractColorStyleType
    extends AbstractSubStyleType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", type = String.class, defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] color;
    @XmlElementRef(name = "abstractColorMode", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<?> abstractColorMode;
    @XmlElement(name = "AbstractColorStyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractColorStyleSimpleExtensionGroup;
    @XmlElement(name = "AbstractColorStyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractColorStyleObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété color.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public byte[] getColor() {
        return color;
    }

    /**
     * Définit la valeur de la propriété color.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setColor(byte[] value) {
        this.color = value;
    }

    /**
     * Obtient la valeur de la propriété abstractColorMode.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ColorModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public JAXBElement<?> getAbstractColorMode() {
        return abstractColorMode;
    }

    /**
     * Définit la valeur de la propriété abstractColorMode.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ColorModeEnumType }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *
     */
    public void setAbstractColorMode(JAXBElement<?> value) {
        this.abstractColorMode = value;
    }

    /**
     * Gets the value of the abstractColorStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractColorStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractColorStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractColorStyleSimpleExtensionGroup() {
        if (abstractColorStyleSimpleExtensionGroup == null) {
            abstractColorStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractColorStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractColorStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractColorStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractColorStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractColorStyleObjectExtensionGroup() {
        if (abstractColorStyleObjectExtensionGroup == null) {
            abstractColorStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractColorStyleObjectExtensionGroup;
    }

}
