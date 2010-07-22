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
 * <p>This enumeration maps flyToMode element.</p>
 *
 * <pre>
 * &lt;element name="flyToMode" type="gx:flyToModeEnumType" default="bounce"/>
 *
 * &lt;simpleType name="flyToModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="bounce"/>
 *      &lt;enumeration value="smooth"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum EnumFlyToMode {

    BOUNCE("bounce"),
    SMOOTH("smooth");

    private final String flyToMode;

    /**
     *
     * @param flyToMode
     */
    private EnumFlyToMode(String flyToMode){
        this.flyToMode = flyToMode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getFlyToMode(){
        return this.flyToMode;
    }

    /**
     *
     * @param flyToMode
     * @return The EnumFlyToMode instance corresponding to the flyToMode parameter.
     */
    public static EnumFlyToMode transform(String flyToMode){
        return transform(flyToMode, null);
    }

    /**
     *
     * @param flyToMode
     * @param defaultValue The default value to return if flyToMode String parameter
     * do not correspond to one EnumFlyToMode instance.
     * @return The EnumFlyToMode instance corresponding to the flyToMode parameter.
     */
    public static EnumFlyToMode transform(String flyToMode, EnumFlyToMode defaultValue){
        for(EnumFlyToMode cm : EnumFlyToMode.values()){
            if(cm.getFlyToMode().equals(flyToMode)) return cm;
        }
        return defaultValue;
    }
}
