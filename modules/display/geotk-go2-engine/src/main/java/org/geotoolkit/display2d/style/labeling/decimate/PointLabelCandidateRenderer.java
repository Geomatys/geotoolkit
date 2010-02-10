/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.display2d.style.labeling.decimate;

import org.geotoolkit.display2d.style.labeling.candidate.PointCandidate;
import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PointLabelCandidateRenderer implements LabelCandidateRenderer<PointLabelDescriptor>{

    private final RenderingContext2D context;
    private final Graphics2D g2;

    public PointLabelCandidateRenderer(RenderingContext2D context) {
        this.context = context;
        g2 = context.getGraphics();
    }

    @Override
    public Candidate generateCandidat(PointLabelDescriptor label) {

        Shape shape = null;

        try {
            shape = label.getGeometry().getDisplayShape();
        } catch (TransformException ex) {
            Logger.getLogger(PointLabelCandidateRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(shape == null) return null;


        final FontMetrics metric = g2.getFontMetrics(label.getTextFont());
        final int textUpper = metric.getAscent();
        final int textLower = metric.getDescent();
        final int textWidth = metric.stringWidth(label.getText());
        final Rectangle2D rect = shape.getBounds2D();

        float refX = (float) rect.getCenterX();
        float refY = (float) rect.getCenterY();
        refX = refX + label.getDisplacementX();
        refY = refY - label.getDisplacementY();

        refX = refX - (label.getAnchorX()*textWidth);
        //text is draw above reference point so use +
        refY = refY + (label.getAnchorY()*(textUpper));

        return new PointCandidate(label,
                textWidth,
                textUpper,
                textLower,
                refX,refY
                );
    }

    @Override
    public void render(Candidate candidate) {
        if(!(candidate instanceof PointCandidate)) return;

        final PointCandidate pointCandidate = (PointCandidate) candidate;
        final PointLabelDescriptor label = pointCandidate.getDescriptor();
        final double rotation = Math.toRadians(label.getRotation());

        context.switchToDisplayCRS();

        ////////////////////////////BBOX FOR TEST///////////////////////////////
//        g2.setStroke(new BasicStroke(1));
//        g2.setColor(Color.BLACK);
//        g2.rotate(rotation, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
//        g2.drawRect((int)pointCandidate.getCorrectedX(),
//                (int)pointCandidate.getCorrectedY()-pointCandidate.upper,
//                pointCandidate.width,
//                pointCandidate.upper+pointCandidate.lower);
//        g2.rotate(-rotation, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
        ////////////////////////////////////////////////////////////////////////


        //TODO draw a nice line if correction is important----------------------
//        g2.setColor(Color.RED);
//        g2.setStroke(new BasicStroke(1));
//        g2.drawLine((int)pointCandidate.x,
//                (int)pointCandidate.y,
//                (int)pointCandidate.getCorrectedX(),
//                (int)pointCandidate.getCorrectedY());

        //rotation--------------------------------------------------------------
        g2.rotate(rotation, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
        
        //paint halo------------------------------------------------------------
        final float haloWidth = label.getHaloWidth();
        if(haloWidth > 0){
            final FontRenderContext fontContext = g2.getFontRenderContext();
            final GlyphVector glyph = label.getTextFont().createGlyphVector(fontContext, label.getText());
            final Shape shape = glyph.getOutline(pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
            g2.setPaint(label.getHaloPaint());
            g2.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.draw(shape);
        }

        //paint text------------------------------------------------------------
        g2.setPaint(label.getTextPaint());
        g2.setFont(label.getTextFont());
        g2.drawString(label.getText(), pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
    }

}
