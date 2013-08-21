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

import java.util.List;

/**
 * Contains the color reference for the bitmap or vector field.
 * @author Johann Sorel (Geomatys)
 */
public class ColorReference extends DAIField{

    /** *A(1) : Letter (ASCII >= 64) used as color index within
     * PBTM.BITR field or within the PBTM.VECT field. */
    public List<String> CIDX;
    /** A(5) : color token which is identified by the letter in CIDX. */
    public List<String> CTOK;

    public ColorReference(String code) {
        super(code);
    }

    @Override
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
