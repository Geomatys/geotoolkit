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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.BoundingBox;


/**
 * This type is adapted from the EnvelopeType of GML 3.1, with modified contents and documentation for encoding a MINIMUM size box SURROUNDING all associated data.
 *
 * <p>Java class for BoundingBoxType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BoundingBoxType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LowerCorner" type="{http://www.opengis.net/ows}PositionType"/>
 *         &lt;element name="UpperCorner" type="{http://www.opengis.net/ows}PositionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="crs" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="dimensions" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingBoxType", propOrder = {
    "lowerCorner",
    "upperCorner"
})
@XmlSeeAlso({
    WGS84BoundingBoxType.class
})
public class BoundingBoxType implements BoundingBox {

    @XmlList
    @XmlElement(name = "LowerCorner", type = Double.class)
    private List<Double> lowerCorner  = new ArrayList<Double>();
    @XmlList
    @XmlElement(name = "UpperCorner", type = Double.class)
    private List<Double> upperCorner = new ArrayList<Double>();
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String crs;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer dimensions;

    public BoundingBoxType(){
    }

    /**
     * Build a 2 dimension boundingBox.
     *
     * @param crs
     * @param maxx
     * @param maxy
     * @param minx
     * @param miny
     */
    public BoundingBoxType(final String crs, final double minx, final double miny, final double maxx, final double maxy){
        this.dimensions = 2;
        this.lowerCorner.add(minx);
        this.lowerCorner.add(miny);
        this.upperCorner.add(maxx);
        this.upperCorner.add(maxy);
        this.crs = crs;
    }

    /**
     * Gets the value of the lowerCorner property.
     * (unmodifiable)
     */
    @Override
    public List<Double> getLowerCorner() {
       return lowerCorner;
    }

    /**
     * Set the lower corner list.
     */
    public void setLowerCorner(final List<Double> lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    /**
     * Set the upper corner list.
     */
    public void setUpperCorner(final List<Double> upperCorner) {
        this.upperCorner = upperCorner;
    }

    /**
     * add a point to the lower corner list.
     */
    public void setLowerCorner(final Double point) {
        this.lowerCorner.add(point);
    }

    /**
     * add a point to the upper corner list.
     */
    public void setUpperCorner(final Double point) {
        this.upperCorner.add(point);
    }

    /**
     * Gets the value of the upperCorner property.
     * (unmodifiable)
     */
    @Override
    public List<Double> getUpperCorner() {
        return upperCorner;
    }

    /**
     * Gets the value of the crs property.
     *
     */
    @Override
    public String getCrs() {
        return crs;
    }

    /**
     * set the crs value
     */
    public void setCrs(final String crs) {
        this.crs = crs;
    }

    /**
     * Gets the value of the dimensions property.
     */
    @Override
    public Integer getDimensions() {
        return dimensions;
    }

     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BoundingBoxType) {
            final BoundingBoxType that = (BoundingBoxType) object;
            return Objects.equals(this.crs        , that.crs)         &&
                   Objects.equals(this.dimensions , that.dimensions)  &&
                   Objects.equals(this.lowerCorner, that.lowerCorner) &&
                   Objects.equals(this.upperCorner, that.upperCorner);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.lowerCorner != null ? this.lowerCorner.hashCode() : 0);
        hash = 43 * hash + (this.upperCorner != null ? this.upperCorner.hashCode() : 0);
        hash = 43 * hash + (this.crs != null ? this.crs.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]:").append('\n');
        if (crs != null) {
            s.append("CRS:").append(crs).append('\n');
        }
        if (dimensions != null) {
            s.append("Dim:").append(dimensions).append('\n');
        }
        if (lowerCorner != null) {
            s.append("lower corner: ");
            for (Double d: lowerCorner) {
                s.append(d).append(' ');
            }
        }
        if (upperCorner != null) {
            s.append("upper corner: ");
            for (Double d: upperCorner) {
                s.append(d).append(' ');
            }
        }
        return s.toString();
    }
}
