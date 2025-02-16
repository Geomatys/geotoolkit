/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.ElementSetName;
import org.geotoolkit.csw.xml.ElementSetType;


/**
 * <p>Classe Java pour ElementSetNameType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ElementSetNameType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/cat/csw/3.0>ElementSetType">
 *       &lt;attribute name="typeNames" type="{http://www.opengis.net/cat/csw/3.0}TypeNameListType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElementSetNameType", propOrder = {
    "value"
})
public class ElementSetNameType implements ElementSetName {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "typeNames")
    protected List<QName> typeNames;

    /**
     * An empty constructor used by JAXB
     */
    ElementSetNameType(){
    }

    /**
     * Build a elementSetName with only the elementSet value (no typeNames).
     */
    public ElementSetNameType(final ElementSetType value){
        if (value != null) {
            this.value = value.value();
        }
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
     * Obtient la valeur de la propriété value.
     */
    @Override
    public ElementSetType getValue() {
        if (value != null) {
            return ElementSetType.fromValue(value);
        }
        return null;
    }

    /**
     * Définit la valeur de la propriété value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the typeNames property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeNames property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeNames().add(newItem);
     * </pre>
     */
    public List<QName> getTypeNames() {
        if (typeNames == null) {
            typeNames = new ArrayList<>();
        }
        return this.typeNames;
    }
}
