/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.JComponent;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.factory.Hints;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Canvas directly painting on a swing component.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class J2DCanvasSwing extends J2DCanvas{

    private final JComponent component = new J2DComponent();
    private Envelope wishedEnvelope = null;

    public J2DCanvasSwing(final CoordinateReferenceSystem crs){
        this(crs,null);
    }

    public J2DCanvasSwing(final CoordinateReferenceSystem crs, final Hints hints){
        super(crs,hints);

        component.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                setDisplayBounds(new Rectangle(component.getWidth(), component.getHeight()));
                if(!component.getBounds().isEmpty()){
                    //first time we affect the size
                    if(wishedEnvelope!=null){
                        try {
                            setVisibleArea(wishedEnvelope);
                        } catch (NoninvertibleTransformException ex) {
                            getLogger().log(Level.SEVERE, null, ex);
                        } catch (TransformException ex) {
                            getLogger().log(Level.SEVERE, null, ex);
                        }
                        wishedEnvelope = null;
                    }
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

    }

    public JComponent getComponent(){
        return component;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getSnapShot(){
        return null;
    }

    @Override
    public void repaint(final Shape displayArea) {
        component.repaint();
    }

    private void paint(final Graphics2D output, final Shape displayArea){
        //finish any previous painting
        getMonitor().stopRendering();

        final Dimension dim = component.getSize();

        monitor.renderingStarted();
        fireRenderingStateChanged(RENDERING);


        Rectangle clipBounds = output.getClipBounds();
        /*
         * Sets a flag for avoiding some "refresh()" events while we are actually painting.
         * For example some implementation of the GraphicPrimitive2D.paint(...) method may
         * detects changes since the last rendering and invokes some kind of invalidate(...)
         * methods before the graphic rendering begin. Invoking those methods may cause in some
         * indirect way a call to GraphicPrimitive2D.refresh(), which will trig an other widget
         * repaint. This second repaint is usually not needed, since Graphics usually managed
         * to update their informations before they start their rendering. Consequently,
         * disabling repaint events while we are painting help to reduces duplicated rendering.
         */
        if (clipBounds == null) {
            clipBounds = new Rectangle(dim);
        }
        output.setClip(clipBounds);
        output.addRenderingHints(getHints(true));

        final RenderingContext2D context = prepareContext(context2D, output,null);

        //paint background if there is one.
        if(painter != null){
            painter.paint(context2D);
        }

        final GraphicContainer container = getContainer();
        if(container != null){
            render(context, container.flatten(true));
        }

        /**
         * End painting, erase dirtyArea
         */
        fireRenderingStateChanged(ON_HOLD);
        monitor.renderingFinished();
    }


    private final class J2DComponent extends JComponent{

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D output = (Graphics2D) g;
            final Shape displayArea = new Rectangle(getSize());
            J2DCanvasSwing.this.paint(output, displayArea);
        }

    }

    @Override
    public void setVisibleArea(final Envelope env) throws NoninvertibleTransformException, TransformException {
        if(component.getBounds().isEmpty()){
            //we don't know our size yet, store the information for later
            wishedEnvelope = env;
        }else{
            super.setVisibleArea(env);
        }
    }
}
