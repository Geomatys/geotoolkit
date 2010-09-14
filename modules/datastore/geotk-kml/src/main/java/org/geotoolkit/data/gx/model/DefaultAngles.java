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
package org.geotoolkit.data.gx.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAngles implements Angles {

    private double heading;
    private double tilt;
    private double roll;

    /**
     *
     */
    public DefaultAngles(){
        this.heading = Double.NaN;
        this.tilt = Double.NaN;
        this.roll = Double.NaN;
    }

    /**
     * 
     * @param angles
     */
    public DefaultAngles(double... angles){
        this();
        switch(angles.length){
            case 3 :
                this.roll = angles[2];
            case 2 :
                this.tilt = angles[1];
            case 1 :
                this.heading = angles[0];
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getHeading() {
        return this.heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getTilt() {
        return this.tilt;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getRoll() {
        return this.roll;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHeading(double heading) {
        this.heading = heading;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRoll(double roll) {
        this.roll = roll;
    }

}
