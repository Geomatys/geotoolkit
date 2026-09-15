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
 * Data consists of measurements of some characteristic attributable to the spatial features.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeasureType", propOrder = {"uom", "uncertainty", "exceptions"})
public class MeasureType {

    @XmlElement(name = "UOM", required = true)
    private UOMType uom;

    @XmlElement(name = "Uncertainty")
    private UncertaintyType uncertainty;

    @XmlElement(name = "Exceptions")
    private MeasureCountExceptionsType exceptions;

    public UOMType getUom() {
        return uom;
    }

    public void setUom(UOMType uom) {
        this.uom = uom;
    }

    public UncertaintyType getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(UncertaintyType uncertainty) {
        this.uncertainty = uncertainty;
    }

    public MeasureCountExceptionsType getExceptions() {
        return exceptions;
    }

    public void setExceptions(MeasureCountExceptionsType exceptions) {
        this.exceptions = exceptions;
    }
}
