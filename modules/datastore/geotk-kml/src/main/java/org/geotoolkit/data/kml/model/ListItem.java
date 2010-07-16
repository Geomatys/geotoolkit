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
 * <p>This enumeration maps listItemTypeEnumType type.</p>
 *
 * <pre>
 * &lt;simpleType name="listItemTypeEnumType">
 *  &lt;restriction base="string">
 *      &lt;enumeration value="radioFolder"/>
 *      &lt;enumeration value="check"/>
 *      &lt;enumeration value="checkHideChildren"/>
 *      &lt;enumeration value="checkOffOnly"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum ListItem {

    RADIO_FOLDER("radioFolder"),
    CHECK("check"),
    CHECK_HIDE_CHILDREN("checkHideChildren"),
    CHECK_OFF_ONLY("checkOffOnly");

    private final String item;

    /**
     * 
     * @param item
     */
    private ListItem(String item){
        this.item = item;
    }

    /**
     *
     * @return
     */
    public String getItem(){
        return this.item;
    }

    /**
     *
     * @param item
     * @return The ListItem instance corresponding to the item parameter.
     */
    public static ListItem transform(String item){
        return transform(item, null);
    }

    /**
     *
     * @param item
     * @param defaultValue The default value to return if item String parameter
     * do not correspond to one ListItem instance.
     * @return The ListItem instance corresponding to the item parameter.
     */
    public static ListItem transform(String item, ListItem defaultValue){
        for(ListItem cm : ListItem.values()){
            if(cm.getItem().equals(item)) return cm;
        }
        return defaultValue;
    }
}
