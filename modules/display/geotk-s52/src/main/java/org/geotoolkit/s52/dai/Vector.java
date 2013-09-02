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
package org.geotoolkit.s52.dai;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.render.SymbolStyle;

/**
 * Contains a vector image definition;
 * Colors are identified by a letter (ASCII>=64);
 * The letter represents a color token defined within the PCRF.CTOK subfield.
 * The letter '@'identifies a fully transparent color.
 * Note: PVCT and PBTM are mutual exclusive.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Vector extends DAIField{

    /** A(1/15) : String of vector commands; */
    public String VECD;

    //cache
    private RenderStep[] steps;

    protected Vector(String code) {
        super(code);
    }

    public synchronized RenderStep[] getSteps() throws PortrayalException{
        if(steps != null) return steps;

        final String[] parts = VECD.split(";");
        steps = new RenderStep[parts.length];
        for(int i=0;i<parts.length;i++){
            String part = parts[i];
            //S52 Annex A Part I p.34 (5)
            final String action = part.substring(0, 2);
            if("SP".equals(action)){
                //color
                final ColorStep step = new ColorStep();
                step.colorName = ""+part.charAt(2);
                if(step.colorName.equals("@")){
                    step.color = new Color(0, 0, 0, 0);
                }
                steps[i] = step;

            }else if("ST".equals(action)){
                //transparency
                final TransparencyStep step = new TransparencyStep();
                final char trans = part.charAt(2);
                switch(trans){
                    case '0' : step.alpha = 1f; break;
                    case '1' : step.alpha = 0.25f; break;
                    case '2' : step.alpha = 0.50f; break;
                    case '3' : step.alpha = 0.75f; break;
                    default : step.alpha = 1f;
                }
                steps[i] = step;

            }else if("SW".equals(action)){
                //pen size
                float size = Integer.valueOf(part.substring(2));
                //one unit = 0.3mm
                size = S52Utilities.mmToPixel(size*0.3f) * (1f/SymbolStyle.SCALE);
                final PenSizeStep step = new PenSizeStep();
                step.stroke = new BasicStroke(size);
                steps[i] = step;

            }else if("PU".equals(action)){
                //move pen , no draw
                part = part.substring(2);
                final int index = part.indexOf(',');
                final PenMoveStep step = new PenMoveStep();
                step.tx = Integer.valueOf(part.substring(0, index));
                step.ty = Integer.valueOf(part.substring(index+1));
                steps[i] = step;

            }else if("PD".equals(action)){
                //pen draw
                part = part.substring(2);
                final PenLineStep step = new PenLineStep();
                final String[] pts = part.split(",");
                step.tx = new int[pts.length/2];
                step.ty = new int[pts.length/2];
                for(int k=0,l=0;k<pts.length;k+=2,l++){
                    step.tx[l] = Integer.valueOf(pts[k]);
                    step.ty[l] = Integer.valueOf(pts[k+1]);
                }
                steps[i] = step;

            }else if("CI".equals(action)){
                //circle
                final PenCircleStep step = new PenCircleStep();
                step.radius = Integer.valueOf(part.substring(2));
                steps[i] = step;

            }else if("AA".equals(action)){
                throw new PortrayalException("Action not implemented yet : "+part);
            }else if("PM".equals(action)){
                //polygon operations
                final PolygonStep step = new PolygonStep();
                step.op = part.charAt(2);
                steps[i] = step;

            }else if("EP".equals(action)){
                //outline polygon
                final PolygonOutlineStep step = new PolygonOutlineStep();
                steps[i] = step;

            }else if("FP".equals(action)){
                //fill polygon
                final PolygonFillStep step = new PolygonFillStep();
                steps[i] = step;

            }else if("SC".equals(action)){
                //sub-symbol
                final SymbolStep step = new SymbolStep();
                step.name = part.substring(2, 10);
                part = part.substring(11);
                step.rotation = Integer.valueOf(part);

            }else{
                throw new PortrayalException("unexpected action : "+part);
            }
        }

        return steps;
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("VECD", VECD);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        VECD = readStringByDelim(str, offset, DELIM_1F, true); //be tolerance, delimiter missing in some files
    }

    public static interface RenderStep{}

    public static class ColorStep implements RenderStep{
        public String colorName;
        public Color color;

        public Color getColor(Map<String,String> map, S52Palette palette){
            if(color!=null) return color;
            return palette.getColor(map.get(colorName));
        }
    }

    public static class TransparencyStep implements RenderStep{
        public float alpha;
    }

    public static class PenSizeStep implements RenderStep{
        public Stroke stroke;
    }

    public static class PenMoveStep implements RenderStep{
        public int tx;
        public int ty;
    }

    public static class PenLineStep implements RenderStep{
        public int[] tx;
        public int[] ty;
    }

    public static class PenCircleStep implements RenderStep{
        public int radius;
    }

    public static class PenArcStep implements RenderStep{
    }

    public static class PolygonStep implements RenderStep{
        public char op;
    }

    public static class PolygonOutlineStep implements RenderStep{
    }

    public static class PolygonFillStep implements RenderStep{
    }

    public static class SymbolStep implements RenderStep{
        public String name;
        /**
         * 0 : symbol upright
         * 1 : direction of the pen
         * 2 : 90° rotation from edge
         */
        public int rotation;
    }


}
