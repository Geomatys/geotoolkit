/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.eop.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v311.CodeListType;


/**
 * <p>Classe Java pour DownlinkInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DownlinkInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="acquisitionStation" type="{http://www.opengis.net/gml}CodeListType"/>
 *         &lt;element name="acquisitionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownlinkInformationType", propOrder = {
    "acquisitionStation",
    "acquisitionDate"
})
public class DownlinkInformationType {

    @XmlElement(required = true)
    protected CodeListType acquisitionStation;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar acquisitionDate;

    /**
     * Obtient la valeur de la propriété acquisitionStation.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getAcquisitionStation() {
        return acquisitionStation;
    }

    /**
     * Définit la valeur de la propriété acquisitionStation.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setAcquisitionStation(CodeListType value) {
        this.acquisitionStation = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Définit la valeur de la propriété acquisitionDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setAcquisitionDate(XMLGregorianCalendar value) {
        this.acquisitionDate = value;
    }

}
