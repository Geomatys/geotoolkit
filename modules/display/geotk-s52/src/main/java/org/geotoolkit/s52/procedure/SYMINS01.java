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
package org.geotoolkit.s52.procedure;

import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.TxtLookupRecord;
import org.geotoolkit.s52.lookuptable.instruction.ConditionalSymbolProcedure;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.210 (12.2.22b)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SYMINS01 extends Procedure{

    public SYMINS01() {
        super("SYMINS01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        final Object value = (graphic.feature.getProperty("SYMINS")==null) ? null : graphic.feature.getProperty("SYMINS").getValue();

        if(value !=null){
            final LookupRecord rec = new TxtLookupRecord("","",value.toString());
            for(Instruction inst : rec.getInstruction()){
                if(inst instanceof ConditionalSymbolProcedure){
                    if( getName().equals( ((ConditionalSymbolProcedure)inst).procedureName) ){
                        //avoid recursive loops
                        continue;
                    }
                }
                inst.render(ctx, context, colorTable, all, graphic);
            }
        }

    }

}
