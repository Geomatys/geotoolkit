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
 * <p>This interface maps vec2Type type.</p>
 *
 * <pre>
 * &lt;complexType name="vec2Type" abstract="false">
 *  &lt;attribute name="x" type="double" default="1.0"/>
 *  &lt;attribute name="y" type="double" default="1.0"/>
 *  &lt;attribute name="xunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 *  &lt;attribute name="yunits" type="kml:unitsEnumType" use="optional" default="fraction"/>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Vec2 {

    /**
     *
     * @return
     */
    double getX();

    /**
     *
     * @return
     */
    double getY();

    /**
     *
     * @return
     */
    Units getXUnits();

    /**
     * 
     * @return
     */
    Units getYUnits();

    /**
     *
     * @param x
     */
    void setX(double x);

    /**
     *
     * @param y
     */
    void setY(double y);

    /**
     *
     * @param xUnitx
     */
    void setXUnits(Units xUnit);

    /**
     *
     * @param yUnits
     */
    void setYUnits(Units yUnit);

}
