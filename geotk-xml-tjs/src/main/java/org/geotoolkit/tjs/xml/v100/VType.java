/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Value of an attribute (i.e. data) applicable to the spatial feature identified by the
 * {@code K} elements of a row. When there is more than one {@code V} element, they are ordered
 * according to the same sequence as the {@code Column} elements of the {@code Columnset}
 * structure. When this value is null (indicated with the {@code null} attribute) an
 * identification of the reason may be included in the content of this element.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VType")
public class VType {

    @XmlValue
    private String value;

    @XmlAttribute
    private String aid;

    @XmlAttribute(name = "null")
    private Boolean nullValue = false;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public Boolean getNullValue() {
        return nullValue;
    }

    public void setNullValue(Boolean nullValue) {
        this.nullValue = nullValue;
    }
}
