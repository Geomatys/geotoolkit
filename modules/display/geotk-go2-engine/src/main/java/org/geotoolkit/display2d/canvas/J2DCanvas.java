/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.AbstractReferencedCanvas2D;
import org.geotoolkit.display.canvas.DefaultCanvasController2D;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.primitive.ReferencedGraphic;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.display.container.ContainerEvent;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class J2DCanvas extends AbstractReferencedCanvas2D{

    protected final CanvasController2D controller = new DefaultCanvasController2D(this);
    protected final DefaultRenderingContext2D context2D = new DefaultRenderingContext2D(this);

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

    /**
     * {@inheritDoc }
     */
    @Override
    public CanvasController2D getController() {
        return controller;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AbstractContainer2D getContainer() {
        return (AbstractContainer2D) super.getContainer();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void graphicsDisplayChanged(final ContainerEvent event) {
        for(Graphic gra : event.getGraphics()){
            if(gra instanceof GraphicJ2D){
                final GraphicJ2D j2d = (GraphicJ2D) gra;
                final Rectangle rect = j2d.getDisplayBounds().getBounds();
                if(getController().isAutoRepaint()){
                    repaint(rect);
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        context2D.dispose();
    }

    protected Shape getObjectiveBounds() throws TransformException{
        final MathTransform2D transform = (MathTransform2D) getObjectiveToDisplay().inverse();
        final Shape bounds = getDisplayBounds();
        return transform.createTransformedShape(bounds);
    }

    /**
     * Prepare the renderingContext before painting, this will initialize the context
     * with the correct bounds and transform datas.
     * You may provide a null Graphic2D if you need to prepare a context for only a "hit"
     * operation.
     */
    public DefaultRenderingContext2D prepareContext(final DefaultRenderingContext2D context,
            final Graphics2D output, Shape paintingDisplayShape){

        final Shape canvasDisplayShape = getDisplayBounds();

        final AffineTransform2D objToDisp = getObjectiveToDisplay();

        if(output != null) output.addRenderingHints(hints);

        final Shape canvasObjectShape;

        try {
            canvasObjectShape = getObjectiveBounds();
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            //we can not continue with this kind of error
            // nothing can be redered with this shape
            return null;
        }

        final Shape paintingObjectiveShape;

        if(paintingDisplayShape == null){
            paintingDisplayShape = canvasDisplayShape;
            paintingObjectiveShape = canvasObjectShape;
        }else{
            try {
                AffineTransform dispToObj = objToDisp.createInverse();
                paintingObjectiveShape = dispToObj.createTransformedShape(paintingDisplayShape);
            } catch (NoninvertibleTransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                //we can not continue with this kind of error
                // nothing can be redered with this shape
                return null;
            }
        }

        //grab the dpi
        Number dpi = (Number)getRenderingHint(GO2Hints.KEY_DPI);
        if(dpi == null){
            dpi = 90;
        }

        context.initParameters(
                objToDisp,
                monitor,
                paintingDisplayShape,
                paintingObjectiveShape,
                canvasDisplayShape,
                canvasObjectShape,
                dpi.doubleValue());
        if(output != null) context.initGraphic(output);

        return context;
    }

    protected void render(final RenderingContext2D context2D, final List<Graphic> graphics){

        /*
         * Draw all graphics, starting with the one with the lowest <var>z</var> value. Before
         * to start the actual drawing,  we will notify all graphics that they are about to be
         * drawn. Some graphics may spend one or two threads for pre-computing data.
         */
        for(final Graphic graphic : graphics){
            if(monitor.stopRequested()){
                return;
            }

            if(graphic instanceof GraphicJ2D){
                ((GraphicJ2D) graphic).paint(context2D);
            }
        }

        if(monitor.stopRequested()){
            return;
        }

        //draw the labels
        final LabelRenderer labelRenderer = context2D.getLabelRenderer(false);
        if(labelRenderer != null){
            try {
                labelRenderer.portrayLabels();
            } catch (TransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }

    }

    /**
     * Visit the {@code Graphics} that occupy the given shape.
     * You should give an Area Object if you can, this will avoid many creation
     * while testting.
     */
    public void getGraphicsIn(final Shape displayShape, final GraphicVisitor visitor, final VisitFilter filter) {
        ensureNonNull("mask", displayShape);
        ensureNonNull("visitor", visitor);
        ensureNonNull("filter", filter);

        visitor.startVisit();

        final AbstractContainer container = (AbstractContainer) getContainer();

        if(container != null){

            final List<Graphic> candidates = new ArrayList<Graphic>();

            final DefaultRenderingContext2D searchContext = context2D;
            prepareContext(searchContext,null,null);

            final AffineTransform dispToObj = searchContext.getDisplayToObjective();

            final Shape objectiveShape = dispToObj.createTransformedShape(displayShape);
            final com.vividsolutions.jts.geom.Geometry displayGeometryJTS = GO2Utilities.toJTS(displayShape);
            final com.vividsolutions.jts.geom.Geometry objectiveGeometryJTS = GO2Utilities.toJTS(objectiveShape);
            final Geometry displayGeometryISO = JTSUtils.toISO(displayGeometryJTS, getDisplayCRS());
            final Geometry objectiveGeometryISO = JTSUtils.toISO(objectiveGeometryJTS, getObjectiveCRS2D());
            final SearchAreaJ2D searchMask = new DefaultSearchAreaJ2D(
                    objectiveGeometryISO, displayGeometryISO,
                    objectiveGeometryJTS, displayGeometryJTS,
                    objectiveShape, displayShape);

            if(container instanceof AbstractContainer2D){
                final AbstractContainer2D r2d = (AbstractContainer2D) container;
                //defensive copy
                final List<Graphic> sorted = new ArrayList<Graphic>(r2d.getSortedGraphics());
                //reverse the list order
                Collections.reverse(sorted);

                //see if the visitor request a stop-----------------------------
                if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                //--------------------------------------------------------------

                for(final Graphic graphic : sorted){
                    search(searchMask,searchContext,graphic,filter,candidates);

                    //see if the visitor request a stop-------------------------
                    if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                    //----------------------------------------------------------

                    //send the found graphics to the visitor
                    for(final Graphic candidate : candidates){
                        visitor.visit(candidate,searchContext,searchMask);

                        //see if the visitor request a stop---------------------
                        if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                        //------------------------------------------------------

                    }
                    //empty the list for next search
                    candidates.clear();
                }

            }else{
                //defensive copy
                final List<Graphic> lstGraphics = new ArrayList<Graphic>(container.graphics());

                //see if the visitor request a stop-----------------------------
                if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                //--------------------------------------------------------------

                for(final Graphic graphic : lstGraphics){
                    search(searchMask,searchContext,graphic,filter,candidates);

                    //see if the visitor request a stop-------------------------
                    if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                    //----------------------------------------------------------

                    //send the found graphics to the visitor
                    for(final Graphic candidate : candidates){
                        visitor.visit(candidate,searchContext,searchMask);

                        //see if the visitor request a stop---------------------
                        if(visitor.isStopRequested()){ visitor.endVisit(); return; }
                        //------------------------------------------------------

                    }
                    //empty the list for next search
                    candidates.clear();
                }
            }
        }

        visitor.endVisit();
    }

    private void search(final SearchAreaJ2D mask, final RenderingContext context, final Graphic graphic, final VisitFilter filter, final List<Graphic> lst){
        if(graphic instanceof ReferencedGraphic){
            final ReferencedGraphic ref = (ReferencedGraphic) graphic;
            ref.getGraphicAt(context, mask, filter, lst);
        }
    }

}
