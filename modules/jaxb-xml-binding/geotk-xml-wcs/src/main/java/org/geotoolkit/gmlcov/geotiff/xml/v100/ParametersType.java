/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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


package org.geotoolkit.gmlcov.geotiff.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compression" type="{http://www.opengis.net/gmlcov/geotiff/1.0}compressionType"/>
 *         &lt;element name="jpeg_quality" type="{http://www.opengis.net/gmlcov/geotiff/1.0}jpeg_qualityType"/>
 *         &lt;element name="predictor" type="{http://www.opengis.net/gmlcov/geotiff/1.0}predictorType"/>
 *         &lt;element name="interleave" type="{http://www.opengis.net/gmlcov/geotiff/1.0}interleaveType"/>
 *         &lt;element name="tiling" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tileheight" type="{http://www.opengis.net/gmlcov/geotiff/1.0}tileheightType"/>
 *         &lt;element name="tilewidth" type="{http://www.opengis.net/gmlcov/geotiff/1.0}tilewidthType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parametersType", propOrder = {
    "compression",
    "jpegQuality",
    "predictor",
    "interleave",
    "tiling",
    "tileheight",
    "tilewidth"
})
public class ParametersType {

    @XmlElement(required = true, defaultValue = "None")
    private CompressionType compression;
    @XmlElement(name = "jpeg_quality", defaultValue = "75")
    private int jpegQuality;
    @XmlElement(required = true, defaultValue = "None")
    private PredictorType predictor;
    @XmlElement(required = true, defaultValue = "Pixel")
    private InterleaveType interleave;
    private boolean tiling;
    @XmlElement(required = true)
    private Integer tileheight;
    @XmlElement(required = true)
    private Integer tilewidth;

    /**
     * Gets the value of the compression property.
     * 
     * @return
     *     possible object is
     *     {@link CompressionType }
     *     
     */
    public CompressionType getCompression() {
        return compression;
    }

    /**
     * Sets the value of the compression property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompressionType }
     *     
     */
    public void setCompression(CompressionType value) {
        this.compression = value;
    }

    /**
     * Gets the value of the jpegQuality property.
     * 
     */
    public int getJpegQuality() {
        return jpegQuality;
    }

    /**
     * Sets the value of the jpegQuality property.
     * 
     */
    public void setJpegQuality(int value) {
        this.jpegQuality = value;
    }

    /**
     * Gets the value of the predictor property.
     * 
     * @return
     *     possible object is
     *     {@link PredictorType }
     *     
     */
    public PredictorType getPredictor() {
        return predictor;
    }

    /**
     * Sets the value of the predictor property.
     * 
     * @param value
     *     allowed object is
     *     {@link PredictorType }
     *     
     */
    public void setPredictor(PredictorType value) {
        this.predictor = value;
    }

    /**
     * Gets the value of the interleave property.
     * 
     * @return
     *     possible object is
     *     {@link InterleaveType }
     *     
     */
    public InterleaveType getInterleave() {
        return interleave;
    }

    /**
     * Sets the value of the interleave property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterleaveType }
     *     
     */
    public void setInterleave(InterleaveType value) {
        this.interleave = value;
    }

    /**
     * Gets the value of the tiling property.
     * 
     */
    public boolean isTiling() {
        return tiling;
    }

    /**
     * Sets the value of the tiling property.
     * 
     */
    public void setTiling(boolean value) {
        this.tiling = value;
    }

    /**
     * Gets the value of the tileheight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTileheight() {
        return tileheight;
    }

    /**
     * Sets the value of the tileheight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTileheight(Integer value) {
        this.tileheight = value;
    }

    /**
     * Gets the value of the tilewidth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTilewidth() {
        return tilewidth;
    }

    /**
     * Sets the value of the tilewidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTilewidth(Integer value) {
        this.tilewidth = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ParametersType) {
            final ParametersType that = (ParametersType) o;
           
            return Objects.equals(this.compression, that.compression) &&
                   Objects.equals(this.interleave,   that.interleave) &&
                   Objects.equals(this.jpegQuality,   that.jpegQuality) &&
                   Objects.equals(this.predictor,   that.predictor) &&
                   Objects.equals(this.tileheight,   that.tileheight) &&
                   Objects.equals(this.tilewidth,   that.tilewidth) &&
                   Objects.equals(this.tiling,   that.tiling);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.compression);
        hash = 29 * hash + this.jpegQuality;
        hash = 29 * hash + Objects.hashCode(this.predictor);
        hash = 29 * hash + Objects.hashCode(this.interleave);
        hash = 29 * hash + (this.tiling ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.tileheight);
        hash = 29 * hash + Objects.hashCode(this.tilewidth);
        return hash;
    }
}
