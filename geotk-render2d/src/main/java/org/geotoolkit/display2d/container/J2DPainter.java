/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.Grid2DPresentation;
import org.geotoolkit.display2d.presentation.TextPresentation;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.renderer.GroupPresentation;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DPainter {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.display2d.container");

    public boolean paint(RenderingContext2D renderingContext, Stream<Presentation> presentations, boolean labelsOnTop) throws PortrayalException {

        boolean dataPainted = false;

        final List<TextPresentation> labels = new ArrayList<>();
        final Iterator<Presentation> iterator = presentations.iterator();
        while (iterator.hasNext()) {
            Presentation next = iterator.next();

            if (labelsOnTop && (next instanceof TextPresentation)) {
                labels.add((TextPresentation) next);
            } else {
                paint(renderingContext, next);
            }
            dataPainted = true;
        }

        if (labelsOnTop) {
            final LabelRenderer lr = renderingContext.getLabelRenderer(true);
            LabelLayer ll = lr.createLabelLayer();
            for (TextPresentation tp : labels) {
                ll.labels().add(tp.labelDesc);
            }
            lr.append(ll);
            try {
                lr.portrayLabels();
            } catch (TransformException ex) {
                throw new PortrayalException(ex.getMessage(), ex);
            }
        }

        return dataPainted;
    }

    public void paint(RenderingContext2D renderingContext, Presentation presentation) throws PortrayalException {
        if (presentation instanceof Grid2DPresentation) {
            final Grid2DPresentation gp = (Grid2DPresentation) presentation;
            gp.paint(renderingContext);
        }

        if (presentation instanceof GroupPresentation) {
            GroupPresentation gp = (GroupPresentation) presentation;
            for (Presentation p : gp.elements()) {
                paint(renderingContext, p);
            }
        }

        logIfError(presentation);
    }

    public boolean hit(RenderingContext2D renderingContext, final SearchAreaJ2D search, Presentation presentation) {
        if (presentation instanceof Grid2DPresentation) {
            final Grid2DPresentation gp = (Grid2DPresentation) presentation;
            return gp.hit(renderingContext, search);
        }

        if (presentation instanceof GroupPresentation) {
            GroupPresentation gp = (GroupPresentation) presentation;
            for (Presentation p : gp.elements()) {
                if (hit(renderingContext, search, p)) {
                    return true;
                }
            }
            return false;
        }

        logIfError(presentation);
        return false;
    }

    private static void logIfError(Presentation presentation) {
        if (presentation instanceof ExceptionPresentation) {
            ExceptionPresentation exP = (ExceptionPresentation) presentation;
            Exception ex = exP.getException();
            if (ex != null) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
}
