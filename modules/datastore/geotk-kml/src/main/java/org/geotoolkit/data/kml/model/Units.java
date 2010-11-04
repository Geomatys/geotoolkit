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
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum Units {

    FRACTION("fraction"),
    PIXELS("pixels"),
    INSET_PIXELS("insetPixels");
    
    private final String unit;

    /**
     * 
     * @param unit
     */
    private Units(String unit){
        this.unit = unit;
    }

    /**
     *
     * @return
     */
    public String getUnit(){
        return this.unit;
    }

    /**
     *
     * @param unit
     * @return The Units instance corresponding to the unit parameter.
     */
    public static Units transform(String unit){
        return transform(unit, null);
    }

    /**
     *
     * @param unit
     * @param defaultValue The default value to return if unit String parameter
     * do not correspond to one Units instance.
     * @return The Units instance corresponding to the unit parameter.
     */
    public static Units transform(String unit, Units defaultValue){
        for(Units u : Units.values()){
            if(u.getUnit().equals(unit)) return u;
        }
        return defaultValue;
    }

}
