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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Vector"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}SquareMatrix"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VectorOrSquareMatrixPropertyType", propOrder = {
    "vector",
    "squareMatrix"
})
public class VectorOrSquareMatrixPropertyType {

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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof VectorOrSquareMatrixPropertyType) {
            final VectorOrSquareMatrixPropertyType that = (VectorOrSquareMatrixPropertyType) object;
            return Utilities.equals(this.squareMatrix, that.squareMatrix) &&
                   Utilities.equals(this.vector,       that.vector);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.vector != null ? this.vector.hashCode() : 0);
        hash = 11 * hash + (this.squareMatrix != null ? this.squareMatrix.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[VectorOrSquareMatrixPropertyType]");
        if (vector != null) {
            s.append("vector:").append(vector).append('\n');
        }
        if (squareMatrix != null) {
            s.append("squareMatrix:").append(squareMatrix).append('\n');
        }
        return s.toString();
    }

}
