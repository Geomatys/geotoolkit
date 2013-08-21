/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.dai;

/**
 * Attribute Combination.
 * Describes individual characteristicsof an object which lead to the 
 * presentation described in theINST‑field.
 * The attributes of the object catalogueshall be used.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class AttributeCombination extends DAIField{

    /** A(6) : 6 Character Attribute Code. */
    public String ATTL;	
    /** A(1/15) : Attribute Value; Shall be a valid value for the domain 
     * specified  by the attribute label in ATTL. */
    public String ATTV;
    
    public AttributeCombination() {
        super("ATTC");
    }

    @Override
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
