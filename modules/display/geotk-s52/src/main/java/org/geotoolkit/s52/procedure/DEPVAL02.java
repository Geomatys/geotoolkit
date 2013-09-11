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
import static org.geotoolkit.s52.procedure.UDWHAZ04.intersects;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.153 (12.2.5)
 *
 * @author Johann Sorel (Geomatys)
 */
public class DEPVAL02 extends Procedure{

    public DEPVAL02() {
        super("DEPVAL02");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    public double[] render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic,String watlev, String expsou) {

        double leastDepth = Double.NaN;
        double seabedDepth = Double.NaN;

        //TODO loop on underlying group 1 objects ? needs clarifications
        for(S52Graphic other : all){
            if(other == graphic) continue;
            final String objClass = S52Utilities.getObjClass(other.feature);

            if("UNSARE".equals(objClass)){
                if(!intersects(graphic, other)) continue;
                leastDepth = Double.NaN;
                break;

            }else if("DEPARE".equals(objClass) || "DRGARE".equals(objClass)){
                if(!intersects(graphic, other)) continue;

                final Number drval1 = (Number) other.feature.getProperty("DRVAL1").getValue();
                if(drval1!=null){
                    if(Double.isNaN(leastDepth)){
                        leastDepth = drval1.doubleValue();
                    }else if(leastDepth >= drval1.doubleValue()){
                        leastDepth = drval1.doubleValue();
                    }
                }
            }
        }

        if(!Double.isNaN(leastDepth)){
            if("3".equals(watlev) && ("1".equals(expsou) || "3".equals(expsou))){
                seabedDepth = leastDepth;
            }else{
                seabedDepth = leastDepth;
                leastDepth = Double.NaN;
            }
        }

        return new double[]{leastDepth,seabedDepth};
    }

}
