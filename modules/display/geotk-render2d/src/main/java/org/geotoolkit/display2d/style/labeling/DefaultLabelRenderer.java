/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2015, Geomatys
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
package org.geotoolkit.display2d.style.labeling;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.j2d.TextStroke;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.referencing.operation.TransformException;

/**
 * Default implementation of label renderer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultLabelRenderer implements LabelRenderer{

    private final List<LabelLayer> layers = new ArrayList<>();
    protected RenderingContext2D context = null;

    public DefaultLabelRenderer() {
    }

    @Override
    public LabelLayer createLabelLayer() {
        return new DefaultLabelLayer(false, true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRenderingContext(final RenderingContext2D context){
        ensureNonNull("context", context);
        this.context = context;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RenderingContext2D getRenderingContext() {
        return context;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void append(final LabelLayer layer) {
        layers.add(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portrayLabels(){
        final Graphics2D g2 = context.getGraphics();
        //enable antialiasing for labels
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(final LabelLayer layer : layers){

            for(LabelDescriptor label : layer.labels()){
                if(label instanceof PointLabelDescriptor){
                    portray(g2, (PointLabelDescriptor)label);
                }else if(label instanceof LinearLabelDescriptor){
                    portray(g2, (LinearLabelDescriptor)label);
                }
            }
        }
        this.layers.clear();
    }

    private void portray(final Graphics2D g2, final PointLabelDescriptor label){
        context.switchToDisplayCRS();
        final FontMetrics metric = context.getFontMetrics(label.getTextFont());
        final int textHeight = metric.getHeight();
        final int textWidth = metric.stringWidth(label.getText());

        final Geometry[] geoms;
        try {
            //we don't use the display geometry because it is clipped to view area
            //it will move the real geometry centroid, we rendering tiles we don' want the
            //point to change from tile to tile
            geoms = label.getGeometry().getDisplayGeometryJTS();
        } catch (TransformException ex) {
            Logging.getLogger("org.geotoolkit.display2d.style.labeling").log(Level.WARNING, null, ex);
            return;
        }

        for(Geometry geom : geoms){
            //get most appropriate point
            final Point pt = GO2Utilities.getBestPoint(geom);
            if(pt==null) continue;
            final Coordinate point = pt.getCoordinate();

            float refX = (float)point.x;
            float refY = (float)point.y;

            //adjust displacement---------------------------------------------------
            //displacement is oriented above and to the right
            refX = refX + label.getDisplacementX();
            refY = refY - label.getDisplacementY();

            //rotation--------------------------------------------------------------
            final float rotate = (float) Math.toRadians(label.getRotation());
            g2.rotate(rotate, refX, refY);

            //adjust anchor---------------------------------------------------------
            refX = refX - (label.getAnchorX()*textWidth);
            //text is draw above reference point so use +
            refY = refY + (label.getAnchorY()*textHeight);

            g2.setFont(label.getTextFont());

            //paint halo------------------------------------------------------------
            final float haloWidth = label.getHaloWidth();
            if(label.getHaloWidth() > 0){
                final float haloWidth2 = haloWidth+haloWidth;
                final Rectangle2D bounds = metric.getStringBounds(label.getText(), g2);
                final Shape shape = new RoundRectangle2D.Double(
                        bounds.getMinX() + refX - haloWidth,
                        bounds.getMinY() + refY - haloWidth,
                        bounds.getWidth() + haloWidth2,
                        bounds.getHeight()+ haloWidth2,
                        2+haloWidth2,
                        2+haloWidth2);

                g2.setPaint(label.getHaloPaint());
                g2.fill(shape);
            }

            //paint text------------------------------------------------------------
            g2.setPaint(label.getTextPaint());
            g2.drawString(label.getText(), refX, refY);
        }

    }

    private void portray(final Graphics2D g2, final LinearLabelDescriptor label){
        context.switchToDisplayCRS();

        final TextStroke stroke = new TextStroke(label.getText(), label.getTextFont(), label.isRepeated(),
                label.getOffSet(), label.getInitialGap(), label.getGap(),context.getCanvasDisplayBounds());

        final Shape[] geoms;
        try {
            geoms = label.getGeometry().getDisplayShape();
        } catch (TransformException ex) {
            Logging.getLogger("org.geotoolkit.display2d.style.labeling").log(Level.WARNING, null, ex);
            return;
        }

        for(Shape geom : geoms){
            final Shape shape = stroke.createStrokedShape(geom);

            //paint halo
            if(label.getHaloWidth() > 0){
                g2.setStroke(new BasicStroke(label.getHaloWidth(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND) );
                g2.setPaint(label.getHaloPaint());
                g2.draw(shape);
            }

            //paint text
            g2.setStroke(new BasicStroke(0));
            g2.setPaint(label.getTextPaint());
            g2.fill(shape);
        }
    }

    @Override
    public void portrayImmidiately(final LabelLayer layer) {
        final Graphics2D g2 = context.getGraphics();
        //enable antialiasing for labels
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(LabelDescriptor label : layer.labels()){
            if(label instanceof PointLabelDescriptor){
                portray(g2, (PointLabelDescriptor)label);
            }else if(label instanceof LinearLabelDescriptor){
                portray(g2, (LinearLabelDescriptor)label);
            }
        }
    }

}
