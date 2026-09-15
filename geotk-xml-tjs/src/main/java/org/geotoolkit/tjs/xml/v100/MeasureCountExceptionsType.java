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
 * Valid exception classes (null values) for a {@code Count} or {@code Measure} attribute.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeasureCountExceptionsType", propOrder = {"nullValues"})
public class MeasureCountExceptionsType {

    @XmlElement(name = "Null")
    private List<ExceptionEntryType> nullValues;

    public List<ExceptionEntryType> getNullValues() {
        if (nullValues == null) {
            nullValues = new ArrayList<>();
        }
        return nullValues;
    }

    public void setNullValues(List<ExceptionEntryType> nullValues) {
        this.nullValues = nullValues;
    }
}
