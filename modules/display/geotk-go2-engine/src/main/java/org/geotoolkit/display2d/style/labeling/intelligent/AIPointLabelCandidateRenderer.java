/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.style.labeling.intelligent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display2d.canvas.RenderingContext2D;

import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AIPointLabelCandidateRenderer implements LabelCandidateRenderer<PointLabelDescriptor>{

    private final RenderingContext2D context;
    private final Graphics2D g2;

    public AIPointLabelCandidateRenderer(RenderingContext2D context) {
        this.context = context;
        g2 = context.getGraphics();
    }

    @Override
    public Candidate generateOptimalCandidat(PointLabelDescriptor label) {

        Shape shape = null;

        try {
            shape = label.getGeometry().getDisplayShape();
        } catch (TransformException ex) {
            Logger.getLogger(AIPointLabelCandidateRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(shape == null) return null;


        final FontMetrics metric = g2.getFontMetrics(label.getTextFont());
        final int textHeight = metric.getHeight();
        final int textWidth = metric.stringWidth(label.getText());
        final Rectangle2D rect = shape.getBounds2D();

        float refX = (float) rect.getCenterX();
        float refY = (float) rect.getCenterY();
        refX = refX + label.getDisplacementX();
        refY = refY - label.getDisplacementY();

        refX = refX - (label.getAnchorX()*textWidth);
        //text is draw above reference point so use +
        refY = refY + (label.getAnchorY()*textHeight);

        refY = refY - (textHeight);


        return new PointCandidate(label, shape, new Point2D.Double(refX, refY), textHeight, textWidth);
    }

    @Override
    public List<Candidate> generateCandidats(PointLabelDescriptor descriptor) {
        return Collections.singletonList(generateOptimalCandidat(descriptor));
    }

    @Override
    public void render(Candidate candidate, PointLabelDescriptor label) {
        context.switchToDisplayCRS();

        PointCandidate pc = (PointCandidate) candidate;

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
        g2.rotate(Math.toRadians(pc.getAlpha()), pc.getPoint().getX(), pc.getPoint().getY());
        g2.drawRect((int)pc.getPoint().getX(), (int)pc.getPoint().getY(), (int)pc.getWidth(), (int)pc.getHeight());
        g2.rotate(-Math.toRadians(pc.getAlpha()), pc.getPoint().getX(), pc.getPoint().getY());

        final FontMetrics metric = g2.getFontMetrics(label.getTextFont());
        final int textHeight = metric.getHeight();
        final int textWidth = metric.stringWidth(label.getText());
        final Rectangle2D rect = candidate.getShape().getBounds2D();
        float refX = (float) rect.getCenterX();
        float refY = (float) rect.getCenterY();
//
        //adjust displacement---------------------------------------------------
        //displacement is oriented above and to the right
        refX = refX + label.getDisplacementX();
        refY = refY - label.getDisplacementY();

        //adjust anchor---------------------------------------------------------
        refX = refX - (label.getAnchorX()*textWidth);
        //text is draw above reference point so use +
        refY = refY + (label.getAnchorY()*textHeight);

//        refY = refY - (textHeight-metric.getDescent());

        //rotation--------------------------------------------------------------
        final float rotate = (float) Math.toRadians(label.getRotation());
        g2.rotate(rotate, refX, refY);

        

        //paint halo------------------------------------------------------------
        final float haloWidth = label.getHaloWidth();
        if(haloWidth > 0){
            final FontRenderContext fontContext = g2.getFontRenderContext();
            final GlyphVector glyph = label.getTextFont().createGlyphVector(fontContext, label.getText());
            final Shape shape = glyph.getOutline(refX,refY);
            g2.setPaint(label.getHaloPaint());
            g2.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.draw(shape);
        }

        //paint text------------------------------------------------------------
        g2.setPaint(label.getTextPaint());
        g2.setFont(label.getTextFont());
        g2.drawString(label.getText(), refX, refY);
    }

}
