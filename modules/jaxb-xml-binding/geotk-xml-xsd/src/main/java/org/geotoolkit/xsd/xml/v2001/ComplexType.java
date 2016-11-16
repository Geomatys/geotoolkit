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
 * <p>Java class for complexType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="complexType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       &lt;group ref="{http://www.w3.org/2001/XMLSchema}complexTypeModel"/>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="mixed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="abstract" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}derivationSet" />
 *       &lt;attribute name="block" type="{http://www.w3.org/2001/XMLSchema}derivationSet" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complexType", propOrder = {
    "simpleContent",
    "complexContent",
    "group",
    "all",
    "choice",
    "sequence",
    "attributeOrAttributeGroup",
    "anyAttribute"
})
@XmlSeeAlso({
    TopLevelComplexType.class,
    LocalComplexType.class
})
public abstract class ComplexType extends Annotated {

    private static final QName FEATURE = new QName("http://www.opengis.net/gml", "_Feature");
    private static final QName FEATURE_TYPE = new QName("http://www.opengis.net/gml", "AbstractFeatureType");
    
    private SimpleContent simpleContent;
    private ComplexContent complexContent;
    private GroupRef group;
    private All all;
    private ExplicitGroup choice;
    private ExplicitGroup sequence;
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
    private Boolean mixed;
    @XmlAttribute(name = "abstract")
    private Boolean _abstract;
    @XmlAttribute(name = "final")
    @XmlSchemaType(name = "derivationSet")
    private List<String> _final;
    @XmlAttribute
    @XmlSchemaType(name = "derivationSet")
    private List<String> block;

    public ComplexType() {

    }

    public ComplexType(final String name, final ComplexContent complexContent) {
        this.name           = name;
        this.complexContent = complexContent;
    }

    public ComplexType(final String name, final ExplicitGroup sequence) {
        this.name     = name;
        this.sequence = sequence;
    }

    /**
     * Gets the value of the simpleContent property.
     *
     * @return
     *     possible object is
     *     {@link SimpleContent }
     *
     */
    public SimpleContent getSimpleContent() {
        return simpleContent;
    }

    /**
     * Sets the value of the simpleContent property.
     *
     * @param value
     *     allowed object is
     *     {@link SimpleContent }
     *
     */
    public void setSimpleContent(final SimpleContent value) {
        this.simpleContent = value;
    }

    /**
     * Gets the value of the complexContent property.
     *
     * @return
     *     possible object is
     *     {@link ComplexContent }
     *
     */
    public ComplexContent getComplexContent() {
        return complexContent;
    }

    /**
     * Sets the value of the complexContent property.
     *
     * @param value
     *     allowed object is
     *     {@link ComplexContent }
     *
     */
    public void setComplexContent(final ComplexContent value) {
        this.complexContent = value;
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
     * Gets the value of the mixed property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isMixed() {
        if (mixed == null) {
            return false;
        } else {
            return mixed;
        }
    }

    /**
     * Sets the value of the mixed property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setMixed(final Boolean value) {
        this.mixed = value;
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
     */
    public List<String> getFinal() {
        if (_final == null) {
            _final = new ArrayList<String>();
        }
        return this._final;
    }

    /**
     * Gets the value of the block property.
     *
     */
    public List<String> getBlock() {
        if (block == null) {
            block = new ArrayList<String>();
        }
        return this.block;
    }

    public boolean extendFeature() {
        if (complexContent != null && complexContent.getExtension() != null) {
            final QName base = complexContent.getExtension().getBase();
            if(base==null || !base.getNamespaceURI().startsWith(FEATURE.getNamespaceURI())){
                return false;
            }
            final String blp = base.getLocalPart();
            return FEATURE.getLocalPart().equals(blp)
                || FEATURE_TYPE.getLocalPart().equals(blp);
        }
        return false;
    }
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComplexType && super.equals(object)) {
            final ComplexType that = (ComplexType) object;
            return Objects.equals(this._abstract,                 that._abstract) &&
                   Objects.equals(this._final,                    that._final) &&
                   Objects.equals(this.all,                       that.all) &&
                   Objects.equals(this.anyAttribute,              that.anyAttribute) &&
                   Objects.equals(this.attributeOrAttributeGroup, that.attributeOrAttributeGroup) &&
                   Objects.equals(this.block,                     that.block) &&
                   Objects.equals(this.choice,                    that.choice) &&
                   Objects.equals(this.complexContent,            that.complexContent) &&
                   Objects.equals(this.group,                     that.group) &&
                   Objects.equals(this.mixed,                     that.mixed) &&
                   Objects.equals(this.name,                      that.name) &&
                   Objects.equals(this.sequence,                  that.sequence) &&
                   Objects.equals(this.simpleContent,             that.simpleContent);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.simpleContent != null ? this.simpleContent.hashCode() : 0);
        hash = 37 * hash + (this.complexContent != null ? this.complexContent.hashCode() : 0);
        hash = 37 * hash + (this.group != null ? this.group.hashCode() : 0);
        hash = 37 * hash + (this.all != null ? this.all.hashCode() : 0);
        hash = 37 * hash + (this.choice != null ? this.choice.hashCode() : 0);
        hash = 37 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        hash = 37 * hash + (this.attributeOrAttributeGroup != null ? this.attributeOrAttributeGroup.hashCode() : 0);
        hash = 37 * hash + (this.anyAttribute != null ? this.anyAttribute.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.mixed != null ? this.mixed.hashCode() : 0);
        hash = 37 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 37 * hash + (this._final != null ? this._final.hashCode() : 0);
        hash = 37 * hash + (this.block != null ? this.block.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (_abstract != null) {
            sb.append("_abstract:").append(_abstract).append('\n');
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
        if (block != null) {
            sb.append("block:").append(block).append('\n');
        }
        if (choice != null) {
            sb.append("choice:").append(choice).append('\n');
        }
        if (complexContent != null) {
            sb.append("complexContent:").append(complexContent).append('\n');
        }
        if (group != null) {
            sb.append("group:").append(group).append('\n');
        }
        if (mixed != null) {
            sb.append("mixed:").append(mixed).append('\n');
        }
        if (sequence != null) {
            sb.append("sequence:").append(sequence).append('\n');
        }
        if (simpleContent != null) {
            sb.append("simpleContent:").append(simpleContent).append('\n');
        }
        if (_final != null) {
            sb.append("_final:").append(_final).append('\n');
        }
        return  sb.toString();
    }
}
