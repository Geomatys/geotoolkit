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
package org.geotoolkit.gml.xml.v311;

import java.util.Objects;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.FeatureProperty;


/**
 * Container for a feature - follow gml:AssociationType pattern.
 *
 * <p>Java class for FeaturePropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FeaturePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractFeature"/>
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
@XmlType(name = "FeaturePropertyType", propOrder = {
    "abstractFeature",
    "unknowFeature"
})
public class FeaturePropertyType implements FeatureProperty {

    @XmlElementRef(name = "AbstractFeature", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractFeatureType> abstractFeature;

    @XmlAnyElement(lax = true)
    private Object unknowFeature;

    /**
     * Allow to record the feature when its in href mode
     */
    @XmlTransient
    AbstractFeatureType  hiddenFeature;

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

    /**
     * An empty constructor used by JAXB.
     */
    protected FeaturePropertyType() {}

    /**
     * Build a new feature.
     */
    public FeaturePropertyType(final JAXBElement<? extends AbstractFeatureType> feature) {
        this.abstractFeature = feature;
    }

    /**
     * Build a new feature.
     */
    public FeaturePropertyType(final String href) {
        this.href = href;
    }

    /**
     * Set the feature into href mode.
     */
    @Override
    public void setToHref() {
        if (abstractFeature != null && abstractFeature.getValue() != null) {
            if (abstractFeature.getValue().getName() != null) {
                this.href       = abstractFeature.getValue().getName().getCode();
            }
            hiddenFeature   = abstractFeature.getValue();
            abstractFeature = null;
        }
    }

    /**
     * Gets the value of the abstractFeature property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractFeatureType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractFeatureCollectionType }{@code >}
     *
     */
    @Override
    public AbstractFeatureType getAbstractFeature() {
        if (abstractFeature != null) {
            return abstractFeature.getValue();
        }  else if (hiddenFeature != null) {
            return hiddenFeature;
        }
        return null;
    }

    /**
     * Sets the value of the abstractFeature property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractFeatureType }{@code >}
     *     {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractFeatureCollectionType }{@code >}
     *
     */
    public void setAbstractFeature(final JAXBElement<? extends AbstractFeatureType> value) {
        this.abstractFeature = ((JAXBElement<? extends AbstractFeatureType> ) value);
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
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
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
    public void setActuate(final String value) {
        this.actuate = value;
    }

    /**
     * @return the unknowFeature
     */
    @Override
    public Object getUnknowFeature() {
        return unknowFeature;
    }

    /**
     * @param unknowFeature the unknowFeature to set
     */
    public void setUnknowFeature(Object unknowFeature) {
        this.unknowFeature = unknowFeature;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeaturePropertyType) {
            final FeaturePropertyType that = (FeaturePropertyType) object;

            boolean feat = false;
            if (this.abstractFeature == null && that.abstractFeature == null) {
                feat = true;
            } else if (this.abstractFeature != null && that.abstractFeature != null) {
                feat = Objects.equals(this.abstractFeature.getValue(),    that.abstractFeature.getValue());
            }
            return feat                                                           &&
                   Objects.equals(this.hiddenFeature,      that.hiddenFeature)    &&
                   Objects.equals(this.unknowFeature,      that.unknowFeature)    &&
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
        hash = 47 * hash + (this.abstractFeature != null ? this.abstractFeature.hashCode() : 0);
        hash = 47 * hash + (this.unknowFeature != null ? this.unknowFeature.hashCode() : 0);
        hash = 47 * hash + (this.hiddenFeature != null ? this.hiddenFeature.hashCode() : 0);
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

    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[FeaturePropertyType]");
        if (abstractFeature != null && abstractFeature.getValue() != null) {
            s.append(abstractFeature.getValue().toString()).append('\n');
        }
        if (hiddenFeature != null) {
            s.append(hiddenFeature.toString()).append('\n');
        }
        if (unknowFeature != null) {
            s.append(unknowFeature.toString()).append('\n');
        }
        if(actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        if(arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if(href != null) {
            s.append("href=").append(href).append('\n');
        }
        if(role != null) {
            s.append("role=").append(role).append('\n');
        }
        if(show != null) {
            s.append("show=").append(show).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        return s.toString();
    }
}
