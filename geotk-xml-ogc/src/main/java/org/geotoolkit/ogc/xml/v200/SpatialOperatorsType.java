/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.capability.SpatialOperator;
import org.geotoolkit.filter.capability.SpatialOperators;


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
 *         &lt;element name="SpatialOperator" type="{http://www.opengis.net/fes/2.0}SpatialOperatorType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialOperatorsType", propOrder = {
    "spatialOperator"
})
public class SpatialOperatorsType extends SpatialOperators {

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
    public List<SpatialOperatorType> getSpatialOperator() {
        if (spatialOperator == null) {
            spatialOperator = new ArrayList<SpatialOperatorType>();
        }
        return this.spatialOperator;
    }

    /**
     * Gets the value of the spatialOperator property.
     */
    @Override
    public Collection<SpatialOperator> getOperators() {
        List<SpatialOperator> result =  new ArrayList<>();
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

    @Override
    public SpatialOperator getOperator(final String name) {
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
