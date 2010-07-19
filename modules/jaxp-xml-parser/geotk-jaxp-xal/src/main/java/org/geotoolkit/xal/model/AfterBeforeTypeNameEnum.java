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
 *
 * <p>23-25 Archer St, where number appears before name.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="BeforeName"/>
 *      &lt;xs:enumeration value="AfterName"/>
 *      &lt;xs:enumeration value="BeforeType"/>
 *      &lt;xs:enumeration value="AfterType"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public enum AfterBeforeTypeNameEnum {

    BEFORE_NAME("BeforeName"),
    AFTER_NAME("AfterName"),
    BEFORE_TYPE("BeforeType"),
    AFTER_TYPE("AfterType");

    private final String afterBeforTypeName;

    /**
     * 
     * @param afterBeforTypeName
     */
    private AfterBeforeTypeNameEnum(String afterBeforTypeName){
        this.afterBeforTypeName = afterBeforTypeName;
    }

    /**
     *
     * @return
     */
    public String getAfterBeforeTypeEnum(){
        return this.afterBeforTypeName;
    }

    /**
     *
     * @param afterBeforTypeName
     * @return The AfterBeforeTypeNameEnum instance corresponding to the afterBeforTypeName parameter.
     */
    public static AfterBeforeTypeNameEnum transform(String afterBeforTypeName){
        return transform(afterBeforTypeName, null);
    }

    /**
     *
     * @param afterBeforTypeName
     * @param defaultValue The default value to return if afterBeforTypeName String parameter
     * do not correspond to one AfterBeforeTypeNameEnum instance.
     * @return The AfterBeforeTypeNameEnum instance corresponding to the afterBeforTypeName parameter.
     */
    public static AfterBeforeTypeNameEnum transform(String afterBeforTypeName, AfterBeforeTypeNameEnum defaultValue){
        for(AfterBeforeTypeNameEnum io : AfterBeforeTypeNameEnum.values()){
            if(io.getAfterBeforeTypeEnum().equals(afterBeforTypeName)) return io;
        }
        return defaultValue;
    }
}
