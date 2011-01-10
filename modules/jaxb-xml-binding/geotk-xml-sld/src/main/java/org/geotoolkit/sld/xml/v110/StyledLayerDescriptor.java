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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
 *         &lt;element ref="{http://www.opengis.net/sld}UseSLDLibrary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sld}NamedLayer"/>
 *           &lt;element ref="{http://www.opengis.net/sld}UserLayer"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://www.opengis.net/se}VersionType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "description",
    "useSLDLibrary",
    "namedLayerOrUserLayer"
})
@XmlRootElement(name = "StyledLayerDescriptor", namespace = "http://www.opengis.net/sld")
public class StyledLayerDescriptor {

    @XmlElement(name = "Name", namespace = "http://www.opengis.net/se")
    protected String name;
    @XmlElement(name = "Description", namespace = "http://www.opengis.net/se")
    protected DescriptionType description;
    @XmlElement(name = "UseSLDLibrary")
    protected List<UseSLDLibrary> useSLDLibrary;
    @XmlElements({
        @XmlElement(name = "NamedLayer", type = NamedLayer.class),
        @XmlElement(name = "UserLayer", type = UserLayer.class)
    })
    protected List<Object> namedLayerOrUserLayer;
    @XmlAttribute(required = true)
    protected String version = "1.1.0";

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
     * Gets the value of the useSLDLibrary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the useSLDLibrary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUseSLDLibrary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UseSLDLibrary }
     * 
     * 
     */
    public List<UseSLDLibrary> getUseSLDLibrary() {
        if (useSLDLibrary == null) {
            useSLDLibrary = new ArrayList<UseSLDLibrary>();
        }
        return this.useSLDLibrary;
    }

    /**
     * Gets the value of the namedLayerOrUserLayer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the namedLayerOrUserLayer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamedLayerOrUserLayer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedLayer }
     * {@link UserLayer }
     * 
     * 
     */
    public List<Object> getNamedLayerOrUserLayer() {
        if (namedLayerOrUserLayer == null) {
            namedLayerOrUserLayer = new ArrayList<Object>();
        }
        return this.namedLayerOrUserLayer;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(final String value) {
        this.version = value;
    }

}
