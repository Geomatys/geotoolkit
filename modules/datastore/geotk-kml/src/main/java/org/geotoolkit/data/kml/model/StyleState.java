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
 * <p>This enumeration maps styleStateEnumType.</p>
 *
 * <pre>
 * &lt;simpleType name="styleStateEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="normal"/>
 *      &lt;enumeration value="highlight"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum StyleState {

    NORMAL("normal"),
    HIGHLIGHT("highlight");

    private final String styleState;

    /**
     * 
     * @param itemIconState
     */
    private StyleState(String itemIconState){
        this.styleState = itemIconState;
    }

    /**
     *
     * @return
     */
    public String getStyleState(){
        return this.styleState;
    }

    /**
     *
     * @param styleState
     * @return The StyleState instance corresponding to the styleState parameter.
     */
    public static StyleState transform(String styleState){
        return transform(styleState, null);
    }

    /**
     *
     * @param styleState
     * @param defaultValue The default value to return if styleState String parameter
     * do not correspond to one StyleState instance.
     * @return The StyleState instance corresponding to the styleState parameter.
     */
    public static StyleState transform(String styleState, StyleState defaultValue){
        for(StyleState cm : StyleState.values()){
            if(cm.getStyleState().equals(styleState)) return cm;
        }
        return defaultValue;
    }
}
