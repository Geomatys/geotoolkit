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

package org.geotoolkit.eop.xml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v321.CodeListType;
import org.geotoolkit.gml.xml.v321.CodeType;


/**
 * <p>Classe Java pour ArchivingInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ArchivingInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="archivingCenter" type="{http://www.opengis.net/gml/3.2}CodeListType"/>
 *         &lt;element name="archivingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="archivingIdentifier" type="{http://www.opengis.net/gml/3.2}CodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchivingInformationType", propOrder = {
    "archivingCenter",
    "archivingDate",
    "archivingIdentifier"
})
public class ArchivingInformationType {

    @XmlElement(required = true)
    protected CodeListType archivingCenter;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar archivingDate;
    protected CodeType archivingIdentifier;

    /**
     * Obtient la valeur de la propriété archivingCenter.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getArchivingCenter() {
        return archivingCenter;
    }

    /**
     * Définit la valeur de la propriété archivingCenter.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setArchivingCenter(CodeListType value) {
        this.archivingCenter = value;
    }

    /**
     * Obtient la valeur de la propriété archivingDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getArchivingDate() {
        return archivingDate;
    }

    /**
     * Définit la valeur de la propriété archivingDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setArchivingDate(XMLGregorianCalendar value) {
        this.archivingDate = value;
    }

    /**
     * Obtient la valeur de la propriété archivingIdentifier.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getArchivingIdentifier() {
        return archivingIdentifier;
    }

    /**
     * Définit la valeur de la propriété archivingIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setArchivingIdentifier(CodeType value) {
        this.archivingIdentifier = value;
    }

}
