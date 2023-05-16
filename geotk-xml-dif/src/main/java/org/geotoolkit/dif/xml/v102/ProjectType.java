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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 *
 *                 This entity contains attributes describing the scientific
 *                 endeavor(s) to which the collection is associated. Scientific
 *                 endeavors include campaigns, projects, interdisciplinary science
 *                 investigations, missions, field experiments, etc.
 *
 *                 * DIF 9 sometimes uses "/" in the Short_Name to denote campain
 *
 *                 * UUID is for mapping to the project as a whole, campaign information is specific to this record.
 *
 *                 * Note: Some CMR software maps ECHO 10 Campain directly to DIF 9 Project
 *
 *                 In the future we can map legacy fields as such:
 *                 * "ARCSS/OAII/SHEBA" goes to Short_Name:ARCSS Campain:SHEBA but will need a review
 *                 * "GTE/TRACE-P" goes to Short_Name:GTE Campain:TRACE-P
 *
 *                 | DIF 9      | ECHO 10   | UMM       | DIF 10     | Notes                                                                      |
 *                 | ---------- | --------- | --------- | ---------- | -------------------------------------------------------------------------- |
 *                 | Project    | Campaign  | Project   | Project    | Renamed Containing field                                                   |
 *                 |                                                                                                                              |
 *                 | Short_Name | ShortName?| ShortName | Short_Name | ECHO short name could be a Project or a Campain                            |
 *                 |     -      | ShortName | Campaign  | Campaign   | Sub projects from ECHO                                                     |
 *                 | Long_Name  | LongName  | LongName  | Long_Name  |                                                                            |
 *                 |     -      | StartDate |     -     | Start_Date | Is a datetime in echo ; aircraft may use time but no record currently does |
 *                 |     -      | EndDate   |     -     | End_Date   | Is a datetime in echo ; aircraft may use time but no record currently does |
 *
 *                 * Short_Name : The unique identifier by which a campaign/project/experiment is known. The campaign/project is the scientific endeavor associated with the acquisition of the collection. Collections may be associated with multiple campaigns.
 *                 * Campaign : A sub project, or name of recurring activity. ECHO only has Campaigns.
 *                 * Long_Name : The expanded name of the campaign/experiment (e.g. Global climate observing system).
 *                 * Start_Date : The start date of the project campaign.
 *                 * End_Date : The end data of the project or campaign.
 *
 *
 * <p>Classe Java pour ProjectType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ProjectType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Short_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Campaign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Long_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Start_Date" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="End_Date" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
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
@XmlType(name = "ProjectType", propOrder = {
    "shortName",
    "campaign",
    "longName",
    "startDate",
    "endDate"
})
public class ProjectType {

    @XmlElement(name = "Short_Name", required = true)
    protected String shortName;
    @XmlElement(name = "Campaign")
    protected String campaign;
    @XmlElement(name = "Long_Name")
    protected String longName;
    @XmlElement(name = "Start_Date")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlElement(name = "End_Date")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
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
     * Obtient la valeur de la propriété campaign.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCampaign() {
        return campaign;
    }

    /**
     * Définit la valeur de la propriété campaign.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCampaign(String value) {
        this.campaign = value;
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
     * Obtient la valeur de la propriété startDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Définit la valeur de la propriété startDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Obtient la valeur de la propriété endDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Définit la valeur de la propriété endDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
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
