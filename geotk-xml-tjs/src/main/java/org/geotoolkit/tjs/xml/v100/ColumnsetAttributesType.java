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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * {@code Attributes} wrapper element nested inside {@code tjs:Columnset}: identifies the columns
 * of the data table that contain data that can be joined to the spatial framework.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColumnsetAttributesType", propOrder = {"column"})
public class ColumnsetAttributesType {

    @XmlElement(name = "Column", required = true)
    private List<AttributeColumnType> column;

    public List<AttributeColumnType> getColumn() {
        if (column == null) {
            column = new ArrayList<>();
        }
        return column;
    }

    public void setColumn(List<AttributeColumnType> column) {
        this.column = column;
    }
}
