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
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.render.SymbolStyle;
import org.opengis.feature.Feature;

/**
 * S-52 Annex A Part I p.146 (12.2.3)
 *
 * @author Johann Sorel (Geomatys)
 */
public class DEPARE02 extends Procedure{

    public DEPARE02() {
        super("DEPARE02");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();
        final Feature feature = (Feature) graphic.getCandidate();

        Number drval1 = (Number) feature.getProperty("DRVAL1").getValue();
        Number drval2 = (Number) feature.getProperty("DRVAL2").getValue();

        if(drval1 == null){
            drval1 = -1;
        }
        if(drval2 == null){
            drval2 = drval1.doubleValue() + 0.01;
        }

        final SEABED01 seabed = new SEABED01();
        seabed.render(ctx, context, colorTable, graphic,drval1.doubleValue(),drval2.doubleValue());

        final String objClassCode = S52Utilities.getObjClass(feature);
        if("DRGARE".equals(objClassCode)){
            final PatternFill pf = new PatternFill();
            pf.patternName = "DRGARE01";
            pf.rotation = "0";
            pf.render(ctx, context, colorTable, graphic, null);
            final SimpleLine sl = new SimpleLine();
            sl.color = "CHGRF";
            sl.style = SimpleLine.PStyle.DASH;
            sl.width = 1;
            sl.render(ctx, context, colorTable, graphic, null);

            final String[] restrn = (String[]) feature.getProperty("RESTRN").getValue();
            if(restrn != null){
                final RESCSP02 rescsp02 = new RESCSP02();
                rescsp02.render(ctx, context, colorTable, graphic, geotype, restrn);
            }
        }

        //TODO continuation A
        //TODO continuation B

    }

}
