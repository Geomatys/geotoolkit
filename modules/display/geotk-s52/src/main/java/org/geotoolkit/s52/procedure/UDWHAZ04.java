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

import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.213 (12.2.24)
 *
 * @author Johann Sorel (Geomatys)
 */
public class UDWHAZ04 extends Procedure{

    private static final Symbol SY_ISODGR01 = new Symbol("ISODGR01", null);

    public UDWHAZ04() {
        super("UDWHAZ04");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    /**
     *
     * @param ctx
     * @param context
     * @param colorTable
     * @param graphic
     * @param depthValue
     * @return
     */
    public Object[] render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic, double depthValue) {

        final boolean showIsolatedDanger = context.isIsolatedDangerInShallowWater();
        final double safetyContour = context.getSafetyContour();
        boolean danger = false;

        if(depthValue <= safetyContour){
            //loop on all other objects
            for(S52Graphic other : all){
                if(other == graphic) continue;//avoid self collision
                final String objClass = S52Utilities.getObjClass(other.feature);
                if(!("DEPARE".equals(objClass) || "DRGARE".equals(objClass))) continue;
                if(!intersects(graphic, other)) continue;

                final Number drval1 = (Number) other.feature.getProperty("DRVAL1").getValue();
                if(drval1==null) continue;

                if(drval1.doubleValue() >= safetyContour){
                    danger = true;
                    break;
                }
            }

            if(danger){
                final String watlev = (String) graphic.feature.getProperty("WATLEV").getValue();
                if("1".equals(watlev) || "2".equals(watlev)){
                    graphic.category = "DISPLAY BASE";
                    graphic.viewingGroup = 14050;
                    return new Object[]{false,null};
                }

                graphic.category = "DISPLAY BASE";
                graphic.minscale = Float.POSITIVE_INFINITY;
                graphic.priority = 8;
                graphic.radarflag = LookupRecord.Radar.O;
                graphic.viewingGroup = 14010;
                return new Object[]{true,SY_ISODGR01};

            }else{
                if(showIsolatedDanger){
                    // Continuation A

                    //loop on all other objects
                    for(S52Graphic other : all){
                        if(other == graphic) continue;//avoid self collision
                        final String objClass = S52Utilities.getObjClass(other.feature);
                        if(!("DEPARE".equals(objClass) || "DRGARE".equals(objClass))) continue;
                        if(!intersects(graphic, other)) continue;

                        final Number drval1 = (Number) other.feature.getProperty("DRVAL1").getValue();
                        if(drval1==null) continue;

                        if(drval1.doubleValue() >= 0d && drval1.doubleValue() < safetyContour){
                            danger = true;
                            break;
                        }
                    }

                    if(danger){
                        final String watlev = (String) graphic.feature.getProperty("WATLEV").getValue();
                        if("1".equals(watlev) || "2".equals(watlev)){
                            graphic.category = "STANDARD";
                            graphic.viewingGroup = 24050;
                            return new Object[]{false,null};
                        }

                        graphic.category = "STANDARD";
                        graphic.minscale = Float.POSITIVE_INFINITY;
                        graphic.priority = 8;
                        graphic.radarflag = LookupRecord.Radar.O;
                        graphic.viewingGroup = 24020;
                        return new Object[]{true,SY_ISODGR01};

                    }else{
                        return new Object[]{false,null};
                    }

                }else{
                    return new Object[]{false,null};
                }
            }

        }else{
            return new Object[]{false,null};
        }

    }

    public static boolean intersects(S52Graphic one, S52Graphic two){
        final Geometry geom1 = (Geometry) one.feature.getDefaultGeometryProperty().getValue();
        final Geometry geom2 = (Geometry) two.feature.getDefaultGeometryProperty().getValue();
        return geom1.intersects(geom2);
    }

}
