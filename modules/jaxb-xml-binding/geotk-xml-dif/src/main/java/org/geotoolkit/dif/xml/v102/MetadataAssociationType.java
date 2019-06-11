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
 *
 *             | DIF 9        | ECHO 10               | UMM                 | DIF 10                | Notes                      |
 *             | ------------ | --------------------- | ------------------- | --------------------- | -------------------------- |
 *             | Parent_DIF   | CollectionAssociation | MetadataAssociation | Metadata_Association  | Added to match UMM         |
 *             |      -       | > ShortName           | > EntryID/ShortName | > Entry_ID/Short_Name |                            |
 *             |      -       | > VersionId           |          -          | > Entry_ID/Version    | Added to support ECHO      |
 *             | >type=Parent | > CollectionType      | > Type              | > Type                | Made an enum               |
 *             |      -       | > CollectionUse       |          -          | > Description         | not candidate for markdown |
 *
 *
 *
 * <p>Classe Java pour MetadataAssociationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="MetadataAssociationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Entry_ID" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}EntryIDType"/>
 *         &lt;element name="Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}MetadataAssociationTypeEnum"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataAssociationType", propOrder = {
    "entryID",
    "type",
    "description"
})
public class MetadataAssociationType {

    @XmlElement(name = "Entry_ID", required = true)
    protected EntryIDType entryID;
    @XmlElement(name = "Type", required = true)
    @XmlSchemaType(name = "string")
    protected MetadataAssociationTypeEnum type;
    @XmlElement(name = "Description")
    protected String description;

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
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link MetadataAssociationTypeEnum }
     *
     */
    public MetadataAssociationTypeEnum getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link MetadataAssociationTypeEnum }
     *
     */
    public void setType(MetadataAssociationTypeEnum value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
