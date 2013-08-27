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
 * S-52 Annex A Part I p.62  7.4.8
 *
 * LS ( PATNAME [,ROTATION] )
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternFill extends Instruction{

    public PatternFill() {
        super("AP");
    }

    /**
     * The pattern symbol name is an 8 letter‑code which is composed
     * of a class code (6 letters) and a serial number (2 letters).
     */
    public String patternName;

    /**
     * 0 to 360 nautical degrees (clockwise, starting North);
     * default: 0 degree;
     *
     * Note: the ROTATION parameter is optional; if a raster symbol is called
     * the ROTATION parameter is ignored; the six character code of
     * an S-57 attribute can be passed as ROTATION parameter.
     *
     * The rotation function would operate on individual symbols of the pattern
     * and not on the pattern as a whole. It is not in use at present.
     */
    public String rotation;

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        patternName = parts[0];
        if(parts.length>1){
            rotation = parts[1];
        }else{
            rotation = null;
        }
    }

}
