/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;
/**
 * <p>This enumeration maps shapeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="shapeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="rectangle"/>
 *      &lt;enumeration value="cylinder"/>
 *      &lt;enumeration value="sphere"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum Shape {

    RECTANGLE("rectangle"),
    CYLINDER("cylinder"),
    SPHERE("sphere");

    private final String shape;

    /**
     *
     * @param shape
     */
    private Shape(String shape){
        this.shape = shape;
    }

    /**
     *
     * @return
     */
    public String getShape(){
        return this.shape;
    }

    /**
     *
     * @param shape
     * @return The Shape instance corresponding to the shape parameter.
     */
    public static Shape transform(String shape){
        return transform(shape, null);
    }

    /**
     *
     * @param unit
     * @param defaultValue The default value to return if shape String parameter
     * do not correspond to one Shape instance.
     * @return The Shape instance corresponding to the shape parameter.
     */
    public static Shape transform(String shape, Shape defaultValue){
        for(Shape s : Shape.values()){
            if(s.getShape().equals(shape)) return s;
        }
        return defaultValue;
    }
}
