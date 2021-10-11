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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Entry_ID" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}EntryIDType"/>
 *         &lt;element name="Version_Description" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}VersionDescriptionType" minOccurs="0"/>
 *         &lt;element name="Entry_Title" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}EntryTitleType"/>
 *         &lt;element name="Dataset_Citation" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DatasetCitationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Personnel" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PersonnelType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Science_Keywords" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ScienceKeywordsType" maxOccurs="unbounded"/>
 *         &lt;element name="ISO_Topic_Category" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ISOTopicCategoryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Ancillary_Keyword" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}AncillaryKeywordType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Platform" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PlatformType" maxOccurs="unbounded"/>
 *         &lt;element name="Temporal_Coverage" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TemporalCoverageType" maxOccurs="unbounded"/>
 *         &lt;element name="Dataset_Progress" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DatasetProgressType" minOccurs="0"/>
 *         &lt;element name="Spatial_Coverage" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}SpatialCoverageType"/>
 *         &lt;element name="Location" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}LocationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Data_Resolution" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DataResolutionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Project" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ProjectType" maxOccurs="unbounded"/>
 *         &lt;element name="Quality" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}QualityType" minOccurs="0"/>
 *         &lt;element name="Access_Constraints" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}AccessConstraintsType" minOccurs="0"/>
 *         &lt;element name="Use_Constraints" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UseConstraintsType" minOccurs="0"/>
 *         &lt;element name="Dataset_Language" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DatasetLanguageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Originating_Center" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OriginatingCenterType" minOccurs="0"/>
 *         &lt;element name="Organization" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OrganizationType" maxOccurs="unbounded"/>
 *         &lt;element name="Distribution" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DistributionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Multimedia_Sample" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MultimediaSampleType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Reference" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Summary" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}SummaryType"/>
 *         &lt;element name="Related_URL" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}RelatedURLType" maxOccurs="unbounded"/>
 *         &lt;element name="Metadata_Association" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MetadataAssociationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="IDN_Node" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}IDNNodeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Originating_Metadata_Node" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OriginatingMetadataNodeType" minOccurs="0"/>
 *         &lt;element name="Metadata_Name" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MetadataNameType"/>
 *         &lt;element name="Metadata_Version" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MetadataVersionType"/>
 *         &lt;element name="DIF_Revision_History" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DIFRevisionHistoryType" minOccurs="0"/>
 *         &lt;element name="Metadata_Dates" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MetadataDatesType"/>
 *         &lt;element name="Private" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PrivateType" minOccurs="0"/>
 *         &lt;element name="Additional_Attributes" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}AdditionalAttributesType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Product_Level_Id" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ProcessingLevelIdType" minOccurs="0"/>
 *         &lt;element name="Collection_Data_Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}CollectionDataTypeEnum" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Product_Flag" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ProductFlagEnum" minOccurs="0"/>
 *         &lt;element name="Extended_Metadata" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ExtendedMetadataType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entryID",
    "versionDescription",
    "entryTitle",
    "datasetCitation",
    "personnel",
    "scienceKeywords",
    "isoTopicCategory",
    "ancillaryKeyword",
    "platform",
    "temporalCoverage",
    "datasetProgress",
    "spatialCoverage",
    "location",
    "dataResolution",
    "project",
    "quality",
    "accessConstraints",
    "useConstraints",
    "datasetLanguage",
    "originatingCenter",
    "organization",
    "distribution",
    "multimediaSample",
    "reference",
    "summary",
    "relatedURL",
    "metadataAssociation",
    "idnNode",
    "originatingMetadataNode",
    "metadataName",
    "metadataVersion",
    "difRevisionHistory",
    "metadataDates",
    "_private",
    "additionalAttributes",
    "productLevelId",
    "collectionDataType",
    "productFlag",
    "extendedMetadata"
})
@XmlRootElement(name = "DIF")
public class DIF {

    @XmlElement(name = "Entry_ID", required = true)
    protected EntryIDType entryID;
    @XmlElement(name = "Version_Description")
    protected String versionDescription;
    @XmlElement(name = "Entry_Title", required = true)
    protected String entryTitle;
    @XmlElement(name = "Dataset_Citation")
    protected List<DatasetCitationType> datasetCitation;
    @XmlElement(name = "Personnel")
    protected List<PersonnelType> personnel;
    @XmlElement(name = "Science_Keywords", required = true)
    protected List<ScienceKeywordsType> scienceKeywords;
    @XmlElement(name = "ISO_Topic_Category")
    protected List<ISOTopicCategoryType> isoTopicCategory;
    @XmlElement(name = "Ancillary_Keyword")
    protected List<String> ancillaryKeyword;
    @XmlElement(name = "Platform", required = true)
    protected List<PlatformType> platform;
    @XmlElement(name = "Temporal_Coverage", required = true)
    protected List<TemporalCoverageType> temporalCoverage;
    @XmlElement(name = "Dataset_Progress")
    @XmlSchemaType(name = "string")
    protected DatasetProgressEnum datasetProgress;
    @XmlElement(name = "Spatial_Coverage", required = true)
    protected SpatialCoverageType spatialCoverage;
    @XmlElement(name = "Location")
    protected List<LocationType> location;
    @XmlElement(name = "Data_Resolution")
    protected List<DataResolutionType> dataResolution;
    @XmlElement(name = "Project", required = true)
    protected List<ProjectType> project;
    @XmlElement(name = "Quality")
    protected QualityType quality;
    @XmlElement(name = "Access_Constraints")
    protected String accessConstraints;
    @XmlElement(name = "Use_Constraints")
    protected UseConstraintsType useConstraints;
    @XmlElement(name = "Dataset_Language")
    @XmlSchemaType(name = "string")
    protected List<DatasetLanguageEnum> datasetLanguage;
    @XmlElement(name = "Originating_Center")
    protected String originatingCenter;
    @XmlElement(name = "Organization", required = true)
    protected List<OrganizationType> organization;
    @XmlElement(name = "Distribution")
    protected List<DistributionType> distribution;
    @XmlElement(name = "Multimedia_Sample")
    protected List<MultimediaSampleType> multimediaSample;
    @XmlElement(name = "Reference")
    protected List<ReferenceType> reference;
    @XmlElement(name = "Summary", required = true)
    protected SummaryType summary;
    @XmlElement(name = "Related_URL", required = true)
    protected List<RelatedURLType> relatedURL;
    @XmlElement(name = "Metadata_Association")
    protected List<MetadataAssociationType> metadataAssociation;
    @XmlElement(name = "IDN_Node")
    protected List<IDNNodeType> idnNode;
    @XmlElement(name = "Originating_Metadata_Node")
    protected String originatingMetadataNode;
    @XmlElement(name = "Metadata_Name", required = true)
    protected String metadataName;
    @XmlElement(name = "Metadata_Version", required = true)
    @XmlSchemaType(name = "string")
    protected MetadataVersionEnum metadataVersion;
    @XmlElement(name = "DIF_Revision_History")
    protected String difRevisionHistory;
    @XmlElement(name = "Metadata_Dates", required = true)
    protected MetadataDatesType metadataDates;
    @XmlElement(name = "Private")
    @XmlSchemaType(name = "string")
    protected PrivateEnum _private;
    @XmlElement(name = "Additional_Attributes")
    protected List<AdditionalAttributesType> additionalAttributes;
    @XmlElement(name = "Product_Level_Id")
    protected String productLevelId;
    @XmlElement(name = "Collection_Data_Type")
    @XmlSchemaType(name = "string")
    protected List<CollectionDataTypeEnum> collectionDataType;
    @XmlElement(name = "Product_Flag")
    @XmlSchemaType(name = "string")
    protected ProductFlagEnum productFlag;
    @XmlElement(name = "Extended_Metadata")
    protected List<ExtendedMetadataType> extendedMetadata;

    /**
     * Obtient la valeur de la propriété entryID.
     *
     * @return
     *     possible object is
     *     {@link EntryIDType }
     *
     */
    public EntryIDType getEntryID() {
        return entryID;
    }

    /**
     * Définit la valeur de la propriété entryID.
     *
     * @param value
     *     allowed object is
     *     {@link EntryIDType }
     *
     */
    public void setEntryID(EntryIDType value) {
        this.entryID = value;
    }

    /**
     * Obtient la valeur de la propriété versionDescription.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersionDescription() {
        return versionDescription;
    }

    /**
     * Définit la valeur de la propriété versionDescription.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersionDescription(String value) {
        this.versionDescription = value;
    }

    /**
     * Obtient la valeur de la propriété entryTitle.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEntryTitle() {
        return entryTitle;
    }

    /**
     * Définit la valeur de la propriété entryTitle.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEntryTitle(String value) {
        this.entryTitle = value;
    }

    /**
     * Gets the value of the datasetCitation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasetCitation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasetCitation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatasetCitationType }
     *
     *
     */
    public List<DatasetCitationType> getDatasetCitation() {
        if (datasetCitation == null) {
            datasetCitation = new ArrayList<>();
        }
        return this.datasetCitation;
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
     * {@link PersonnelType }
     *
     *
     */
    public List<PersonnelType> getPersonnel() {
        if (personnel == null) {
            personnel = new ArrayList<PersonnelType>();
        }
        return this.personnel;
    }

    /**
     * Gets the value of the scienceKeywords property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scienceKeywords property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScienceKeywords().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScienceKeywordsType }
     *
     *
     */
    public List<ScienceKeywordsType> getScienceKeywords() {
        if (scienceKeywords == null) {
            scienceKeywords = new ArrayList<ScienceKeywordsType>();
        }
        return this.scienceKeywords;
    }

    /**
     * Gets the value of the isoTopicCategory property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the isoTopicCategory property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getISOTopicCategory().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ISOTopicCategoryType }
     *
     *
     */
    public List<ISOTopicCategoryType> getISOTopicCategory() {
        if (isoTopicCategory == null) {
            isoTopicCategory = new ArrayList<ISOTopicCategoryType>();
        }
        return this.isoTopicCategory;
    }

    /**
     * Gets the value of the ancillaryKeyword property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ancillaryKeyword property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAncillaryKeyword().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAncillaryKeyword() {
        if (ancillaryKeyword == null) {
            ancillaryKeyword = new ArrayList<String>();
        }
        return this.ancillaryKeyword;
    }

    /**
     * Gets the value of the platform property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the platform property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPlatform().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PlatformType }
     *
     *
     */
    public List<PlatformType> getPlatform() {
        if (platform == null) {
            platform = new ArrayList<PlatformType>();
        }
        return this.platform;
    }

    /**
     * Gets the value of the temporalCoverage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the temporalCoverage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTemporalCoverage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TemporalCoverageType }
     *
     *
     */
    public List<TemporalCoverageType> getTemporalCoverage() {
        if (temporalCoverage == null) {
            temporalCoverage = new ArrayList<>();
        }
        return this.temporalCoverage;
    }

    /**
     * Obtient la valeur de la propriété datasetProgress.
     *
     * @return
     *     possible object is
     *     {@link DatasetProgressEnum }
     *
     */
    public DatasetProgressEnum getDatasetProgress() {
        return datasetProgress;
    }

    /**
     * Définit la valeur de la propriété datasetProgress.
     *
     * @param value
     *     allowed object is
     *     {@link DatasetProgressEnum }
     *
     */
    public void setDatasetProgress(DatasetProgressEnum value) {
        this.datasetProgress = value;
    }

    /**
     * Obtient la valeur de la propriété spatialCoverage.
     *
     * @return
     *     possible object is
     *     {@link SpatialCoverageType }
     *
     */
    public SpatialCoverageType getSpatialCoverage() {
        return spatialCoverage;
    }

    /**
     * Définit la valeur de la propriété spatialCoverage.
     *
     * @param value
     *     allowed object is
     *     {@link SpatialCoverageType }
     *
     */
    public void setSpatialCoverage(SpatialCoverageType value) {
        this.spatialCoverage = value;
    }

    /**
     * Gets the value of the location property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the location property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationType }
     *
     *
     */
    public List<LocationType> getLocation() {
        if (location == null) {
            location = new ArrayList<LocationType>();
        }
        return this.location;
    }

    /**
     * Gets the value of the dataResolution property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataResolution property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataResolution().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataResolutionType }
     *
     *
     */
    public List<DataResolutionType> getDataResolution() {
        if (dataResolution == null) {
            dataResolution = new ArrayList<DataResolutionType>();
        }
        return this.dataResolution;
    }

    /**
     * Gets the value of the project property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the project property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProject().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProjectType }
     *
     *
     */
    public List<ProjectType> getProject() {
        if (project == null) {
            project = new ArrayList<ProjectType>();
        }
        return this.project;
    }

    /**
     * Obtient la valeur de la propriété quality.
     *
     * @return
     *     possible object is
     *     {@link QualityType }
     *
     */
    public QualityType getQuality() {
        return quality;
    }

    /**
     * Définit la valeur de la propriété quality.
     *
     * @param value
     *     allowed object is
     *     {@link QualityType }
     *
     */
    public void setQuality(QualityType value) {
        this.quality = value;
    }

    /**
     * Obtient la valeur de la propriété accessConstraints.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * Définit la valeur de la propriété accessConstraints.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccessConstraints(String value) {
        this.accessConstraints = value;
    }

    /**
     * Obtient la valeur de la propriété useConstraints.
     *
     * @return
     *     possible object is
     *     {@link UseConstraintsType }
     *
     */
    public UseConstraintsType getUseConstraints() {
        return useConstraints;
    }

    /**
     * Définit la valeur de la propriété useConstraints.
     *
     * @param value
     *     allowed object is
     *     {@link UseConstraintsType }
     *
     */
    public void setUseConstraints(UseConstraintsType value) {
        this.useConstraints = value;
    }

    /**
     * Gets the value of the datasetLanguage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datasetLanguage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatasetLanguage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatasetLanguageEnum }
     *
     *
     */
    public List<DatasetLanguageEnum> getDatasetLanguage() {
        if (datasetLanguage == null) {
            datasetLanguage = new ArrayList<DatasetLanguageEnum>();
        }
        return this.datasetLanguage;
    }

    /**
     * Obtient la valeur de la propriété originatingCenter.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOriginatingCenter() {
        return originatingCenter;
    }

    /**
     * Définit la valeur de la propriété originatingCenter.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOriginatingCenter(String value) {
        this.originatingCenter = value;
    }

    /**
     * Gets the value of the organization property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the organization property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrganization().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganizationType }
     *
     *
     */
    public List<OrganizationType> getOrganization() {
        if (organization == null) {
            organization = new ArrayList<>();
        }
        return this.organization;
    }

    /**
     * Gets the value of the distribution property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the distribution property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDistribution().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DistributionType }
     *
     *
     */
    public List<DistributionType> getDistribution() {
        if (distribution == null) {
            distribution = new ArrayList<DistributionType>();
        }
        return this.distribution;
    }

    /**
     * Gets the value of the multimediaSample property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multimediaSample property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultimediaSample().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MultimediaSampleType }
     *
     *
     */
    public List<MultimediaSampleType> getMultimediaSample() {
        if (multimediaSample == null) {
            multimediaSample = new ArrayList<MultimediaSampleType>();
        }
        return this.multimediaSample;
    }

    /**
     * Gets the value of the reference property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     *
     *
     */
    public List<ReferenceType> getReference() {
        if (reference == null) {
            reference = new ArrayList<ReferenceType>();
        }
        return this.reference;
    }

    /**
     * Obtient la valeur de la propriété summary.
     *
     * @return
     *     possible object is
     *     {@link SummaryType }
     *
     */
    public SummaryType getSummary() {
        return summary;
    }

    /**
     * Définit la valeur de la propriété summary.
     *
     * @param value
     *     allowed object is
     *     {@link SummaryType }
     *
     */
    public void setSummary(SummaryType value) {
        this.summary = value;
    }

    /**
     * Gets the value of the relatedURL property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedURL property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedURL().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedURLType }
     *
     *
     */
    public List<RelatedURLType> getRelatedURL() {
        if (relatedURL == null) {
            relatedURL = new ArrayList<RelatedURLType>();
        }
        return this.relatedURL;
    }

    /**
     * Gets the value of the metadataAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadataAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadataAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetadataAssociationType }
     *
     *
     */
    public List<MetadataAssociationType> getMetadataAssociation() {
        if (metadataAssociation == null) {
            metadataAssociation = new ArrayList<MetadataAssociationType>();
        }
        return this.metadataAssociation;
    }

    /**
     * Gets the value of the idnNode property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idnNode property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIDNNode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDNNodeType }
     *
     *
     */
    public List<IDNNodeType> getIDNNode() {
        if (idnNode == null) {
            idnNode = new ArrayList<IDNNodeType>();
        }
        return this.idnNode;
    }

    /**
     * Obtient la valeur de la propriété originatingMetadataNode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOriginatingMetadataNode() {
        return originatingMetadataNode;
    }

    /**
     * Définit la valeur de la propriété originatingMetadataNode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOriginatingMetadataNode(String value) {
        this.originatingMetadataNode = value;
    }

    /**
     * Obtient la valeur de la propriété metadataName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMetadataName() {
        return metadataName;
    }

    /**
     * Définit la valeur de la propriété metadataName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMetadataName(String value) {
        this.metadataName = value;
    }

    /**
     * Obtient la valeur de la propriété metadataVersion.
     *
     * @return
     *     possible object is
     *     {@link MetadataVersionEnum }
     *
     */
    public MetadataVersionEnum getMetadataVersion() {
        return metadataVersion;
    }

    /**
     * Définit la valeur de la propriété metadataVersion.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataVersionEnum }
     *
     */
    public void setMetadataVersion(MetadataVersionEnum value) {
        this.metadataVersion = value;
    }

    /**
     * Obtient la valeur de la propriété difRevisionHistory.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDIFRevisionHistory() {
        return difRevisionHistory;
    }

    /**
     * Définit la valeur de la propriété difRevisionHistory.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDIFRevisionHistory(String value) {
        this.difRevisionHistory = value;
    }

    /**
     * Obtient la valeur de la propriété metadataDates.
     *
     * @return
     *     possible object is
     *     {@link MetadataDatesType }
     *
     */
    public MetadataDatesType getMetadataDates() {
        return metadataDates;
    }

    /**
     * Définit la valeur de la propriété metadataDates.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataDatesType }
     *
     */
    public void setMetadataDates(MetadataDatesType value) {
        this.metadataDates = value;
    }

    /**
     * Obtient la valeur de la propriété private.
     *
     * @return
     *     possible object is
     *     {@link PrivateEnum }
     *
     */
    public PrivateEnum getPrivate() {
        return _private;
    }

    /**
     * Définit la valeur de la propriété private.
     *
     * @param value
     *     allowed object is
     *     {@link PrivateEnum }
     *
     */
    public void setPrivate(PrivateEnum value) {
        this._private = value;
    }

    /**
     * Gets the value of the additionalAttributes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalAttributes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalAttributes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalAttributesType }
     *
     *
     */
    public List<AdditionalAttributesType> getAdditionalAttributes() {
        if (additionalAttributes == null) {
            additionalAttributes = new ArrayList<AdditionalAttributesType>();
        }
        return this.additionalAttributes;
    }

    /**
     * Obtient la valeur de la propriété productLevelId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductLevelId() {
        return productLevelId;
    }

    /**
     * Définit la valeur de la propriété productLevelId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductLevelId(String value) {
        this.productLevelId = value;
    }

    /**
     * Gets the value of the collectionDataType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the collectionDataType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCollectionDataType().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CollectionDataTypeEnum }
     *
     *
     */
    public List<CollectionDataTypeEnum> getCollectionDataType() {
        if (collectionDataType == null) {
            collectionDataType = new ArrayList<CollectionDataTypeEnum>();
        }
        return this.collectionDataType;
    }

    /**
     * Obtient la valeur de la propriété productFlag.
     *
     * @return
     *     possible object is
     *     {@link ProductFlagEnum }
     *
     */
    public ProductFlagEnum getProductFlag() {
        return productFlag;
    }

    /**
     * Définit la valeur de la propriété productFlag.
     *
     * @param value
     *     allowed object is
     *     {@link ProductFlagEnum }
     *
     */
    public void setProductFlag(ProductFlagEnum value) {
        this.productFlag = value;
    }

    /**
     * Gets the value of the extendedMetadata property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedMetadata property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedMetadata().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtendedMetadataType }
     *
     *
     */
    public List<ExtendedMetadataType> getExtendedMetadata() {
        if (extendedMetadata == null) {
            extendedMetadata = new ArrayList<ExtendedMetadataType>();
        }
        return this.extendedMetadata;
    }

}
