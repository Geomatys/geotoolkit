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
package org.geotoolkit.ogc.xml.v110;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;


/**
 * <p>Java class for SpatialOperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatialOperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SpatialOperator" type="{http://www.opengis.net/ogc}SpatialOperatorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialOperatorsType", propOrder = {
    "spatialOperator"
})
public class SpatialOperatorsType implements SpatialOperators {

    @XmlElement(name = "SpatialOperator", required = true)
    private List<SpatialOperatorType> spatialOperator;

     /**
     * An empty constructor used by JAXB
     */
    public SpatialOperatorsType() {
        
    }
    
    /**
     * Build a new comparison operators with the specified array of operator 
     * 
     * @param operators an array of comparison operator
     */
    public SpatialOperatorsType( SpatialOperator[] operators ) {
        if ( operators == null ){
            operators = new SpatialOperator[]{};
        }
        this.spatialOperator = new ArrayList(Arrays.asList(operators));
    }
    
    /**
     * Gets the value of the spatialOperator property.
     */
    public Collection<SpatialOperator> getOperators() {
        List<SpatialOperator> result =  new ArrayList<SpatialOperator>();
        if (spatialOperator == null) {
            spatialOperator = new ArrayList<SpatialOperatorType>();
            return result;
        } else {
            for (SpatialOperatorType c: spatialOperator) {
                result.add(c);
            }
        }
        return result;
    }

    public SpatialOperator getOperator(String name) {
        if ( name == null || spatialOperator == null) {
            return null;
        }        
        for (SpatialOperator operator : spatialOperator ) {            
            if ( name.equals( operator.getName() ) ) {
                return operator;
            }
        }        
        return null;
    }

}
