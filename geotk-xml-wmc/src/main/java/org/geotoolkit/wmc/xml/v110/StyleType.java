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
package org.geotoolkit.wmc.xml.v110;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for StyleType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="StyleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *           &lt;element name="LegendURL" type="{http://www.opengis.net/context}URLType" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element name="SLD" type="{http://www.opengis.net/context}SLDType" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="current" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StyleType", propOrder = {
    "name",
    "title",
    "_abstract",
    "legendURL",
    "sld"
})
public class StyleType {

    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "LegendURL")
    protected URLType legendURL;
    @XmlElement(name = "SLD")
    protected SLDType sld;
    @XmlAttribute
    protected Boolean current;

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
     * Gets the value of the legendURL property.
     *
     * @return
     *     possible object is
     *     {@link URLType }
     *
     */
    public URLType getLegendURL() {
        return legendURL;
    }

    /**
     * Sets the value of the legendURL property.
     *
     * @param value
     *     allowed object is
     *     {@link URLType }
     *
     */
    public void setLegendURL(final URLType value) {
        this.legendURL = value;
    }

    /**
     * Gets the value of the sld property.
     *
     * @return
     *     possible object is
     *     {@link SLDType }
     *
     */
    public SLDType getSLD() {
        return sld;
    }

    /**
     * Sets the value of the sld property.
     *
     * @param value
     *     allowed object is
     *     {@link SLDType }
     *
     */
    public void setSLD(final SLDType value) {
        this.sld = value;
    }

    /**
     * Gets the value of the current property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isCurrent() {
        return current;
    }

    /**
     * Sets the value of the current property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setCurrent(final Boolean value) {
        this.current = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[StyleType]\n");
        if (name != null) {
            sb.append("name:").append(name).append("\n");
        }
        if (title != null) {
            sb.append("title:").append(title).append("\n");
        }
        if (_abstract != null) {
            sb.append("_abstract:").append(_abstract).append("\n");
        }
        if (legendURL != null) {
            sb.append("legendURL:").append(legendURL).append("\n");
        }
        if (sld != null) {
            sb.append("sld:").append(sld).append("\n");
        }
        sb.append("current:").append(current).append("\n");
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof StyleType) {
            final StyleType that = (StyleType) object;

            return Objects.equals(this._abstract, that._abstract) &&
                   Objects.equals(this.current, that.current) &&
                   Objects.equals(this.legendURL, that.legendURL) &&
                   Objects.equals(this.title, that.title) &&
                   Objects.equals(this.name, that.name) &&
                   Objects.equals(this.sld, that.sld);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 53 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 53 * hash + (this.legendURL != null ? this.legendURL.hashCode() : 0);
        hash = 53 * hash + (this.sld != null ? this.sld.hashCode() : 0);
        hash = 53 * hash + (this.current != null ? this.current.hashCode() : 0);
        return hash;
    }


}
