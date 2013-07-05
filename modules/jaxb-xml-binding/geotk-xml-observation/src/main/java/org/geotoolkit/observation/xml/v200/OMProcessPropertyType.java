/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.observation.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.xlink.xml.v100.ActuateType;
import org.geotoolkit.xlink.xml.v100.ShowType;
import org.w3c.dom.Element;


/**
 *  The purpose of an observation process is to generate an observation
 * 				result. An instance is often an instrument or sensor, but may be a human observer, a
 * 				simulator, or a process or algorithm applied to more primitive results used as
 * 				inputs. The model for OM_Process is abstract, and has no attributes, operations, or
 * 				associations. NOTE ISO 19115-2:2008 provides MI_Instrument, LE_Processing and
 * 				LE_Algorithm, which could all be modelled as specializations of OM_Process. Any
 * 				suitable XML may be used to describe the observation process in line, provided that
 * 				it is contained in a single XML element. If reference to a schema is provided it
 * 				must also be valid. OGC SensorML provides a model which is suitable for many
 * 				observation procedures.
 *
 * <p>Java class for OM_ProcessPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OM_ProcessPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;any processContents='lax'/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OM_ProcessPropertyType", propOrder = {
    "any"
})
public class OMProcessPropertyType implements org.geotoolkit.observation.xml.Process {

    @XmlAnyElement(lax = true)
    private Object any;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml/3.2")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ShowType show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ActuateType actuate;

    public OMProcessPropertyType() {

    }

    public OMProcessPropertyType(final String href) {
        this.href = href;
    }

    /**
     * Gets the value of the any property.
     *
     * @return
     *     possible object is
     *     {@link Element }
     *     {@link Object }
     *
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     *
     * @param value
     *     allowed object is
     *     {@link Element }
     *     {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Gets the value of the nilReason property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
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
    public void setRemoteSchema(String value) {
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
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the titleTemp property.
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
     * Sets the value of the titleTemp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link ShowType }
     *
     */
    public ShowType getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link ShowType }
     *
     */
    public void setShow(ShowType value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link ActuateType }
     *
     */
    public ActuateType getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link ActuateType }
     *
     */
    public void setActuate(ActuateType value) {
        this.actuate = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OMProcessPropertyType) {
            final OMProcessPropertyType that = (OMProcessPropertyType) object;

            return Objects.equals(this.any,                that.any)      &&
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
        int hash = 5;
        hash = 47 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 47 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
