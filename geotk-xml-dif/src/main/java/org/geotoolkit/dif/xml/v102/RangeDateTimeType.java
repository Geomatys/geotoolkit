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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 This entity stores the start and end date/time of a collection.
 *
 *                 * BeginningDateTime : The time when the temporal coverage period being described began.
 *                 * EndingDateTime : The time when the temporal coverage period being described ended.
 *
 *
 * <p>Classe Java pour RangeDateTimeType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RangeDateTimeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Beginning_Date_Time" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Ending_Date_Time" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeDateTimeType", propOrder = {
    "beginningDateTime",
    "endingDateTime"
})
public class RangeDateTimeType {

    @XmlElement(name = "Beginning_Date_Time", required = true)
    protected String beginningDateTime;
    @XmlElement(name = "Ending_Date_Time")
    protected String endingDateTime;

    public RangeDateTimeType() {

    }

    public RangeDateTimeType(String beginningDateTime, String endingDateTime) {
        this.beginningDateTime = beginningDateTime;
        this.endingDateTime = endingDateTime;
    }

    /**
     * Obtient la valeur de la propriété beginningDateTime.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBeginningDateTime() {
        return beginningDateTime;
    }

    /**
     * Définit la valeur de la propriété beginningDateTime.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBeginningDateTime(String value) {
        this.beginningDateTime = value;
    }

    /**
     * Obtient la valeur de la propriété endingDateTime.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEndingDateTime() {
        return endingDateTime;
    }

    /**
     * Définit la valeur de la propriété endingDateTime.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEndingDateTime(String value) {
        this.endingDateTime = value;
    }

}
