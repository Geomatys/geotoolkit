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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JComponent;
import javax.swing.Timer;

import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SwingVolatileGeoComponent extends JComponent{

    private final J2DCanvasVolatile canvas;
    private Image img = null;

    /**
     * Updates the enclosing canvas according various AWT events.
     */
    private final ComponentListener listener = new ComponentAdapter(){

        /** Invoked when the component's size changes. */
        @Override public void componentResized(final ComponentEvent event) {
            synchronized (SwingVolatileGeoComponent.this) {
                canvas.resize(SwingVolatileGeoComponent.this.getSize());
            }
        }

    };

    public SwingVolatileGeoComponent(CoordinateReferenceSystem crs){

        final Timer timer = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    repaint();
            }
        });
        
        canvas = new J2DCanvasVolatile(crs,null);
        canvas.addCanvasListener(new CanvasListener() {

            @Override
            public void canvasChanged(CanvasEvent event) {
                if(RenderingState.ON_HOLD.equals(event.getNewRenderingstate())){
                    timer.stop();
                    repaint();
                }else if(RenderingState.RENDERING.equals(event.getNewRenderingstate())){
                        img = canvas.getVolatile();
                        timer.start();
                }else{
                    timer.stop();
                }
            }
        });

        addComponentListener(listener);
    }

    public J2DCanvas getCanvas(){
        return canvas;
    }

    @Override
    public void paintComponent(final Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, this);
        }
    }

    public void dispose(){
        canvas.dispose();
    }

}
