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
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 * TemporalKeywords : This attribute specifies a word or phrase which serves to summarize the temporal characteristics referenced in the collection.
 *                 * Temporal_Resolution : UMM still has not decided if this field is to be added here.
 *
 *                 | DIF 9                     | ECHO 10                  | DIF 10                     | Notes                            |
 *                 | ------------------------- | ------------------------ | -------------------------- | -------------------------------- |
 *                 |            -              | TemporalKeywords/Keyword | Ancillary_Temporal_Keyword | Added from ECHO                  |
 *                 | Temporal_Resolution       |            -             | Temporal_Resolution        | Removed                          |
 *                 | Temporal_Resolution_Range |            -             |             -              | Still in Data_Resolution         |
 *
 *
 * <p>Classe Java pour TemporalInfoType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TemporalInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Ancillary_Temporal_Keyword" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalInfoType", propOrder = {
    "ancillaryTemporalKeyword"
})
public class TemporalInfoType {

    @XmlElement(name = "Ancillary_Temporal_Keyword")
    protected List<String> ancillaryTemporalKeyword;

    /**
     * Gets the value of the ancillaryTemporalKeyword property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ancillaryTemporalKeyword property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAncillaryTemporalKeyword().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAncillaryTemporalKeyword() {
        if (ancillaryTemporalKeyword == null) {
            ancillaryTemporalKeyword = new ArrayList<>();
        }
        return this.ancillaryTemporalKeyword;
    }

}
