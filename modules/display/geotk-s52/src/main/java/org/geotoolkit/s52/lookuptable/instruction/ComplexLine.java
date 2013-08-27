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
 * S-52 Annex A part I 7.3.5 p.55
 * LC(LINNAME)
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexLine extends Instruction{

    /**
     * The line-style name is an 8 letter‑code that is composed from
     * an object class code and a serial number (2 letters).
     */
    public String LINNAME;

    public ComplexLine() {
        super("LC");
    }

    @Override
    protected void readParameters(String str) throws IOException {
        LINNAME = str.trim();
    }

}
