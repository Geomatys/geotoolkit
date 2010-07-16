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
package org.geotoolkit.data.xal.model;

/**
 *
 * <p>Building 12-14 is "Range" and Building 12 is "Single"</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;xs:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="Single"/>
 *      &lt;xs:enumeration value="Range"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 *</pre>
 *
 * @author Samuel Andrés
 */
public enum SingleRangeEnum {

    SINGLE("Single"),
    RANGE("Range");

    private String singleRange;

    /**
     * 
     * @param singleRange
     */
    private SingleRangeEnum(String singleRange){
        this.singleRange = singleRange;
    }

    /**
     *
     * @return
     */
    public String getSingleRange(){
        return this.singleRange;
    }

    /**
     *
     * @param singleRange
     * @return The SingleRangeEnum instance corresponding to the singleRange parameter.
     */
    public static SingleRangeEnum transform(String singleRange){
        return transform(singleRange, null);
    }

    /**
     *
     * @param singleRange
     * @param defaultValue The default value to return if singleRange String parameter
     * do not correspond to one SingleRangeEnum instance.
     * @return The SingleRangeEnum instance corresponding to the singleRange parameter.
     */
    public static SingleRangeEnum transform(String singleRange, SingleRangeEnum defaultValue){
        SingleRangeEnum resultat = defaultValue;
        for(SingleRangeEnum nt : SingleRangeEnum.values()){
            if(nt.getSingleRange().equals(singleRange)){
                resultat = nt;
                break;
            }
        }
        return resultat;
    }
}
