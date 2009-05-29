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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.se.xml.v110.CoverageStyleType;
import org.geotoolkit.se.xml.v110.DescriptionType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.se.xml.v110.OnlineResourceType;


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
 *         &lt;element ref="{http://www.opengis.net/sld}IsDefault" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://www.opengis.net/se}FeatureTypeStyle"/>
 *           &lt;element ref="{http://www.opengis.net/se}CoverageStyle"/>
 *           &lt;element ref="{http://www.opengis.net/se}OnlineResource"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "description",
    "isDefault",
    "featureTypeStyleOrCoverageStyleOrOnlineResource"
})
@XmlRootElement(name = "UserStyle")
public class UserStyle {

    @XmlElement(name = "Name", namespace = "http://www.opengis.net/se")
    protected String name;
    @XmlElement(name = "Description", namespace = "http://www.opengis.net/se")
    protected DescriptionType description;
    @XmlElement(name = "IsDefault")
    protected Boolean isDefault;
    @XmlElements({
        @XmlElement(name = "FeatureTypeStyle", namespace = "http://www.opengis.net/se", type = FeatureTypeStyleType.class),
        @XmlElement(name = "CoverageStyle", namespace = "http://www.opengis.net/se", type = CoverageStyleType.class),
        @XmlElement(name = "OnlineResource", namespace = "http://www.opengis.net/se", type = OnlineResourceType.class)
    })
    protected List<Object> featureTypeStyleOrCoverageStyleOrOnlineResource;

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
    public void setName(String value) {
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
    public void setDescription(DescriptionType value) {
        this.description = value;
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

    /**
     * Gets the value of the featureTypeStyleOrCoverageStyleOrOnlineResource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the featureTypeStyleOrCoverageStyleOrOnlineResource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFeatureTypeStyleOrCoverageStyleOrOnlineResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FeatureTypeStyleType }
     * {@link CoverageStyleType }
     * {@link OnlineResourceType }
     * 
     * 
     */
    public List<Object> getFeatureTypeStyleOrCoverageStyleOrOnlineResource() {
        if (featureTypeStyleOrCoverageStyleOrOnlineResource == null) {
            featureTypeStyleOrCoverageStyleOrOnlineResource = new ArrayList<Object>();
        }
        return this.featureTypeStyleOrCoverageStyleOrOnlineResource;
    }

}
