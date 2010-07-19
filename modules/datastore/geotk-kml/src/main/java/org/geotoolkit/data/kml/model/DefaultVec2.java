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

import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultVec2 implements Vec2 {

    private double x;
    private double y;
    private Units xUnit;
    private Units yUnit;

    /**
     * 
     */
    public DefaultVec2() {
        this.x = DEF_VEC2_X;
        this.y = DEF_VEC2_Y;
        this.xUnit = DEF_VEC2_XUNIT;
        this.yUnit = DEF_VEC2_YUNIT;
    }

    /**
     * 
     * @param x
     * @param y
     * @param xUnit
     * @param yUnit
     */
    public DefaultVec2(double x, double y, Units xUnit, Units yUnit) {
        this.x = x;
        this.y = y;
        this.xUnit = xUnit;
        this.yUnit = yUnit;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Units getXUnits() {
        return this.xUnit;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Units getYUnits() {
        return this.yUnit;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setXUnits(Units xUnit) {
        this.xUnit = xUnit;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setYUnits(Units yUnit) {
        this.yUnit = yUnit;
    }

    @Override
    public String toString() {
        return "Vec2Default : "
                + "\n\tx : " + this.x
                + "\n\ty : " + this.y
                + "\n\txUnit : " + this.xUnit
                + "\n\tyUnit : " + this.yUnit;
    }
}
