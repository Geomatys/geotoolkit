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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.j2d.PathWalker;
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

    public BufferedImage asImage(S52Context context, int margin) throws PortrayalException{
        final Rectangle2D rect = getBounds();
         final Coordinate coord = new Coordinate(
                definition.getPivotX()*SCALE - rect.getMinX(),
                definition.getPivotY()*SCALE - rect.getMinY());

        final BufferedImage image = new BufferedImage(
                (int)Math.ceil(rect.getWidth()) + 2*margin,
                (int)Math.ceil(rect.getHeight())+ 2*margin,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        render(g, context, context.getPalette(), coord, 0);
        return image;
    }

    /**
     * Render symbol at given point.
     *
     * @param g2d
     * @param context
     * @param colorTable
     * @param center
     * @param rotation
     * @throws PortrayalException
     */
    public void render(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{

        if(definition.getType().equals("R")){
            renderRaster(g2d, context, colorTable, center, rotation);
        }else if(definition.getType().equals("V")){
            renderVector(g2d, context, colorTable, center, rotation);
        }
    }

    /**
     * Render symbol following given path.
     *
     * @param g2d
     * @param context
     * @param colorTable
     * @param walker
     * @throws PortrayalException
     */
    public void render(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            PathWalker walker) throws PortrayalException{

        if(definition.getType().equals("R")){
            renderRaster(g2d, context, colorTable, walker);
        }else if(definition.getType().equals("V")){
            renderVector(g2d, context, colorTable, walker);
        }
    }

    private void renderRaster(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            PathWalker walker) throws PortrayalException{
        System.out.println(">>>>>>>>>>>>>>> RASTER");
    }

    private void renderRaster(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{
        System.out.println(">>>>>>>>>>>>>>> RASTER");
    }

    private void renderVector(final Graphics2D g2d, S52Context context, S52Palette colorTable,
            Coordinate center, float rotation) throws PortrayalException{

        final AffineTransform old = g2d.getTransform();
        final RenderState rs = new RenderState();

        for(Vector sv : vectors){
            renderVector(sv, g2d, context, colorTable, center, rotation, rs);
        }

        g2d.setTransform(old);
    }

    private void renderVector(final Graphics2D g2d, final S52Context context, S52Palette colorTable,
           PathWalker walker) throws PortrayalException{

        if(vectors.isEmpty()) return;

        final AffineTransform old = g2d.getTransform();

        //order vectors by distance, this way we always go forward with the path walker
        final Vector[] vectors = this.vectors.toArray(new Vector[this.vectors.size()]);
        Arrays.sort(vectors, new Comparator<Vector>() {
            @Override
            public int compare(Vector o1, Vector o2) {
                final double d1 = o1.getBounds(context).getCenterX();
                final double d2 = o2.getBounds(context).getCenterX();
                return Double.compare(d1, d2);
            }
        });

        final RenderState rs = new RenderState();

        final float serieLength = (float) getBounds().getWidth();
        float advance = 0;
        Point2D pt = new Point2D.Double();
        float rotation = 0;
        int vindex = 0;
        walker.walk(0f);
        while(!walker.isFinished()){

            final Vector sv = vectors[vindex];
            final Rectangle2D bounds = sv.getBounds(context);
            advance = (float) (bounds.getCenterX()*SCALE - advance);

            walker.walk(advance);
            if(walker.isFinished()) break;

            pt = walker.getPosition(pt);
            rotation = walker.getRotation();


            renderVector(sv, g2d, context, colorTable, new Coordinate(pt.getX(), pt.getY()), rotation, rs);

            //prepare next vector index
            vindex++;
            if(vindex>=vectors.length){
                advance=0;
                vindex=0;

                //move remaining
                walker.walk(serieLength - advance);
            }
        }

        g2d.setTransform(old);
    }

    private void renderVector(final Vector sv, final Graphics2D g2d, final S52Context context,
            final S52Palette colorTable, final Coordinate center, final float rotation, RenderState rs) throws PortrayalException{

        final float pivotX    = definition.getPivotX();
        final float pivotY    = definition.getPivotY();
        final AffineTransform old = g2d.getTransform();

        final AffineTransform trs = new AffineTransform();
        trs.translate(center.x, center.y);
        trs.scale(SCALE, SCALE);
        trs.rotate(rotation);
        trs.translate(-pivotX, -pivotY);
        g2d.setTransform(trs);

        final Vector.RenderStep[] steps = sv.getSteps();
        for(int i=0;i<steps.length;i++){
            final Vector.RenderStep step = steps[i];
            if(step instanceof Vector.ColorStep){
                g2d.setColor(((Vector.ColorStep)step).getColor(colors.colors, colorTable));
            }else if(step instanceof Vector.TransparencyStep){
                rs.alpha = ((Vector.TransparencyStep)step).alpha;
            }else if(step instanceof Vector.PenSizeStep){
                g2d.setStroke(((Vector.PenSizeStep)step).stroke);
            }else if(step instanceof Vector.PenMoveStep){
                final Vector.PenMoveStep st = (Vector.PenMoveStep) step;
                rs.ltx = rs.tx; rs.tx = st.tx;
                rs.lty = rs.ty; rs.ty = st.ty;
                if(rs.polygonMode){
                    rs.path.moveTo(rs.tx,rs.ty);
                }
            }else if(step instanceof Vector.PenLineStep){
                final Vector.PenLineStep st = (Vector.PenLineStep) step;
                final Path2D line = (rs.polygonMode) ? rs.path : new Path2D.Float(PathIterator.WIND_EVEN_ODD);
                final Point2D last = line.getCurrentPoint();
                if(last == null ||last.getX() != rs.tx || last.getY() != rs.ty){
                    line.moveTo(rs.tx, rs.ty);
                }
                for(int k=0;k<st.tx.length;k++){
                    line.lineTo(st.tx[k],st.ty[k]);
                    rs.ltx = rs.tx;
                    rs.lty = rs.ty;
                    rs.tx = st.tx[k];
                    rs.ty = st.ty[k];
                }
                if(!rs.polygonMode){
                    g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                    g2d.draw(line);
                }
            }else if(step instanceof Vector.PenCircleStep){
                final Vector.PenCircleStep st = (Vector.PenCircleStep) step;
                final Shape shp = new Ellipse2D.Float(rs.tx-st.radius, rs.ty-st.radius, st.radius*2, st.radius*2);
                if(rs.polygonMode){
                    rs.path.append(shp, true);
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
                        rs.path = new Path2D.Float(PathIterator.WIND_EVEN_ODD);
                        rs.polygonMode = true;
                        break;
                    case '1' :
                       if(rs.path!=null) rs.path.closePath();
                        break;
                    case '2' :
                        if(rs.path!=null)rs.path.closePath();
                        rs.polygonMode = false;
                        break;
                    default : throw new PortrayalException("unexpected polygon action : "+st.op);
                }

            }else if(step instanceof Vector.PolygonOutlineStep){
                final Vector.PolygonOutlineStep st = (Vector.PolygonOutlineStep) step;
                g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                if(rs.path!=null)g2d.draw(rs.path);

            }else if(step instanceof Vector.PolygonFillStep){
                final Vector.PolygonFillStep st = (Vector.PolygonFillStep) step;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, rs.alpha));
                if(rs.path!=null)g2d.fill(rs.path);

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
                    float angle = S52Utilities.angle(rs.ltx, rs.lty, rs.tx, rs.ty);
                    ssr = rotation + angle;

                }else if(st.rotation == 2){
                    //90° rotation from edge
                    float angle = S52Utilities.angle(rs.ltx, rs.lty, rs.tx, rs.ty);
                    ssr = rotation + angle + (float)Math.PI/2;
                }else{
                    throw new PortrayalException("unexpected rotation value : "+st.rotation);
                }

                final Point2D pt = new Point2D.Float(rs.tx, rs.ty);
                trs.transform(pt, pt);
                g2d.setTransform(old);
                ss.render(g2d, context, colorTable, new Coordinate(pt.getX(), pt.getY()), ssr);
                g2d.setTransform(trs);

            }else{
                throw new PortrayalException("unexpected render step : "+step);
            }
        }
    }

    /**
     * Vector drawing informations are sequencial, we must keep state between vectors.
     */
    private static class RenderState{
        float alpha = 1f;
        float ltx = 0f;
        float lty = 0f;
        float tx = 0f;
        float ty = 0f;
        boolean polygonMode = false;
        Path2D path = null;
    }

}
