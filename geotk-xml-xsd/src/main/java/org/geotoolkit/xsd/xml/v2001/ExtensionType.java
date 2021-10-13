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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for extensionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="extensionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.w3.org/2001/XMLSchema}typeDefParticle" minOccurs="0"/>
 *         &lt;group ref="{http://www.w3.org/2001/XMLSchema}attrDecls"/>
 *       &lt;/sequence>
 *       &lt;attribute name="base" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extensionType", propOrder = {
    "group",
    "all",
    "choice",
    "sequence",
    "attributeOrAttributeGroup",
    "anyAttribute"
})
@XmlSeeAlso({
    SimpleExtensionType.class
})
public class ExtensionType extends Annotated {

    private GroupRef group;
    private All all;
    private ExplicitGroup choice;
    private ExplicitGroup sequence;
    @XmlElements({
        @XmlElement(name = "attributeGroup", type = AttributeGroupRef.class),
        @XmlElement(name = "attribute", type = Attribute.class)
    })
    private List<Annotated> attributeOrAttributeGroup;
    private Wildcard anyAttribute;
    @XmlAttribute(required = true)
    private QName base;

    public ExtensionType() {

    }

    public ExtensionType(final QName base, final ExplicitGroup sequence) {
        this.base     = base;
        this.sequence = sequence;
    }
    /**
     * Gets the value of the group property.
     *
     * @return
     *     possible object is
     *     {@link GroupRef }
     *
     */
    public GroupRef getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     *
     * @param value
     *     allowed object is
     *     {@link GroupRef }
     *
     */
    public void setGroup(final GroupRef value) {
        this.group = value;
    }

    /**
     * Gets the value of the all property.
     *
     * @return
     *     possible object is
     *     {@link All }
     *
     */
    public All getAll() {
        return all;
    }

    /**
     * Sets the value of the all property.
     *
     * @param value
     *     allowed object is
     *     {@link All }
     *
     */
    public void setAll(final All value) {
        this.all = value;
    }

    /**
     * Gets the value of the choice property.
     *
     * @return
     *     possible object is
     *     {@link ExplicitGroup }
     *
     */
    public ExplicitGroup getChoice() {
        return choice;
    }

    /**
     * Sets the value of the choice property.
     *
     * @param value
     *     allowed object is
     *     {@link ExplicitGroup }
     *
     */
    public void setChoice(final ExplicitGroup value) {
        this.choice = value;
    }

    /**
     * Gets the value of the sequence property.
     *
     * @return
     *     possible object is
     *     {@link ExplicitGroup }
     *
     */
    public ExplicitGroup getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     *
     * @param value
     *     allowed object is
     *     {@link ExplicitGroup }
     *
     */
    public void setSequence(final ExplicitGroup value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the attributeOrAttributeGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeOrAttributeGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeOrAttributeGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeGroupRef }
     * {@link Attribute }
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
     * Gets the value of the base property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setBase(final QName value) {
        this.base = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ExtensionType && super.equals(object)) {
            final ExtensionType that = (ExtensionType) object;
                   return
                   Objects.equals(this.all,                       that.all) &&
                   Objects.equals(this.anyAttribute,              that.anyAttribute) &&
                   Objects.equals(this.attributeOrAttributeGroup, that.attributeOrAttributeGroup) &&
                   Objects.equals(this.base,                      that.base) &&
                   Objects.equals(this.choice,                    that.choice) &&
                   Objects.equals(this.group,                     that.group) &&
                   Objects.equals(this.sequence,                  that.sequence);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.base != null ? this.base.hashCode() : 0);
        hash = 37 * hash + (this.group != null ? this.group.hashCode() : 0);
        hash = 37 * hash + (this.all != null ? this.all.hashCode() : 0);
        hash = 37 * hash + (this.choice != null ? this.choice.hashCode() : 0);
        hash = 37 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        hash = 37 * hash + (this.attributeOrAttributeGroup != null ? this.attributeOrAttributeGroup.hashCode() : 0);
        hash = 37 * hash + (this.anyAttribute != null ? this.anyAttribute.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (base != null) {
            sb.append("base:").append(base).append('\n');
        }
        if (all != null) {
            sb.append("all:").append(all).append('\n');
        }
        if (anyAttribute != null) {
            sb.append("anyAttribute:").append(anyAttribute).append('\n');
        }
        if (attributeOrAttributeGroup != null) {
            sb.append("attributeOrAttributeGroup:").append(attributeOrAttributeGroup).append('\n');
        }
        if (choice != null) {
            sb.append("choice:").append(choice).append('\n');
        }
        if (group != null) {
            sb.append("group:").append(group).append('\n');
        }
        if (sequence != null) {
            sb.append("sequence:").append(sequence).append('\n');
        }
        return  sb.toString();
    }
}
