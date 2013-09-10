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

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.feature.Feature;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.223 (12.2.27)
 *
 * @author Johann Sorel (Geomatys)
 */
public class WRECKS04 extends Procedure{

    private static final Symbol SY_DANGER01 = new Symbol("DANGER01", null);
    private static final Symbol SY_DANGER02 = new Symbol("DANGER02", null);
    private static final Symbol SY_WRECK01  = new Symbol("WRECK01", null);
    private static final Symbol SY_WRECK04  = new Symbol("WRECK04", null);
    private static final Symbol SY_WRECK05  = new Symbol("WRECK05", null);

    public WRECKS04() {
        super("WRECKS04");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        final Feature feature = graphic.feature;

        //used at multiple places
        final Number valsou = (Number) feature.getProperty("VALSOU").getValue();
        final String catwrk = (String) feature.getProperty("CATWRK").getValue();
        final String watlev = (String) feature.getProperty("WATLEV").getValue();

        double depthValue;
        //TODO handle viewgroup somehow.
        int viewingGroup;
        Symbol[] isolateSymbols = null;

        if(valsou != null){
            depthValue = valsou.doubleValue();
            viewingGroup = 34051;

            final SNDFRM03 sndfrm03 = new SNDFRM03();
            isolateSymbols = sndfrm03.render(ctx, context, colorTable, all, graphic, depthValue);

        }else{
            double leastDepth = Double.NaN;
            double seabedDepth = Double.NaN;
            final String expsou = (String) feature.getProperty("EXPSOU").getValue();

            final DEPVAL02 depval02 = new DEPVAL02();
            double[] vals = depval02.render(ctx, context, colorTable, all, graphic, expsou, watlev);
            leastDepth = vals[0];
            seabedDepth = vals[1];

            if(Double.isNaN(leastDepth)){
                if(catwrk != null){
                    if("1".equals(catwrk)){
                        depthValue = 20.1;
                        if(!Double.isNaN(seabedDepth)){
                            leastDepth = seabedDepth - 66;
                            if(leastDepth >= 20.1){
                                depthValue = leastDepth;
                            }
                        }
                    }else{
                        depthValue = 15;
                    }
                }else{
                    if(watlev != null){
                        if("3".equals(watlev) || "5".equals(watlev)){
                            depthValue = 0;
                        }else{
                            depthValue = 15;
                        }
                    }else{
                        depthValue = 15;
                    }
                }
            }else{
                depthValue = leastDepth;
            }
        }

        final UDWHAZ04 udwhaz04 = new UDWHAZ04();
        final Object[] udwhaz04Res = udwhaz04.render(ctx, context, colorTable, all, graphic, depthValue);
        final boolean renderIsolatedDanger = (Boolean)udwhaz04Res[0];
        final Symbol dangerSymbol = (Symbol) udwhaz04Res[1];


        final QUAPNT02 quapnt02 = new QUAPNT02();
        final Object[] res = quapnt02.eval(ctx, context, colorTable, all, graphic);
        final boolean displayLowAccuracy = (Boolean)res[0];
        final Symbol lowAccuracy = (Symbol) res[1];

        if(graphic.geoType == S52Context.GeoType.POINT){
            final Coordinate center;
            try {
                center = graphic.graphic.getGeometry(null).getDisplayGeometryJTS().getCoordinate();
            } catch (TransformException ex) {
                throw new PortrayalException(ex);
            }

            if(renderIsolatedDanger){
                dangerSymbol.render(ctx, context, colorTable, all, graphic);
                if(isolateSymbols!=null){
                    for(Symbol ss : isolateSymbols){
                        ss.render(ctx, context, colorTable, all, graphic);
                    }
                }
            }else{
                //continuation A
                if(valsou != null){
                    final Symbol danger;
                    if(valsou.doubleValue() <= 20){
                        danger = SY_DANGER01;
                    }else{
                        danger = SY_DANGER02;
                    }
                    danger.render(ctx, context, colorTable, all, graphic);
                    if(displayLowAccuracy) lowAccuracy.render(ctx, context, colorTable, all, graphic);
                    if(isolateSymbols!=null){
                        for(Symbol ss : isolateSymbols){
                            ss.render(ctx, context, colorTable, all, graphic);
                        }
                    }

                }else{
                    final Symbol wreck;
                    if("1".equals(catwrk) && "3".equals(watlev)){
                        wreck = SY_WRECK04;
                    }else if("2".equals(catwrk) && "3".equals(watlev)){
                        wreck = SY_WRECK05;
                    }else if("4".equals(catwrk)){
                        wreck = SY_WRECK01;
                    }else if("5".equals(catwrk)){
                        wreck = SY_WRECK01;
                    }else if("1".equals(watlev)){
                        wreck = SY_WRECK01;
                    }else if("2".equals(watlev)){
                        wreck = SY_WRECK01;
                    }else if("5".equals(watlev)){
                        wreck = SY_WRECK01;
                    }else if("4".equals(watlev)){
                        wreck = SY_WRECK01;
                    }else{
                        wreck = SY_WRECK05;
                    }

                    wreck.render(ctx, context, colorTable, all, graphic);
                    if(displayLowAccuracy && lowAccuracy != null){
                        lowAccuracy.render(ctx, context, colorTable, all, graphic);
                    }
                }
            }
        }else{
            //continuation B
            System.out.println("Procedure "+getName()+" not implemented yet");
        }

    }

}
