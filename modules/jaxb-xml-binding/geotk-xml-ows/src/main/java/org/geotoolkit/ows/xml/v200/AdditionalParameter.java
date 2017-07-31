/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.opengis.net/ows/2.0}CodeType"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "value"
})
public class AdditionalParameter {

    @XmlElement(name = "Name", required = true)
    private CodeType name;
    @XmlElement(name = "Value", required = true)
    private List<Object> value;

    public AdditionalParameter() {

    }

    public AdditionalParameter(CodeType name, List<Object> value) {
        this.name = name;
        this.value = value;
    }
    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setName(CodeType value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     *
     */
    public List<Object> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        List<Object> cleanValues = new ArrayList<>();
        for (Object val : value) {
            if (val instanceof Node) {
                Node n = (Node) val;
                if (n.getLocalName().equals("Value") && n.hasChildNodes()) {
                    Node child = n.getChildNodes().item(0);
                    if (child instanceof Text) {
                        cleanValues.add(((Text)child).getNodeValue());
                    }
                    continue;
                }
            }
            cleanValues.add(val);
        }
        return cleanValues;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof  AdditionalParameter) {
            final AdditionalParameter that = (AdditionalParameter) object;
            return Objects.equals(this.name, that.name) &&
                   Objects.equals(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[AdditionalParameter]\n");
        s.append("name=").append(name).append('\n');
        if (value != null) {
            s.append("values:\n");
            for (Object a : value) {
                s.append(a).append('\n');
            }
            s.append("cleaned values:\n");
            for (Object a : getValue()) {
                s.append(a).append('\n');
            }
        }
        return s.toString();
    }

}
