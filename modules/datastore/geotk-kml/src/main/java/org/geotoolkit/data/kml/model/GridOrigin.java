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
 * <p>This enumeration maps gridOriginEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="gridOriginEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="lowerLeft"/>
 *      &lt;enumeration value="upperLeft"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum GridOrigin {

    LOWER_LEFT("lowerLeft"),
    UPPER_LEFT("upperLeft");

    private final String gridOrigin;

    private GridOrigin(String gridOrigin){
        this.gridOrigin = gridOrigin;
    }

    /**
     *
     * @return The String value of the enumeration.
     */
    public String getGridOrigin(){
        return this.gridOrigin;
    }

    /**
     *
     * @param gridOrigin
     * @return The GridOrigin instance corresponding to the gridOrigin parameter.
     */
    public static GridOrigin transform(String gridOrigin){
        return transform(gridOrigin, null);
    }

    /**
     *
     * @param gridOrigin
     * @param defaultValue The default value to return if gridOrigin String parameter
     * do not correspond to one GridOrigin instance.
     * @return The GridOrigin instance corresponding to the gridOrigin parameter.
     */
    public static GridOrigin transform(String gridOrigin, GridOrigin defaultValue){
        for(GridOrigin go : GridOrigin.values()){
            if(go.getGridOrigin().equals(gridOrigin)) return go;
        }
        return defaultValue;
    }
}
