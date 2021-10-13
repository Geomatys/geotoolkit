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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ViewContextType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ViewContextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="General" type="{http://www.opengis.net/context}GeneralType"/>
 *         &lt;element name="LayerList" type="{http://www.opengis.net/context}LayerListType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.1.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewContextType", propOrder = {
    "general",
    "layerList"
})
@XmlRootElement(name = "ViewContext")
public class ViewContextType {

    @XmlElement(name = "General", required = true)
    protected GeneralType general;
    @XmlElement(name = "LayerList", required = true)
    protected LayerListType layerList;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String version;

    /**
     * Gets the value of the general property.
     *
     * @return
     *     possible object is
     *     {@link GeneralType }
     *
     */
    public GeneralType getGeneral() {
        return general;
    }

    /**
     * Sets the value of the general property.
     *
     * @param value
     *     allowed object is
     *     {@link GeneralType }
     *
     */
    public void setGeneral(final GeneralType value) {
        this.general = value;
    }

    /**
     * Gets the value of the layerList property.
     *
     * @return
     *     possible object is
     *     {@link LayerListType }
     *
     */
    public LayerListType getLayerList() {
        return layerList;
    }

    /**
     * Sets the value of the layerList property.
     *
     * @param value
     *     allowed object is
     *     {@link LayerListType }
     *
     */
    public void setLayerList(final LayerListType value) {
        this.layerList = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(final String value) {
        this.id = value;
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
        if (version == null) {
            return "1.1.0";
        } else {
            return version;
        }
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ViewContextType) {
            final ViewContextType that = (ViewContextType) object;

            return Objects.equals(this.general, that.general) &&
                   Objects.equals(this.layerList, that.layerList) &&
                   Objects.equals(this.version, that.version) &&
                   Objects.equals(this.id,  that.id);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.general != null ? this.general.hashCode() : 0);
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.layerList != null ? this.layerList.hashCode() : 0);
        hash = 79 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[ViewContextType]\n");
        if (id != null) {
            s.append("id:").append(id).append('\n');
        }
        if (general != null) {
            s.append("general:").append(general).append('\n');
        }
        if (version != null) {
            s.append("version:").append(version).append('\n');
        }
        if (layerList != null) {
            s.append("layerList:").append(layerList).append('\n');
        }
        return s.toString();
    }
}
