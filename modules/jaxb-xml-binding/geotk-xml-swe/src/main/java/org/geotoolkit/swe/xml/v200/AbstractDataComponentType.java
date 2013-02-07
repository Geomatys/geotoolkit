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

package org.geotoolkit.swe.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.Code;
import org.geotoolkit.gml.xml.Reference;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractDataComponentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDataComponentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEIdentifiableType">
 *       &lt;attribute name="updatable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="definition" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDataComponentType")
@XmlSeeAlso({
    DataArrayType.class,
    DataChoiceType.class,
    AbstractSimpleComponentType.class,
    DataRecordType.class,
    VectorType.class
})
public abstract class AbstractDataComponentType extends AbstractSWEIdentifiableType implements AbstractDataComponent {

    @XmlAttribute
    private Boolean updatable;
    @XmlAttribute
    private Boolean optional;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String definition;

    public AbstractDataComponentType() {
        
    }
    
    public AbstractDataComponentType(final String id, final String definition, final Boolean updatable) {
        super(id);
        this.definition = definition;
    }
    
    public AbstractDataComponentType(final AbstractDataComponentType that) {
        super(that);
        this.definition = that.definition;
        this.optional   = that.optional;
        this.updatable  = that.updatable;
    }
    
    /**
     * Gets the value of the updatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUpdatable() {
        return updatable;
    }

    /**
     * Sets the value of the updatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUpdatable(Boolean value) {
        this.updatable = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return false;
        } else {
            return optional;
        }
    }
    
    @Override
    public Boolean isFixed() {
        if (updatable != null) {
            return !updatable;
        }
        return null;
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the definition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefinition(String value) {
        this.definition = value;
    }

    @Override
    public Code getParameterName() {
        return null;
    }
    
    @Override
    public Reference getDescriptionReference() {
        return null;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractDataComponentType && super.equals(object)) {
            final AbstractDataComponentType that = (AbstractDataComponentType) object;

            return Utilities.equals(this.definition,  that.definition) &&
                   Utilities.equals(this.optional,    that.optional) &&
                   Utilities.equals(this.updatable,   that.updatable);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.definition != null ? this.definition.hashCode() : 0);
        hash = 47 * hash + (this.optional != null ? this.optional.hashCode() : 0);
        hash = 47 * hash + (this.updatable != null ? this.updatable.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (definition != null) {
            s.append("definition=").append(definition).append('\n');
        }
        if (optional != null) {
            s.append("optional=").append(optional).append('\n');
        }
        if (updatable != null) {
            s.append("updatable=").append(updatable).append('\n');
        }
        return s.toString();
    }
}
