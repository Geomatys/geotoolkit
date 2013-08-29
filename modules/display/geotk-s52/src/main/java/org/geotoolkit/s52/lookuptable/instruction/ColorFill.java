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
package org.geotoolkit.s52.lookuptable.instruction;

import java.io.IOException;

/**
 * S-52 Annex A Part I p.61  7.4.7
 *
 * @author Johann Sorel (Geomatys)
 */
public class ColorFill extends Instruction{

    public ColorFill() {
        super("AC");
    }

    /**
     * Color token as described in section 4 and 13.
     */
    public String color;

    /**
     * 0 opaque (= default value)
     * 1 25 % (3 of 4 pixels use COLOUR, 1 uses TRNSP)
     * 2 50 % (2 of 4 pixels use COLOUR, 2 use TRNSP)
     * 3 75 % (1 of 4 pixels use COLOUR, 3 use TRNSP)
     * Note: the TRANSPARENCY parameter is an optional part of the colour fill command;
     * if it is not included, the command defaults to opaque fill.
     */
    public int transparency;

    public float getAlpha(){
        switch(transparency){
            case 1 : return 0.25f;
            case 2 : return 0.5f;
            case 3 : return 0.75f;
            default : return 1f;
        }
    }

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        color = parts[0];
        if(parts.length>1){
            transparency = Integer.valueOf(parts[1]);
        }else{
            transparency = 0;
        }
    }

}
