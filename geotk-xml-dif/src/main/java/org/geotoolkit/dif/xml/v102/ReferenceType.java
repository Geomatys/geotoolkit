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
 *                 * mixed= "true" : Removed and the field Citation has been added to ease in translation
 *                 * Citation : the human readable reference; what used to be the Reference tag text
 *                 * External_Identifier : Was DOI (digital object identifier), use: "doi:10.1000/182" or "ark:/NAAN/Name[Qualifier]"
 *
 *                 | DIF 9                   | ECHO 10 | UMM                   | DIF 10                  | Notes                                                          |
 *                 | ----------------------- | ------- | --------------------- | ----------------------- | -------------------------------------------------------------- |
 *                 | mixed="true"            |    -    | mixed="true"          | Citation                | Added to support the removal of the mixed mode, Reference text |
 *                 | Publication_Date        |    -    | PublicationDate       | Publication_Date        | Renamed                                                        |
 *                 | Report_Number           |    -    | ReportNumber          | Report_Number           | Renamed                                                        |
 *                 | Publication_Place       |    -    | PublicationPlace      | Publication_Place       | Renamed                                                        |
 *                 | DOI                     |    -    | DOI                   | External_Identifier     | Made into a complex type so that DOI and ARK can be stored     |
 *                 | Other_Reference_Details |    -    | OtherReferenceDetails | Other_Reference_Details | Renamed                                                        |
 *
 *
 *
 * <p>Classe Java pour ReferenceType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Citation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Author" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Publication_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Series" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Edition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Volume" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Issue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Report_Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Publication_Place" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Publisher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pages" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ISBN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Persistent_Identifier" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PersistentIdentifierType" minOccurs="0"/>
 *         &lt;element name="Online_Resource" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Other_Reference_Details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", propOrder = {
    "citation",
    "author",
    "publicationDate",
    "title",
    "series",
    "edition",
    "volume",
    "issue",
    "reportNumber",
    "publicationPlace",
    "publisher",
    "pages",
    "isbn",
    "persistentIdentifier",
    "onlineResource",
    "otherReferenceDetails"
})
public class ReferenceType {

    @XmlElement(name = "Citation")
    protected String citation;
    @XmlElement(name = "Author")
    protected String author;
    @XmlElement(name = "Publication_Date")
    protected String publicationDate;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Series")
    protected String series;
    @XmlElement(name = "Edition")
    protected String edition;
    @XmlElement(name = "Volume")
    protected String volume;
    @XmlElement(name = "Issue")
    protected String issue;
    @XmlElement(name = "Report_Number")
    protected String reportNumber;
    @XmlElement(name = "Publication_Place")
    protected String publicationPlace;
    @XmlElement(name = "Publisher")
    protected String publisher;
    @XmlElement(name = "Pages")
    protected String pages;
    @XmlElement(name = "ISBN")
    protected String isbn;
    @XmlElement(name = "Persistent_Identifier")
    protected PersistentIdentifierType persistentIdentifier;
    @XmlElement(name = "Online_Resource")
    @XmlSchemaType(name = "anyURI")
    protected String onlineResource;
    @XmlElement(name = "Other_Reference_Details")
    protected String otherReferenceDetails;

    /**
     * Obtient la valeur de la propriété citation.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCitation() {
        return citation;
    }

    /**
     * Définit la valeur de la propriété citation.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCitation(String value) {
        this.citation = value;
    }

    /**
     * Obtient la valeur de la propriété author.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété publicationDate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * Définit la valeur de la propriété publicationDate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPublicationDate(String value) {
        this.publicationDate = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété series.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSeries() {
        return series;
    }

    /**
     * Définit la valeur de la propriété series.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSeries(String value) {
        this.series = value;
    }

    /**
     * Obtient la valeur de la propriété edition.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdition() {
        return edition;
    }

    /**
     * Définit la valeur de la propriété edition.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdition(String value) {
        this.edition = value;
    }

    /**
     * Obtient la valeur de la propriété volume.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVolume() {
        return volume;
    }

    /**
     * Définit la valeur de la propriété volume.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVolume(String value) {
        this.volume = value;
    }

    /**
     * Obtient la valeur de la propriété issue.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIssue() {
        return issue;
    }

    /**
     * Définit la valeur de la propriété issue.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIssue(String value) {
        this.issue = value;
    }

    /**
     * Obtient la valeur de la propriété reportNumber.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReportNumber() {
        return reportNumber;
    }

    /**
     * Définit la valeur de la propriété reportNumber.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReportNumber(String value) {
        this.reportNumber = value;
    }

    /**
     * Obtient la valeur de la propriété publicationPlace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPublicationPlace() {
        return publicationPlace;
    }

    /**
     * Définit la valeur de la propriété publicationPlace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPublicationPlace(String value) {
        this.publicationPlace = value;
    }

    /**
     * Obtient la valeur de la propriété publisher.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Définit la valeur de la propriété publisher.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPublisher(String value) {
        this.publisher = value;
    }

    /**
     * Obtient la valeur de la propriété pages.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPages() {
        return pages;
    }

    /**
     * Définit la valeur de la propriété pages.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPages(String value) {
        this.pages = value;
    }

    /**
     * Obtient la valeur de la propriété isbn.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getISBN() {
        return isbn;
    }

    /**
     * Définit la valeur de la propriété isbn.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setISBN(String value) {
        this.isbn = value;
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

    /**
     * Obtient la valeur de la propriété otherReferenceDetails.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOtherReferenceDetails() {
        return otherReferenceDetails;
    }

    /**
     * Définit la valeur de la propriété otherReferenceDetails.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOtherReferenceDetails(String value) {
        this.otherReferenceDetails = value;
    }

}
