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
package org.geotoolkit.xsd.xml.v2001;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for attribute complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="attribute">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;sequence>
 *         &lt;element name="simpleType" type="{http://www.w3.org/2001/XMLSchema}localSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}defRef"/>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="use" default="optional">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="prohibited"/>
 *             &lt;enumeration value="optional"/>
 *             &lt;enumeration value="required"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fixed" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="form" type="{http://www.w3.org/2001/XMLSchema}formChoice" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attribute", propOrder = {
    "simpleType"
})
@XmlSeeAlso({
    TopLevelAttribute.class
})
public class Attribute extends Annotated {

    private LocalSimpleType simpleType;
    @XmlAttribute
    private QName type;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String use;
    @XmlAttribute(name = "default")
    private String _default;
    @XmlAttribute
    private String fixed;
    @XmlAttribute
    private FormChoice form;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute
    private QName ref;

    /**
     * Gets the value of the simpleType property.
     *
     * @return
     *     possible object is
     *     {@link LocalSimpleType }
     *
     */
    public LocalSimpleType getSimpleType() {
        return simpleType;
    }

    /**
     * Sets the value of the simpleType property.
     *
     * @param value
     *     allowed object is
     *     {@link LocalSimpleType }
     *
     */
    public void setSimpleType(final LocalSimpleType value) {
        this.simpleType = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setType(final QName value) {
        this.type = value;
    }

    /**
     * Gets the value of the use property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUse() {
        if (use == null) {
            return "optional";
        } else {
            return use;
        }
    }

    /**
     * Sets the value of the use property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUse(final String value) {
        this.use = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefault(final String value) {
        this._default = value;
    }

    /**
     * Gets the value of the fixed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFixed(final String value) {
        this.fixed = value;
    }

    /**
     * Gets the value of the form property.
     *
     * @return
     *     possible object is
     *     {@link FormChoice }
     *
     */
    public FormChoice getForm() {
        return form;
    }

    /**
     * Sets the value of the form property.
     *
     * @param value
     *     allowed object is
     *     {@link FormChoice }
     *
     */
    public void setForm(final FormChoice value) {
        this.form = value;
    }

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
     * Gets the value of the ref property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setRef(final QName value) {
        this.ref = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Attribute && super.equals(object)) {
            final Attribute that = (Attribute) object;
            return Utilities.equals(this._default,     that._default) &&
                   Utilities.equals(this.fixed,        that.fixed) &&
                   Utilities.equals(this.name,         that.name) &&
                   Utilities.equals(this.form,         that.form) &&
                   Utilities.equals(this.simpleType,   that.simpleType) &&
                   Utilities.equals(this.type,         that.type) &&
                   Utilities.equals(this.use,          that.use) &&
                   Utilities.equals(this.ref,          that.ref);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.simpleType != null ? this.simpleType.hashCode() : 0);
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + (this.use != null ? this.use.hashCode() : 0);
        hash = 53 * hash + (this._default != null ? this._default.hashCode() : 0);
        hash = 53 * hash + (this.fixed != null ? this.fixed.hashCode() : 0);
        hash = 53 * hash + (this.form != null ? this.form.hashCode() : 0);
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }




    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (_default != null) {
            sb.append("_default:").append(_default).append('\n');
        }
        if (fixed != null) {
            sb.append("fixed:").append(fixed).append('\n');
        }
        if (form != null) {
            sb.append("form:").append(form).append('\n');
        }
        if (ref != null) {
            sb.append("ref:").append(ref).append('\n');
        }
        if (simpleType != null) {
            sb.append("simpleType:").append(simpleType).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (use != null) {
            sb.append("use:").append(use).append('\n');
        }
        return  sb.toString();
    }
}
