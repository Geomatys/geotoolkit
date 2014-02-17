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
 * <p>This enumeration maps colorModeEnumType.</p>
 *
 * <pre>
 * &lt;simpleType name="colorModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="normal"/>
 *      &lt;enumeration value="random"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum ColorMode {

    NORMAL("normal"),
    RANDOM("random");

    private final String colorMode;

    /**
     *
     * @param colorMode
     */
    private ColorMode(String colorMode){
        this.colorMode = colorMode;
    }

    /**
     *
     * @return
     */
    public String getColorMode(){
        return this.colorMode;
    }

    /**
     *
     * @param colorMode
     * @return The ColorMode instance corresponding to the colorMode parameter.
     */
    public static ColorMode transform(String colorMode){
        return transform(colorMode, null);
    }

    /**
     *
     * @param colorMode
     * @param defaultValue The default value to return if colorMode String parameter
     * do not correspond to one ColorMode instance.
     * @return The ColorMode instance corresponding to the colorMode parameter.
     */
    public static ColorMode transform(String colorMode, ColorMode defaultValue){
        for(ColorMode cm : ColorMode.values()){
            if(cm.getColorMode().equals(colorMode)) return cm;
        }
        return defaultValue;
    }
}
