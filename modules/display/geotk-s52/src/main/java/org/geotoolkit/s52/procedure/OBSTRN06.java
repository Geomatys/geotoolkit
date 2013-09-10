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
import org.geotoolkit.s52.lookuptable.instruction.ColorFill;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.feature.Feature;

/**
 * S-52 Annex A Part I p.163 (12.2.9)
 *
 * @author Johann Sorel (Geomatys)
 */
public class OBSTRN06 extends Procedure{

    private static final Symbol SY_DANGER01 = new Symbol("DANGER01", null);
    private static final Symbol SY_DANGER02 = new Symbol("DANGER02", null);
    private static final Symbol SY_DANGER03 = new Symbol("DANGER03", null);

    private static final Symbol SY_OBSTRN01 = new Symbol("OBSTRN01", null);
    private static final Symbol SY_OBSTRN03 = new Symbol("OBSTRN03", null);
    private static final Symbol SY_OBSTRN11 = new Symbol("OBSTRN11", null);
    private static final Symbol SY_UWTROC03 = new Symbol("UWTROC03", null);
    private static final Symbol SY_UWTROC04 = new Symbol("UWTROC04", null);

    private static final ComplexLine LC_LOWACC = new ComplexLine("LOWACC41");
    private static final SimpleLine LS_DOTT_CHBLK = new SimpleLine(SimpleLine.PStyle.DOTT, 2, "CHBLK");
    private static final SimpleLine LS_DASH_CHBLK = new SimpleLine(SimpleLine.PStyle.DASH, 2, "CHBLK");
    private static final SimpleLine LS_DASH_CHGRD = new SimpleLine(SimpleLine.PStyle.DASH, 2, "CHGRD");
    private static final SimpleLine LS_SOLD_CSTLN = new SimpleLine(SimpleLine.PStyle.SOLD, 2, "CSTLN");
    private static final SimpleLine LS_DASH_CSTLN = new SimpleLine(SimpleLine.PStyle.DASH, 2, "CSTLN");

    private static final ColorFill CF_DEPVS = new ColorFill("DEPVS");
    private static final ColorFill CF_CHBRN = new ColorFill("CHBRN");
    private static final ColorFill CF_DEPIT = new ColorFill("DEPIT");
    private static final PatternFill PF_FOULAR01 = new PatternFill("FOULAR01");

    public OBSTRN06() {
        super("OBSTRN06");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {

        final Number valsou = (Number) graphic.feature.getProperty("VALSOU").getValue();
        final String watlev = (String) graphic.feature.getProperty("WATLEV").getValue();
        final String expsou = (String) graphic.feature.getProperty("EXPSOU").getValue();
        final String catobs = (String) ((graphic.feature.getProperty("CATOBS")==null) ? null : graphic.feature.getProperty("CATOBS").getValue());

        double depthval;
        int viewinggroup;
        Symbol[] soundingSymbols = null;

        if(valsou != null){
            depthval = valsou.doubleValue();
            viewinggroup = 34051;
            final SNDFRM03 sndfrm03 = (SNDFRM03) context.getProcedure("SNDFRM03");
            soundingSymbols = sndfrm03.render(ctx, context, colorTable, all, graphic, depthval);
        }else{
            double leastdepth = Double.NaN;
            final DEPVAL02 depval02 = (DEPVAL02) context.getProcedure("DEPVAL02");
            final double[] res = depval02.render(ctx, context, colorTable, all, graphic, watlev, expsou);
            leastdepth = res[0];

            if(Double.isNaN(leastdepth)){
                if("6".equals(catobs)){
                    depthval = 0.01;
                }else if("5".equals(watlev)){
                    depthval = 0;
                }else if("3".equals(watlev)){
                    depthval = 0.01;
                }else if("4".equals(watlev)){
                    depthval = -15;
                }else if("1".equals(watlev) || "2".equals(watlev)){
                    depthval = -15;
                }else{
                    depthval = -15;
                }
            }else{
                depthval = leastdepth;
            }
        }

        final UDWHAZ04 udwhaz04 = (UDWHAZ04) context.getProcedure("UDWHAZ04");
        final Object[] udwhaz04Res = udwhaz04.render(ctx, context, colorTable, all, graphic, depthval);
        final boolean renderIsolatedDanger = (Boolean)udwhaz04Res[0];
        final Symbol dangerSymbol = (Symbol) udwhaz04Res[1];

        if(graphic.geoType == S52Context.GeoType.POINT){
            //Continuation A
            final QUAPNT02 quapnt02 = (QUAPNT02) context.getProcedure("QUAPNT02");
            final Object[] quapnt02Res = quapnt02.eval(ctx, context, colorTable, all, graphic);
            final boolean showLowAccuracy = (Boolean)quapnt02Res[0];
            final Symbol lowAccSymbol = (Symbol) quapnt02Res[1];

            if(renderIsolatedDanger){
                dangerSymbol.render(ctx, context, colorTable, all, graphic);
                if(showLowAccuracy){
                    lowAccSymbol.render(ctx, context, colorTable, all, graphic);
                }
                return; //finished
            }

            boolean sounding = false;
            Symbol selection;
            if(valsou != null){
                if(valsou.doubleValue() <= 20){
                    final String objClass = S52Utilities.getObjClass(graphic.feature);
                    if("UWTROC".equals(objClass)){
                        if("3".equals(watlev)){
                            selection = SY_DANGER01;
                            sounding = true;
                        }else if("4".equals(watlev)){
                            selection = SY_UWTROC04;
                            sounding = false;
                        }else if("5".equals(watlev)){
                            selection = SY_UWTROC04;
                            sounding = false;
                        }else{
                            selection = SY_DANGER01;
                            sounding = true;
                        }
                    }else{
                        if(!"OBSTRN".equals(objClass)){
                            throw new PortrayalException("Object class must be of type OBSTRN but was "+objClass);
                        }

                        if("6".equals(catobs)){
                            selection = SY_DANGER01;
                            sounding = true;
                        }else if("1".equals(watlev) || "2".equals(watlev)){
                            selection = SY_OBSTRN11;
                            sounding = false;
                        }else if("3".equals(watlev)){
                            selection = SY_DANGER01;
                            sounding = true;
                        }else if("4".equals(watlev) || "5".equals(watlev)){
                            selection = SY_DANGER03;
                            sounding = true;
                        }else{
                            selection = SY_DANGER01;
                            sounding = true;
                        }
                    }
                }else{
                    selection = SY_DANGER02;
                    sounding = true;
                }
            }else{
                final String objClass = S52Utilities.getObjClass(graphic.feature);
                if("UWTROC".equals(objClass)){
                    if("3".equals(watlev)){
                        selection = SY_UWTROC03;
                    }else{
                        selection = SY_UWTROC04;
                    }
                }else{
                    if(!"OBSTRN".equals(objClass)){
                        throw new PortrayalException("Object class must be of type OBSTRN but was "+objClass);
                    }

                    if("6".equals(catobs)){
                        selection = SY_OBSTRN01;
                    }else if("1".equals(watlev) || "2".equals(watlev)){
                        selection = SY_OBSTRN11;
                    }else if("3".equals(watlev)){
                        selection = SY_OBSTRN01;
                    }else if("4".equals(watlev) || "5".equals(watlev)){
                        selection = SY_OBSTRN03;
                    }else{
                        selection = SY_OBSTRN01;
                    }
                }
            }

            selection.render(ctx, context, colorTable, all, graphic);
            if(sounding && soundingSymbols!=null){
                for(Symbol ss : soundingSymbols){
                    ss.render(ctx, context, colorTable, all, graphic);
                }
            }
            if(showLowAccuracy){
                lowAccSymbol.render(ctx, context, colorTable, all, graphic);
            }


        }else if(graphic.geoType == S52Context.GeoType.LINE){
            //Continuation B

            //TODO for each spatial
            spatialloop:
            if(true){
                final Object quapos = (graphic.feature.getProperty("QUAPOS")==null) ? null : graphic.feature.getProperty("QUAPOS").getValue();
                if(quapos!=null){
                    final int val = Integer.valueOf(quapos.toString());
                    if(val > 1 && val < 10){
                        //position is inaccurate
                        LC_LOWACC.render(ctx, context, colorTable, all, graphic);
                        //continue spatial loop
                        break spatialloop;
                    }
                }

                if(renderIsolatedDanger){
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                    //continue spatial loop
                    break spatialloop;
                }else if(valsou != null){
                    if(valsou.doubleValue() <= 20){
                        LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                    }else{
                        LS_DASH_CHBLK.render(ctx, context, colorTable, all, graphic);
                    }
                }else{
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }
            } //end of spatial loop

            if(renderIsolatedDanger){
                dangerSymbol.render(ctx, context, colorTable, all, graphic);
            }else{
                if(valsou!=null){
                    if(soundingSymbols!=null){
                        for(Symbol ss : soundingSymbols){
                            ss.render(ctx, context, colorTable, all, graphic);
                        }
                    }
                }
            }

        }else if(graphic.geoType == S52Context.GeoType.AREA){
            //Continuation C

            final QUAPNT02 quapnt02 = (QUAPNT02) context.getProcedure("QUAPNT02");
            final Object[] quapnt02Res = quapnt02.eval(ctx, context, colorTable, all, graphic);
            final boolean showLowAccuracy = (Boolean)quapnt02Res[0];
            final Symbol lowAccSymbol = (Symbol) quapnt02Res[1];

            if(renderIsolatedDanger){
                CF_DEPVS.render(ctx, context, colorTable, all, graphic);
                PF_FOULAR01.render(ctx, context, colorTable, all, graphic);
                LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                dangerSymbol.render(ctx, context, colorTable, all, graphic);

                if(showLowAccuracy){
                    lowAccSymbol.render(ctx, context, colorTable, all, graphic);
                }
                return; //finished
            }

            if(valsou != null){
                if(valsou.doubleValue() <= 20){
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }else{
                    LS_DASH_CHGRD.render(ctx, context, colorTable, all, graphic);
                }
                if(soundingSymbols!=null){
                    for(Symbol ss : soundingSymbols){
                        ss.render(ctx, context, colorTable, all, graphic);
                    }
                }

            }else{
                if("6".equals("CATOBS")){
                    PF_FOULAR01.render(ctx, context, colorTable, all, graphic);
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }else if("1".equals("WATLEV")){
                    CF_CHBRN.render(ctx, context, colorTable, all, graphic);
                    LS_SOLD_CSTLN.render(ctx, context, colorTable, all, graphic);
                }else if("2".equals("WATLEV")){
                    CF_CHBRN.render(ctx, context, colorTable, all, graphic);
                    LS_SOLD_CSTLN.render(ctx, context, colorTable, all, graphic);
                }else if("4".equals("WATLEV")){
                    CF_DEPIT.render(ctx, context, colorTable, all, graphic);
                    LS_DASH_CSTLN.render(ctx, context, colorTable, all, graphic);
                }else if("5".equals("WATLEV")){
                    CF_DEPVS.render(ctx, context, colorTable, all, graphic);
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }else if("3".equals("WATLEV")){
                    CF_DEPVS.render(ctx, context, colorTable, all, graphic);
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }else{
                    CF_DEPVS.render(ctx, context, colorTable, all, graphic);
                    LS_DOTT_CHBLK.render(ctx, context, colorTable, all, graphic);
                }
            }

            if(showLowAccuracy){
                lowAccSymbol.render(ctx, context, colorTable, all, graphic);
            }
        }

    }

}
