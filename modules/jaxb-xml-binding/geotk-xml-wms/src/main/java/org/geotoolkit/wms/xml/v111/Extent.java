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
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Extent")
public class Extent {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String name;
    @XmlAttribute(name = "default")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String _default;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String nearestValue;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String multipleValues;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String current;
    @XmlValue
    private String value;

    /**
     * An empty constructor used by JAXB.
     */
     Extent() {
     }

     /**
      * Build a new Extent object.
      *  
      * @param name     The name (often the type) of this Dimension block (time, elevation,...)
      * @param _default The default value if its not specified.
      */
    public Extent(final String name, final String _default, String value) {
        
        this.name           = name;
        this._default       = _default;
        this.value          = value;
    }
    
    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }


    /**
     * Gets the value of the default property.
     */
    public String getDefault() {
        return _default;
    }

    /**
     * Gets the value of the nearestValue property.
     * 
     *     
     */
    public String getNearestValue() {
        if (nearestValue == null) {
            return "0";
        } else {
            return nearestValue;
        }
    }


    /**
     * Gets the value of the multipleValues property.
     */
    public String getMultipleValues() {
        if (multipleValues == null) {
            return "0";
        } else {
            return multipleValues;
        }
    }

    /**
     * Gets the value of the current property.
     * 
     *     
     */
    public String getCurrent() {
        if (current == null) {
            return "0";
        } else {
            return current;
        }
    }


    /**
     * Gets the value of the value property.
     * 
     *     
     */
    public String getvalue() {
        return value;
    }
}
