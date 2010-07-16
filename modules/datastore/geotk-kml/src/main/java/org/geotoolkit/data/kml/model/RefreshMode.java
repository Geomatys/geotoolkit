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
 * <p>This enumeration maps refreshModeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="refreshModeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="onChange"/>
 *      &lt;enumeration value="onInterval"/>
 *      &lt;enumeration value="onExpire"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum RefreshMode {

    ON_CHANGE("onChange"),
    ON_INTERVAL("onInterval"),
    ON_EXPIRE("onExpire");

    private final String refreshMode;

    /**
     * 
     * @param refreshMode
     */
    private RefreshMode(String refreshMode){
        this.refreshMode = refreshMode;
    }

    /**
     *
     * @return
     */
    public String getRefreshMode(){
        return this.refreshMode;
    }

    /**
     *
     * @param refreshMode
     * @return The RefreshMode instance corresponding to the refreshMode parameter.
     */
    public static RefreshMode transform(String refreshMode){
        return transform(refreshMode, null);
    }

    /**
     *
     * @param refreshMode
     * @param defaultValue The default value to return if refreshMode String parameter
     * do not correspond to one RefreshMode instance.
     * @return The RefreshMode instance corresponding to the refreshMode parameter.
     */
    public static RefreshMode transform(String refreshMode, RefreshMode defaultValue){
        for(RefreshMode cm : RefreshMode.values()){
            if(cm.getRefreshMode().equals(refreshMode)) return cm;
        }
        return defaultValue;
    }
}
