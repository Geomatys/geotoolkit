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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PolyStyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PolyStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractColorStyleType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}fill" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}outline" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolyStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}PolyStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "PolyStyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "fill",
    "outline",
    "polyStyleSimpleExtensionGroup",
    "polyStyleObjectExtensionGroup"
})
public class PolyStyleType
    extends AbstractColorStyleType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1")
    protected Boolean fill;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1")
    protected Boolean outline;
    @XmlElement(name = "PolyStyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> polyStyleSimpleExtensionGroup;
    @XmlElement(name = "PolyStyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> polyStyleObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété fill.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isFill() {
        return fill;
    }

    /**
     * Définit la valeur de la propriété fill.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setFill(Boolean value) {
        this.fill = value;
    }

    /**
     * Obtient la valeur de la propriété outline.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isOutline() {
        return outline;
    }

    /**
     * Définit la valeur de la propriété outline.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setOutline(Boolean value) {
        this.outline = value;
    }

    /**
     * Gets the value of the polyStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polyStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolyStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getPolyStyleSimpleExtensionGroup() {
        if (polyStyleSimpleExtensionGroup == null) {
            polyStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.polyStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the polyStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the polyStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolyStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getPolyStyleObjectExtensionGroup() {
        if (polyStyleObjectExtensionGroup == null) {
            polyStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.polyStyleObjectExtensionGroup;
    }

}
