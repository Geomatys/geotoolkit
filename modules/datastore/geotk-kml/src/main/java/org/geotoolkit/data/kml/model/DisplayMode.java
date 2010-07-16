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
 * <p>Thi enumeration maps displayMode type.</p>
 *
 * <pre>
 * &lt;simpleType name="displayModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="default"/>
 *      &lt;enumeration value="hide"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum DisplayMode {

    DEFAULT("default"),
    HIDE("hide");

    private final String mode;

    /**
     *
     * @param mode
     */
    private DisplayMode(String mode){
        this.mode = mode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getDisplayMode(){
        return this.mode;
    }

    /**
     *
     * @param mode
     * @return The DisplayMode instance corresponding to the mode parameter.
     */
    public static DisplayMode transform(String mode){
        return transform(mode, null);
    }

    /**
     *
     * @param mode
     * @param defaultValue The default value to return if mode String parameter
     * do not correspond to one DisplayMode instance.
     * @return The DisplayMode instance corresponding to the mode parameter.
     */
    public static DisplayMode transform(String mode, DisplayMode defaultValue){
        for(DisplayMode cm : DisplayMode.values()){
            if(cm.getDisplayMode().equals(mode)) return cm;
        }
        return defaultValue;
    }

}
