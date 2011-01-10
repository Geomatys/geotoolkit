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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InterpolationMethod" type="{http://www.opengis.net/wcs}InterpolationMethodType" maxOccurs="unbounded"/>
 *         &lt;element name="Default" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "interpolationMethod",
    "_default"
})
@XmlRootElement(name = "InterpolationMethods")
public class InterpolationMethods {

    @XmlElement(name = "InterpolationMethod", required = true)
    private List<InterpolationMethodType> interpolationMethod = new ArrayList<InterpolationMethodType>();
    @XmlElement(name = "Default")
    private String _default;

    /**
     * Empty constructor used by JAXB.
     */
    InterpolationMethods(){
    }
    
    /**
     * build a new interpolation method.
     */
    public InterpolationMethods(final List<InterpolationMethodType> interpolationMethod, final String _default){
        this._default            = _default;
        this.interpolationMethod = interpolationMethod;
    }
    
    /**
     * Gets the value of the interpolationMethod property.
     */
    public List<InterpolationMethodType> getInterpolationMethod() {
        return Collections.unmodifiableList(interpolationMethod);
    }

    /**
     * Gets the value of the default property.
     */
    public String getDefault() {
        return _default;
    }
}
