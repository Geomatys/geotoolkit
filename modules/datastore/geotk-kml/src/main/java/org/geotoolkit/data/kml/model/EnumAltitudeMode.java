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
 * <p>Thi enumeration maps altitudeMode whose altitudeModeEnumType
 * is the only one imlementation.</p>
 *
 * <pre>
 * &lt;element name="altitudeMode" type="kml:altitudeModeEnumType" default="clampToGround" substitutionGroup="kml:altitudeModeGroup"/>
 * 
 * &lt;simpleType name="altitudeModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="clampToGround"/>
 *      &lt;enumeration value="relativeToGround"/>
 *      &lt;enumeration value="absolute"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum EnumAltitudeMode implements AltitudeMode {

    CLAMP_TO_GROUND("clampToGround"),
    RELATIVE_TO_GROUND("relativeToGround"),
    ABSOLUTE("absolute");

    private final String altitudeMode;

    /**
     *
     * @param altitudeMode
     */
    private EnumAltitudeMode(String altitudeMode){
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    @Override
    public String getAltitudeMode(){
        return this.altitudeMode;
    }

    /**
     *
     * @param altitudeMode
     * @return The AltitudeMode instance corresponding to the altitudeMode parameter.
     */
    public static EnumAltitudeMode transform(String altitudeMode){
        return transform(altitudeMode, null);
    }

    /**
     *
     * @param altitudeMode
     * @param defaultValue The default value to return if altitudeMode String parameter
     * do not correspond to one AltitudeMode instance.
     * @return The AltitudeMode instance corresponding to the altitudeMode parameter.
     */
    public static EnumAltitudeMode transform(String altitudeMode, EnumAltitudeMode defaultValue){
        for(EnumAltitudeMode cm : EnumAltitudeMode.values()){
            if(cm.getAltitudeMode().equals(altitudeMode)) return cm;
        }
        return defaultValue;
    }
}
