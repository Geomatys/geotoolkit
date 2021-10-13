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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 | DIF 9       | ECHO             | UMM             | DIF 10            | Notes                       |
 *                 | ----------- |------------------|-----------------| ----------------- |---------------------------- |
 *                 | Sensor_Name | Instrument       | Instrument      | Instrument        | Moved to within Platform    |
 *                 |      -      | Technique        | Technique       | Technique         | Added from ECHO             |
 *                 |      -      | NumberOfSensors  | NumberOfSensors | Number_Of_Sensors | Added from ECHO             |
 *                 |      -      | Characteristics  | Characteristics | Characteristics   | Added from ECHO             |
 *                 |      -      | OperationalMode  | OperationalMode | Operational_Mode  | Added from ECHO             |
 *                 |      -      | Sensor           | Sensor          | Sensor            | Added from ECHO             |
 *
 *
 * <p>Classe Java pour InstrumentType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="InstrumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Short_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Long_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Technique" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NumberOfSensors" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Characteristics" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}CharacteristicType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="OperationalMode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Sensor" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}SensorType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "InstrumentType", propOrder = {
    "shortName",
    "longName",
    "technique",
    "numberOfSensors",
    "characteristics",
    "operationalMode",
    "sensor"
})
public class InstrumentType {

    @XmlElement(name = "Short_Name", required = true)
    protected String shortName;
    @XmlElement(name = "Long_Name")
    protected String longName;
    @XmlElement(name = "Technique")
    protected String technique;
    @XmlElement(name = "NumberOfSensors")
    protected Integer numberOfSensors;
    @XmlElement(name = "Characteristics")
    protected List<CharacteristicType> characteristics;
    @XmlElement(name = "OperationalMode")
    protected List<String> operationalMode;
    @XmlElement(name = "Sensor")
    protected List<SensorType> sensor;
    @XmlAttribute(name = "uuid")
    protected String uuid;

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
     * Obtient la valeur de la propriété technique.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTechnique() {
        return technique;
    }

    /**
     * Définit la valeur de la propriété technique.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTechnique(String value) {
        this.technique = value;
    }

    /**
     * Obtient la valeur de la propriété numberOfSensors.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getNumberOfSensors() {
        return numberOfSensors;
    }

    /**
     * Définit la valeur de la propriété numberOfSensors.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setNumberOfSensors(Integer value) {
        this.numberOfSensors = value;
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
     * Gets the value of the operationalMode property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationalMode property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationalMode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getOperationalMode() {
        if (operationalMode == null) {
            operationalMode = new ArrayList<String>();
        }
        return this.operationalMode;
    }

    /**
     * Gets the value of the sensor property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sensor property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSensor().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SensorType }
     *
     *
     */
    public List<SensorType> getSensor() {
        if (sensor == null) {
            sensor = new ArrayList<SensorType>();
        }
        return this.sensor;
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
