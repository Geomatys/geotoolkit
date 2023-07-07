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
 * <p>Classe Java pour AbstractTourPrimitiveType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractTourPrimitiveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractTourPrimitiveObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "AbstractTourPrimitiveType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractTourPrimitiveSimpleExtensionGroup",
    "abstractTourPrimitiveObjectExtensionGroup"
})
@XmlSeeAlso({
    FlyToType.class,
    AnimatedUpdateType.class,
    SoundCueType.class,
    TourControlType.class,
    WaitType.class
})
public class AbstractTourPrimitiveType
    extends AbstractObjectType
{

    @XmlElement(name = "AbstractTourPrimitiveSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractTourPrimitiveSimpleExtensionGroup;
    @XmlElement(name = "AbstractTourPrimitiveObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractTourPrimitiveObjectExtensionGroup;

    /**
     * Gets the value of the abstractTourPrimitiveSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractTourPrimitiveSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractTourPrimitiveSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractTourPrimitiveSimpleExtensionGroup() {
        if (abstractTourPrimitiveSimpleExtensionGroup == null) {
            abstractTourPrimitiveSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractTourPrimitiveSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractTourPrimitiveObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractTourPrimitiveObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractTourPrimitiveObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractTourPrimitiveObjectExtensionGroup() {
        if (abstractTourPrimitiveObjectExtensionGroup == null) {
            abstractTourPrimitiveObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractTourPrimitiveObjectExtensionGroup;
    }

}
