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

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * CRSs for the request bounding box and for the result coverage.
 *
 * <p>
 * Classe Java pour CrsType complex type.
 *
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 *
 * <pre>
 * &lt;complexType name="CrsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subsettingCrs" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="outputCrs" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrsType", propOrder = {
    "subsettingCrs",
    "outputCrs"
})
@XmlRootElement(name = "Crs")
public class CrsType {

    @XmlSchemaType(name = "anyURI")
    private String subsettingCrs;
    @XmlSchemaType(name = "anyURI")
    private String outputCrs;

    public CrsType() {

    }

    public CrsType(String subsettingCrs, String outputCrs) {
        this.outputCrs = outputCrs;
        this.subsettingCrs = subsettingCrs;
    }

    /**
     * Obtient la valeur de la propriété subsettingCrs.
     *
     * @return possible object is {@link String }
     *
     */
    public String getSubsettingCrs() {
        return subsettingCrs;
    }

    /**
     * Définit la valeur de la propriété subsettingCrs.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setSubsettingCrs(String value) {
        this.subsettingCrs = value;
    }

    /**
     * Obtient la valeur de la propriété outputCrs.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOutputCrs() {
        return outputCrs;
    }

    /**
     * Définit la valeur de la propriété outputCrs.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOutputCrs(String value) {
        this.outputCrs = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CrsType && super.equals(o)) {
            final CrsType that = (CrsType) o;
            return Objects.equals(this.outputCrs, that.outputCrs)
                    && Objects.equals(this.subsettingCrs, that.subsettingCrs);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + java.util.Objects.hashCode(this.outputCrs);
        hash = 41 * hash + java.util.Objects.hashCode(this.subsettingCrs);
        return hash;
    }
}
