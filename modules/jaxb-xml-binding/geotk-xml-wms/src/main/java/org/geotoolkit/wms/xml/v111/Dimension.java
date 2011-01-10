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
import org.geotoolkit.wms.xml.AbstractDimension;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Dimension")
public class Dimension extends AbstractDimension {

    @XmlValue
    private String value;
    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute(required = true)
    private String units;
    @XmlAttribute
    private String unitSymbol;
    @XmlAttribute(name = "default")
    private String _default;
    @XmlAttribute
    private Boolean multipleValues;
    @XmlAttribute
    private Boolean nearestValue;
    @XmlAttribute
    private Boolean current;

    /**
     * An empty constructor used by JAXB.
     */
     Dimension() {
     }

     /**
      * Build a new Dimension object.
      *  
      * @param name     The name (often the type) of this Dimension block (time, elevation,...)
      * @param units    The unit of the value (example:ISO8601 for time).
      * @param _default The default value if its not specified.
      */
    public Dimension(final String name, final String units, final String _default, final String value) {
        
        this.name           = name;
        this._default       = _default;
        this.units          = units;
        this.value          = value;
    }
    
    /**
     * Build a new Dimension object with full parameter.
     */
    public Dimension(final String value, final String name, final String units, final String unitSymbol, 
            final String _default, final Boolean multipleValues, final Boolean nearestValue,
            final Boolean current) {
       
        this.current        = current;
        this.multipleValues = multipleValues;
        this.name           = name;
        this._default       = _default;
        this.nearestValue   = nearestValue;
        this.unitSymbol     = unitSymbol;
        this.units          = units;
        this.value          = value;
    }
    
    /**
     * Gets the value of the value property.
     * 
    */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
    */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the units property.
     * 
     */
    public String getUnits() {
        return units;
    }

    /**
     * Gets the value of the unitSymbol property.
     * 
     */
    public String getUnitSymbol() {
        return unitSymbol;
    }

    /**
     * Gets the value of the default property.
     */
    public String getDefault() {
        return _default;
    }
  
    /**
     * Sets the value of the Default property.
     * 
     */
    public void setDefault(final String _default) {
        this._default = _default;
    }
    
    /**
     * Gets the value of the multipleValues property.
     */
    public Boolean isMultipleValues() {
        return multipleValues;
    }

    /**
     * Gets the value of the nearestValue property.
     * 
     */
    public Boolean isNearestValue() {
        return nearestValue;
    }

    /**
     * Gets the value of the current property.
     * 
     */
    public Boolean isCurrent() {
        return current;
    }
}
