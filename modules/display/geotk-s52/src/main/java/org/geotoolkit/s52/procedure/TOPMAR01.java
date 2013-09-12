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
 * S-52 Annex A Part I p.212 (12.2.23)
 *
 * @author Johann Sorel (Geomatys)
 */
public class TOPMAR01 extends Procedure{

    private static final Symbol SY_QUESMRK1 = new Symbol("QUESMRK1", null);

    private static final Symbol SY_TOPMAR02 = new Symbol("TOPMAR02", null);
    private static final Symbol SY_TOPMAR04 = new Symbol("TOPMAR04", null);
    private static final Symbol SY_TOPMAR05 = new Symbol("TOPMAR05", null);
    private static final Symbol SY_TOPMAR06 = new Symbol("TOPMAR06", null);
    private static final Symbol SY_TOPMAR07 = new Symbol("TOPMAR07", null);
    private static final Symbol SY_TOPMAR08 = new Symbol("TOPMAR08", null);
    private static final Symbol SY_TOPMAR10 = new Symbol("TOPMAR10", null);
    private static final Symbol SY_TOPMAR12 = new Symbol("TOPMAR12", null);
    private static final Symbol SY_TOPMAR13 = new Symbol("TOPMAR13", null);
    private static final Symbol SY_TOPMAR14 = new Symbol("TOPMAR14", null);
    private static final Symbol SY_TOPMAR16 = new Symbol("TOPMAR16", null);
    private static final Symbol SY_TOPMAR17 = new Symbol("TOPMAR17", null);
    private static final Symbol SY_TOPMAR18 = new Symbol("TOPMAR18", null);
    private static final Symbol SY_TOPMAR22 = new Symbol("TOPMAR22", null);
    private static final Symbol SY_TOPMAR24 = new Symbol("TOPMAR24", null);
    private static final Symbol SY_TOPMAR25 = new Symbol("TOPMAR25", null);
    private static final Symbol SY_TOPMAR26 = new Symbol("TOPMAR26", null);
    private static final Symbol SY_TOPMAR27 = new Symbol("TOPMAR27", null);
    private static final Symbol SY_TOPMAR28 = new Symbol("TOPMAR28", null);
    private static final Symbol SY_TOPMAR30 = new Symbol("TOPMAR30", null);
    private static final Symbol SY_TOPMAR32 = new Symbol("TOPMAR32", null);
    private static final Symbol SY_TOPMAR33 = new Symbol("TOPMAR33", null);
    private static final Symbol SY_TOPMAR34 = new Symbol("TOPMAR34", null);
    private static final Symbol SY_TOPMAR36 = new Symbol("TOPMAR36", null);
    private static final Symbol SY_TOPMAR65 = new Symbol("TOPMAR65", null);
    private static final Symbol SY_TOPMAR85 = new Symbol("TOPMAR85", null);
    private static final Symbol SY_TOPMAR86 = new Symbol("TOPMAR86", null);
    private static final Symbol SY_TOPMAR87 = new Symbol("TOPMAR87", null);
    private static final Symbol SY_TOPMAR88 = new Symbol("TOPMAR88", null);
    private static final Symbol SY_TOPMAR89 = new Symbol("TOPMAR89", null);

    private static final Symbol SY_TMARDEF1 = new Symbol("TMARDEF1", null);
    private static final Symbol SY_TMARDEF2 = new Symbol("TMARDEF2", null);

    public TOPMAR01() {
        super("TOPMAR01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        final String topshp = (String) graphic.feature.getProperty("TOPSHP").getValue();

        if(topshp !=null){
            final int itopshp = Integer.valueOf(topshp);
            boolean floating = false;
            final Symbol ss;
            if(floating){
                //object is a buoy, light float or light vessel
                switch(itopshp){
                    case 1 : ss = SY_TOPMAR02; break;
                    case 2 : ss = SY_TOPMAR04; break;
                    case 3 : ss = SY_TOPMAR10; break;
                    case 4 : ss = SY_TOPMAR12; break;

                    case 5 : ss = SY_TOPMAR13; break;
                    case 6 : ss = SY_TOPMAR14; break;
                    case 7 : ss = SY_TOPMAR65; break;
                    case 8 : ss = SY_TOPMAR17; break;

                    case 9 : ss = SY_TOPMAR16; break;
                    case 10 : ss = SY_TOPMAR08; break;
                    case 11 : ss = SY_TOPMAR07; break;
                    case 12 : ss = SY_TOPMAR14; break;

                    case 13 : ss = SY_TOPMAR05; break;
                    case 14 : ss = SY_TOPMAR06; break;
                    case 17 : ss = SY_TMARDEF2; break;
                    case 18 : ss = SY_TOPMAR10; break;

                    case 19 : ss = SY_TOPMAR13; break;
                    case 20 : ss = SY_TOPMAR14; break;
                    case 21 : ss = SY_TOPMAR13; break;
                    case 22 : ss = SY_TOPMAR14; break;

                    case 23 : ss = SY_TOPMAR14; break;
                    case 24 : ss = SY_TOPMAR02; break;
                    case 25 : ss = SY_TOPMAR04; break;
                    case 26 : ss = SY_TOPMAR10; break;

                    case 27 : ss = SY_TOPMAR17; break;
                    case 28 : ss = SY_TOPMAR18; break;
                    case 29 : ss = SY_TOPMAR02; break;
                    case 30 : ss = SY_TOPMAR17; break;

                    case 31 : ss = SY_TOPMAR14; break;
                    case 32 : ss = SY_TOPMAR10; break;
                    case 33 : ss = SY_TMARDEF2; break;

                    default : ss = SY_TMARDEF2; break;
                }
            }else{
                //object is a beacon, daymark
                switch(itopshp){
                    case 1 : ss = SY_TOPMAR22; break;
                    case 2 : ss = SY_TOPMAR24; break;
                    case 3 : ss = SY_TOPMAR30; break;
                    case 4 : ss = SY_TOPMAR32; break;

                    case 5 : ss = SY_TOPMAR33; break;
                    case 6 : ss = SY_TOPMAR34; break;
                    case 7 : ss = SY_TOPMAR85; break;
                    case 8 : ss = SY_TOPMAR86; break;

                    case 9 : ss = SY_TOPMAR36; break;
                    case 10 : ss = SY_TOPMAR28; break;
                    case 11 : ss = SY_TOPMAR27; break;
                    case 12 : ss = SY_TOPMAR14; break;

                    case 13 : ss = SY_TOPMAR25; break;
                    case 14 : ss = SY_TOPMAR26; break;
                    case 15 : ss = SY_TOPMAR88; break;
                    case 16 : ss = SY_TOPMAR87; break;

                    case 17 : ss = SY_TMARDEF1; break;
                    case 18 : ss = SY_TOPMAR30; break;
                    case 19 : ss = SY_TOPMAR33; break;
                    case 20 : ss = SY_TOPMAR34; break;

                    case 21 : ss = SY_TOPMAR33; break;
                    case 22 : ss = SY_TOPMAR34; break;
                    case 23 : ss = SY_TOPMAR34; break;
                    case 24 : ss = SY_TOPMAR22; break;

                    case 25 : ss = SY_TOPMAR24; break;
                    case 26 : ss = SY_TOPMAR30; break;
                    case 27 : ss = SY_TOPMAR86; break;
                    case 28 : ss = SY_TOPMAR89; break;

                    case 29 : ss = SY_TOPMAR22; break;
                    case 30 : ss = SY_TOPMAR86; break;
                    case 31 : ss = SY_TOPMAR14; break;
                    case 32 : ss = SY_TOPMAR30; break;
                    case 33 : ss = SY_TMARDEF1; break;

                    default : ss = SY_TMARDEF1; break;
                }
            }

            ss.render(ctx, context, colorTable, all, graphic);

        }else{
            SY_QUESMRK1.render(ctx, context, colorTable, all, graphic);
        }

    }

}
