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
 *                 | DIF 9             | ECHO 10                        | UMM                | DIF 10                | Note                  |
 *                 | ----------------- | ------------------------------ | ------------------ | --------------------- | --------------------- |
 *                 | Data_Set_Citation | CitationforExternalPublication | CollectionCitation | Dataset_Citation      | Renamed               |
 *                 |                                                                                                                         |
 *                 | Dataset_DOI       |               -                | DOI                | Persistent_Identifier | New type, generalized |
 *
 *                 * Persistent_Identifier (PID) for Data (Examples: DOI, ARK,…)
 *                 * Dataset_DOI : a digital object identifier, use: "doi:10.1000/182" or "ark:/NAAN/Name[Qualifier]"
 *
 *
 * <p>Classe Java pour DatasetCitationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DatasetCitationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Dataset_Creator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Editor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Series_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Release_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Release_Place" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Dataset_Publisher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Issue_Identification" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Data_Presentation_Form" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Other_Citation_Details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Persistent_Identifier" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PersistentIdentifierType" minOccurs="0"/>
 *         &lt;element name="Online_Resource" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatasetCitationType", propOrder = {
    "datasetCreator",
    "datasetEditor",
    "datasetTitle",
    "datasetSeriesName",
    "datasetReleaseDate",
    "datasetReleasePlace",
    "datasetPublisher",
    "version",
    "issueIdentification",
    "dataPresentationForm",
    "otherCitationDetails",
    "persistentIdentifier",
    "onlineResource"
})
public class DatasetCitationType {

    @XmlElement(name = "Dataset_Creator")
    protected String datasetCreator;
    @XmlElement(name = "Dataset_Editor")
    protected String datasetEditor;
    @XmlElement(name = "Dataset_Title")
    protected String datasetTitle;
    @XmlElement(name = "Dataset_Series_Name")
    protected String datasetSeriesName;
    @XmlElement(name = "Dataset_Release_Date")
    protected String datasetReleaseDate;
    @XmlElement(name = "Dataset_Release_Place")
    protected String datasetReleasePlace;
    @XmlElement(name = "Dataset_Publisher")
    protected String datasetPublisher;
    @XmlElement(name = "Version")
    protected String version;
    @XmlElement(name = "Issue_Identification")
    protected String issueIdentification;
    @XmlElement(name = "Data_Presentation_Form")
    protected String dataPresentationForm;
    @XmlElement(name = "Other_Citation_Details")
    protected String otherCitationDetails;
    @XmlElement(name = "Persistent_Identifier")
    protected PersistentIdentifierType persistentIdentifier;
    @XmlElement(name = "Online_Resource")
    @XmlSchemaType(name = "anyURI")
    protected String onlineResource;

    public DatasetCitationType() {

    }
    /**
     * Obtient la valeur de la propriété datasetCreator.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetCreator() {
        return datasetCreator;
    }

    /**
     * Définit la valeur de la propriété datasetCreator.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetCreator(String value) {
        this.datasetCreator = value;
    }

    /**
     * Obtient la valeur de la propriété datasetEditor.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetEditor() {
        return datasetEditor;
    }

    /**
     * Définit la valeur de la propriété datasetEditor.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetEditor(String value) {
        this.datasetEditor = value;
    }

    /**
     * Obtient la valeur de la propriété datasetTitle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetTitle() {
        return datasetTitle;
    }

    /**
     * Définit la valeur de la propriété datasetTitle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetTitle(String value) {
        this.datasetTitle = value;
    }

    /**
     * Obtient la valeur de la propriété datasetSeriesName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetSeriesName() {
        return datasetSeriesName;
    }

    /**
     * Définit la valeur de la propriété datasetSeriesName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetSeriesName(String value) {
        this.datasetSeriesName = value;
    }

    /**
     * Obtient la valeur de la propriété datasetReleaseDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetReleaseDate() {
        return datasetReleaseDate;
    }

    /**
     * Définit la valeur de la propriété datasetReleaseDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetReleaseDate(String value) {
        this.datasetReleaseDate = value;
    }

    /**
     * Obtient la valeur de la propriété datasetReleasePlace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetReleasePlace() {
        return datasetReleasePlace;
    }

    /**
     * Définit la valeur de la propriété datasetReleasePlace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetReleasePlace(String value) {
        this.datasetReleasePlace = value;
    }

    /**
     * Obtient la valeur de la propriété datasetPublisher.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDatasetPublisher() {
        return datasetPublisher;
    }

    /**
     * Définit la valeur de la propriété datasetPublisher.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDatasetPublisher(String value) {
        this.datasetPublisher = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété issueIdentification.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIssueIdentification() {
        return issueIdentification;
    }

    /**
     * Définit la valeur de la propriété issueIdentification.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIssueIdentification(String value) {
        this.issueIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété dataPresentationForm.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataPresentationForm() {
        return dataPresentationForm;
    }

    /**
     * Définit la valeur de la propriété dataPresentationForm.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataPresentationForm(String value) {
        this.dataPresentationForm = value;
    }

    /**
     * Obtient la valeur de la propriété otherCitationDetails.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOtherCitationDetails() {
        return otherCitationDetails;
    }

    /**
     * Définit la valeur de la propriété otherCitationDetails.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOtherCitationDetails(String value) {
        this.otherCitationDetails = value;
    }

    /**
     * Obtient la valeur de la propriété persistentIdentifier.
     *
     * @return
     *     possible object is
     *     {@link PersistentIdentifierType }
     *
     */
    public PersistentIdentifierType getPersistentIdentifier() {
        return persistentIdentifier;
    }

    /**
     * Définit la valeur de la propriété persistentIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link PersistentIdentifierType }
     *
     */
    public void setPersistentIdentifier(PersistentIdentifierType value) {
        this.persistentIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété onlineResource.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOnlineResource() {
        return onlineResource;
    }

    /**
     * Définit la valeur de la propriété onlineResource.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnlineResource(String value) {
        this.onlineResource = value;
    }

}
