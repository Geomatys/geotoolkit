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
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.procedure.Procedure;
import org.geotoolkit.s52.symbolizer.S52Graphic;

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

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException {

        final Procedure proc = context.getProcedure(procedureName);
        proc.render(ctx, context, colorTable, all, s52graphic);
    }

}
