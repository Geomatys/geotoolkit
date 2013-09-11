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
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.180 (12.2.15)
 *
 * @author Johann Sorel (Geomatys)
 */
public class RESARE03 extends Procedure{

    private static final Symbol SY_INFARE51 = new Symbol("INFARE51", null);

    private static final Symbol SY_RSRDEF51 = new Symbol("RSRDEF51", null);

    private static final Symbol SY_ENTRES51 = new Symbol("ENTRES51", null);
    private static final Symbol SY_ENTRES61 = new Symbol("ENTRES61", null);
    private static final Symbol SY_ENTRES71 = new Symbol("ENTRES71", null);

    private static final Symbol SY_ACHRES51 = new Symbol("ACHRES51", null);
    private static final Symbol SY_ACHRES61 = new Symbol("ACHRES61", null);
    private static final Symbol SY_ACHRES71 = new Symbol("ACHRES71", null);

    private static final Symbol SY_FSHRES51 = new Symbol("FSHRES51", null);
    private static final Symbol SY_FSHRES61 = new Symbol("FSHRES61", null);
    private static final Symbol SY_FSHRES71 = new Symbol("FSHRES71", null);

    private static final Symbol SY_CTYARE51 = new Symbol("CTYARE51", null);
    private static final Symbol SY_CTYARE71 = new Symbol("CTYARE71", null);

    private static final ComplexLine LC_ACHRES51 = new ComplexLine("ACHRES51");
    private static final ComplexLine LC_CTYARE51 = new ComplexLine("CTYARE51");
    private static final ComplexLine LC_FSHRES51 = new ComplexLine("FSHRES51");
    private static final SimpleLine LS_DASH_2_CHMGD = new SimpleLine(SimpleLine.PStyle.DASH,2,"CHMGD");

    public RESARE03() {
        super("RESARE03");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        final boolean areaboundaries = context.getActiveAreaTable().equals(S52Context.LKN_AREA_SYMBOLIZED);

        final String[] restrn = (String[]) graphic.feature.getProperty("RESTRN").getValue();
        final String[] catrea = (String[]) graphic.feature.getProperty("CATREA").getValue();

        final Symbol areaCenteredSymbol;
        final Instruction areaBorder;

        if(restrn!=null){
            if(S52Utilities.containsAny(restrn, "7","8","14")){
                // Continuation A
                if(S52Utilities.containsAny(restrn, "1","2","3","4","5","6","13","16","17","23","24","25","26","27")){
                    areaCenteredSymbol = SY_ENTRES61;
                }else if(S52Utilities.containsAny(catrea, "1","8","9","12","14","18","19","21","24","25","26")){
                    areaCenteredSymbol = SY_ENTRES61;
                }else if(S52Utilities.containsAny(restrn, "9","10","11","12","15","18","19","20","21","22")){
                    areaCenteredSymbol = SY_ENTRES71;
                }else if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                    areaCenteredSymbol = SY_ENTRES71;
                }else{
                    areaCenteredSymbol = SY_ENTRES51;
                }
                graphic.priority = 6;
                areaBorder = areaboundaries ? LC_CTYARE51 : LS_DASH_2_CHMGD;

            }else if(S52Utilities.containsAny(restrn, "1","2")){
                // Continuation B
                if(S52Utilities.containsAny(restrn, "3","4","5","6","13","16","17","23","24","25","26","27")){
                    areaCenteredSymbol = SY_ACHRES61;
                }else if(S52Utilities.containsAny(catrea, "1","8","9","12","14","18","19","21","24","25","26")){
                    areaCenteredSymbol = SY_ACHRES61;
                }else if(S52Utilities.containsAny(restrn, "9","10","11","12","15","18","19","20","21","22")){
                    areaCenteredSymbol = SY_ACHRES71;
                }else if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                    areaCenteredSymbol = SY_ACHRES71;
                }else{
                    areaCenteredSymbol = SY_ACHRES51;
                }
                graphic.priority = 6;
                areaBorder = areaboundaries ? LC_ACHRES51 : LS_DASH_2_CHMGD;


            }else if(S52Utilities.containsAny(restrn, "3","4","5","6","24")){
                // Continuation C
                if(S52Utilities.containsAny(restrn, "13","16","17","23","24","25","26","27")){
                    areaCenteredSymbol = SY_FSHRES61;
                }else if(S52Utilities.containsAny(catrea, "1","8","9","12","14","18","19","21","24","25","26")){
                    areaCenteredSymbol = SY_FSHRES61;
                }else if(S52Utilities.containsAny(restrn, "9","10","11","12","15","18","19","20","21","22")){
                    areaCenteredSymbol = SY_FSHRES71;
                }else if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                    areaCenteredSymbol = SY_FSHRES71;
                }else{
                    areaCenteredSymbol = SY_FSHRES51;
                }
                graphic.priority = 6;
                areaBorder = areaboundaries ? LC_FSHRES51 : LS_DASH_2_CHMGD;

            }else if(S52Utilities.containsAny(restrn, "13","16","17","23","25","26","27")){
                // Continuation D
                if(S52Utilities.containsAny(restrn, "9","10","11","12","15","18","19","20","21","22")){
                    areaCenteredSymbol = SY_CTYARE71;
                }else if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                    areaCenteredSymbol = SY_CTYARE71;
                }else{
                    areaCenteredSymbol = SY_CTYARE51;
                }

                graphic.priority = 6;
                areaBorder = areaboundaries ? LC_CTYARE51 : LS_DASH_2_CHMGD;

            }else if(S52Utilities.containsAny(restrn, "9","10","11","12","15","18","19","20","21","22")){
                areaCenteredSymbol = SY_INFARE51;
                areaBorder = areaboundaries ? LC_CTYARE51 : LS_DASH_2_CHMGD;
            }else{
                areaCenteredSymbol = SY_RSRDEF51;
                areaBorder = areaboundaries ? LC_CTYARE51 : LS_DASH_2_CHMGD;
            }

        }else{
            // Continuation E
            if(catrea != null){
                if(S52Utilities.containsAny(catrea, "1","8","9","12","14","18","19","21","24","25","26")){
                    if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                        areaCenteredSymbol = SY_CTYARE71;
                    }else{
                        areaCenteredSymbol = SY_CTYARE51;
                    }
                }else{
                    if(S52Utilities.containsAny(catrea, "4","5","6","7","10","20","22","23")){
                        areaCenteredSymbol = SY_INFARE51;
                    }else{
                        areaCenteredSymbol = SY_RSRDEF51;
                    }
                }
            }else{
                areaCenteredSymbol = SY_RSRDEF51;
            }

            areaBorder = areaboundaries ? LC_CTYARE51 : LS_DASH_2_CHMGD;
        }

        areaCenteredSymbol.render(ctx, context, colorTable, all, graphic);
        areaBorder.render(ctx, context, colorTable, all, graphic);
    }

}
