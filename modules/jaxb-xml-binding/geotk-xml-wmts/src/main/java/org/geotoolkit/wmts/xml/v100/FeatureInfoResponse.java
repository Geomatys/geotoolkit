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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureCollectionType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractFeatureCollection"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TextPayload"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}BinaryPayload"/>
 *         &lt;element name="AnyContent" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "abstractFeatureCollection",
    "textPayload",
    "binaryPayload",
    "anyContent"
})
@XmlRootElement(name = "FeatureInfoResponse")
public class FeatureInfoResponse {

    @XmlElement(name = "AbstractFeatureCollection", namespace = "http://www.opengis.net/gml")
    private AbstractFeatureCollectionType abstractFeatureCollection;
    @XmlElement(name = "TextPayload")
    private TextPayload textPayload;
    @XmlElement(name = "BinaryPayload")
    private BinaryPayload binaryPayload;
    @XmlElement(name = "AnyContent")
    private Object anyContent;

    /**
     * This allows to define any FeatureCollection that is a substitutionGroup  of gml:_GML and use it here.
     * A Geography Markup Language GML Simple Features Profile level 0 response format is strongly recommended as a FeatureInfo response.
     * 
     * @return
     *     possible object is
     *     {@link AbstractFeatureCollectionType }
     *     
     */
    public AbstractFeatureCollectionType getAbstractFeatureCollection() {
        return abstractFeatureCollection;
    }

    /**
     * Sets the value of the abstractFeatureCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractFeatureCollectionType }
     *     
     */
    public void setAbstractFeatureCollection(final AbstractFeatureCollectionType value) {
        this.abstractFeatureCollection = value;
    }

    /**
     * This allows to include any text format that is not a gml:_FeatureCollection like HTML, TXT, etc
     * @return
     *     possible object is
     *     {@link TextPayload }
     *     
     */
    public TextPayload getTextPayload() {
        return textPayload;
    }

    /**
     * Sets the value of the textPayload property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextPayload }
     *     
     */
    public void setTextPayload(final TextPayload value) {
        this.textPayload = value;
    }

    /**
     * This allows to include any binary format. Binary formats are not common
     * response for a GeFeatureInfo requests but possible for some imaginative implementations.
     * 
     * @return
     *     possible object is
     *     {@link BinaryPayload }
     *     
     */
    public BinaryPayload getBinaryPayload() {
        return binaryPayload;
    }

    /**
     * Sets the value of the binaryPayload property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryPayload }
     *     
     */
    public void setBinaryPayload(final BinaryPayload value) {
        this.binaryPayload = value;
    }

    /**
     * Gets the value of the anyContent property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAnyContent() {
        return anyContent;
    }

    /**
     * Sets the value of the anyContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAnyContent(final Object value) {
        this.anyContent = value;
    }

}
