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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import org.geotoolkit.display2d.style.labeling.candidate.PointCandidate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PointLabelCandidateRenderer implements LabelCandidateRenderer<PointLabelDescriptor>{

    private static final Candidate[] EMPTY = new Candidate[0];

    private final RenderingContext2D context;
    private final Graphics2D g2;

    public PointLabelCandidateRenderer(final RenderingContext2D context) {
        this.context = context;
        g2 = context.getGraphics();
    }

    @Override
    public Candidate[] generateCandidat(final PointLabelDescriptor label) {

        Geometry[] shapes = null;

        try {
            shapes = label.getGeometry().getDisplayGeometryJTS();
        } catch (TransformException ex) {
            Logging.getLogger("org.geotoolkit.display2d.style.labeling.decimate").log(Level.WARNING, null, ex);
        }
        if (shapes == null) return null;

        final List<Candidate> candidates = new ArrayList<>(shapes.length);

        for (int i = 0; i < shapes.length; i++) {
            final Geometry shape = shapes[i];
            final Point pt = GO2Utilities.getBestPoint(shape);
            if (pt == null || pt.isEmpty()) continue;
            final Coordinate point = pt.getCoordinate();

            final FontMetrics metric = context.getFontMetrics(label.getTextFont());
            final int textUpper = metric.getAscent();
            final int textLower = metric.getDescent();
            final int textWidth = metric.stringWidth(label.getText());

            float refX = (float) point.x;
            float refY = (float) point.y;
            refX = refX + label.getDisplacementX();
            refY = refY - label.getDisplacementY();

            refX = refX - (label.getAnchorX() * textWidth);
            //text is draw above reference point so use +
            refY = refY + (label.getAnchorY() * textUpper);

            candidates.add(new PointCandidate(label,
                    textWidth,
                    textUpper,
                    textLower,
                    refX,refY
                    ));
        }

        return candidates.toArray(EMPTY);
    }

    @Override
    public void render(final Candidate candidate) {
        if (!(candidate instanceof PointCandidate)) return;

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
        if (rotation != 0) {
            g2.rotate(rotation, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
        }

        final String text = label.getText();
        final Font font = label.getTextFont();
        g2.setFont(font);

        //paint halo------------------------------------------------------------
        final float haloWidth = label.getHaloWidth();
        if (haloWidth > 0) {
            final float haloWidth2 = haloWidth+haloWidth;
            final GlyphVector glyphVector = font.createGlyphVector(g2.getFontRenderContext(), text);
            final Rectangle2D bounds = glyphVector.getVisualBounds();
            Shape shape = new RoundRectangle2D.Double(
                    bounds.getMinX() + pointCandidate.getCorrectedX() - haloWidth,
                    bounds.getMinY() + pointCandidate.getCorrectedY() - haloWidth,
                    bounds.getWidth() + haloWidth2,
                    bounds.getHeight()+ haloWidth2,
                    2+haloWidth2,
                    2+haloWidth2);

            g2.setPaint(label.getHaloPaint());
            g2.fill(shape);
        }

        //paint text------------------------------------------------------------
        g2.setPaint(label.getTextPaint());
        g2.drawString(text, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());

        //reset rotation
        if (rotation != 0) {
            g2.rotate(-rotation, pointCandidate.getCorrectedX(), pointCandidate.getCorrectedY());
        }

    }

}
