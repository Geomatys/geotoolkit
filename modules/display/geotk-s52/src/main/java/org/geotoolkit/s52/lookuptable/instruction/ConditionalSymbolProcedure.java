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
 * S-52 Annex A Part I p.63  7.5.2
 *
 * CS ( PROCNAME )
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConditionalSymbolProcedure extends Instruction{

    /**
     * Conditional symbology procedures are named by the object class that is
     * interpreted by the procedure. The name is an 8 letter‑code that is
     * composed of the class code (6 letters) and a serial number (2 letters).
     */
    public String procedureName;

    public ConditionalSymbolProcedure() {
        super("CS");
    }

    @Override
    protected void readParameters(String str) throws IOException {
        procedureName = str;
    }

}
