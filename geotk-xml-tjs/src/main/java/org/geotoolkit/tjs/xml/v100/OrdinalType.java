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
 * Ordered classifications data.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrdinalType", propOrder = {"classes", "exceptions"})
public class OrdinalType {

    @XmlElement(name = "Classes")
    private OrdinalClassesType classes;

    @XmlElement(name = "Exceptions")
    private NominalOrdinalExceptionsType exceptions;

    public OrdinalClassesType getClasses() {
        return classes;
    }

    public void setClasses(OrdinalClassesType classes) {
        this.classes = classes;
    }

    public NominalOrdinalExceptionsType getExceptions() {
        return exceptions;
    }

    public void setExceptions(NominalOrdinalExceptionsType exceptions) {
        this.exceptions = exceptions;
    }
}
