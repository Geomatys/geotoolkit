/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.Parameter;
import org.w3c.dom.Element;


/**
 * <p>Java class for ParameterType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ParameterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParameterType", propOrder = {
    "content"
})
public class ParameterType implements Parameter {

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> content;
    @XmlAttribute(required = true)
    private String name;

    public ParameterType() {

    }

    public ParameterType(final String name, final List<Object> content) {
        this.name = name;
        this.content = content;
    }

    public ParameterType(final String name, final Object content) {
        this.name = name;
        if (content != null) {
            this.content = new ArrayList<Object>();
            this.content.add(content);
        }
    }

    /**
     * Gets the value of the content property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * {@link String }
     *
     */
    @Override
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    @Override
    public void setName(final String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ParameterType]\n");
        if (name != null) {
            sb.append("name:").append(name);
        }
        if (content != null) {
            sb.append("content:\n");
            for (Object o : content) {
                sb.append(o).append('\n');
            }
        }
        return sb.toString();
    }
}
