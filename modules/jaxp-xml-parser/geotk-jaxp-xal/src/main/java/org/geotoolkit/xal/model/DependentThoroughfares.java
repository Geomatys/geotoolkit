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
 * <p>This enumeration maps dependentThoroughfares attribute.</p>
 *
 * <p>Does this thoroughfare have a a dependent thoroughfare? Corner of street X, etc.</p>
 *
 * <pre>
 * &lt;xs:simpleType>
 *  &lt;xs:restriction base="xs:NMTOKEN">
 *      &lt;xs:enumeration value="Yes"/>
 *      &lt;xs:enumeration value="No"/>
 *  &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public enum DependentThoroughfares {

    YES("Yes"),
    NO("No");

    private String dependentThoroughfares;

    /**
     * 
     * @param dependentThoroughfares
     */
    private DependentThoroughfares(String dependentThoroughfares){
        this.dependentThoroughfares = dependentThoroughfares;
    }

    /**
     *
     * @return
     */
    public String getDependentThoroughfares(){
        return this.dependentThoroughfares;
    }

    /**
     *
     * @param dependentThoroughfares
     * @return The DependentThoroughfares instance corresponding to the dependentThoroughfares parameter.
     */
    public static DependentThoroughfares transform(String dependentThoroughfares){
        return transform(dependentThoroughfares, null);
    }

    /**
     *
     * @param dependentThoroughfares
     * @param defaultValue The default value to return if dependentThoroughfares String parameter
     * do not correspond to one DependentThoroughfares instance.
     * @return The DependentThoroughfares instance corresponding to the dependentThoroughfares parameter.
     */
    public static DependentThoroughfares transform(String dependentThoroughfares, DependentThoroughfares defaultValue){
        for(DependentThoroughfares dtf : DependentThoroughfares.values()){
            if(dtf.getDependentThoroughfares().equals(dependentThoroughfares)){
                return dtf;
            }
        }
        return defaultValue;
    }
}
