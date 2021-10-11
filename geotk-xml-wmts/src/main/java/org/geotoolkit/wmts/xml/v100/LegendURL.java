/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.OnlineResourceType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}OnlineResourceType">
 *       &lt;attribute name="format" type="{http://www.opengis.net/ows/1.1}MimeType" />
 *       &lt;attribute name="minScaleDenominator" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxScaleDenominator" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "LegendURL")
public class LegendURL extends OnlineResourceType {

    @XmlAttribute
    private String format;
    @XmlAttribute
    private Double minScaleDenominator;
    @XmlAttribute
    private Double maxScaleDenominator;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger width;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger height;

    public LegendURL() {

    }

    public LegendURL(final String format, final BigInteger width,
            final BigInteger height, final Double minScaleDenominator, final Double maxScaleDenominator) {
        this.format              = format;
        this.height              = height;
        this.width               = width;
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
    }

    /**
     * Gets the value of the format property.
     *
     * @return
     *     possible object is
     *     {@link String }
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
     *     {@link String }
     *
     */
    public void setFormat(final String value) {
        this.format = value;
    }

    /**
     * Gets the value of the minScaleDenominator property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMinScaleDenominator() {
        return minScaleDenominator;
    }

    /**
     * Sets the value of the minScaleDenominator property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMinScaleDenominator(final Double value) {
        this.minScaleDenominator = value;
    }

    /**
     * Gets the value of the maxScaleDenominator property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getMaxScaleDenominator() {
        return maxScaleDenominator;
    }

    /**
     * Sets the value of the maxScaleDenominator property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setMaxScaleDenominator(final Double value) {
        this.maxScaleDenominator = value;
    }

    /**
     * Gets the value of the width property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setWidth(final BigInteger value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setHeight(final BigInteger value) {
        this.height = value;
    }

}
