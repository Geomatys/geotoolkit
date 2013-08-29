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
package org.geotoolkit.s52.render;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.dai.Bitmap;
import org.geotoolkit.s52.dai.ColorReference;
import org.geotoolkit.s52.dai.Definition;
import org.geotoolkit.s52.dai.Exposition;
import org.geotoolkit.s52.dai.SymbolIdentifier;
import org.geotoolkit.s52.dai.Vector;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SymbolStyle {

    public static final float SCALE = S52Utilities.mmToPixel(1) * 0.01f;

    /** holds the color map */
    public ColorReference colors;
    /** style definition */
    public Definition definition;
    /** explication of the style */
    public Exposition explication;
    /** drawing commands */
    public final List<Vector> vectors = new ArrayList<>();
    /** bitmap style */
    public Bitmap bitmap;

    public Rectangle2D getBounds(){
        final float minX = Math.min(definition.getBoxULX(),definition.getPivotX()) * SCALE;
        final float maxX = Math.max(definition.getBoxULX()+definition.getBoxWidth(),definition.getPivotX()) * SCALE;
        final float minY = Math.min(definition.getBoxULY(),definition.getPivotY()) * SCALE;
        final float maxY = Math.max(definition.getBoxULY()+definition.getBoxHeight(),definition.getPivotY()) * SCALE;
        return new Rectangle2D.Float(minX, minY, maxX-minX, maxY-minY);
    }

    public void render(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws IOException{

        if(definition.getType().equals("R")){
            renderRaster(g2d, context, colorTable, center, rotation);
        }else if(definition.getType().equals("V")){
            renderVector(g2d, context, colorTable, center, rotation);
        }
    }

    private void renderRaster(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws IOException{
        System.out.println(">>>>>>>>>>>>>>> RASTER");
    }

    private void renderVector(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws IOException{
        final float pivotX    = definition.getPivotX();
        final float pivotY    = definition.getPivotY();
        //one unit = 0.01mm
        //adjust values back to pixel units
        final float scale = SCALE;

        final AffineTransform old = g2d.getTransform();
        final AffineTransform trs = new AffineTransform();
        trs.translate(center.x, center.y);
        trs.scale(scale, scale);
        trs.rotate(rotation);
        trs.translate(-pivotX, -pivotY);
        g2d.setTransform(trs);

        float alpha = 1f;
        float ltx = 0f;
        float lty = 0f;
        float tx = 0f;
        float ty = 0f;

        boolean polygonMode = false;
        Path2D path = null;

        for(Vector sv : vectors){
            final String[] parts = sv.VECD.split(";");
            for(String part : parts){
                //S52 Annex A Part I p.34 (5)
                final String action = part.substring(0, 2);
                if("SP".equals(action)){
                    //color
                    final String colorCode = ""+part.charAt(2);
                    final Color color;
                    if(colorCode.equals("@")){
                        color = new Color(0, 0, 0, 0);
                    }else{
                        final String colorName = colors.colors.get(colorCode);
                        color = colorTable.getColor(colorName);
                    }
                    g2d.setColor(color);

                }else if("ST".equals(action)){
                    //transparency
                    final char trans = part.charAt(2);
                    switch(trans){
                        case '0' : alpha = 1f; break;
                        case '1' : alpha = 0.25f; break;
                        case '2' : alpha = 0.50f; break;
                        case '3' : alpha = 0.75f; break;
                        default : alpha = 1f;
                    }

                }else if("SW".equals(action)){
                    //pen size
                    float size = Integer.valueOf(part.substring(2));
                    //one unit = 0.3mm
                    size = S52Utilities.mmToPixel(size*0.3f) * (1f/scale);
                    g2d.setStroke(new BasicStroke(size));

                }else if("PU".equals(action)){
                    //move pen , no draw
                    part = part.substring(2);
                    final int index = part.indexOf(',');
                    ltx = tx;
                    lty = ty;
                    tx = Integer.valueOf(part.substring(0, index));
                    ty = Integer.valueOf(part.substring(index+1));
                    if(polygonMode){
                        path.moveTo(tx,ty);
                    }

                }else if("PD".equals(action)){
                    part = part.substring(2);
                    final String[] pts = part.split(",");
                    final Path2D line = (polygonMode) ? path : new Path2D.Float();
                    line.moveTo(tx, ty);
                    for(int k=0;k<pts.length;k+=2){
                        final float ex = Integer.valueOf(pts[k]);
                        final float ey = Integer.valueOf(pts[k+1]);
                        line.lineTo(ex,ey);
                        ltx = tx;
                        lty = ty;
                        tx = ex;
                        ty = ey;
                    }
                    if(!polygonMode){
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                        g2d.draw(line);
                    }

                }else if("CI".equals(action)){
                    //circle
                    float size = Integer.valueOf(part.substring(2));
                    final Shape shp = new Ellipse2D.Float(tx-size, ty-size, size*2, size*2);
                    if(polygonMode){
                        path.append(shp, true);
                    }else{
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                        g2d.draw(shp);
                    }

                }else if("AA".equals(action)){
                    throw new IOException("Action not implemented yet : "+part);
                }else if("PM".equals(action)){
                    //polygon operations
                    final char trans = part.charAt(2);
                    switch(trans){
                        case '0' :
                            path = new Path2D.Float();
                            polygonMode = true;
                            break;
                        case '1' :
                            path.closePath();
                            break;
                        case '2' :
                            polygonMode = false;
                            break;
                        default : throw new IOException("unexpected action : "+part);
                    }

                }else if("EP".equals(action)){
                    //outline polygon
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2d.draw(path);

                }else if("FP".equals(action)){
                    //fill polygon
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.draw(path);

                }else if("SC".equals(action)){
                    final String name = part.substring(2, 10);
                    part = part.substring(11);
                    final SymbolStyle ss = context.getSyle(name);

                    //rotation type
                    //S-52 Annex A part I p.36
                    final float ssr;
                    if(part.equals("0")){
                        //symbol upright
                        ssr = 0;
                    }else if(part.equals("1")){
                        //direction of the pen
                        float angle = angle(ltx, lty, tx, ty);
                        ssr = rotation + angle;

                    }else if(part.equals("2")){
                        //90° rotation from edge
                        float angle = angle(ltx, lty, tx, ty);
                        ssr = rotation + angle + (float)Math.PI/2;
                    }else{
                        throw new IOException("unexpected rotation value : "+part);
                    }

                    final Point2D pt = new Point2D.Float(tx, ty);
                    trs.transform(pt, pt);
                    g2d.setTransform(old);
                    ss.render(g2d, context, colorTable, new Coordinate(pt.getX(), pt.getY()), ssr);
                    g2d.setTransform(trs);

                }else{
                    throw new IOException("unexpected action : "+part);
                }
            }
        }

        g2d.setTransform(old);
    }

    private static float angle(final float x1, final float y1, final float x2, final float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.atan2(dy, dx);
    }

}
