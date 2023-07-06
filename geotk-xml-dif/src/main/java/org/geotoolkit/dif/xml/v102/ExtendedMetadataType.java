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
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *                 One of three classes of structures for storing "name-value" style information:
 *                 * Additional Attributes - granual attributes to be advertised in the collection
 *                 * Measured Parameters - Parameters measured in the Granuals to be advertised in the collection
 *                 * Extended Metadata - Collection metadata values, or other external - non granual details - specific to the provider or collection
 *
 *                 | DIF 9             | ECHO 10 | UMM               | DIF 10            | Notes                                    |
 *                 | ----------------- | ------- | ----------------- | ----------------- | ---------------------------------------- |
 *                 | Extended_Metadata |    -    | Extended_Metadata | Extended_Metadata | No change                                |
 *                 |                                                                                                                |
 *                 | > Group           |    -    | > Group           | > Group           | something unique, like a java class path |
 *                 | > Name            |    -    | > Name            | > Name            | Unique name for a given group            |
 *                 | > Description     |    -    | > Description     | > Description     | Optional Description of the name-value   |
 *                 | > Type            |    -    | > Type            | > Type            | A mimetype if applicable                 |
 *                 | > Update_Date     |    -    | > Update_Date     | > Update_Date     | Not part of metadata, see note below     |
 *                 | > Value           |    -    | > Value           | > Value           | Assume a positive state                  |
 *
 *                 Note:
 *
 *                 * if providing multiple values, the type attribute on __Value__ should be used
 *                 * __Update_Date__ is only used if the name-value has been updated by an external process and should be ignored in most cases
 *                 * __Value__ can be assumed, if not provided, to be in the selected/positive state; "true", "on", "active"
 *
 *                 The following groups are reserved:
 *
 *                 | Group              | Owner    |
 *                 | ------------------ | -------- |
 *                 | gov.nasa.gsfc.gcmd | GCMD     |
 *                 | gov.nasa.gcsf.ems  | EMS      |
 *                 | gov.nasa.cmr       | ECHO/CMR |
 *
 *
 *
 * <p>Classe Java pour ExtendedMetadataType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ExtendedMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Metadata" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Metadata" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedMetadataType", propOrder = {
    "metadata"
})
public class ExtendedMetadataType {

    @XmlElement(name = "Metadata", required = true)
    protected List<Metadata> metadata;

    /**
     * Gets the value of the metadata property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadata property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadata().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Metadata }
     *
     *
     */
    public List<Metadata> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<Metadata>();
        }
        return this.metadata;
    }

}
