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

import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.instruction.ColorFill;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;

/**
 * S-52 Annex A Part I p.201 (12.2.20)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SEABED01 extends Procedure{

    private static final PatternFill SHALLOW_PATTERN = new PatternFill("DIAMOND1");

    public SEABED01() {
        super("SEABED01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            ProjectedObject feature, S52Context.GeoType geotype, double drval1, double drval2) throws PortrayalException {

        final boolean twocolorshades = context.isTwoShades();
        final double safetyContour = context.getSafetyContour();
        final double shallowContour = context.getShallowContour();
        final double deepContour = context.getDeepContour();

        String color = "DEPIT";
        boolean shallow = true;

        if(twocolorshades){
            if(drval1 >= 0 && drval2 > 0){
                color = "DEPVS";
            }
            if(drval1 >= safetyContour && drval2 > safetyContour){
                color = "DEPDW";
                shallow = false;
            }

        }else{
            if(drval1 >= 0 && drval2 > 0){
                color = "DEPVS";
            }
            if(drval1 >= shallowContour && drval2 > shallowContour){
                color = "DEPMS";
            }
            if(drval1 >= safetyContour && drval2 > safetyContour){
                color = "DEPMD";
                shallow = false;
            }
            if(drval1 >= deepContour && drval2 > deepContour){
                color = "DEPDW";
                shallow = false;
            }
        }

        //draw fill
        final ColorFill cf = new ColorFill(color);
        cf.render(ctx, context, colorTable, feature, geotype);

        if(shallow && context.isShallowPattern()){
            //draw pattern
            SHALLOW_PATTERN.render(ctx, context, colorTable, feature, geotype);
        }

    }

}
