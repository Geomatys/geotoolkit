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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractVectorOrSquareMatrixProperty;


/**
 * Vector/SquareMatrix is a data-type so usually appears "by value" rather than by reference.
 * 
 * <p>Java class for VectorOrSquareMatrixPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VectorOrSquareMatrixPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Vector"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}SquareMatrix"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VectorOrSquareMatrixPropertyType", propOrder = {
    "vector",
    "squareMatrix"
})
public class VectorOrSquareMatrixPropertyType implements AbstractVectorOrSquareMatrixProperty {

    @XmlElement(name = "Vector")
    private VectorType vector;
    @XmlElement(name = "SquareMatrix")
    private SquareMatrixType squareMatrix;

     public VectorOrSquareMatrixPropertyType() {

    }

    public VectorOrSquareMatrixPropertyType(VectorType vector) {
        this.vector = vector;
    }

    public VectorOrSquareMatrixPropertyType(SquareMatrixType squareMatrix) {
        this.squareMatrix = squareMatrix;
    }
    
    /**
     * Gets the value of the vector property.
     */
    public VectorType getVector() {
        return vector;
    }

    /**
     * Sets the value of the vector property.
     */
    public void setVector(VectorType value) {
        this.vector = value;
    }

    /**
     * Gets the value of the squareMatrix property.
     */
    public SquareMatrixType getSquareMatrix() {
        return squareMatrix;
    }

    /**
     * Sets the value of the squareMatrix property.
     */
    public void setSquareMatrix(SquareMatrixType value) {
        this.squareMatrix = value;
    }

}
