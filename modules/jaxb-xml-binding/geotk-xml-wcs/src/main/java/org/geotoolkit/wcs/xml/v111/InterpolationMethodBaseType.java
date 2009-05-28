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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;


/**
 * Adapts gml:CodeWithAuthorityType from GML 3.2 for this WCS purpose, allowing the codeSpace to be omitted but providing a default value for the standard interpolation methods defined in Annex C of ISO 19123: Geographic information - Schema for coverage geometry and functions. 
 * 
 * <p>Java class for InterpolationMethodBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InterpolationMethodBaseType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://www.opengis.net/ows/1.1>CodeType">
 *       &lt;attribute name="codeSpace" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://schemas.opengis.net/wcs/1.1.0/interpolationMethods.xml" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterpolationMethodBaseType")
@XmlSeeAlso({
    InterpolationMethodType.class
})
public class InterpolationMethodBaseType extends CodeType {
    
    /**
     * Empty constructor used by JAXB
     */
    InterpolationMethodBaseType(){
    }
    
    /**
     * Build a simple interpolation method
     */
    public InterpolationMethodBaseType(String value){
        super(value);
    }

}
