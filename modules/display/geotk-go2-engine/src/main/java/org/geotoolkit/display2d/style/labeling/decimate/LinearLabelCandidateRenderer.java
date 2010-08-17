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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.j2d.TextStroke;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import org.geotoolkit.display2d.style.labeling.candidate.LinearCandidate;

import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LinearLabelCandidateRenderer implements LabelCandidateRenderer<LinearLabelDescriptor>{

    private final RenderingContext2D context;
    private final Graphics2D g2;

    public LinearLabelCandidateRenderer(RenderingContext2D context) {
        this.context = context;
        g2 = context.getGraphics();
    }

    @Override
    public Candidate generateCandidat(LinearLabelDescriptor descriptor) {
        try {
            return new LinearCandidate(descriptor,descriptor.getGeometry().getDisplayShape());
        } catch (TransformException ex) {
            Logger.getLogger(LinearLabelCandidateRenderer.class.getName()).log(Level.WARNING, null, ex);
        }

        return null;
    }

    @Override
    public void render(Candidate candidate) {
        if(!(candidate instanceof LinearCandidate)) return;

        LinearCandidate linearCandidate = (LinearCandidate) candidate;
        LinearLabelDescriptor label = linearCandidate.getDescriptor();

        context.switchToDisplayCRS();

        final TextStroke stroke = new TextStroke(label.getText(), label.getTextFont(), label.isRepeated(),
                label.getOffSet(), label.getInitialGap(), label.getGap(),context.getCanvasDisplayBounds());
        final Shape shape = stroke.createStrokedShape(linearCandidate.getShape());

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
