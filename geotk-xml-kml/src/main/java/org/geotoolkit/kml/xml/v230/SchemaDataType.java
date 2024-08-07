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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SchemaDataType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SchemaDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}SimpleData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}SimpleArrayData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}SchemaDataExtension" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="schemaUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchemaDataType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "simpleData",
    "simpleArrayData",
    "schemaDataExtension"
})
public class SchemaDataType
    extends AbstractObjectType
{

    @XmlElement(name = "SimpleData", namespace = "http://www.opengis.net/kml/2.2")
    protected List<SimpleDataType> simpleData;
    @XmlElement(name = "SimpleArrayData", namespace = "http://www.opengis.net/kml/2.2")
    protected List<SimpleArrayDataType> simpleArrayData;
    @XmlElement(name = "SchemaDataExtension", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> schemaDataExtension;
    @XmlAttribute(name = "schemaUrl")
    @XmlSchemaType(name = "anyURI")
    protected String schemaUrl;

    /**
     * Gets the value of the simpleData property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleData property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleData().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleDataType }
     *
     *
     */
    public List<SimpleDataType> getSimpleData() {
        if (simpleData == null) {
            simpleData = new ArrayList<SimpleDataType>();
        }
        return this.simpleData;
    }

    /**
     * Gets the value of the simpleArrayData property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleArrayData property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleArrayData().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleArrayDataType }
     *
     *
     */
    public List<SimpleArrayDataType> getSimpleArrayData() {
        if (simpleArrayData == null) {
            simpleArrayData = new ArrayList<SimpleArrayDataType>();
        }
        return this.simpleArrayData;
    }

    /**
     * Gets the value of the schemaDataExtension property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the schemaDataExtension property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSchemaDataExtension().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getSchemaDataExtension() {
        if (schemaDataExtension == null) {
            schemaDataExtension = new ArrayList<Object>();
        }
        return this.schemaDataExtension;
    }

    /**
     * Obtient la valeur de la propriété schemaUrl.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSchemaUrl() {
        return schemaUrl;
    }

    /**
     * Définit la valeur de la propriété schemaUrl.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSchemaUrl(String value) {
        this.schemaUrl = value;
    }

}
