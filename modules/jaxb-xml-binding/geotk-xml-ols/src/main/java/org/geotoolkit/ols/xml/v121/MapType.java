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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;


/**
 * <p>Java class for MapType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MapType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractDataType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="Content" type="{http://www.opengis.net/xls}ContentType"/>
 *         &lt;choice>
 *           &lt;element name="BBoxContext" type="{http://www.opengis.net/gml}EnvelopeType"/>
 *           &lt;element name="CenterContext" type="{http://www.opengis.net/xls}CenterContextType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapType", propOrder = {
    "content",
    "bBoxContext",
    "centerContext"
})
@XmlSeeAlso({
    RouteMapType.class
})
public class MapType
    extends AbstractDataType
{

    @XmlElement(name = "Content")
    private ContentType content;
    @XmlElement(name = "BBoxContext")
    private EnvelopeType bBoxContext;
    @XmlElement(name = "CenterContext")
    private CenterContextType centerContext;

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link ContentType }
     *     
     */
    public ContentType getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentType }
     *     
     */
    public void setContent(ContentType value) {
        this.content = value;
    }

    /**
     * Gets the value of the bBoxContext property.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getBBoxContext() {
        return bBoxContext;
    }

    /**
     * Sets the value of the bBoxContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setBBoxContext(EnvelopeType value) {
        this.bBoxContext = value;
    }

    /**
     * Gets the value of the centerContext property.
     * 
     * @return
     *     possible object is
     *     {@link CenterContextType }
     *     
     */
    public CenterContextType getCenterContext() {
        return centerContext;
    }

    /**
     * Sets the value of the centerContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link CenterContextType }
     *     
     */
    public void setCenterContext(CenterContextType value) {
        this.centerContext = value;
    }

}
