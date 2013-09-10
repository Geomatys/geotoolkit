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
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class QUAPNT02 extends Procedure{

    public QUAPNT02() {
        super("QUAPNT02");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    /**
     * returns
     * [0] boolean : flag indicating whether or not to display the low accuracy symbol.
     * [1] Symbol : selected symbol
     *
     * @param ctx
     * @param context
     * @param colorTable
     * @param graphic
     * @return
     */
    public Object[] eval(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        boolean accurate = true;
        Symbol symbol = null;

        if(context.isLowAccuracySymbols()){
            //TODO for each spatial component
            final Object value = (graphic.feature.getProperty("QUAPOS")==null) ? null : graphic.feature.getProperty("QUAPOS").getValue();
            if(value != null){
                int val = Integer.valueOf(value.toString());
                if(val > 1 && val < 10){
                    accurate = false;
                    //TODO break spatial loop
                }
            }
        }

        if(!accurate){
            symbol = new Symbol("LOWACC01", null);
        }

        return new Object[]{!accurate,symbol};
    }

}
