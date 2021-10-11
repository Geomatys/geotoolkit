/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.dif.xml.v102;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 *
 * | DIF 9 | ECHO 10 | UMM | DIF 10 | Notes | | ---------------- |
 * ----------------- | ------------------- | ----------------- |
 * --------------------------------------- | | Data_Center_Name | - |
 * OrganizationName | Organization_Name | Changed to match similar UMM field
 * name |
 *
 *
 *
 * <p>
 * Classe Java pour OrganizationNameType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="OrganizationNameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Short_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Long_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UuidType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationNameType", propOrder = {
    "shortName",
    "longName"
})
public class OrganizationNameType {

    @XmlElement(name = "Short_Name", required = true)
    protected String shortName;
    @XmlElement(name = "Long_Name")
    protected String longName;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    public OrganizationNameType() {

    }

    public OrganizationNameType(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    /**
     * Obtient la valeur de la propriété shortName.
     *
     * @return possible object is {@link String }
     *
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Définit la valeur de la propriété shortName.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Obtient la valeur de la propriété longName.
     *
     * @return possible object is {@link String }
     *
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Définit la valeur de la propriété longName.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setLongName(String value) {
        this.longName = value;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return possible object is {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
