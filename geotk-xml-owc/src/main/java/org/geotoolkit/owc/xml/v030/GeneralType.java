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
package org.geotoolkit.owc.xml.v030;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.ows.xml.v100.KeywordsType;
import org.geotoolkit.ows.xml.v100.ServiceProvider;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;


/**
 * <p>Java class for GeneralType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeneralType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Window" type="{http://www.opengis.net/ows-context}WindowType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}BoundingBox"/>
 *         &lt;element ref="{http://www.opengis.net/sld}MinScaleDenominator" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sld}MaxScaleDenominator" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Title" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Keywords" minOccurs="0"/>
 *         &lt;element name="LogoURL" type="{http://www.opengis.net/ows-context}URLType" minOccurs="0"/>
 *         &lt;element name="DescriptionURL" type="{http://www.opengis.net/ows-context}URLType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}ServiceProvider" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.opengis.net/ows-context}ExtensionType" minOccurs="0"/>
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
@XmlType(name = "GeneralType", propOrder = {
    "window",
    "boundingBox",
    "minScaleDenominator",
    "maxScaleDenominator",
    "title",
    "_abstract",
    "keywords",
    "logoURL",
    "descriptionURL",
    "serviceProvider",
    "extension"
})
public class GeneralType {

    @XmlElement(name = "Window")
    protected WindowType window;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    protected JAXBElement<? extends BoundingBoxType> boundingBox;
    @XmlElement(name = "MinScaleDenominator", namespace = "http://www.opengis.net/sld")
    protected Double minScaleDenominator;
    @XmlElement(name = "MaxScaleDenominator", namespace = "http://www.opengis.net/sld")
    protected Double maxScaleDenominator;
    @XmlElement(name = "Title", namespace = "http://www.opengis.net/ows")
    protected String title;
    @XmlElement(name = "Abstract", namespace = "http://www.opengis.net/ows")
    protected String _abstract;
    @XmlElement(name = "Keywords", namespace = "http://www.opengis.net/ows")
    protected KeywordsType keywords;
    @XmlElement(name = "LogoURL")
    protected URLType logoURL;
    @XmlElement(name = "DescriptionURL")
    protected URLType descriptionURL;
    @XmlElement(name = "ServiceProvider", namespace = "http://www.opengis.net/ows")
    protected ServiceProvider serviceProvider;
    @XmlElement(name = "Extension")
    protected ExtensionType extension;

    /**
     * Gets the value of the window property.
     *
     * @return
     *     possible object is
     *     {@link WindowType }
     *
     */
    public WindowType getWindow() {
        return window;
    }

    /**
     * Sets the value of the window property.
     *
     * @param value
     *     allowed object is
     *     {@link WindowType }
     *
     */
    public void setWindow(final WindowType value) {
        this.window = value;
    }

    /**
     * Gets the value of the boundingBox property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     *
     */
    public JAXBElement<? extends BoundingBoxType> getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     *
     */
    public void setBoundingBox(final JAXBElement<? extends BoundingBoxType> value) {
        this.boundingBox = ((JAXBElement<? extends BoundingBoxType> ) value);
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
     * Gets the value of the title property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the abstract property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAbstract(final String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the keywords property.
     *
     * @return
     *     possible object is
     *     {@link KeywordsType }
     *
     */
    public KeywordsType getKeywords() {
        return keywords;
    }

    /**
     * Sets the value of the keywords property.
     *
     * @param value
     *     allowed object is
     *     {@link KeywordsType }
     *
     */
    public void setKeywords(final KeywordsType value) {
        this.keywords = value;
    }

    /**
     * Gets the value of the logoURL property.
     *
     * @return
     *     possible object is
     *     {@link URLType }
     *
     */
    public URLType getLogoURL() {
        return logoURL;
    }

    /**
     * Sets the value of the logoURL property.
     *
     * @param value
     *     allowed object is
     *     {@link URLType }
     *
     */
    public void setLogoURL(final URLType value) {
        this.logoURL = value;
    }

    /**
     * Gets the value of the descriptionURL property.
     *
     * @return
     *     possible object is
     *     {@link URLType }
     *
     */
    public URLType getDescriptionURL() {
        return descriptionURL;
    }

    /**
     * Sets the value of the descriptionURL property.
     *
     * @param value
     *     allowed object is
     *     {@link URLType }
     *
     */
    public void setDescriptionURL(final URLType value) {
        this.descriptionURL = value;
    }

    /**
     * Gets the value of the serviceProvider property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProvider }
     *
     */
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Sets the value of the serviceProvider property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProvider }
     *
     */
    public void setServiceProvider(final ServiceProvider value) {
        this.serviceProvider = value;
    }

    /**
     * Gets the value of the extension property.
     *
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *
     */
    public void setExtension(final ExtensionType value) {
        this.extension = value;
    }

}
