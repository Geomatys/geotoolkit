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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for simpleType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="simpleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;group ref="{http://www.w3.org/2001/XMLSchema}simpleDerivation"/>
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}simpleDerivationSet" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simpleType", propOrder = {
    "restriction",
    "list",
    "union"
})
@XmlSeeAlso({
    TopLevelSimpleType.class,
    LocalSimpleType.class
})
public abstract class SimpleType extends Annotated {

    private Restriction restriction;
    private org.geotoolkit.xsd.xml.v2001.List list;
    private Union union;
    @XmlAttribute(name = "final")
    @XmlSchemaType(name = "simpleDerivationSet")
    private java.util.List<String> _final;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;

    public SimpleType() {

    }

    public SimpleType(final String name) {
        this.name = name;
    }

    /**
     * Gets the value of the restriction property.
     *
     * @return
     *     possible object is
     *     {@link Restriction }
     *
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     *
     * @param value
     *     allowed object is
     *     {@link Restriction }
     *
     */
    public void setRestriction(final Restriction value) {
        this.restriction = value;
    }

    /**
     * Gets the value of the list property.
     *
     * @return
     *     possible object is
     *     {@link org.w3._2001.xmlschema.List }
     *
     */
    public org.geotoolkit.xsd.xml.v2001.List getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     *
     * @param value
     *     allowed object is
     *     {@link org.w3._2001.xmlschema.List }
     *
     */
    public void setList(final org.geotoolkit.xsd.xml.v2001.List value) {
        this.list = value;
    }

    /**
     * Gets the value of the union property.
     *
     * @return
     *     possible object is
     *     {@link Union }
     *
     */
    public Union getUnion() {
        return union;
    }

    /**
     * Sets the value of the union property.
     *
     * @param value
     *     allowed object is
     *     {@link Union }
     *
     */
    public void setUnion(final Union value) {
        this.union = value;
    }

    /**
     * Gets the value of the final property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the final property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public java.util.List<String> getFinal() {
        if (_final == null) {
            _final = new ArrayList<String>();
        }
        return this._final;
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
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SimpleType && super.equals(object)) {
            final SimpleType that = (SimpleType) object;
            return Objects.equals(this.list,                      that.list) &&
                   Objects.equals(this._final,                    that._final) &&
                   Objects.equals(this.name,                      that.name) &&
                   Objects.equals(this.restriction,               that.restriction) &&
                   Objects.equals(this.union,                     that.union);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.list != null ? this.list.hashCode() : 0);
        hash = 37 * hash + (this.restriction != null ? this.restriction.hashCode() : 0);
        hash = 37 * hash + (this.union != null ? this.union.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this._final != null ? this._final.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (list != null) {
            sb.append("list:").append(list).append('\n');
        }
        if (restriction != null) {
            sb.append("restriction:").append(restriction).append('\n');
        }
        if (union != null) {
            sb.append("union:").append(union).append('\n');
        }
        if (_final != null) {
            sb.append("_final:").append(_final).append('\n');
        }
        return  sb.toString();
    }
}
