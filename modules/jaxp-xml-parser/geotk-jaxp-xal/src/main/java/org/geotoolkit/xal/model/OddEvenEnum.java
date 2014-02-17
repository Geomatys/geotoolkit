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
 * <p>This enumeration maps oddEven element.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;s:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="Odd"/>
 *      &lt;xs:enumeration value="Even"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum OddEvenEnum {

    ODD("Odd"),
    EVEN("Even");

    private String oddEven;

    /**
     * 
     * @param oddEven
     */
    private OddEvenEnum(String oddEven){
        this.oddEven = oddEven;
    }

    /**
     *
     * @return
     */
    public String getOddEven(){
        return this.oddEven;
    }

    /**
     *
     * @param oddEven
     * @return The OddEvenEnum instance corresponding to the oddEven parameter.
     */
    public static OddEvenEnum transform(String oddEven){
        return transform(oddEven, null);
    }

    /**
     *
     * @param oddEven
     * @param defaultValue The default value to return if oddEven String parameter
     * do not correspond to one OddEvenEnum instance.
     * @return The OddEvenEnum instance corresponding to the oddEven parameter.
     */
    public static OddEvenEnum transform(String oddEven, OddEvenEnum defaultValue){
        OddEvenEnum resultat = defaultValue;
        for(OddEvenEnum rt : OddEvenEnum.values()){
            if(rt.getOddEven().equals(oddEven)){
                resultat = rt;
                break;
            }
        }
        return resultat;
    }
}
