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

import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.SwingVolatileGeoComponent;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;

import org.geotoolkit.util.logging.Logging;
import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JMap2D extends AbstractMap2D{

    private static final Logger LOGGER = Logging.getLogger(JMap2D.class);
    
    private CanvasHandler handler;
    private final SwingVolatileGeoComponent geoComponent;

    public JMap2D(){
        this(false);
    }

    public JMap2D(boolean statefull){
        super();
        geoComponent = new SwingVolatileGeoComponent(DefaultGeographicCRS.WGS84);
        setMapComponent(geoComponent);
        final ReferencedCanvas2D canvas = geoComponent.getCanvas();
        canvas.setContainer(new DefaultContextContainer2D(canvas, statefull));
        canvas.getController().setAutoRepaint(true);
//        canvas.setRenderingHint(GO2Hints.KEY_GENERALIZE, true);
//        canvas.setRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER, true);

        canvas.addCanvasListener(new CanvasListener() {

            @Override
            public void canvasChanged(CanvasEvent event) {

                if(canvas.getController().isAutoRepaint()){
                    //dont show the painting icon if the cans is in auto render mode
                    // since it may repaint dynamic graphic it would show up all the time
                    return;
                }

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
            LOGGER.log(Level.SEVERE, null, ex);
        }
                
    }

    @Override
    public J2DCanvas getCanvas() {
        return geoComponent.getCanvas();
    }

    public void setStatefull(boolean stateFull){
        MapContext context = getContainer().getContext();
        ContextContainer2D container = new DefaultContextContainer2D(geoComponent.getCanvas(), stateFull);
        container.setContext(context);
        geoComponent.getCanvas().setContainer(container);
    }

    public ContextContainer2D getContainer(){
        return (ContextContainer2D) geoComponent.getCanvas().getContainer();
    }
    
    @Override
    public void dispose() {
        geoComponent.dispose();
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
