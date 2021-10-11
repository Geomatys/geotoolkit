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
package org.geotoolkit.ows.xml.v110;

import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDomain;


/**
 * Valid domain (or allowed set of values) of one quantity, with its name or identifier.
 *
 * <p>Java class for DomainType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DomainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}UnNamedDomainType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainType")
public class DomainType extends UnNamedDomainType implements AbstractDomain {

    @XmlAttribute(required = true)
    private String name;

    /**
     * Empty constructor used by JAXB.
     */
    DomainType(){
    }

    public DomainType(final DomainType that){
        super(that);
        if (that != null) {
            this.name = that.name;
        }
    }

    /**
     * Build a new Domain with the specified name.
     */
    public DomainType(final String name){
        this.name = name;
    }

    /**
     * Build a new Domain with the specified name.
     */
    public DomainType(final String name, final AllowedValues values){
        super(values);
        this.name = name;
    }

    /**
     * Build a new Domain with the specified name.
     */
    public DomainType(final String name, final AnyValue values){
        super(values);
        this.name = name;
    }

    public DomainType(final String name, final ValueType defaultValue){
        super(defaultValue);
        this.name = name;
    }

    public DomainType(final String name, final NoValues noValues, final ValueType defaultValue){
        super(noValues, defaultValue);
        this.name = name;
    }

    /**
     * Build a new Domain with the specified name.
     */
    public DomainType(final String name, final String value){
        super(value);
        this.name = name;
    }

    /**
     * Build a new Domain with the specified list of values.
     */
    public DomainType(final String name, final List<String> value) {
        super(value);
        this.name  = name;
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getValue() {
        if (this.getAllowedValues() != null) {
            return this.getAllowedValues().getStringValues();
        }
        return null;
    }

    @Override
    public void setValue(final List<String> values) {
        if (values != null) {
            this.setAllowedValues(new AllowedValues(values));
        }
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DomainType && super.equals(object)) {
            final DomainType that = (DomainType) object;
            return Objects.equals(this.name, that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     *
     */
    @Override
    public String toString() {
        return super.toString() + '\n' + "name: " + name;
    }

}
