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
 * <p>Classe Java pour TimeSpanType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TimeSpanType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractTimePrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}begin" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}end" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TimeSpanSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}TimeSpanObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TimeSpanType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "begin",
    "end",
    "timeSpanSimpleExtensionGroup",
    "timeSpanObjectExtensionGroup"
})
public class TimeSpanType
    extends AbstractTimePrimitiveType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String begin;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String end;
    @XmlElement(name = "TimeSpanSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> timeSpanSimpleExtensionGroup;
    @XmlElement(name = "TimeSpanObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> timeSpanObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété begin.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBegin() {
        return begin;
    }

    /**
     * Définit la valeur de la propriété begin.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBegin(String value) {
        this.begin = value;
    }

    /**
     * Obtient la valeur de la propriété end.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnd() {
        return end;
    }

    /**
     * Définit la valeur de la propriété end.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnd(String value) {
        this.end = value;
    }

    /**
     * Gets the value of the timeSpanSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeSpanSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeSpanSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getTimeSpanSimpleExtensionGroup() {
        if (timeSpanSimpleExtensionGroup == null) {
            timeSpanSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.timeSpanSimpleExtensionGroup;
    }

    /**
     * Gets the value of the timeSpanObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeSpanObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeSpanObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getTimeSpanObjectExtensionGroup() {
        if (timeSpanObjectExtensionGroup == null) {
            timeSpanObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.timeSpanObjectExtensionGroup;
    }

}
