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
package org.geotoolkit.kml.xml.v220;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LinkType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}BasicLinkType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}refreshMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}refreshInterval" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewRefreshMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewRefreshTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewBoundScale" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}viewFormat" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}httpQuery" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LinkSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}LinkObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkType", propOrder = {
    "refreshMode",
    "refreshInterval",
    "viewRefreshMode",
    "viewRefreshTime",
    "viewBoundScale",
    "viewFormat",
    "httpQuery",
    "linkSimpleExtensionGroup",
    "linkObjectExtensionGroup"
})
public class LinkType extends BasicLinkType {

    @XmlElement(defaultValue = "onChange")
    protected RefreshModeEnumType refreshMode;
    @XmlElement(defaultValue = "4.0")
    protected Double refreshInterval;
    @XmlElement(defaultValue = "never")
    protected ViewRefreshModeEnumType viewRefreshMode;
    @XmlElement(defaultValue = "4.0")
    protected Double viewRefreshTime;
    @XmlElement(defaultValue = "1.0")
    protected Double viewBoundScale;
    protected String viewFormat;
    protected String httpQuery;
    @XmlElement(name = "LinkSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> linkSimpleExtensionGroup;
    @XmlElement(name = "LinkObjectExtensionGroup")
    protected List<AbstractObjectType> linkObjectExtensionGroup;

    /**
     * Gets the value of the refreshMode property.
     *
     * @return
     *     possible object is
     *     {@link RefreshModeEnumType }
     *
     */
    public RefreshModeEnumType getRefreshMode() {
        return refreshMode;
    }

    /**
     * Sets the value of the refreshMode property.
     *
     * @param value
     *     allowed object is
     *     {@link RefreshModeEnumType }
     *
     */
    public void setRefreshMode(final RefreshModeEnumType value) {
        this.refreshMode = value;
    }

    /**
     * Gets the value of the refreshInterval property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Sets the value of the refreshInterval property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setRefreshInterval(final Double value) {
        this.refreshInterval = value;
    }

    /**
     * Gets the value of the viewRefreshMode property.
     *
     * @return
     *     possible object is
     *     {@link ViewRefreshModeEnumType }
     *
     */
    public ViewRefreshModeEnumType getViewRefreshMode() {
        return viewRefreshMode;
    }

    /**
     * Sets the value of the viewRefreshMode property.
     *
     * @param value
     *     allowed object is
     *     {@link ViewRefreshModeEnumType }
     *
     */
    public void setViewRefreshMode(final ViewRefreshModeEnumType value) {
        this.viewRefreshMode = value;
    }

    /**
     * Gets the value of the viewRefreshTime property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getViewRefreshTime() {
        return viewRefreshTime;
    }

    /**
     * Sets the value of the viewRefreshTime property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setViewRefreshTime(final Double value) {
        this.viewRefreshTime = value;
    }

    /**
     * Gets the value of the viewBoundScale property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getViewBoundScale() {
        return viewBoundScale;
    }

    /**
     * Sets the value of the viewBoundScale property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setViewBoundScale(final Double value) {
        this.viewBoundScale = value;
    }

    /**
     * Gets the value of the viewFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getViewFormat() {
        return viewFormat;
    }

    /**
     * Sets the value of the viewFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setViewFormat(final String value) {
        this.viewFormat = value;
    }

    /**
     * Gets the value of the httpQuery property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHttpQuery() {
        return httpQuery;
    }

    /**
     * Sets the value of the httpQuery property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHttpQuery(final String value) {
        this.httpQuery = value;
    }

    /**
     * Gets the value of the linkSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getLinkSimpleExtensionGroup() {
        if (linkSimpleExtensionGroup == null) {
            linkSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.linkSimpleExtensionGroup;
    }

    /**
     * Gets the value of the linkObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getLinkObjectExtensionGroup() {
        if (linkObjectExtensionGroup == null) {
            linkObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.linkObjectExtensionGroup;
    }

}
