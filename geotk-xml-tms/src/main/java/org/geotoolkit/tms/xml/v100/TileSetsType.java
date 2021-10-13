/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TileSetsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TileSetsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TileSet" type="{}TileSetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="profile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TileSetsType", propOrder = {
    "tileSet"
})
public class TileSetsType {

    @XmlElement(name = "TileSet")
    protected List<TileSetType> tileSet;
    @XmlAttribute(name = "profile")
    protected String profile;

    /**
     * Gets the value of the tileSet property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tileSet property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTileSet().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TileSetType }
     *
     *
     */
    public List<TileSetType> getTileSet() {
        if (tileSet == null) {
            tileSet = new ArrayList<TileSetType>();
        }
        return this.tileSet;
    }

    /**
     * Gets the value of the profile property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProfile(String value) {
        this.profile = value;
    }

}
