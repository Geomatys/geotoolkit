/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Identifier of a spatial interpolation method applicable to continuous grid coverages, plus the optional "null Resistance" parameter. 
 * 
 * <p>Java class for InterpolationMethodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterpolationMethodType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/wcs>InterpolationMethodBaseType">
 *       &lt;attribute name="nullResistance" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterpolationMethodType")
public class InterpolationMethodType extends InterpolationMethodBaseType {

    @XmlAttribute
    private String nullResistance;

    /**
     * An empty constructor used by JAXB.
     */
     InterpolationMethodType() {
     }
     
    /**
     * build a new Interpolation Method.
     */
     public InterpolationMethodType(String methodName, String nullResistance) {
         super(methodName);
         this.nullResistance = nullResistance;
     }
     
    /**
     * Gets the value of the nullResistance property.
     */
    public String getNullResistance() {
        return nullResistance;
    }
}
