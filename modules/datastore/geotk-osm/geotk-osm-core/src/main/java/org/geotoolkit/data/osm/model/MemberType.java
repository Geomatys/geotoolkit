/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.osm.model;


/**
 * The different relation members types from the OSM model.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public enum MemberType {
	BOUND('B'),
	NODE('N'),
	WAY('W'),
	RELATION('R');

    final char c;

    private MemberType(char c){
        this.c = c;
    }

    public char charValue(){
        return c;
    }

    public static MemberType valueOfIgnoreCase(String str) {
        if("bound".equalsIgnoreCase(str)){
            return BOUND;
        }else if("node".equalsIgnoreCase(str)){
            return NODE;
        }else if("way".equalsIgnoreCase(str)){
            return WAY;
        }else if("relation".equalsIgnoreCase(str)){
            return RELATION;
        }

        throw new IllegalArgumentException("Unexpected member type : "+ str);
    }

}
