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
 * Type of values and valid values for the contents of a column.
 * <p>
 * Exactly one of nominal/ordinal/count/measure shall be set (XSD choice).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValuesType", propOrder = {"nominal", "ordinal", "count", "measure"})
public class ValuesType {

    @XmlElement(name = "Nominal")
    private NominalType nominal;

    @XmlElement(name = "Ordinal")
    private OrdinalType ordinal;

    @XmlElement(name = "Count")
    private CountType count;

    @XmlElement(name = "Measure")
    private MeasureType measure;

    public NominalType getNominal() {
        return nominal;
    }

    public void setNominal(NominalType nominal) {
        this.nominal = nominal;
    }

    public OrdinalType getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(OrdinalType ordinal) {
        this.ordinal = ordinal;
    }

    public CountType getCount() {
        return count;
    }

    public void setCount(CountType count) {
        this.count = count;
    }

    public MeasureType getMeasure() {
        return measure;
    }

    public void setMeasure(MeasureType measure) {
        this.measure = measure;
    }
}
