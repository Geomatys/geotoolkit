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
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.ElementSetName;
import org.geotoolkit.csw.xml.ElementSetType;


/**
 * <p>Java class for ElementSetNameType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ElementSetNameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/cat/csw>ElementSetType">
 *       &lt;attribute name="typeNames" type="{http://www.opengis.net/cat/csw}TypeNameListType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElementSetNameType", propOrder = {
    "value"
})
public class ElementSetNameType implements ElementSetName {

    @XmlValue
    private ElementSetType value;
    @XmlAttribute
    private List<QName> typeNames;

    /**
     * An empty constructor used by JAXB
     */
    ElementSetNameType(){

    }

    /**
     * Build a elementSetName with only the elementSet value (no typeNames).
     */
    public ElementSetNameType(final ElementSetType value){
        this.value = value;
    }

    public ElementSetNameType(final ElementSetNameType other) {
        if (other != null) {
            this.value = other.value;
            if (other.typeNames != null) {
                this.typeNames = new ArrayList<>(other.typeNames);
            }
        }
    }

    /**
     * Named subsets of catalogue object properties;
     * these views are bound to a specific information model and are defined within an application profile.
     */
    public ElementSetType getValue() {
        return value;
    }

    /**
     * Named subsets of catalogue object properties;
     * these views are bound to a specific information model and are defined within an application profile.
     */
    public void setValue(final ElementSetType value) {
        this.value = value;
    }

    /**
     * Gets the value of the typeNames property.
     *
     */
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<>();
        }
        return this.typeNames;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ElementSetNameType) {
            final ElementSetNameType that = (ElementSetNameType) object;
            return Objects.equals(this.typeNames,  that.typeNames)   &&
                   Objects.equals(this.value,  that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 43 * hash + (this.typeNames != null ? this.typeNames.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ElementSetNameType]");
        if (typeNames != null) {
            sb.append("typeNames").append('\n');
            for (QName q: typeNames) {
                sb.append(q).append('\n');
            }
        }
        if (value != null) {
            sb.append("value").append(value).append('\n');
        }
        return sb.toString();
    }
}
