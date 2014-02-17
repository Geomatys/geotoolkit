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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.BoundingBoxType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}BoundingBox" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}SupportedCRS"/>
 *         &lt;element name="WellKnownScaleSet" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TileMatrix" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "boundingBox",
    "supportedCRS",
    "wellKnownScaleSet",
    "tileMatrix"
})
@XmlRootElement(name = "TileMatrixSet")
public class TileMatrixSet extends DescriptionType {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    private CodeType identifier;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    private JAXBElement<? extends BoundingBoxType> boundingBox;
    @XmlElement(name = "SupportedCRS", namespace = "http://www.opengis.net/ows/1.1", required = true)
    @XmlSchemaType(name = "anyURI")
    private String supportedCRS;
    @XmlElement(name = "WellKnownScaleSet")
    @XmlSchemaType(name = "anyURI")
    private String wellKnownScaleSet;
    @XmlElement(name = "TileMatrix", required = true)
    private List<TileMatrix> tileMatrix;

    public TileMatrixSet() {

    }

    public TileMatrixSet(final CodeType identifier, final String supportedCRS) {
        this.identifier = identifier;
        this.supportedCRS = supportedCRS;
    }

    public TileMatrixSet(final CodeType identifier, final String supportedCRS, final JAXBElement<? extends BoundingBoxType> bbox, final String wellKnownScaleSet) {
        this.identifier        = identifier;
        this.supportedCRS      = supportedCRS;
        this.boundingBox       = bbox;
        this.wellKnownScaleSet = wellKnownScaleSet;
    }

    /**
     * Tile matrix set identifier
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Tile matrix set identifier
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(final CodeType value) {
        this.identifier = value;
    }

    /**
     * Minimum bounding rectangle surrounding the visible layer presented by this tile matrix set, in the supported CRS
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     *     
     */
    public JAXBElement<? extends BoundingBoxType> getBoundingBox() {
        return boundingBox;
    }

    /**
     * Minimum bounding rectangle surrounding the visible layer presented by this tile matrix set, in the supported CRS
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     *     
     */
    public void setBoundingBox(final JAXBElement<? extends BoundingBoxType> value) {
        this.boundingBox = ((JAXBElement<? extends BoundingBoxType> ) value);
    }

    /**
     * Reference to one coordinate reference system (CRS).
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupportedCRS() {
        return supportedCRS;
    }

    /**
     * Reference to one coordinate reference system (CRS).
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupportedCRS(final String value) {
        this.supportedCRS = value;
    }

    /**
     * Gets the value of the wellKnownScaleSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWellKnownScaleSet() {
        return wellKnownScaleSet;
    }

    /**
     * Sets the value of the wellKnownScaleSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWellKnownScaleSet(final String value) {
        this.wellKnownScaleSet = value;
    }

    /**
     * Describes a scale level and its tile matrix.Gets the value of the tileMatrix property.
     */
    public List<TileMatrix> getTileMatrix() {
        if (tileMatrix == null) {
            tileMatrix = new ArrayList<TileMatrix>();
        }
        return this.tileMatrix;
    }

    public void setTileMatrix(final List<TileMatrix> tm) {
        this.tileMatrix = tm;
}

    public TileMatrix getTileMatrixByName(final String name) {
        if (tileMatrix == null) {
            tileMatrix = new ArrayList<TileMatrix>();
        }
        for (TileMatrix tm : tileMatrix) {
            if (tm.getIdentifier() != null && tm.getIdentifier().getValue().equals(name)) {
                return tm;
            }
        }
        return null;
    }

}
