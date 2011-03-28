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
 * <p>This enumeration maps playMode element.</p>
 *
 * <pre>
 * &lt;element name="playMode" type="gx:playModeEnumType" default="pause"/>
 *
 * &lt;simpleType name="playModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="pause"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum EnumPlayMode {

    PAUSE("pause");

    private final String playMode;

    /**
     *
     * @param playMode
     */
    private EnumPlayMode(String playMode){
        this.playMode = playMode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getPlayMode(){
        return this.playMode;
    }

    /**
     *
     * @param playMode
     * @return The EnumPlayMode instance corresponding to the playMode parameter.
     */
    public static EnumPlayMode transform(String playMode){
        return transform(playMode, null);
    }

    /**
     *
     * @param playMode
     * @param defaultValue The default value to return if playMode String parameter
     * do not correspond to one EnumPlayMode instance.
     * @return The EnumPlayMode instance corresponding to the playMode parameter.
     */
    public static EnumPlayMode transform(String playMode, EnumPlayMode defaultValue){
        for(EnumPlayMode cm : EnumPlayMode.values()){
            if(cm.getPlayMode().equals(playMode)) return cm;
        }
        return defaultValue;
    }
}
