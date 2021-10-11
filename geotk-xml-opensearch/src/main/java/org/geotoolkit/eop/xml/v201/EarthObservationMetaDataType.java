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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.gml.xml.v321.CodeListType;
import org.geotoolkit.gml.xml.v321.MeasureType;


/**
 * <p>Classe Java pour EarthObservationMetaDataType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationMetaDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="doi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parentIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="acquisitionType" type="{http://www.opengis.net/eop/2.1}AcquisitionTypeValueType"/>
 *         &lt;element name="acquisitionSubType" type="{http://www.opengis.net/gml/3.2}CodeListType" minOccurs="0"/>
 *         &lt;element name="productType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.opengis.net/eop/2.1}StatusValueType"/>
 *         &lt;element name="statusSubType" type="{http://www.opengis.net/eop/2.1}StatusSubTypeValueEnumerationType" minOccurs="0"/>
 *         &lt;element name="statusDetail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="downlinkedTo" type="{http://www.opengis.net/eop/2.1}DownlinkInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="archivedIn" type="{http://www.opengis.net/eop/2.1}ArchivingInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="imageQualityDegradation" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="productQualityDegradation" type="{http://www.opengis.net/gml/3.2}MeasureType" minOccurs="0"/>
 *         &lt;element name="imageQualityDegradationQuotationMode" type="{http://www.opengis.net/eop/2.1}DegradationQuotationModeValueType" minOccurs="0"/>
 *         &lt;element name="productQualityDegradationQuotationMode" type="{http://www.opengis.net/eop/2.1}DegradationQuotationModeValueType" minOccurs="0"/>
 *         &lt;element name="imageQualityStatus" type="{http://www.opengis.net/eop/2.1}ImageQualityStatusValueType" minOccurs="0"/>
 *         &lt;element name="productQualityStatus" type="{http://www.opengis.net/eop/2.1}ProductQualityStatusValueType" minOccurs="0"/>
 *         &lt;element name="imageQualityDegradationTag" type="{http://www.opengis.net/gml/3.2}CodeListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="productQualityDegradationTag" type="{http://www.opengis.net/gml/3.2}CodeListType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="imageQualityReportURL" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="productQualityReportURL" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="histograms" type="{http://www.opengis.net/eop/2.1}HistogramPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="composedOf" type="{http://www.opengis.net/eop/2.1}EarthObservationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="subsetOf" type="{http://www.opengis.net/eop/2.1}EarthObservationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="linkedWith" type="{http://www.opengis.net/eop/2.1}EarthObservationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="processing" type="{http://www.opengis.net/eop/2.1}ProcessingInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="productGroupId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vendorSpecific" type="{http://www.opengis.net/eop/2.1}SpecificInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationMetaDataType", propOrder = {
    "identifier",
    "creationDate",
    "modificationDate",
    "doi",
    "parentIdentifier",
    "acquisitionType",
    "acquisitionSubType",
    "productType",
    "status",
    "statusSubType",
    "statusDetail",
    "downlinkedTo",
    "archivedIn",
    "imageQualityDegradation",
    "productQualityDegradation",
    "imageQualityDegradationQuotationMode",
    "productQualityDegradationQuotationMode",
    "imageQualityStatus",
    "productQualityStatus",
    "imageQualityDegradationTag",
    "productQualityDegradationTag",
    "imageQualityReportURL",
    "productQualityReportURL",
    "histograms",
    "composedOf",
    "subsetOf",
    "linkedWith",
    "processing",
    "productGroupId",
    "vendorSpecific"
})
public class EarthObservationMetaDataType {

    @XmlElement(required = true)
    protected String identifier;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modificationDate;
    protected String doi;
    protected String parentIdentifier;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AcquisitionTypeValueType acquisitionType;
    protected CodeListType acquisitionSubType;
    protected String productType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String status;
    @XmlSchemaType(name = "string")
    protected StatusSubTypeValueEnumerationType statusSubType;
    protected String statusDetail;
    protected List<DownlinkInformationPropertyType> downlinkedTo;
    protected List<ArchivingInformationPropertyType> archivedIn;
    protected MeasureType imageQualityDegradation;
    protected MeasureType productQualityDegradation;
    @XmlSchemaType(name = "anySimpleType")
    protected String imageQualityDegradationQuotationMode;
    @XmlSchemaType(name = "anySimpleType")
    protected String productQualityDegradationQuotationMode;
    @XmlSchemaType(name = "string")
    protected ImageQualityStatusValueType imageQualityStatus;
    @XmlSchemaType(name = "string")
    protected ProductQualityStatusValueType productQualityStatus;
    protected List<CodeListType> imageQualityDegradationTag;
    protected List<CodeListType> productQualityDegradationTag;
    @XmlSchemaType(name = "anyURI")
    protected String imageQualityReportURL;
    @XmlSchemaType(name = "anyURI")
    protected String productQualityReportURL;
    protected List<HistogramPropertyType> histograms;
    protected List<EarthObservationPropertyType> composedOf;
    protected List<EarthObservationPropertyType> subsetOf;
    protected List<EarthObservationPropertyType> linkedWith;
    protected List<ProcessingInformationPropertyType> processing;
    protected String productGroupId;
    protected List<SpecificInformationPropertyType> vendorSpecific;

    /**
     * Obtient la valeur de la propriété identifier.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Définit la valeur de la propriété identifier.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Obtient la valeur de la propriété creationDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Définit la valeur de la propriété creationDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Obtient la valeur de la propriété modificationDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getModificationDate() {
        return modificationDate;
    }

    /**
     * Définit la valeur de la propriété modificationDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setModificationDate(XMLGregorianCalendar value) {
        this.modificationDate = value;
    }

    /**
     * Obtient la valeur de la propriété doi.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDoi() {
        return doi;
    }

    /**
     * Définit la valeur de la propriété doi.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDoi(String value) {
        this.doi = value;
    }

    /**
     * Obtient la valeur de la propriété parentIdentifier.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParentIdentifier() {
        return parentIdentifier;
    }

    /**
     * Définit la valeur de la propriété parentIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParentIdentifier(String value) {
        this.parentIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionType.
     *
     * @return
     *     possible object is
     *     {@link AcquisitionTypeValueType }
     *
     */
    public AcquisitionTypeValueType getAcquisitionType() {
        return acquisitionType;
    }

    /**
     * Définit la valeur de la propriété acquisitionType.
     *
     * @param value
     *     allowed object is
     *     {@link AcquisitionTypeValueType }
     *
     */
    public void setAcquisitionType(AcquisitionTypeValueType value) {
        this.acquisitionType = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionSubType.
     *
     * @return
     *     possible object is
     *     {@link CodeListType }
     *
     */
    public CodeListType getAcquisitionSubType() {
        return acquisitionSubType;
    }

    /**
     * Définit la valeur de la propriété acquisitionSubType.
     *
     * @param value
     *     allowed object is
     *     {@link CodeListType }
     *
     */
    public void setAcquisitionSubType(CodeListType value) {
        this.acquisitionSubType = value;
    }

    /**
     * Obtient la valeur de la propriété productType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Définit la valeur de la propriété productType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductType(String value) {
        this.productType = value;
    }

    /**
     * Obtient la valeur de la propriété status.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     * Définit la valeur de la propriété status.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Obtient la valeur de la propriété statusSubType.
     *
     * @return
     *     possible object is
     *     {@link StatusSubTypeValueEnumerationType }
     *
     */
    public StatusSubTypeValueEnumerationType getStatusSubType() {
        return statusSubType;
    }

    /**
     * Définit la valeur de la propriété statusSubType.
     *
     * @param value
     *     allowed object is
     *     {@link StatusSubTypeValueEnumerationType }
     *
     */
    public void setStatusSubType(StatusSubTypeValueEnumerationType value) {
        this.statusSubType = value;
    }

    /**
     * Obtient la valeur de la propriété statusDetail.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatusDetail() {
        return statusDetail;
    }

    /**
     * Définit la valeur de la propriété statusDetail.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatusDetail(String value) {
        this.statusDetail = value;
    }

    /**
     * Gets the value of the downlinkedTo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the downlinkedTo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDownlinkedTo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DownlinkInformationPropertyType }
     *
     *
     */
    public List<DownlinkInformationPropertyType> getDownlinkedTo() {
        if (downlinkedTo == null) {
            downlinkedTo = new ArrayList<DownlinkInformationPropertyType>();
        }
        return this.downlinkedTo;
    }

    /**
     * Gets the value of the archivedIn property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archivedIn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchivedIn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchivingInformationPropertyType }
     *
     *
     */
    public List<ArchivingInformationPropertyType> getArchivedIn() {
        if (archivedIn == null) {
            archivedIn = new ArrayList<ArchivingInformationPropertyType>();
        }
        return this.archivedIn;
    }

    /**
     * Obtient la valeur de la propriété imageQualityDegradation.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getImageQualityDegradation() {
        return imageQualityDegradation;
    }

    /**
     * Définit la valeur de la propriété imageQualityDegradation.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setImageQualityDegradation(MeasureType value) {
        this.imageQualityDegradation = value;
    }

    /**
     * Obtient la valeur de la propriété productQualityDegradation.
     *
     * @return
     *     possible object is
     *     {@link MeasureType }
     *
     */
    public MeasureType getProductQualityDegradation() {
        return productQualityDegradation;
    }

    /**
     * Définit la valeur de la propriété productQualityDegradation.
     *
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *
     */
    public void setProductQualityDegradation(MeasureType value) {
        this.productQualityDegradation = value;
    }

    /**
     * Obtient la valeur de la propriété imageQualityDegradationQuotationMode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getImageQualityDegradationQuotationMode() {
        return imageQualityDegradationQuotationMode;
    }

    /**
     * Définit la valeur de la propriété imageQualityDegradationQuotationMode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setImageQualityDegradationQuotationMode(String value) {
        this.imageQualityDegradationQuotationMode = value;
    }

    /**
     * Obtient la valeur de la propriété productQualityDegradationQuotationMode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductQualityDegradationQuotationMode() {
        return productQualityDegradationQuotationMode;
    }

    /**
     * Définit la valeur de la propriété productQualityDegradationQuotationMode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductQualityDegradationQuotationMode(String value) {
        this.productQualityDegradationQuotationMode = value;
    }

    /**
     * Obtient la valeur de la propriété imageQualityStatus.
     *
     * @return
     *     possible object is
     *     {@link ImageQualityStatusValueType }
     *
     */
    public ImageQualityStatusValueType getImageQualityStatus() {
        return imageQualityStatus;
    }

    /**
     * Définit la valeur de la propriété imageQualityStatus.
     *
     * @param value
     *     allowed object is
     *     {@link ImageQualityStatusValueType }
     *
     */
    public void setImageQualityStatus(ImageQualityStatusValueType value) {
        this.imageQualityStatus = value;
    }

    /**
     * Obtient la valeur de la propriété productQualityStatus.
     *
     * @return
     *     possible object is
     *     {@link ProductQualityStatusValueType }
     *
     */
    public ProductQualityStatusValueType getProductQualityStatus() {
        return productQualityStatus;
    }

    /**
     * Définit la valeur de la propriété productQualityStatus.
     *
     * @param value
     *     allowed object is
     *     {@link ProductQualityStatusValueType }
     *
     */
    public void setProductQualityStatus(ProductQualityStatusValueType value) {
        this.productQualityStatus = value;
    }

    /**
     * Gets the value of the imageQualityDegradationTag property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the imageQualityDegradationTag property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImageQualityDegradationTag().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeListType }
     *
     *
     */
    public List<CodeListType> getImageQualityDegradationTag() {
        if (imageQualityDegradationTag == null) {
            imageQualityDegradationTag = new ArrayList<CodeListType>();
        }
        return this.imageQualityDegradationTag;
    }

    /**
     * Gets the value of the productQualityDegradationTag property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productQualityDegradationTag property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductQualityDegradationTag().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodeListType }
     *
     *
     */
    public List<CodeListType> getProductQualityDegradationTag() {
        if (productQualityDegradationTag == null) {
            productQualityDegradationTag = new ArrayList<CodeListType>();
        }
        return this.productQualityDegradationTag;
    }

    /**
     * Obtient la valeur de la propriété imageQualityReportURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getImageQualityReportURL() {
        return imageQualityReportURL;
    }

    /**
     * Définit la valeur de la propriété imageQualityReportURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setImageQualityReportURL(String value) {
        this.imageQualityReportURL = value;
    }

    /**
     * Obtient la valeur de la propriété productQualityReportURL.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductQualityReportURL() {
        return productQualityReportURL;
    }

    /**
     * Définit la valeur de la propriété productQualityReportURL.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductQualityReportURL(String value) {
        this.productQualityReportURL = value;
    }

    /**
     * Gets the value of the histograms property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the histograms property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistograms().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HistogramPropertyType }
     *
     *
     */
    public List<HistogramPropertyType> getHistograms() {
        if (histograms == null) {
            histograms = new ArrayList<HistogramPropertyType>();
        }
        return this.histograms;
    }

    /**
     * Gets the value of the composedOf property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the composedOf property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComposedOf().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EarthObservationPropertyType }
     *
     *
     */
    public List<EarthObservationPropertyType> getComposedOf() {
        if (composedOf == null) {
            composedOf = new ArrayList<EarthObservationPropertyType>();
        }
        return this.composedOf;
    }

    /**
     * Gets the value of the subsetOf property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subsetOf property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubsetOf().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EarthObservationPropertyType }
     *
     *
     */
    public List<EarthObservationPropertyType> getSubsetOf() {
        if (subsetOf == null) {
            subsetOf = new ArrayList<EarthObservationPropertyType>();
        }
        return this.subsetOf;
    }

    /**
     * Gets the value of the linkedWith property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkedWith property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkedWith().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EarthObservationPropertyType }
     *
     *
     */
    public List<EarthObservationPropertyType> getLinkedWith() {
        if (linkedWith == null) {
            linkedWith = new ArrayList<EarthObservationPropertyType>();
        }
        return this.linkedWith;
    }

    /**
     * Gets the value of the processing property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processing property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessing().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessingInformationPropertyType }
     *
     *
     */
    public List<ProcessingInformationPropertyType> getProcessing() {
        if (processing == null) {
            processing = new ArrayList<ProcessingInformationPropertyType>();
        }
        return this.processing;
    }

    /**
     * Obtient la valeur de la propriété productGroupId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductGroupId() {
        return productGroupId;
    }

    /**
     * Définit la valeur de la propriété productGroupId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductGroupId(String value) {
        this.productGroupId = value;
    }

    /**
     * Gets the value of the vendorSpecific property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vendorSpecific property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVendorSpecific().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecificInformationPropertyType }
     *
     *
     */
    public List<SpecificInformationPropertyType> getVendorSpecific() {
        if (vendorSpecific == null) {
            vendorSpecific = new ArrayList<SpecificInformationPropertyType>();
        }
        return this.vendorSpecific;
    }

}
