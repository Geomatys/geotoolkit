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
package org.geotoolkit.data.gx;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotoolkit.data.gx.model.Angles;

/**
 *
 * @author Samuel Andrés
 */
public class GxUtilities {

    /**
     *
     * @param coordinates A string of coordinates separeted by spaces.
     * @return a 2D/3D coordinate object.
     */
    public static Coordinate toCoordinate(final String coordinates){

        final String[] coordinatesList = coordinates.split(" ");
        final Coordinate c = new Coordinate();

        c.x = Double.valueOf(coordinatesList[0].trim());
        c.y = Double.valueOf(coordinatesList[1].trim());
        if(coordinatesList.length == 3){
            c.z = Double.valueOf(coordinatesList[2].trim());
        }

        return c;
    }

    /**
     *
     * @param coordinate a 2D/3D coordinate object.
     * @return a string of coordinates separated by spaces.
     */
    public static String toString(final Coordinate coordinate) {

        final StringBuilder sb = new StringBuilder();
        sb.append(coordinate.x);
        sb.append(' ');
        sb.append(coordinate.y);
        if(!Double.isNaN(coordinate.z)) {
            sb.append(' ');
            sb.append(coordinate.z);
        }
        return sb.toString();
    }

    /**
     *
     * @param angles contains heading, tilt and roll values
     * @return angles values separated by spaces.
     */
    public static String toString(final Angles angles) {
        
        final StringBuilder sb = new StringBuilder();
        sb.append(angles.getHeading());
        sb.append(' ');
        sb.append(angles.getTilt());
        if(!Double.isNaN(angles.getRoll())) {
            sb.append(' ');
            sb.append(angles.getRoll());
        }
        return sb.toString();
    }
}
