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
package org.geotoolkit.sld.xml.v110;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.se.xml.v110.DescriptionType;


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
 *         &lt;element ref="{http://www.opengis.net/se}Name" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Description" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sld}RemoteOWS"/>
 *           &lt;element ref="{http://www.opengis.net/sld}InlineFeature"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sld}LayerFeatureConstraints"/>
 *           &lt;element ref="{http://www.opengis.net/sld}LayerCoverageConstraints"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sld}UserStyle" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "description",
    "remoteOWS",
    "inlineFeature",
    "layerFeatureConstraints",
    "layerCoverageConstraints",
    "userStyle"
})
@XmlRootElement(name = "UserLayer")
public class UserLayer {

    @XmlElement(name = "Name", namespace = "http://www.opengis.net/se")
    protected String name;
    @XmlElement(name = "Description", namespace = "http://www.opengis.net/se")
    protected DescriptionType description;
    @XmlElement(name = "RemoteOWS")
    protected RemoteOWS remoteOWS;
    @XmlElement(name = "InlineFeature")
    protected InlineFeature inlineFeature;
    @XmlElement(name = "LayerFeatureConstraints")
    protected LayerFeatureConstraints layerFeatureConstraints;
    @XmlElement(name = "LayerCoverageConstraints")
    protected LayerCoverageConstraints layerCoverageConstraints;
    @XmlElement(name = "UserStyle", required = true)
    protected List<UserStyle> userStyle;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link DescriptionType }
     *
     */
    public DescriptionType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link DescriptionType }
     *
     */
    public void setDescription(final DescriptionType value) {
        this.description = value;
    }

    /**
     * Gets the value of the remoteOWS property.
     *
     * @return
     *     possible object is
     *     {@link RemoteOWS }
     *
     */
    public RemoteOWS getRemoteOWS() {
        return remoteOWS;
    }

    /**
     * Sets the value of the remoteOWS property.
     *
     * @param value
     *     allowed object is
     *     {@link RemoteOWS }
     *
     */
    public void setRemoteOWS(final RemoteOWS value) {
        this.remoteOWS = value;
    }

    /**
     * Gets the value of the inlineFeature property.
     *
     * @return
     *     possible object is
     *     {@link InlineFeature }
     *
     */
    public InlineFeature getInlineFeature() {
        return inlineFeature;
    }

    /**
     * Sets the value of the inlineFeature property.
     *
     * @param value
     *     allowed object is
     *     {@link InlineFeature }
     *
     */
    public void setInlineFeature(final InlineFeature value) {
        this.inlineFeature = value;
    }

    /**
     * Gets the value of the layerFeatureConstraints property.
     *
     * @return
     *     possible object is
     *     {@link LayerFeatureConstraints }
     *
     */
    public LayerFeatureConstraints getLayerFeatureConstraints() {
        return layerFeatureConstraints;
    }

    /**
     * Sets the value of the layerFeatureConstraints property.
     *
     * @param value
     *     allowed object is
     *     {@link LayerFeatureConstraints }
     *
     */
    public void setLayerFeatureConstraints(final LayerFeatureConstraints value) {
        this.layerFeatureConstraints = value;
    }

    /**
     * Gets the value of the layerCoverageConstraints property.
     *
     * @return
     *     possible object is
     *     {@link LayerCoverageConstraints }
     *
     */
    public LayerCoverageConstraints getLayerCoverageConstraints() {
        return layerCoverageConstraints;
    }

    /**
     * Sets the value of the layerCoverageConstraints property.
     *
     * @param value
     *     allowed object is
     *     {@link LayerCoverageConstraints }
     *
     */
    public void setLayerCoverageConstraints(final LayerCoverageConstraints value) {
        this.layerCoverageConstraints = value;
    }

    /**
     * Gets the value of the userStyle property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userStyle property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserStyle().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UserStyle }
     *
     *
     */
    public List<UserStyle> getUserStyle() {
        if (userStyle == null) {
            userStyle = new ArrayList<UserStyle>();
        }
        return this.userStyle;
    }

}
