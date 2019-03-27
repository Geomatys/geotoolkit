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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * Was a top level field in DIF 9 but is now one of the choices within the Temporal_Coverage field.
 *
 * | DIF 9                   | ECHO     | UMM                   | DIF 10          | Notes                               |
 * | ----------------------- |----------| --------------------- | --------------- | ------------------------------------|
 * | Paleo_Temporal_Coverage |     -    | PaleoTemporalCoverage | Paleo_Date_Time | Moved to Temporal_Coverage choice   |
 *
 *
 *
 * <p>Classe Java pour PaleoDateTimeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PaleoDateTimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Paleo_Start_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Paleo_Stop_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Chronostratigraphic_Unit" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ChronostratigraphicUnitType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaleoDateTimeType", propOrder = {
    "paleoStartDate",
    "paleoStopDate",
    "chronostratigraphicUnit"
})
public class PaleoDateTimeType {

    @XmlElement(name = "Paleo_Start_Date")
    protected String paleoStartDate;
    @XmlElement(name = "Paleo_Stop_Date")
    protected String paleoStopDate;
    @XmlElement(name = "Chronostratigraphic_Unit")
    protected List<ChronostratigraphicUnitType> chronostratigraphicUnit;

    /**
     * Obtient la valeur de la propriété paleoStartDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaleoStartDate() {
        return paleoStartDate;
    }

    /**
     * Définit la valeur de la propriété paleoStartDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaleoStartDate(String value) {
        this.paleoStartDate = value;
    }

    /**
     * Obtient la valeur de la propriété paleoStopDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaleoStopDate() {
        return paleoStopDate;
    }

    /**
     * Définit la valeur de la propriété paleoStopDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaleoStopDate(String value) {
        this.paleoStopDate = value;
    }

    /**
     * Gets the value of the chronostratigraphicUnit property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chronostratigraphicUnit property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChronostratigraphicUnit().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChronostratigraphicUnitType }
     *
     *
     */
    public List<ChronostratigraphicUnitType> getChronostratigraphicUnit() {
        if (chronostratigraphicUnit == null) {
            chronostratigraphicUnit = new ArrayList<ChronostratigraphicUnitType>();
        }
        return this.chronostratigraphicUnit;
    }

}
