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

package org.geotoolkit.display2d.container.statefull;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullMapLayerJ2D<T extends MapLayer> extends StatefullMapItemJ2D<T> {

    protected volatile GridCoverage2D buffer = null;
    private volatile Updater updater = null;
    
    public StatefullMapLayerJ2D(J2DCanvas canvas, StatefullMapItemJ2D parent, T item) {
        super(canvas, parent, item);
    }

    @Override
    public void paint(RenderingContext2D renderingContext) {
        super.paint(renderingContext);
        
        if(!item.isVisible()) return;
        
        final Envelope env = renderingContext.getCanvasObjectiveBounds();
        final Envelope env2d = renderingContext.getCanvasObjectiveBounds2D();
        final Dimension rect = renderingContext.getCanvasDisplayBounds().getSize();
        updateRequest(env, env2d, rect);
        
        GridCoverage2D coverage = this.buffer;        
        if(coverage == null) return;
        
        final Graphics2D g = renderingContext.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)item.getOpacity()));
        try {
            GO2Utilities.portray(renderingContext, coverage);
        } catch (PortrayalException ex) {
            Logger.getLogger(StatefullMapLayerJ2D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        final String propName = event.getPropertyName();
        if (MapLayer.STYLE_PROPERTY.equals(propName)
                || MapLayer.SELECTION_FILTER_PROPERTY.equals(propName)
                || MapLayer.VISIBILITY_PROPERTY.equals(propName)
                || MapLayer.OPACITY_PROPERTY.equals(propName)
                || MapLayer.QUERY_PROPERTY.equals(propName) ){
            update();
        }
    }
    
    private final AtomicBoolean needUpdate = new AtomicBoolean();
    private Envelope env;
    private Envelope env2d;
    private Dimension dim;
    
    private synchronized void updateRequest(Envelope env, Envelope env2d, Dimension dim){
        boolean mustUpdate = false;
        if(this.env == null || !this.env.equals(env) ||
           this.env2d == null || !this.env2d.equals(env2d) ||
           this.dim == null || !this.dim.equals(dim)){
            mustUpdate = true;
        }
        this.env = env;
        this.env2d = env2d;
        this.dim = new Dimension(dim);
        if(mustUpdate){
            update();
        }
    }
    
    protected synchronized void update(){
        needUpdate.set(true);
        checkUpdater();
    }
    
    private synchronized void checkUpdater(){
        if(needUpdate.get() && updater == null){
            needUpdate.set(false);
            if(env != null){
                updater = new Updater(env, env2d, dim);
                getExecutor().execute(updater);
            }
        }
    }
    
    
    protected final class Updater implements Runnable{
        
        private Envelope env;
        private Envelope env2d;
        private Dimension dim;
        
        private Updater(Envelope env, Envelope env2d, Dimension dim){
            this.env = env;
            this.env2d = env2d;
            this.dim = dim;
        }
                
        @Override
        public void run() {            
            try{
                final MapContext context = MapBuilder.createContext();
                context.items().add(item);

                final CanvasDef cdef = new CanvasDef(dim, null);
                final ViewDef vdef = new ViewDef(env);
                final SceneDef sdef = new SceneDef(context);
                final BufferedImage img = DefaultPortrayalService.portray(cdef, sdef, vdef);
                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setEnvelope(env2d);
                gcb.setRenderedImage(img);
                buffer = gcb.getGridCoverage2D();
            }catch(Exception ex){
                ex.printStackTrace();
            }    
            getCanvas().repaint();
            
            updater = null;
            checkUpdater();
        }
        
    }
    
}
