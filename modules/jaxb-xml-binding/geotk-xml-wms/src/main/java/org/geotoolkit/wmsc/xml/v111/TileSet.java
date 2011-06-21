/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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


package org.geotoolkit.wmsc.xml.v111;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wms.xml.v111.BoundingBox;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "srs",
    "boundingBox",
    "resolutions",
    "width",
    "height",
    "format",
    "layers",
    "styles"
})
@XmlRootElement(name = "TileSet")
public class TileSet {

    @XmlElement(name = "SRS", required = true)
    private String srs;
    @XmlElement(name = "BoundingBox")
    private BoundingBox boundingBox;
    
    @XmlList
    @XmlElement(name = "Resolutions", required = true)
    private List<Double> resolutions;
    @XmlElement(name = "Width", required = true)
    private Integer width;
    @XmlElement(name = "Height", required = true)
    private Integer height;
    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "Layers")
    private List<String> layers;
    @XmlElement(name = "Styles")
    private List<String> styles;

    public TileSet() {
        
    }
    
    public TileSet(String srs, BoundingBox boundingBox, List<Double> resolutions, Integer width, Integer height, String format, List<String> layers) {
        this.boundingBox = boundingBox;
        this.format = format;
        this.height = height;
        this.layers = layers;
        this.resolutions = resolutions;
        this.srs = srs;
        this.width = width;
    }
    
    /**
     * Gets the value of the srs property.
     * 
     * @return
     *     possible object is
     *     {@link SRS }
     *     
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Sets the value of the srs property.
     * 
     * @param value
     *     allowed object is
     *     {@link SRS }
     *     
     */
    public void setSRS(String value) {
        this.srs = value;
    }

    /**
     * Gets the value of the boundingBox property.
     * 
     * @return
     *     possible object is
     *     {@link BoundingBox }
     *     
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoundingBox }
     *     
     */
    public void setBoundingBox(BoundingBox value) {
        this.boundingBox = value;
    }

    /**
     * Gets the value of the resolutions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public List<Double> getResolutions() {
        return resolutions;
    }

    /**
     * Sets the value of the resolutions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolutions(List<Double> value) {
        this.resolutions = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(Integer value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(Integer value) {
        this.height = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link Format }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link Format }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the layers property.
     */
    public List<String> getLayers() {
        if (layers == null) {
            layers = new ArrayList<String>();
        }
        return this.layers;
    }

    /**
     * Gets the value of the styles property.
     * 
     */
    public List<String> getStyles() {
        if (styles == null) {
            styles = new ArrayList<String>();
        }
        return this.styles;
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TileSet) {
            final TileSet that = (TileSet) object;
            return Utilities.equals(this.boundingBox, that.boundingBox) &&
                   Utilities.equals(this.format,     that.format)     &&
                   Utilities.equals(this.height,   that.height)   &&
                   Utilities.equals(this.layers,   that.layers)   &&
                   Utilities.equals(this.srs,   that.srs)   &&
                   Utilities.equals(this.styles,   that.styles)   &&
                   Utilities.equals(this.width,   that.width)   &&
                   Utilities.equals(this.resolutions,   that.resolutions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.srs != null ? this.srs.hashCode() : 0);
        hash = 79 * hash + (this.boundingBox != null ? this.boundingBox.hashCode() : 0);
        hash = 79 * hash + (this.resolutions != null ? this.resolutions.hashCode() : 0);
        hash = 79 * hash + (this.width != null ? this.width.hashCode() : 0);
        hash = 79 * hash + (this.height != null ? this.height.hashCode() : 0);
        hash = 79 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 79 * hash + (this.layers != null ? this.layers.hashCode() : 0);
        hash = 79 * hash + (this.styles != null ? this.styles.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[TileSet]\n");
        if (boundingBox != null) {
            s.append("boundingBox:").append(boundingBox).append('\n');
        }
        if (format != null) {
            s.append("format:").append(format).append('\n');
        }
        if (height != null) {
            s.append("height:").append(height).append('\n');
        }
        if (width != null) {
           s.append("width:").append(width).append('\n');
        }
        if (resolutions != null) {
            s.append("resolutions:").append('\n');
            for (Double d : resolutions) {
                s.append(d).append('\n');
            }
        }
        if (layers != null) {
            s.append("layers:").append('\n');
            for (String d : layers) {
                s.append(d).append('\n');
            }
        }
        if (styles != null) {
            s.append("styles:").append('\n');
            for (String d : styles) {
                s.append(d).append("\n");
            }
        }
        return s.toString();
    }
}
