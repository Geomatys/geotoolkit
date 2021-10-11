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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for attributeGroup complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="attributeGroup">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;group ref="{http://www.w3.org/2001/XMLSchema}attrDecls"/>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}defRef"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attributeGroup", propOrder = {
    "attributeOrAttributeGroup",
    "anyAttribute"
})
@XmlSeeAlso({
    NamedAttributeGroup.class,
    AttributeGroupRef.class
})
public abstract class AttributeGroup extends Annotated {

    @XmlElements({
        @XmlElement(name = "attribute", type = Attribute.class),
        @XmlElement(name = "attributeGroup", type = AttributeGroupRef.class)
    })
    private List<Annotated> attributeOrAttributeGroup;
    private Wildcard anyAttribute;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute
    private QName ref;

    /**
     * Gets the value of the attributeOrAttributeGroup property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * {@link AttributeGroupRef }
     *
     *
     */
    public List<Annotated> getAttributeOrAttributeGroup() {
        if (attributeOrAttributeGroup == null) {
            attributeOrAttributeGroup = new ArrayList<Annotated>();
        }
        return this.attributeOrAttributeGroup;
    }

    /**
     * Gets the value of the anyAttribute property.
     *
     * @return
     *     possible object is
     *     {@link Wildcard }
     *
     */
    public Wildcard getAnyAttribute() {
        return anyAttribute;
    }

    /**
     * Sets the value of the anyAttribute property.
     *
     * @param value
     *     allowed object is
     *     {@link Wildcard }
     *
     */
    public void setAnyAttribute(final Wildcard value) {
        this.anyAttribute = value;
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
        if (object instanceof AttributeGroup && super.equals(object)) {
            final AttributeGroup that = (AttributeGroup) object;
            return Objects.equals(this.anyAttribute,              that.anyAttribute) &&
                   Objects.equals(this.attributeOrAttributeGroup, that.attributeOrAttributeGroup) &&
                   Objects.equals(this.name,                      that.name) &&
                   Objects.equals(this.ref,                       that.ref);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.anyAttribute != null ? this.anyAttribute.hashCode() : 0);
        hash = 53 * hash + (this.attributeOrAttributeGroup != null ? this.attributeOrAttributeGroup.hashCode() : 0);
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
        if (anyAttribute != null) {
            sb.append("anyAttribute:").append(anyAttribute).append('\n');
        }
        if (attributeOrAttributeGroup != null) {
            sb.append("attributeOrAttributeGroup:").append(attributeOrAttributeGroup).append('\n');
        }
        if (ref != null) {
            sb.append("ref:").append(ref).append('\n');
        }
        return  sb.toString();
    }
}
