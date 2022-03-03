/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.canvas;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.map.Presentation;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.display2d.container.J2DPainter;
import org.geotoolkit.display2d.container.MapLayerJ2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.renderer.GroupPresentation;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class J2DCanvas extends AbstractCanvas2D{

    protected BackgroundPainter painter = null;

    protected J2DCanvas(final CoordinateReferenceSystem crs,final Hints hints) {
        super(crs,hints);
    }

    public void setBackgroundPainter(final BackgroundPainter painter) {
        this.painter = painter;
    }

    public BackgroundPainter getBackgroundPainter() {
        return painter;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Prepare the renderingContext before painting, this will initialize the context
     * with the correct bounds and transform datas.
     * You may provide a null Graphic2D if you need to prepare a context for only a "hit"
     * operation.
     */
    public RenderingContext2D prepareContext(final Graphics2D output) {

        if(output != null) output.addRenderingHints(getHints(true));

        //grab the dpi
        Number dpi = (Number)getRenderingHint(GO2Hints.KEY_DPI);
        if(dpi == null) {
            dpi = 90;
        }

        RenderingContext2D context = new RenderingContext2D(this,
                getGridGeometry(),
                getGridGeometry2D(),
                monitor,
                dpi.doubleValue());
        if (output != null) context.initGraphic(output);

        try {
            setVisibleArea(getVisibleEnvelope());
        } catch (NoninvertibleTransformException | TransformException e) {
            // TODO : log warning
        }

        return context;
    }

    protected boolean render(final RenderingContext2D context2D, final List<SceneNode> graphics){

        boolean dataPainted = false;
        /*
         * Draw all graphics, starting with the one with the lowest <var>z</var> value. Before
         * to start the actual drawing,  we will notify all graphics that they are about to be
         * drawn. Some graphics may spend one or two threads for pre-computing data.
         */
        Stream<Presentation> presentations = Stream.empty();
        for (final Graphic graphic : graphics) {
            if (monitor.stopRequested()) {
                return dataPainted;
            }

            if (graphic instanceof MapLayerJ2D) {
                try {
                    presentations = Stream.concat(presentations, ((MapLayerJ2D) graphic) .paintLayer(context2D));
                } catch (PortrayalException ex) {
                    monitor.exceptionOccured(ex, Level.INFO);
                } catch (DataStoreException ex) {
                    monitor.exceptionOccured(ex, Level.INFO);
                }
            } else if (graphic instanceof GraphicJ2D) {
                dataPainted |= ((GraphicJ2D) graphic).paint(context2D);
            }
        }

        final J2DPainter painter = new J2DPainter();
        try {
            dataPainted |= painter.paint(context2D, presentations, true);
        } catch (PortrayalException ex) {
            monitor.exceptionOccured(ex, Level.INFO);
        } finally {
            presentations.close();
        }

        if (monitor.stopRequested()) {
            return dataPainted;
        }

        //draw the labels : this does not include labels in the presentations
        final LabelRenderer labelRenderer = context2D.getLabelRenderer(false);
        if (labelRenderer != null) {
            try {
                dataPainted |= labelRenderer.portrayLabels();
            } catch (TransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }

        return dataPainted;
    }

    /**
     * Visit the {@code Graphics} that occupy the given shape.
     * You should give an Area Object if you can, this will avoid many creation
     * while testing.
     */
    public void getGraphicsIn(final Shape displayShape, final GraphicVisitor visitor) {
        ensureNonNull("mask", displayShape);
        ensureNonNull("visitor", visitor);

        visitor.startVisit();

        final GraphicContainer container = getContainer();

        if (container != null) {

            final RenderingContext2D searchContext = prepareContext(null);

            final SearchAreaJ2D searchMask = J2DCanvas.createSearchArea(searchContext, displayShape);

            final J2DPainter painter = new J2DPainter();

            final List<SceneNode> sorted = container.flatten(true);
            //reverse the list order
            Collections.reverse(sorted);

            //see if the visitor request a stop-----------------------------
            if (visitor.isStopRequested()){ visitor.endVisit(); return; }
            //--------------------------------------------------------------

            for (final Graphic graphic : sorted) {
                if (graphic instanceof MapLayerJ2D) {
                    try (Stream<Presentation> presentations = ((MapLayerJ2D) graphic).paintLayer(searchContext)) {
                        final Iterator<Presentation> iterator = presentations.iterator();
                        while (iterator.hasNext()) {
                            final Presentation presentation = iterator.next();
                            visitHit(searchContext, painter, searchMask, visitor, presentation);
                            if (visitor.isStopRequested()) {
                                visitor.endVisit();
                                return;
                            }
                        }
                    } catch (PortrayalException | DataStoreException ex) {
                        Logging.getLogger("org.geotoolkit.display2d").log(Level.INFO, ex.getMessage(), ex);
                    }
                }
            }
        }

        visitor.endVisit();
    }

    private void visitHit(RenderingContext2D context2D, J2DPainter painter, SearchAreaJ2D searchMask, GraphicVisitor visitor, Presentation presentation) {
        if (presentation instanceof GroupPresentation) {
            GroupPresentation gp = (GroupPresentation) presentation;
            for (Presentation p : gp.elements()) {
                visitHit(context2D, painter, searchMask, visitor, p);
            }
        } else {
            if (painter.hit(context2D, searchMask, presentation)) {
                visitor.visit(presentation, context2D, searchMask);
            }
        }
    }

    public static SearchAreaJ2D createSearchArea(RenderingContext2D searchContext, final Shape displayShape) {

        final AffineTransform dispToObj = searchContext.getDisplayToObjective();
        final CoordinateReferenceSystem displayCRS = searchContext.getDisplayCRS();
        final CoordinateReferenceSystem objectiveCRS2D = searchContext.getObjectiveCRS2D();

        final Shape objectiveShape = dispToObj.createTransformedShape(displayShape);
        final org.locationtech.jts.geom.Geometry displayGeometryJTS = GO2Utilities.toJTS(displayShape);
        final org.locationtech.jts.geom.Geometry objectiveGeometryJTS = GO2Utilities.toJTS(objectiveShape);
        final Geometry displayGeometryISO = JTSUtils.toISO(displayGeometryJTS, displayCRS);
        final Geometry objectiveGeometryISO = JTSUtils.toISO(objectiveGeometryJTS, objectiveCRS2D);
        return new DefaultSearchAreaJ2D(
                objectiveGeometryISO, displayGeometryISO,
                objectiveGeometryJTS, displayGeometryJTS,
                objectiveShape, displayShape);
    }

}
