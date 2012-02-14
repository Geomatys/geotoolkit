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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.Timer;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SwingVolatileGeoComponent extends JComponent{

    private final J2DCanvasVolatile canvas;
    private GridCoverage2D coverage = null;

    /**
     * Updates the enclosing canvas according various AWT events.
     */
    private final ComponentListener listener = new ComponentAdapter(){

        /** Invoked when the component's size changes. */
        @Override
        public void componentResized(final ComponentEvent event) {
            synchronized (SwingVolatileGeoComponent.this) {                
                canvas.resize(SwingVolatileGeoComponent.this.getSize());
            }
        }

    };

    public SwingVolatileGeoComponent(final CoordinateReferenceSystem crs){

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
                if(RenderingState.RENDERING.equals(event.getNewRenderingstate())){
                    timer.start();
                }else{
                    final Object val = canvas.getRenderingHint(GO2Hints.KEY_BEHAVIOR_MODE);
                    
                    if(GO2Hints.BEHAVIOR_KEEP_TILE.equals(val) || GO2Hints.BEHAVIOR_ON_FINISH.equals(val)){
                        //create a buffer only if it was a successful paint
                        //otherwise reuse previous buffer
                        if(!canvas.getMonitor().stopRequested()){
                            try{
                                final RenderedImage buffer = (RenderedImage) canvas.getSnapShot();
                                if(buffer!=null){
                                    MathTransform2D aff = canvas.getController().getTransform();
                                    aff = aff.inverse();
                                    GridCoverageFactory gcf = new GridCoverageFactory();
                                    coverage = gcf.create("temp", buffer, canvas.getObjectiveCRS2D(), aff, null,null,null);
                                }
                            }catch(Exception ex){
                                Logger.getLogger(SwingVolatileGeoComponent.class.getName()).log(Level.INFO, ex.getMessage(), ex);
                                //we tryed
                            }
                        }
                    }else{
                        coverage = null;
                    }
                    
                    timer.stop();
                    repaint();
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
        
        final Object val = canvas.getRenderingHint(GO2Hints.KEY_BEHAVIOR_MODE);
                    
        if(val == null || GO2Hints.BEHAVIOR_PROGRESSIVE.equals(val) || GO2Hints.BEHAVIOR_KEEP_TILE.equals(val)){
            //progressive repaint
            final Image img = canvas.getVolatile();
            if (img != null) {
                g.drawImage(img, 0, 0, this);
            }
            
        }
        
        if (GO2Hints.BEHAVIOR_KEEP_TILE.equals(val) || GO2Hints.BEHAVIOR_ON_FINISH.equals(val)) {
            if (coverage != null) {
                //we want to render as if we where on the canvas
                final DefaultRenderingContext2D context = new DefaultRenderingContext2D(canvas);
                canvas.prepareContext(context, (Graphics2D) g, g.getClip());
                try {
                    GO2Utilities.portray(context, coverage);
                } catch (PortrayalException ex) {
                    Logger.getLogger(SwingVolatileGeoComponent.class.getName()).log(Level.INFO, ex.getMessage(), ex);
                }
            }
        }
        
    }

    public void dispose(){
        canvas.dispose();
    }

}
