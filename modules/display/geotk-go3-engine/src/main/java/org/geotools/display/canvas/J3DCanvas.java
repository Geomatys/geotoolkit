/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.display.canvas;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


import javax.media.opengl.GLAutoDrawable;
import org.geotoolkit.display.canvas.ReferencedCanvas;
import org.geotoolkit.display.primitive.ReferencedGraphic;
import org.geotoolkit.display.container.AbstractContainer;
import org.geotoolkit.display.container.AbstractContainer2D;

import org.geotools.display.primitive.Graphic3D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;

import org.opengis.display.container.ContainerEvent;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author axel
 */
public abstract class J3DCanvas extends ReferencedCanvas {

    
    protected J3DCanvas(CoordinateReferenceSystem crs,Hints hints){
        super(crs,hints);
    }

    /**
     * {@inheritDoc }
     * signaux graphiques
     */
    @Override
    protected void graphicsDisplayChanged(final ContainerEvent event) {


    }


    protected void render(GLAutoDrawable drawable){

        Collection <Graphic> graphics =  getContainer().graphics(); // recuper tout les objets graphiques
        //System.out.println(graphics.size());
        for (Graphic graphic : graphics) {
            if (graphic instanceof Graphic3D){
                  ((Graphic3D)graphic).paint(drawable); // draw mes objets graphique
            }
        }
        
        /*
         * Draw all graphics, starting with the one with the lowest <var>z</var> value. Before
         * to start the actual drawing,  we will notify all graphics that they are about to be
         * drawn. Some graphics may spend one or two threads for pre-computing data.
         */
//        for(final Graphic graphic : graphics){
//            if(monitor.stopRequested()){
//                return;
//            }
//
//            if(graphic instanceof GraphicJ2D){
//                ((GraphicJ2D) graphic).paint(context2D);
//            }
//        }
//
//        if(monitor.stopRequested()){
//            return;
//        }
//
//        //draw the labels
//        final LabelRenderer labelRenderer = context2D.getLabelRenderer(false);
//        if(labelRenderer != null){
//            try {
//                labelRenderer.portrayLabels();
//            } catch (TransformException ex) {
//                monitor.exceptionOccured(ex, Level.SEVERE);
//            }
//        }

    }

    



}





