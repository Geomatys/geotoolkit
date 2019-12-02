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
import javax.xml.bind.annotation.XmlType;

/**
 *
 *
 * A union of the DIF Metadata event date fields with the three ECHO event time
 * fields. For each event authors can choose to write either a date or datetime
 * field depending on the known or desired resolution. DateTime is preferred but
 * for legacy records may not be available.
 *
 * | DIF 9 | ECHO 10 | UMM | DIF 10 | Notes | | ----------------------- |
 * ------------ | -------------------- | ---------------------- |
 * -------------------------------- | | /DIF_Creation_Date | - |
 * MetadataCreationDate | Metadata_Creation | Metadata Record was created | |
 * /Last_DIF_Revision_Date | - | MetadataRevisionDate | Metadata_Last_Revision |
 * Metadata Record was updated | | /Future_DIF_Review_Date | - |
 * MetadataReviewDate | Metadata_Future_Review | Metadata Record is to be
 * updated | | - | | - | Metadata_Delete | Metadata Record was deleted | | - |
 * InsertTime | - | Data_Creation | Granular data was inserted | | - |
 * LastUpdate | - | Data_Last_Revision | Granular data was updated | | - |
 * RevisionDate | - | Data_Future_Review | Granular data is to be updated | | -
 * | DeleteTime | - | Data_Delete | Granular data removed |
 *
 *
 *
 * <p>
 * Classe Java pour MetadataDatesType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="MetadataDatesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Metadata_Creation" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Metadata_Last_Revision" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Metadata_Future_Review" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" minOccurs="0"/>
 *         &lt;element name="Metadata_Delete" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" minOccurs="0"/>
 *         &lt;element name="Data_Creation" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Data_Last_Revision" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType"/>
 *         &lt;element name="Data_Future_Review" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" minOccurs="0"/>
 *         &lt;element name="Data_Delete" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataDatesType", propOrder = {
    "metadataCreation",
    "metadataLastRevision",
    "metadataFutureReview",
    "metadataDelete",
    "dataCreation",
    "dataLastRevision",
    "dataFutureReview",
    "dataDelete"
})
public class MetadataDatesType {

    @XmlElement(name = "Metadata_Creation", required = true)
    protected String metadataCreation;
    @XmlElement(name = "Metadata_Last_Revision", required = true)
    protected String metadataLastRevision;
    @XmlElement(name = "Metadata_Future_Review")
    protected String metadataFutureReview;
    @XmlElement(name = "Metadata_Delete")
    protected String metadataDelete;
    @XmlElement(name = "Data_Creation", required = true)
    protected String dataCreation;
    @XmlElement(name = "Data_Last_Revision", required = true)
    protected String dataLastRevision;
    @XmlElement(name = "Data_Future_Review")
    protected String dataFutureReview;
    @XmlElement(name = "Data_Delete")
    protected String dataDelete;

    public MetadataDatesType() {

    }

    public MetadataDatesType(String metadataCreation) {
        this.metadataCreation = metadataCreation;
    }

    /**
     * Obtient la valeur de la propriété metadataCreation.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMetadataCreation() {
        return metadataCreation;
    }

    /**
     * Définit la valeur de la propriété metadataCreation.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMetadataCreation(String value) {
        this.metadataCreation = value;
    }

    /**
     * Obtient la valeur de la propriété metadataLastRevision.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMetadataLastRevision() {
        return metadataLastRevision;
    }

    /**
     * Définit la valeur de la propriété metadataLastRevision.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMetadataLastRevision(String value) {
        this.metadataLastRevision = value;
    }

    /**
     * Obtient la valeur de la propriété metadataFutureReview.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMetadataFutureReview() {
        return metadataFutureReview;
    }

    /**
     * Définit la valeur de la propriété metadataFutureReview.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMetadataFutureReview(String value) {
        this.metadataFutureReview = value;
    }

    /**
     * Obtient la valeur de la propriété metadataDelete.
     *
     * @return possible object is {@link String }
     *
     */
    public String getMetadataDelete() {
        return metadataDelete;
    }

    /**
     * Définit la valeur de la propriété metadataDelete.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setMetadataDelete(String value) {
        this.metadataDelete = value;
    }

    /**
     * Obtient la valeur de la propriété dataCreation.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDataCreation() {
        return dataCreation;
    }

    /**
     * Définit la valeur de la propriété dataCreation.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDataCreation(String value) {
        this.dataCreation = value;
    }

    /**
     * Obtient la valeur de la propriété dataLastRevision.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDataLastRevision() {
        return dataLastRevision;
    }

    /**
     * Définit la valeur de la propriété dataLastRevision.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDataLastRevision(String value) {
        this.dataLastRevision = value;
    }

    /**
     * Obtient la valeur de la propriété dataFutureReview.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDataFutureReview() {
        return dataFutureReview;
    }

    /**
     * Définit la valeur de la propriété dataFutureReview.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDataFutureReview(String value) {
        this.dataFutureReview = value;
    }

    /**
     * Obtient la valeur de la propriété dataDelete.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDataDelete() {
        return dataDelete;
    }

    /**
     * Définit la valeur de la propriété dataDelete.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDataDelete(String value) {
        this.dataDelete = value;
    }

}
