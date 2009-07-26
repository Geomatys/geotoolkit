/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.primitive.ReferencedGraphic;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;

import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.logging.Logging;
import org.opengis.display.container.ContainerEvent;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class J2DCanvas extends ReferencedCanvas2D{

    protected BackgroundPainter painter = null;
    
    protected J2DCanvas(CoordinateReferenceSystem crs,Hints hints){
        super(crs,hints);
    }

    public void setBackgroundPainter(BackgroundPainter painter) {
        this.painter = painter;
    }

    public BackgroundPainter getBackgroundPainter() {
        return painter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void graphicsDisplayChanged(final ContainerEvent event) {
        for(Graphic gra : event.getGraphics()){
            if(gra instanceof GraphicJ2D){
                GraphicJ2D j2d = (GraphicJ2D) gra;
                Rectangle rect = j2d.getDisplayBounds().getBounds();
                repaint(rect);
            }
        }
    }

    /**
     * Prepare the renderingContext before painting, this will initialize the context
     * with the correct bounds and transform datas.
     * You may provide a null Graphic2D if you need to prepare a context for only a "hit"
     * operation.
     */
    public DefaultRenderingContext2D prepareContext(final DefaultRenderingContext2D context, final Graphics2D output, Shape paintingDisplayShape){

        final Shape canvasDisplayShape = getDisplayBounds();
        
        //correct the displayToDevice transform
        GraphicsConfiguration gc = (output != null) ? output.getDeviceConfiguration() : null;
        if(gc != null){
            displayToDevice = new AffineMatrix3(gc.getNormalizingTransform());
        }else{
            displayToDevice = new AffineMatrix3();
        }


        final AffineMatrix3 old =  previousObjectiveToDisplay.clone();
        AffineMatrix3 objToDisp = null;

        //retrieve an affineTransform that will not be modify
        // while rendering
        try{
            objToDisp = setObjectiveToDisplayTransform(canvasDisplayShape.getBounds());
        }catch(TransformException exception){
            exception.printStackTrace();
            return null;
        }

        //notify graphics that the affine changed
        if( !old.equals(objToDisp) ){
            propertyListeners.firePropertyChange(AbstractCanvas.OBJECTIVE_TO_DISPLAY_PROPERTY, old, objToDisp);
        }
        
        if(output != null) output.addRenderingHints(hints);

        
        final Shape canvasObjectShape;
        
        try { 
            canvasObjectShape = getObjectiveBounds();
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.SEVERE);
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
                monitor.exceptionOccured(ex, Level.SEVERE);
                //we can not continue with this kind of error
                // nothing can be redered with this shape
                return null;
            }
        }

        context.initParameters(new AffineTransform2D(objToDisp), monitor, paintingDisplayShape, paintingObjectiveShape, canvasDisplayShape, canvasObjectShape);
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
                monitor.exceptionOccured(ex, Level.SEVERE);
            }
        }
        
    }

    /**
     * Visit the {@code Graphics} that occupy the given shape.
     * You should give an Area Object if you can, this will avoid many creation
     * while testting.
     */
    public void getGraphicsIn(final Shape displayShape, final GraphicVisitor visitor, final VisitFilter filter) {
        if(displayShape == null) throw new NullPointerException("Mask can not be null");
        if(visitor == null)      throw new NullPointerException("Visitor can not be null");
        if(filter == null)       throw new NullPointerException("Filter can not be null");

        visitor.startVisit();

        final AbstractContainer container = getContainer();

        if(container != null){

            final List<Graphic> candidates = new ArrayList<Graphic>();

            final DefaultRenderingContext2D searchContext = (DefaultRenderingContext2D)getRenderingContext();
            prepareContext(searchContext,null,null);

            final AffineTransform dispToObj = new AffineTransform(searchContext.getObjectiveToDisplay());
            try {
                dispToObj.invert();
            } catch (NoninvertibleTransformException ex) {
                Logging.getLogger(J2DCanvas.class).log(Level.SEVERE, null, ex);
            }

            final Shape objectiveShape = dispToObj.createTransformedShape(displayShape);
            final com.vividsolutions.jts.geom.Geometry displayGeometryJTS = GO2Utilities.toJTS(displayShape);
            final com.vividsolutions.jts.geom.Geometry objectiveGeometryJTS = GO2Utilities.toJTS(objectiveShape);
            final Geometry displayGeometryISO = JTSUtils.toISO(displayGeometryJTS, getDisplayCRS());
            final Geometry objectiveGeometryISO = JTSUtils.toISO(objectiveGeometryJTS, getObjectiveCRS());
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
                        visitor.visit(candidate,displayShape);

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
                        visitor.visit(candidate,displayShape);

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

    private void search(SearchAreaJ2D mask, RenderingContext context, Graphic graphic, VisitFilter filter, List<Graphic> lst){
        if(graphic instanceof ReferencedGraphic){
            final ReferencedGraphic ref = (ReferencedGraphic) graphic;
            ref.getGraphicAt(context, mask, filter, lst);
        }
    }

}
