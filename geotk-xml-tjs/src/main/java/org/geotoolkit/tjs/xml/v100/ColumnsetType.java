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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Ordered list of columns found in a dataset.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColumnsetType", propOrder = {"frameworkKey", "attributes"})
public class ColumnsetType {

    @XmlElement(name = "FrameworkKey", required = true)
    private ColumnsetFrameworkKeyType frameworkKey;

    @XmlElement(name = "Attributes", required = true)
    private ColumnsetAttributesType attributes;

    public ColumnsetFrameworkKeyType getFrameworkKey() {
        return frameworkKey;
    }

    public void setFrameworkKey(ColumnsetFrameworkKeyType frameworkKey) {
        this.frameworkKey = frameworkKey;
    }

    public ColumnsetAttributesType getAttributes() {
        return attributes;
    }

    public void setAttributes(ColumnsetAttributesType attributes) {
        this.attributes = attributes;
    }
}
