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
package org.geotoolkit.swe.xml.v101;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractAllowedValuesProperty;


/**
 * <p>Java class for AllowedValuesPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AllowedValuesPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}AllowedValues"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowedValuesPropertyType", propOrder = {
    "allowedValues"
})
public class AllowedValuesPropertyType implements AbstractAllowedValuesProperty {

    @XmlElement(name = "AllowedValues")
    private AllowedValues allowedValues;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    public AllowedValuesPropertyType() {

    }

    public AllowedValuesPropertyType(final AbstractAllowedValuesProperty avp) {
        if (avp != null) {
            this.actuate = avp.getActuate();
            if (avp.getAllowedValues() != null) {
                this.allowedValues = new AllowedValues(avp.getAllowedValues());
            }
            this.arcrole = avp.getArcrole();
            this.href    = avp.getHref();
            this.remoteSchema = avp.getRemoteSchema();
            this.role    = avp.getRole();
            this.show    = avp.getShow();
            this.title   = avp.getTitle();
            this.type    = avp.getType();
        }
    }

    /**
     * Gets the value of the allowedValues property.
     *
     * @return
     *     possible object is
     *     {@link AllowedValues }
     *
     */
    @Override
    public AllowedValues getAllowedValues() {
        return allowedValues;
    }

    /**
     * Sets the value of the allowedValues property.
     *
     * @param value
     *     allowed object is
     *     {@link AllowedValues }
     *
     */
    public void setAllowedValues(final AllowedValues value) {
        this.allowedValues = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    @Override
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setActuate(final String value) {
        this.actuate = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AllowedValuesPropertyType) {
            final AllowedValuesPropertyType that = (AllowedValuesPropertyType) object;

            return Objects.equals(this.allowedValues,      that.allowedValues)    &&
                   Objects.equals(this.actuate,            that.actuate)          &&
                   Objects.equals(this.arcrole,            that.arcrole)          &&
                   Objects.equals(this.type,               that.type)             &&
                   Objects.equals(this.href,               that.href)             &&
                   Objects.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Objects.equals(this.show,               that.show)             &&
                   Objects.equals(this.role,               that.role)             &&
                   Objects.equals(this.title,              that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.allowedValues != null ? this.allowedValues.hashCode() : 0);
        hash = 71 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 71 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 71 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 71 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 71 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 71 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 71 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

}
