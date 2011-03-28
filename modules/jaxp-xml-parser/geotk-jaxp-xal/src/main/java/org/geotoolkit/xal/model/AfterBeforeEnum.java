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
package org.geotoolkit.xal.model;

/**
 * <p>This enumeration maps AfterBeforeEnum element.</p>
 *
 * <p>No.12-14 where "No." is before actual street number.</p>
 *
 * <pre>
 *  &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *  &lt;xs:enumeration value="Before"/>
 *  &lt;xs:enumeration value="After"/>
 *  &lt;/xs:restriction>
 *  &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum AfterBeforeEnum {

    BEFORE("Before"),
    AFTER("After");

    private final  String beforeAfter;

    /**
     * 
     * @param beforeAfter
     */
    private AfterBeforeEnum(String beforeAfter){
        this.beforeAfter = beforeAfter;
    }

    /**
     *
     * @return
     */
    public String getAfterBeforeEnum(){
        return this.beforeAfter;
    }

    /**
     *
     * @param beforeAfter
     * @return The AfterBeforeEnum instance corresponding to the beforeAfter parameter.
     */
    public static AfterBeforeEnum transform(String beforeAfter){
        return transform(beforeAfter, null);
    }

    /**
     *
     * @param beforeAfter
     * @param defaultValue The default value to return if beforeAfter String parameter
     * do not correspond to one AfterBeforeEnum instance.
     * @return The AfterBeforeEnum instance corresponding to the beforeAfter parameter.
     */
    public static AfterBeforeEnum transform(String beforeAfter, AfterBeforeEnum defaultValue){
        for(AfterBeforeEnum ba : AfterBeforeEnum.values()){
            if(ba.getAfterBeforeEnum().equals(beforeAfter)) return ba;
        }
        return defaultValue;
    }
}
