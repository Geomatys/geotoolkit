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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 Required by the UMM; use the "not applicable" platform and
 *                 instrument if a metadata record explicitly needs to not have a
 *                 platform. Metadata records found to be missing platforms by
 *                 software should use "undefined" until it can be confirmed
 *                 and moved to "not applicable"; "undefined" can be used for
 *                 cases where either the platform or instrument are unknown.
 *
 *                 | DIF 9            | ECHO            | UMM            | DIF 10        | Notes
 *                 | ---------------- |-----------------| ---------------|---------------| ----------------------------------------------- |
 *                 | Source_Name      | Platform        | Platform       | Platform      | Field was renamed                               |
 *                 |        -         | Type            |       -        | Type          | Added from ECHO, made required as it is in ECHO |
 *                 |        -         | Long_Name       |       -        |       -       | Made required                                   |
 *                 |        -         | Characteristics |       -        |       -       | Added From ECHO                                 |
 *                 | /DIF/Sensor_Name | Instrument      |       -        |       -       | Moved to Platform                               |
 *
 *
 * <p>Classe Java pour PlatformType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PlatformType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PlatformTypeEnum"/>
 *         &lt;element name="Short_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Long_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Characteristics" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}CharacteristicType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Instrument" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}InstrumentType" maxOccurs="unbounded"/>
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
@XmlType(name = "PlatformType", propOrder = {
    "type",
    "shortName",
    "longName",
    "characteristics",
    "instrument"
})
public class PlatformType {

    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected PlatformTypeEnum type;
    @XmlElement(name = "Short_Name", required = true)
    protected String shortName;
    @XmlElement(name = "Long_Name")
    protected String longName;
    @XmlElement(name = "Characteristics")
    protected List<CharacteristicType> characteristics;
    @XmlElement(name = "Instrument", required = true)
    protected List<InstrumentType> instrument;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link PlatformTypeEnum }
     *
     */
    public PlatformTypeEnum getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link PlatformTypeEnum }
     *
     */
    public void setType(PlatformTypeEnum value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété shortName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Définit la valeur de la propriété shortName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Obtient la valeur de la propriété longName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Définit la valeur de la propriété longName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLongName(String value) {
        this.longName = value;
    }

    /**
     * Gets the value of the characteristics property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the characteristics property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCharacteristics().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CharacteristicType }
     *
     *
     */
    public List<CharacteristicType> getCharacteristics() {
        if (characteristics == null) {
            characteristics = new ArrayList<CharacteristicType>();
        }
        return this.characteristics;
    }

    /**
     * Gets the value of the instrument property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the instrument property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstrument().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InstrumentType }
     *
     *
     */
    public List<InstrumentType> getInstrument() {
        if (instrument == null) {
            instrument = new ArrayList<InstrumentType>();
        }
        return this.instrument;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
