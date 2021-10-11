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

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TileMatrixLimits" maxOccurs="unbounded"/>
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
    "tileMatrixLimits"
})
@XmlRootElement(name = "TileMatrixSetLimits")
public class TileMatrixSetLimits {

    @XmlElement(name = "TileMatrixLimits", required = true)
    private List<TileMatrixLimits> tileMatrixLimits;

    /**
     * Metadata describing the limits of the TileMatrixSet indices.
     * Multiplicity must be the multiplicity of TileMatrix in this TileMatrixSet.
     * Gets the value of the tileMatrixLimits property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TileMatrixLimits }
     *
     *
     */
    public List<TileMatrixLimits> getTileMatrixLimits() {
        if (tileMatrixLimits == null) {
            tileMatrixLimits = new ArrayList<TileMatrixLimits>();
        }
        return this.tileMatrixLimits;
    }

}
