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
 * <p>This enumeration maps viewRefreshModeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="viewRefreshModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="never"/>
 *      &lt;enumeration value="onRequest"/>
 *      &lt;enumeration value="onStop"/>
 *      &lt;enumeration value="onRegion"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum ViewRefreshMode {

    NEVER("never"),
    ON_REQUEST("onRequest"),
    ON_STOP("onStop"),
    ON_REGION("onRegion");

    private final String viewRefreshMode;

    /**
     * 
     * @param viewRefreshMode
     */
    private ViewRefreshMode(String viewRefreshMode){
        this.viewRefreshMode = viewRefreshMode;
    }

    /**
     *
     * @return
     */
    public String getViewRefreshMode(){
        return this.viewRefreshMode;
    }

    /**
     *
     * @param viewRefreshMode
     * @return The ViewRefreshMode instance corresponding to the viewRefreshMode parameter.
     */
    public static ViewRefreshMode transform(String viewRefreshMode){
        return transform(viewRefreshMode, null);
    }

    /**
     *
     * @param refreshMode
     * @param defaultValue The default value to return if viewRefreshMode String parameter
     * do not correspond to one ViewRefreshMode instance.
     * @return The ViewRefreshMode instance corresponding to the viewRefreshMode parameter.
     */
    public static ViewRefreshMode transform(String viewRefreshMode, ViewRefreshMode defaultValue){
        for(ViewRefreshMode cm : ViewRefreshMode.values()){
            if(cm.getViewRefreshMode().equals(viewRefreshMode)) return cm;
        }
        return defaultValue;
    }

}
