/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.wcs.xml.v200.crs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * CRS information reported in the Capabilities document of a WCS service
 * supporting the CRS Extension.
 *
 * <p>
 * Classe Java pour CrsMetadataType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="CrsMetadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crsSupported" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrsMetadataType", propOrder = {
    "crsSupported"
})
@XmlRootElement(name = "CrsMetadata")
public class CrsMetadataType {

    @XmlSchemaType(name = "anyURI")
    private List<String> crsSupported;

    public CrsMetadataType() {

    }

    public CrsMetadataType(List<String> crsSupported) {
        this.crsSupported = crsSupported;
    }

    /**
     * Gets the value of the crsSupported property.
     *
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     */
    public List<String> getCrsSupported() {
        if (crsSupported == null) {
            crsSupported = new ArrayList<>();
        }
        return this.crsSupported;
    }

    /**
     * @param crsSupported the crsSupported to set
     */
    public void setCrsSupported(List<String> crsSupported) {
        this.crsSupported = crsSupported;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CrsMetadataType && super.equals(o)) {
            final CrsMetadataType that = (CrsMetadataType) o;
            return Objects.equals(this.crsSupported, that.crsSupported);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + java.util.Objects.hashCode(this.crsSupported);
        return hash;
    }
}
