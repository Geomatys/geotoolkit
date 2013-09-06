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
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class QUALIN01 extends Procedure{

    private static final ComplexLine LOWACC21 = new ComplexLine("LOWACC21");
    private static final SimpleLine SIMPLE = new SimpleLine(SimpleLine.PStyle.SOLD,1,"CSTLN");
    private static final SimpleLine LOWACC = new SimpleLine(SimpleLine.PStyle.SOLD,3,"CHMGF");

    public QUALIN01() {
        super("QUALIN01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Feature feature = (Feature) graphic.getCandidate();

        //TODO for each spatial component
        final Object value = (feature.getProperty("QUAPOS")==null) ? null : feature.getProperty("QUAPOS").getValue();

        if(value != null){
            int val = Integer.valueOf(value.toString());
            if(val > 1 && val < 10){
                LOWACC21.render(ctx, context, colorTable, graphic, geotype);
                //TODO continue loop
                return;
            }
        }

        final String type = S52Utilities.getObjClass(feature);
        if("COALNE".equals(type)){
            final Object conrad = feature.getProperty("CONRAD").getValue();
            if(conrad != null){
                if("1".equals(conrad)){
                    LOWACC.render(ctx, context, colorTable, graphic, geotype);
                    SIMPLE.render(ctx, context, colorTable, graphic, geotype);
                }else{
                    SIMPLE.render(ctx, context, colorTable, graphic, geotype);
                }
            }else{
                SIMPLE.render(ctx, context, colorTable, graphic, geotype);
            }

        }else if("LNDARE".equals(type)){
            SIMPLE.render(ctx, context, colorTable, graphic, geotype);
        }else{
            throw new PortrayalException("Unexpected object class for this procedure(QUALIN01) : "+type);
        }

    }

}
