/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.geotoolkit.display2d.canvas.J2DCanvasComponentAdapter;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.geotoolkit.gui.swing.map.map2d.AbstractMap2D;

import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel
 */
public class J2DMap extends AbstractMap2D implements GoMap2D{

    
    private CanvasHandler handler;
    
    private final J2DCanvasComponentAdapter canvas;
    private final ContextContainer2D renderer;
        
    public J2DMap(){
        super();
        canvas = new J2DCanvasComponentAdapter(DefaultGeographicCRS.WGS84,this);
        renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
        canvas.getController().setAutoRepaint(true);
        canvas.setRenderingHint(GO2Hints.KEY_GENERALIZE, true);
        canvas.setRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER, true);

        canvas.addCanvasListener(new CanvasListener() {

            @Override
            public void canvasChanged(CanvasEvent event) {
                if(RenderingState.ON_HOLD.equals(event.getNewRenderingstate())){
                    getInformationDecoration().setPaintingIconVisible(false);
                }else if(RenderingState.RENDERING.equals(event.getNewRenderingstate())){
                    getInformationDecoration().setPaintingIconVisible(true);
                }else{
                    getInformationDecoration().setPaintingIconVisible(false);
                }
            }
        });

        try {
            canvas.setObjectiveCRS(DefaultGeographicCRS.WGS84);
        } catch (TransformException ex) {
            Logger.getLogger(J2DMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {        
        super.paintComponent(g);
        Graphics2D output = (Graphics2D) g;     
        canvas.paint(output);
    }

    @Override
    public J2DCanvas getCanvas() {
        return canvas;
    }
    
    public ContextContainer2D getRenderer(){
        return renderer;
    }
    

    @Override
    public void dispose() {
        canvas.dispose();
    }
    
    
    @Override
    public CanvasHandler getHandler(){
        return handler;
    }

    @Override
    public void setHandler(CanvasHandler handler){

        if(this.handler != handler) {
            //TODO : check for possible vetos

            final CanvasHandler old = this.handler;

            if (this.handler != null){
                this.handler.uninstall(this);
            }

            this.handler = handler;

            if (this.handler != null) {
                this.handler.install(this);
            }

//            propertyListeners.firePropertyChange(HANDLER_PROPERTY, old, handler);
        }

    }

        }
