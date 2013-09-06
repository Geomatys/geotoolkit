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

import java.awt.Graphics2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.opengis.feature.Feature;

/**
 * S-52 Annex A Part I p.152 (12.2.4)
 *
 * @author Johann Sorel (Geomatys)
 */
public class DEPCNT03 extends Procedure{

    private static final SimpleLine NOSET = new SimpleLine(SimpleLine.PStyle.SOLD, 1, "DEPCN");
    private static final SimpleLine SET = new SimpleLine(SimpleLine.PStyle.DASH, 1, "DEPCN");


    public DEPCNT03() {
        super("DEPCNT03");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();
        final Feature feature = (Feature) graphic.getCandidate();

        //TODO for each spatial component
        final Object value = (feature.getProperty("QUAPOS")==null) ? null : feature.getProperty("QUAPOS").getValue();

        if(value != null){
            final int val = Integer.valueOf(value.toString());
            if(val > 1 && val < 10){
                SET.render(ctx, context, colorTable, graphic, geotype);
            }else{
                NOSET.render(ctx, context, colorTable, graphic, geotype);
            }
        }else{
            NOSET.render(ctx, context, colorTable, graphic, geotype);
        }

        if(context.isContourLabels()){
            Double valdco = (Double) ((feature.getProperty("VALDCO")==null) ? 0.0 : feature.getProperty("VALDCO").getValue());
            if(valdco == null){
                valdco = 0.0d;
            }

            final SAFCON01 safcon01 = (SAFCON01) context.getProcedure("SAFCON01");
            final Symbol[] symbols = safcon01.eval(ctx, context, colorTable, graphic, geotype, valdco);
            for(Symbol s : symbols){
                s.render(ctx, context, colorTable, graphic, geotype);
            }
        }

    }

}
