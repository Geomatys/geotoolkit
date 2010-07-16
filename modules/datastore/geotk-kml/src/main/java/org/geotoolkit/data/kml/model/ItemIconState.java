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
 * <p>This enumeration maps itemIconStateEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="itemIconStateEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="open"/>
 *      &lt;enumeration value="closed"/>
 *      &lt;enumeration value="error"/>
 *      &lt;enumeration value="fetching0"/>
 *      &lt;enumeration value="fetching1"/>
 *      &lt;enumeration value="fetching2"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum ItemIconState {

    OPEN("open"),
    CLOSED("closed"),
    ERROR("error"),
    FETCHING_0("fetching0"),
    FETCHING_1("fetching1"),
    FETCHING_2("fetching2");

    private final String itemIconState;

    /**
     *
     * @param itemIconState
     */
    private ItemIconState(String itemIconState){
        this.itemIconState = itemIconState;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getItemIconState(){
        return this.itemIconState;
    }

    /**
     *
     * @param itemIconState
     * @return The ItemIconState instance corresponding to the itemIconState parameter.
     */
    public static ItemIconState transform(String itemIconState){
        return transform(itemIconState, null);
    }

    /**
     *
     * @param altitudeMode
     * @param defaultValue The default value to return if itemIconState String parameter
     * do not correspond to one ItemIconState instance.
     * @return The ItemIconState instance corresponding to the itemIconState parameter.
     */
    public static ItemIconState transform(String itemIconState, ItemIconState defaultValue){
        for(ItemIconState cm : ItemIconState.values()){
            if(cm.getItemIconState().equals(itemIconState)) return cm;
        }
        return defaultValue;
    }
}
