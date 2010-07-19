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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="ScaleDenominator" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="TopLeftCorner" type="{http://www.opengis.net/ows/1.1}PositionType"/>
 *         &lt;element name="TileWidth" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="TileHeight" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="MatrixWidth" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="MatrixHeight" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
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
@XmlType(name = "TileMatrixType", propOrder = {
    "identifier",
    "scaleDenominator",
    "topLeftCorner",
    "topLeftPoint",
    "tileWidth",
    "tileHeight",
    "matrixWidth",
    "matrixHeight"
})
@XmlRootElement(name = "TileMatrix")
public class TileMatrix extends DescriptionType {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    private CodeType identifier;
    @XmlElement(name = "ScaleDenominator")
    private double scaleDenominator;
    @XmlList
    @XmlElement(name = "TopLeftCorner", type = Double.class)
    private List<Double> topLeftCorner;
    @XmlElement(name = "TopLeftPoint")
    private TopLeftPoint topLeftPoint;
    
    @XmlElement(name = "TileWidth", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger tileWidth;
    @XmlElement(name = "TileHeight", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger tileHeight;
    @XmlElement(name = "MatrixWidth", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger matrixWidth;
    @XmlElement(name = "MatrixHeight", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger matrixHeight;

    public TileMatrix() {

    }

    public TileMatrix(CodeType identifier, double scaleDenominator, TopLeftPoint topLeftPoint, BigInteger tileWidth, BigInteger tileHeight,
            BigInteger matrixWidth, BigInteger matrixHeight) {
        this.identifier       = identifier;
        this.scaleDenominator = scaleDenominator;
        this.matrixHeight     = matrixHeight;
        this.matrixWidth      = matrixWidth;
        this.tileHeight       = tileHeight;
        this.tileWidth        = tileWidth;
        this.topLeftPoint     = topLeftPoint;
    }

    /**
     * Tile matrix identifier. Typically an abreviation of the ScaleDenominator value or its equivalent pixel size
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
     * Tile matrix identifier. Typically an abreviation of the ScaleDenominator value or its equivalent pixel size
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the scaleDenominator property.
     * 
     */
    public double getScaleDenominator() {
        return scaleDenominator;
    }

    /**
     * Sets the value of the scaleDenominator property.
     * 
     */
    public void setScaleDenominator(double value) {
        this.scaleDenominator = value;
    }

    /**
     * Gets the value of the topLeftCorner property.
     * 
     * 
     */
    public List<Double> getTopLeftCorner() {
        if (topLeftCorner == null) {
            topLeftCorner = new ArrayList<Double>();
        }
        return this.topLeftCorner;
    }

    /**
     * Gets the value of the tileWidth property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTileWidth() {
        return tileWidth;
    }

    /**
     * Sets the value of the tileWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTileWidth(BigInteger value) {
        this.tileWidth = value;
    }

    /**
     * Gets the value of the tileHeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTileHeight() {
        return tileHeight;
    }

    /**
     * Sets the value of the tileHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTileHeight(BigInteger value) {
        this.tileHeight = value;
    }

    /**
     * Gets the value of the matrixWidth property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMatrixWidth() {
        return matrixWidth;
    }

    /**
     * Sets the value of the matrixWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMatrixWidth(BigInteger value) {
        this.matrixWidth = value;
    }

    /**
     * Gets the value of the matrixHeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMatrixHeight() {
        return matrixHeight;
    }

    /**
     * Sets the value of the matrixHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMatrixHeight(BigInteger value) {
        this.matrixHeight = value;
    }

    /**
     * @return the topLeftPoint
     */
    public TopLeftPoint getTopLeftPoint() {
        return topLeftPoint;
    }

    /**
     * @param topLeftPoint the topLeftPoint to set
     */
    public void setTopLeftPoint(TopLeftPoint topLeftPoint) {
        this.topLeftPoint = topLeftPoint;
    }

    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TileMatrix) {
            final TileMatrix that = (TileMatrix) object;
            return  Utilities.equals(this.identifier, that.identifier)     &&
                    Utilities.equals(this.matrixHeight, that.matrixHeight) &&
                    Utilities.equals(this.matrixWidth, that.matrixWidth)   &&
                    Utilities.equals(this.scaleDenominator, that.scaleDenominator) &&
                    Utilities.equals(this.tileHeight, that.tileHeight) &&
                    Utilities.equals(this.tileWidth, that.tileWidth) &&
                    Utilities.equals(this.topLeftCorner, that.topLeftCorner) &&
                    Utilities.equals(this.topLeftPoint, that.topLeftPoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.scaleDenominator) ^ (Double.doubleToLongBits(this.scaleDenominator) >>> 32));
        hash = 41 * hash + (this.topLeftCorner != null ? this.topLeftCorner.hashCode() : 0);
        hash = 41 * hash + (this.topLeftPoint != null ? this.topLeftPoint.hashCode() : 0);
        hash = 41 * hash + (this.tileWidth != null ? this.tileWidth.hashCode() : 0);
        hash = 41 * hash + (this.tileHeight != null ? this.tileHeight.hashCode() : 0);
        hash = 41 * hash + (this.matrixWidth != null ? this.matrixWidth.hashCode() : 0);
        hash = 41 * hash + (this.matrixHeight != null ? this.matrixHeight.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (identifier != null) {
            sb.append("identifier").append(identifier).append('\n');
        }
        sb.append("scaleDenominator").append(scaleDenominator).append('\n');

        if (topLeftCorner != null) {
            sb.append("topLeftCorner").append(topLeftCorner).append('\n');
        }
        if (topLeftPoint != null) {
            sb.append("topLeftPoint").append(topLeftPoint).append('\n');
        }
        if (tileWidth != null) {
            sb.append("tileWidth").append(tileWidth).append('\n');
        }
        if (tileHeight != null) {
            sb.append("tileHeight").append(tileHeight).append('\n');
        }
        if (matrixWidth != null) {
            sb.append("matrixWidth").append(matrixWidth).append('\n');
        }
        if (matrixHeight != null) {
            sb.append("matrixHeight").append(matrixHeight).append('\n');
        }
        return sb.toString();

    }

}
