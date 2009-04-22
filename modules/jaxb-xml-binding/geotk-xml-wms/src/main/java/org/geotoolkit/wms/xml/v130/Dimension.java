/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v130;

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
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="units" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="unitSymbol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="multipleValues" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="nearestValue" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="current" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
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
    public Dimension(final String name, final String units, final String _default, String value) {
        
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
    public void setValue(String value) {
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
    public void setDefault(String _default) {
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
