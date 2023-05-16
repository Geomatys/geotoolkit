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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * The GCMD collection ID, identifies the metadata record. Uniqueness is defined
 * using Originating_Metadata_Node, Entry_ID, and Version and can be represented
 * as: `[Orig_Node]Entry_ID[Version]`
 *
 *                 * Note, version constants: * 'Not applicable' means Not Applicable for the
 * metadata situation and should be ignored * 'Not provided' means Not provided
 * and should be ignored unless record is in review * Also see Parent_Metadata
 *
 * | DIF 9 | ECHO 10 | UMM | DIF 10 | Notes | | -------- | --------- |
 * ----------------- | ------------------- | -------------------- | | Entry_ID |
 * ShortName | EntryID/ShortName | Entry_ID/Short_Name | No change | | | Version
 * | EntryID/Version | Entry_ID/Version | moved from top level |
 *
 *
 *
 * <p>
 * Classe Java pour EntryIDType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="EntryIDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Short_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntryIDType", propOrder = {
    "shortName",
    "version"
})
@XmlSeeAlso({
    ParentMetadataType.class
})
public class EntryIDType {

    @XmlElement(name = "Short_Name", required = true)
    protected String shortName;
    @XmlElement(name = "Version", required = true)
    protected String version;

    public EntryIDType() {

    }

    public EntryIDType(String shortName, String version) {
        this.shortName = shortName;
        this.version = version;
    }

    /**
     * Obtient la valeur de la propriété shortName.
     *
     * @return possible object is {@link String }
     *
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Définit la valeur de la propriété shortName.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     *
     * @return possible object is {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
