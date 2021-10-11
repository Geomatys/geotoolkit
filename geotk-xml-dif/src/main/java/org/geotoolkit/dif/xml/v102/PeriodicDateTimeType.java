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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * This entity contains the name of the temporal period in addition to the date,
 * time, duration unit, and value, and cycle duration unit and value. Used at
 * the collection level to describe a collection having granules, which cover a
 * regularly occurring period.
 *
 *                 * Name : The name given to the recurring time period. e.g. 'spring - north
 * hemi.' * Start_Date : This attribute provides the date (day and time) of the
 * first occurrence of this regularly occurring period which is relevant to the
 * collection, granule, or event coverage. * End_Date : This attribute provides
 * the date (day and time) of the last occurrence of this regularly occurring
 * period which is relevant to the collection, granule, or event coverage. *
 * Duration_Unit : The unit specification for the period duration.
 *
 * Duration_Value : The number of PeriodDurationUnits in the RegularPeriodic
 * period. e.g. the RegularPeriodic event 'Spring-North Hemi' might have a: *
 * PeriodDurationUnit='MONTH', * PeriodDurationValue='3' , *
 * PeriodCycleDurationUnit='YEAR', * PeriodCycleDurationValue='1',
 *
 * indicating that Spring-North Hemi lasts for 3 months and has a cycle duration
 * of 1 year. The unit for the attribute is the value of the attribute
 * PeriodDurationValue.
 *
 *                 * Period_Cycle_Duration_Unit : The unit specification of the period cycle
 * duration. * Period_Cycle_Duration_Value : The value of the attribute.
 *
 *
 *
 * <p>Classe Java pour PeriodicDateTimeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="PeriodicDateTimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Start_Date" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="End_Date" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Duration_Unit" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DurationUnitEnum"/>
 *         &lt;element name="Duration_Value" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Period_Cycle_Duration_Unit" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DurationUnitEnum"/>
 *         &lt;element name="Period_Cycle_Duration_Value" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeriodicDateTimeType", propOrder = {
    "name",
    "startDate",
    "endDate",
    "durationUnit",
    "durationValue",
    "periodCycleDurationUnit",
    "periodCycleDurationValue"
})
public class PeriodicDateTimeType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Start_Date", required = true)
    protected String startDate;
    @XmlElement(name = "End_Date", required = true)
    protected String endDate;
    @XmlElement(name = "Duration_Unit", required = true)
    @XmlSchemaType(name = "string")
    protected DurationUnitEnum durationUnit;
    @XmlElement(name = "Duration_Value")
    protected int durationValue;
    @XmlElement(name = "Period_Cycle_Duration_Unit", required = true)
    @XmlSchemaType(name = "string")
    protected DurationUnitEnum periodCycleDurationUnit;
    @XmlElement(name = "Period_Cycle_Duration_Value")
    protected int periodCycleDurationValue;

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété startDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Définit la valeur de la propriété startDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Obtient la valeur de la propriété endDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Définit la valeur de la propriété endDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Obtient la valeur de la propriété durationUnit.
     *
     * @return
     *     possible object is
     *     {@link DurationUnitEnum }
     *
     */
    public DurationUnitEnum getDurationUnit() {
        return durationUnit;
    }

    /**
     * Définit la valeur de la propriété durationUnit.
     *
     * @param value
     *     allowed object is
     *     {@link DurationUnitEnum }
     *
     */
    public void setDurationUnit(DurationUnitEnum value) {
        this.durationUnit = value;
    }

    /**
     * Obtient la valeur de la propriété durationValue.
     *
     */
    public int getDurationValue() {
        return durationValue;
    }

    /**
     * Définit la valeur de la propriété durationValue.
     *
     */
    public void setDurationValue(int value) {
        this.durationValue = value;
    }

    /**
     * Obtient la valeur de la propriété periodCycleDurationUnit.
     *
     * @return
     *     possible object is
     *     {@link DurationUnitEnum }
     *
     */
    public DurationUnitEnum getPeriodCycleDurationUnit() {
        return periodCycleDurationUnit;
    }

    /**
     * Définit la valeur de la propriété periodCycleDurationUnit.
     *
     * @param value
     *     allowed object is
     *     {@link DurationUnitEnum }
     *
     */
    public void setPeriodCycleDurationUnit(DurationUnitEnum value) {
        this.periodCycleDurationUnit = value;
    }

    /**
     * Obtient la valeur de la propriété periodCycleDurationValue.
     *
     */
    public int getPeriodCycleDurationValue() {
        return periodCycleDurationValue;
    }

    /**
     * Définit la valeur de la propriété periodCycleDurationValue.
     *
     */
    public void setPeriodCycleDurationValue(int value) {
        this.periodCycleDurationValue = value;
    }

}
