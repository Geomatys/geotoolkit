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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 * Was Data_Center in the DIF
 *                 * Type is required in UMM
 *
 *                 | DIF 9            | ECHO 10           | UMM                 | DIF 10            | Notes                               |
 *                 | ---------------- | ----------------- | ------------------- | ----------------- | ------------------------------------|
 *                 | Data_Center      |         -         | Organization        | Organization      | Changed to match UMM field          |
 *                 |        -         | ProcessingCenter  | Organization        | Organization      | Changed to match UMM field          |
 *                 |        -         | ArchivingCenter   | Organization        | Organization      | Changed to match UMM field          |
 *                 |                                                                                                                      |
 *                 |        -         |         -         | Type                | Organization_Type | Enum                                |
 *                 | Data_Center_Name |         -         | OrganizationName    | Organization_Name | Changed to match similar UMM field  |
 *                 |        -         |         -         | HoursOfService      | Hours_Of_Service  | Pulled out of Personnel             |
 *                 |        -         |         -         | ContactInstructions | Instructions      | Pulled out of Personnel             |
 *                 | Data_Center_URL  |         -         | OrganizationURL     | Organization_URL  | Changed to match similar UMM field  |
 *                 | Data_Set_ID      | /DataSetId        |          -          | Dataset_ID        | Renamed                             |
 *                 | Personnel        |         -         | Personnel           | Personnel         | No Change                           |
 *
 *
 *
 * <p>Classe Java pour OrganizationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="OrganizationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Organization_Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OrganizationTypeEnum" maxOccurs="unbounded"/>
 *         &lt;element name="Organization_Name" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OrganizationNameType"/>
 *         &lt;element name="Hours_Of_Service" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Instructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Organization_URL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_ID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Personnel" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OrgPersonnelType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType", propOrder = {
    "organizationType",
    "organizationName",
    "hoursOfService",
    "instructions",
    "organizationURL",
    "datasetID",
    "personnel"
})
public class OrganizationType {

    @XmlElement(name = "Organization_Type", required = true)
    @XmlSchemaType(name = "string")
    protected List<OrganizationTypeEnum> organizationType;
    @XmlElement(name = "Organization_Name", required = true)
    protected OrganizationNameType organizationName;
    @XmlElement(name = "Hours_Of_Service")
    protected String hoursOfService;
    @XmlElement(name = "Instructions")
    protected String instructions;
    @XmlElement(name = "Organization_URL")
    protected String organizationURL;
    @XmlElement(name = "Dataset_ID")
    protected List<String> datasetID;
    @XmlElement(name = "Personnel", required = true)
    protected List<OrgPersonnelType> personnel;

    public OrganizationType() {

    }

    public OrganizationType(OrganizationTypeEnum organizationType, OrganizationNameType orgName, String organizationURL, OrgPersonnelType personnel) {
        if (organizationType != null) {
            this.organizationType = new ArrayList<>();
            this.organizationType.add(organizationType);
        }
        this.organizationName = orgName;
        this.organizationURL = organizationURL;
        if (personnel != null) {
            this.personnel = new ArrayList<>();
            this.personnel.add(personnel);
        }
    }

    /**
     * Gets the value of the organizationType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the organizationType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganizationType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganizationTypeEnum }
     *
     *
     */
    public List<OrganizationTypeEnum> getOrganizationType() {
        if (organizationType == null) {
            organizationType = new ArrayList<>();
        }
        return this.organizationType;
    }

    /**
     * Obtient la valeur de la propriété organizationName.
     *
     * @return
     *     possible object is
     *     {@link OrganizationNameType }
     *
     */
    public OrganizationNameType getOrganizationName() {
        return organizationName;
    }

    /**
     * Définit la valeur de la propriété organizationName.
     *
     * @param value
     *     allowed object is
     *     {@link OrganizationNameType }
     *
     */
    public void setOrganizationName(OrganizationNameType value) {
        this.organizationName = value;
    }

    /**
     * Obtient la valeur de la propriété hoursOfService.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHoursOfService() {
        return hoursOfService;
    }

    /**
     * Définit la valeur de la propriété hoursOfService.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHoursOfService(String value) {
        this.hoursOfService = value;
    }

    /**
     * Obtient la valeur de la propriété instructions.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Définit la valeur de la propriété instructions.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstructions(String value) {
        this.instructions = value;
    }

    /**
     * Obtient la valeur de la propriété organizationURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrganizationURL() {
        return organizationURL;
    }

    /**
     * Définit la valeur de la propriété organizationURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrganizationURL(String value) {
        this.organizationURL = value;
    }

    /**
     * Gets the value of the datasetID property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasetID property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasetID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getDatasetID() {
        if (datasetID == null) {
            datasetID = new ArrayList<String>();
        }
        return this.datasetID;
    }

    /**
     * Gets the value of the personnel property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personnel property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonnel().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrgPersonnelType }
     *
     *
     */
    public List<OrgPersonnelType> getPersonnel() {
        if (personnel == null) {
            personnel = new ArrayList<OrgPersonnelType>();
        }
        return this.personnel;
    }

}
