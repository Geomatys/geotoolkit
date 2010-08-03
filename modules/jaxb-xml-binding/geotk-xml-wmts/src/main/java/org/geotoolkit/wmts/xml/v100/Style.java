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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}LegendURL" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifier",
    "legendURL"
})
@XmlRootElement(name = "Style")
public class Style extends DescriptionType {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/1.1", required = true)
    private CodeType identifier;
    @XmlElement(name = "LegendURL")
    private List<LegendURL> legendURL;
    @XmlAttribute
    private Boolean isDefault;

    public Style() {

    }

    public Style(CodeType identifier, List<LegendURL> legendURL) {
        this.identifier = identifier;
        this.legendURL  = legendURL;
    }

    /**
     * An unambiguous reference to this style, identifying a specific version when needed, normally used by software
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
     * An unambiguous reference to this style, identifying a specific version when needed, normally used by software
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
     * Description of an image that represents the legend of the map Gets the value of the legendURL property.
     */
    public List<LegendURL> getLegendURL() {
        if (legendURL == null) {
            legendURL = new ArrayList<LegendURL>();
        }
        return this.legendURL;
    }

    /**
     * Gets the value of the isDefault property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDefault() {
        return isDefault;
    }

    /**
     * Sets the value of the isDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDefault(Boolean value) {
        this.isDefault = value;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class=Style").append('\n');
        s.append("identifier:").append(getIdentifier().getValue()).append('\n');
        s.append("legendURL:").append('\n');
        for (LegendURL l:getLegendURL()) {
             s.append(l).append('\n');
        }
        s.append("isDefault:").append(isIsDefault()).append('\n');
        return s.toString();
    }
}
