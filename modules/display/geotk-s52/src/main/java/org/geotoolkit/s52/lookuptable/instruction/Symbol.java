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
 * S-52 Annex A Part I p.52  7.2
 *
 * @author Johann Sorel (Geomatys)
 */
public class Symbol extends Instruction{

    public Symbol() {
        super("SY");
    }

    /**
     * The symbol name is an 8 letter‑code that is composed of a class code
     * (6 letters) and a serial number (2 letters).
     */
    public String symbolName;

    /**
     * .2.1 Symbols with no rotation should always be drawn upright with respect to the screen.
     * .2.2 Symbols with a rotation instruction should be rotated with respect to the
     *      top of the screen (-y axis in figure 2 of section 5.1). (See example below).
     * .2.3 Symbols rotated by means of the six-character code of an S-57 attribute
     *      such as ORIENT should be rotated with respect to true north.
     * .2.4 The symbol should be rotated about its pivot point. Rotation angle is
     *      in degrees clockwise from 0 to 360. The default value is 0 degrees."
     */
    public String rotation;

    @Override
    protected void readParameters(String str) throws IOException {
        final String[] parts = str.split(",");
        symbolName = parts[0];
        if(parts.length>1){
            rotation = parts[1];
        }else{
            rotation = null;
        }
    }

}
