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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 *
 *    The element element can be used either
 *    at the top level to define an element-type binding globally,
 *    or within a content model to either reference a globally-defined
 *    element or type or declare an element-type binding locally.
 *    The ref form is not allowed at the top level.
 *
 * <p>Java class for element complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="element">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="simpleType" type="{http://www.w3.org/2001/XMLSchema}localSimpleType"/>
 *           &lt;element name="complexType" type="{http://www.w3.org/2001/XMLSchema}localComplexType"/>
 *         &lt;/choice>
 *         &lt;group ref="{http://www.w3.org/2001/XMLSchema}identityConstraint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}occurs"/>
 *       &lt;attGroup ref="{http://www.w3.org/2001/XMLSchema}defRef"/>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="substitutionGroup" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fixed" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nillable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="abstract" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}derivationSet" />
 *       &lt;attribute name="block" type="{http://www.w3.org/2001/XMLSchema}blockSet" />
 *       &lt;attribute name="form" type="{http://www.w3.org/2001/XMLSchema}formChoice" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "element", propOrder = {
    "simpleType",
    "complexType",
    "identityConstraint"
})
@XmlSeeAlso({
    TopLevelElement.class,
    LocalElement.class
})
public abstract class Element extends Annotated {

    private LocalSimpleType simpleType;
    private LocalComplexType complexType;
    @XmlElementRefs({
        @XmlElementRef(name = "unique", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class),
        @XmlElementRef(name = "keyref", namespace = "http://www.w3.org/2001/XMLSchema", type = Keyref.class),
        @XmlElementRef(name = "key", namespace = "http://www.w3.org/2001/XMLSchema", type = JAXBElement.class)
    })
    private List<Object> identityConstraint;
    @XmlAttribute
    private QName type;
    @XmlAttribute
    private QName substitutionGroup;
    @XmlAttribute(name = "default")
    private String _default;
    @XmlAttribute
    private String fixed;
    @XmlAttribute
    private Boolean nillable;
    @XmlAttribute(name = "abstract")
    private Boolean _abstract;
    @XmlAttribute(name = "final")
    @XmlSchemaType(name = "derivationSet")
    private List<String> _final;
    @XmlAttribute
    @XmlSchemaType(name = "blockSet")
    private List<String> block;
    @XmlAttribute
    private FormChoice form;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer minOccurs;
    @XmlAttribute
    @XmlSchemaType(name = "allNNI")
    private String maxOccurs;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String name;
    @XmlAttribute
    private QName ref;

    public Element() {

    }

    public Element(final String name, final QName type) {
        this.name = name;
        this.type = type;
    }

    public Element(final String name, final QName type, final QName substitutionGroup) {
        this.name              = name;
        this.type              = type;
        this.substitutionGroup = substitutionGroup;
    }

    public Element(final String name, final QName type, final Integer minOccurs, final String maxOccurs, final Boolean nillable) {
        this.name      = name;
        this.type      = type;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        this.nillable  = nillable;
    }

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
     * Gets the value of the complexType property.
     *
     * @return
     *     possible object is
     *     {@link LocalComplexType }
     *
     */
    public LocalComplexType getComplexType() {
        return complexType;
    }

    /**
     * Sets the value of the complexType property.
     *
     * @param value
     *     allowed object is
     *     {@link LocalComplexType }
     *
     */
    public void setComplexType(final LocalComplexType value) {
        this.complexType = value;
    }

    /**
     * Gets the value of the identityConstraint property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Keyref }
     * {@link JAXBElement }{@code <}{@link Keybase }{@code >}
     *
     *
     */
    public List<Object> getIdentityConstraint() {
        if (identityConstraint == null) {
            identityConstraint = new ArrayList<Object>();
        }
        return this.identityConstraint;
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
     * Gets the value of the substitutionGroup property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getSubstitutionGroup() {
        return substitutionGroup;
    }

    /**
     * Sets the value of the substitutionGroup property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setSubstitutionGroup(final QName value) {
        this.substitutionGroup = value;
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
     * Gets the value of the nillable property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isNillable() {
        if (nillable == null) {
            return false;
        } else {
            return nillable;
        }
    }

    /**
     * Sets the value of the nillable property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setNillable(final Boolean value) {
        this.nillable = value;
    }

    /**
     * Gets the value of the abstract property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isAbstract() {
        if (_abstract == null) {
            return false;
        } else {
            return _abstract;
        }
    }

    /**
     * Sets the value of the abstract property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setAbstract(final Boolean value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the final property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getFinal() {
        if (_final == null) {
            _final = new ArrayList<String>();
        }
        return this._final;
    }

    /**
     * Gets the value of the block property.
     */
    public List<String> getBlock() {
        if (block == null) {
            block = new ArrayList<String>();
        }
        return this.block;
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
     * Gets the value of the minOccurs property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public Integer getMinOccurs() {
        if (minOccurs == null) {
            return 1;
        } else {
            return minOccurs;
        }
    }

    /**
     * Sets the value of the minOccurs property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setMinOccurs(final Integer value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaxOccurs() {
        if (maxOccurs == null) {
            return "1";
        } else {
            return maxOccurs;
        }
    }

    /**
     * Sets the value of the maxOccurs property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaxOccurs(final String value) {
        this.maxOccurs = value;
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

    @Override
    public String toString() {
         final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (_abstract != null) {
            sb.append("abstract:").append(_abstract).append('\n');
        }
        if (_default != null) {
            sb.append("default:").append(_default).append('\n');
        }
        if (_final != null) {
            sb.append("final:").append(_final).append('\n');
        }
        if (block != null) {
            sb.append("block:").append(block).append('\n');
        }
        if (complexType != null) {
            sb.append("complexType:").append(complexType).append('\n');
        }
        if (fixed != null) {
            sb.append("fixed:").append(fixed).append('\n');
        }
        if (form != null) {
            sb.append("form:").append(form).append('\n');
        }
        if (identityConstraint != null) {
            sb.append("identityConstraint:").append(identityConstraint).append('\n');
        }
        if (maxOccurs != null) {
            sb.append("maxOccurs:").append(maxOccurs).append('\n');
        }
        if (minOccurs != null) {
            sb.append("minOccurs:").append(minOccurs).append('\n');
        }
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (nillable != null) {
            sb.append("nillable:").append(nillable).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (ref != null) {
            sb.append("ref:").append(ref).append('\n');
        }
        if (simpleType != null) {
            sb.append("simpleType:").append(simpleType).append('\n');
        }
        if (substitutionGroup != null) {
            sb.append("substitutionGroup:").append(substitutionGroup).append('\n');
        }
        return  sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Element && super.equals(object)) {
            final Element that = (Element) object;
            return Objects.equals(this._abstract,          that._abstract)          &&
                   Objects.equals(this._default,           that._default)           &&
                   Objects.equals(this._final,             that._final)             &&
                   Objects.equals(this.block,              that.block)              &&
                   Objects.equals(this.complexType,        that.complexType)        &&
                   Objects.equals(this.fixed,              that.fixed)              &&
                   Objects.equals(this.form,               that.form)               &&
                   Objects.equals(this.identityConstraint, that.identityConstraint) &&
                   Objects.equals(this.maxOccurs,          that.maxOccurs)          &&
                   Objects.equals(this.minOccurs,          that.minOccurs)          &&
                   Objects.equals(this.name,               that.name)               &&
                   Objects.equals(this.nillable,           that.nillable)           &&
                   Objects.equals(this.ref,                that.ref)                &&
                   Objects.equals(this.simpleType,         that.simpleType)         &&
                   Objects.equals(this.substitutionGroup,  that.substitutionGroup)  &&
                   Objects.equals(this.type,               that.type) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.simpleType != null ? this.simpleType.hashCode() : 0);
        hash = 59 * hash + (this.complexType != null ? this.complexType.hashCode() : 0);
        hash = 59 * hash + (this.identityConstraint != null ? this.identityConstraint.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 59 * hash + (this.substitutionGroup != null ? this.substitutionGroup.hashCode() : 0);
        hash = 59 * hash + (this._default != null ? this._default.hashCode() : 0);
        hash = 59 * hash + (this.fixed != null ? this.fixed.hashCode() : 0);
        hash = 59 * hash + (this.nillable != null ? this.nillable.hashCode() : 0);
        hash = 59 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 59 * hash + (this._final != null ? this._final.hashCode() : 0);
        hash = 59 * hash + (this.block != null ? this.block.hashCode() : 0);
        hash = 59 * hash + (this.form != null ? this.form.hashCode() : 0);
        hash = 59 * hash + (this.minOccurs != null ? this.minOccurs.hashCode() : 0);
        hash = 59 * hash + (this.maxOccurs != null ? this.maxOccurs.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }

}
