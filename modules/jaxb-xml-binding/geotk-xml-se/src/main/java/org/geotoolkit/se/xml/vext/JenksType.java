/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.se.xml.vext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.se.xml.v110.FunctionType;

/**
 * Java class for Jenks function.
 * @module pending
 * @author Quentin Boileau (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JenksType", propOrder = {
    "classNumber",
    "palette"
})
public class JenksType extends FunctionType {
    
    @XmlElement(name = "ClassNumber", required = true, namespace="http://www.opengis.net/se")
    protected Integer classNumber;
    
    @XmlElement(name = "Palette", namespace="http://www.opengis.net/se")
    protected String palette;

    public Integer getClassNumber() {
        return classNumber;
    }

    public String getPalette() {
        return palette;
    }

    public void setClassNumber(final Integer classNumber) {
        this.classNumber = classNumber;
    }

    public void setPalette(final String paletteName) {
        this.palette= paletteName;
    }
    
}
