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
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour AbstractSubStyleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractSubStyleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractSubStyleSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractSubStyleObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AbstractSubStyleType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractSubStyleSimpleExtensionGroup",
    "abstractSubStyleObjectExtensionGroup"
})
@XmlSeeAlso({
    ListStyleType.class,
    AbstractColorStyleType.class,
    BalloonStyleType.class
})
public abstract class AbstractSubStyleType
    extends AbstractObjectType
{

    @XmlElement(name = "AbstractSubStyleSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractSubStyleSimpleExtensionGroup;
    @XmlElement(name = "AbstractSubStyleObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractSubStyleObjectExtensionGroup;

    /**
     * Gets the value of the abstractSubStyleSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractSubStyleSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractSubStyleSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractSubStyleSimpleExtensionGroup() {
        if (abstractSubStyleSimpleExtensionGroup == null) {
            abstractSubStyleSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractSubStyleSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractSubStyleObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractSubStyleObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractSubStyleObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractSubStyleObjectExtensionGroup() {
        if (abstractSubStyleObjectExtensionGroup == null) {
            abstractSubStyleObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractSubStyleObjectExtensionGroup;
    }

}