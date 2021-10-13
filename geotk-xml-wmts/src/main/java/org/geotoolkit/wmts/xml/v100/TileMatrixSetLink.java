/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

package org.geotoolkit.wmts.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TileMatrixSet" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TileMatrixSetLimits" minOccurs="0"/>
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
    "tileMatrixSet",
    "tileMatrixSetLimits"
})
@XmlRootElement(name = "TileMatrixSetLink")
public class TileMatrixSetLink {

    @XmlElement(name = "TileMatrixSet", required = true)
    private String tileMatrixSet;
    @XmlElement(name = "TileMatrixSetLimits")
    private TileMatrixSetLimits tileMatrixSetLimits;

    public TileMatrixSetLink() {

    }

    public TileMatrixSetLink(final String tileMatrixSet) {
        this.tileMatrixSet = tileMatrixSet;
    }

    /**
     * Gets the value of the tileMatrixSet property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    /**
     * Sets the value of the tileMatrixSet property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTileMatrixSet(final String value) {
        this.tileMatrixSet = value;
    }

    /**
     * Indices limits for this tileMatrixSet.
     * The absence of this element means that tile row and tile col indices are only limited by 0
     * and the corresponding tileMatrixSet maximum definitions.
     *
     * @return
     *     possible object is
     *     {@link TileMatrixSetLimits }
     *
     */
    public TileMatrixSetLimits getTileMatrixSetLimits() {
        return tileMatrixSetLimits;
    }

    /**
     * Sets the value of the tileMatrixSetLimits property.
     *
     * @param value
     *     allowed object is
     *     {@link TileMatrixSetLimits }
     *
     */
    public void setTileMatrixSetLimits(final TileMatrixSetLimits value) {
        this.tileMatrixSetLimits = value;
    }

}
