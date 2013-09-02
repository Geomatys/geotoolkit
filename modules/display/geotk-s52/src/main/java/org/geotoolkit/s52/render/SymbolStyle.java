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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.dai.Bitmap;
import org.geotoolkit.s52.dai.ColorReference;
import org.geotoolkit.s52.dai.Definition;
import org.geotoolkit.s52.dai.Exposition;
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

    //caches
    private Rectangle2D bounds;

    public Rectangle2D getBounds(){
        if(bounds==null){
            final float minX = Math.min(definition.getBoxULX(),definition.getPivotX()) * SCALE;
            final float maxX = Math.max(definition.getBoxULX()+definition.getBoxWidth(),definition.getPivotX()) * SCALE;
            final float minY = Math.min(definition.getBoxULY(),definition.getPivotY()) * SCALE;
            final float maxY = Math.max(definition.getBoxULY()+definition.getBoxHeight(),definition.getPivotY()) * SCALE;
            bounds = new Rectangle2D.Float(minX, minY, maxX-minX, maxY-minY);
        }
        return bounds;
    }

    public void render(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{

        if(definition.getType().equals("R")){
            renderRaster(g2d, context, colorTable, center, rotation);
        }else if(definition.getType().equals("V")){
            renderVector(g2d, context, colorTable, center, rotation);
        }
    }

    private void renderRaster(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{
        System.out.println(">>>>>>>>>>>>>>> RASTER");
    }

    private void renderVector(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{
        final float pivotX    = definition.getPivotX();
        final float pivotY    = definition.getPivotY();
        //one unit = 0.01mm
        //adjust values back to pixel units
        final float scale = SCALE;

        final AffineTransform old = g2d.getTransform();
        final AffineTransform trs = new AffineTransform();
        trs.translate(center.x, center.y);
        trs.scale(SCALE, SCALE);
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
            final Vector.RenderStep[] steps = sv.getSteps();
            for(int i=0;i<steps.length;i++){
                final Vector.RenderStep step = steps[i];
                if(step instanceof Vector.ColorStep){
                    g2d.setColor(((Vector.ColorStep)step).getColor(colors.colors, colorTable));
                }else if(step instanceof Vector.TransparencyStep){
                    alpha = ((Vector.TransparencyStep)step).alpha;
                }else if(step instanceof Vector.PenSizeStep){
                    g2d.setStroke(((Vector.PenSizeStep)step).stroke);
                }else if(step instanceof Vector.PenMoveStep){
                    final Vector.PenMoveStep st = (Vector.PenMoveStep) step;
                    ltx = tx; tx = st.tx;
                    lty = ty; ty = st.ty;
                    if(polygonMode){
                        path.moveTo(tx,ty);
                    }
                }else if(step instanceof Vector.PenLineStep){
                    final Vector.PenLineStep st = (Vector.PenLineStep) step;
                    final Path2D line = (polygonMode) ? path : new Path2D.Float();
                    line.moveTo(tx, ty);
                    for(int k=0;k<st.tx.length;k++){
                        line.lineTo(st.tx[k],st.ty[k]);
                        ltx = tx;
                        lty = ty;
                        tx = st.tx[k];
                        ty = st.ty[k];
                    }
                    if(!polygonMode){
                        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                        g2d.draw(line);
                    }
                }else if(step instanceof Vector.PenCircleStep){
                    final Vector.PenCircleStep st = (Vector.PenCircleStep) step;
                    final Shape shp = new Ellipse2D.Float(tx-st.radius, ty-st.radius, st.radius*2, st.radius*2);
                    if(polygonMode){
                        path.append(shp, true);
                    }else{
                        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                        g2d.draw(shp);
                    }
                }else if(step instanceof Vector.PenArcStep){
                    throw new PortrayalException("Action Pen Arc not implemented yet.");
                }else if(step instanceof Vector.PolygonStep){
                    final Vector.PolygonStep st = (Vector.PolygonStep) step;
                    switch(st.op){
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
                        default : throw new PortrayalException("unexpected polygon action : "+st.op);
                    }

                }else if(step instanceof Vector.PolygonOutlineStep){
                    final Vector.PolygonOutlineStep st = (Vector.PolygonOutlineStep) step;
                    g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                    g2d.draw(path);

                }else if(step instanceof Vector.PolygonFillStep){
                    final Vector.PolygonFillStep st = (Vector.PolygonFillStep) step;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.draw(path);

                }else if(step instanceof Vector.SymbolStep){
                    final Vector.SymbolStep st = (Vector.SymbolStep) step;
                    final SymbolStyle ss = context.getSyle(st.name);

                    //rotation type
                    //S-52 Annex A part I p.36
                    final float ssr;
                    if(st.rotation == 0){
                        //symbol upright
                        ssr = 0;
                    }else if(st.rotation == 1){
                        //direction of the pen
                        float angle = S52Utilities.angle(ltx, lty, tx, ty);
                        ssr = rotation + angle;

                    }else if(st.rotation == 2){
                        //90° rotation from edge
                        float angle = S52Utilities.angle(ltx, lty, tx, ty);
                        ssr = rotation + angle + (float)Math.PI/2;
                    }else{
                        throw new PortrayalException("unexpected rotation value : "+st.rotation);
                    }

                    final Point2D pt = new Point2D.Float(tx, ty);
                    trs.transform(pt, pt);
                    g2d.setTransform(old);
                    ss.render(g2d, context, colorTable, new Coordinate(pt.getX(), pt.getY()), ssr);
                    g2d.setTransform(trs);

                }else{
                    throw new PortrayalException("unexpected render step : "+step);
                }
            }

        }

        g2d.setTransform(old);
    }

}
