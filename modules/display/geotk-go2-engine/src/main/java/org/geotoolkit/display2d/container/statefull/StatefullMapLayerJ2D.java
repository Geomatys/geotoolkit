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
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullMapLayerJ2D extends StatefullMapItemJ2D<MapLayer> {

    private final Updater updater = new Updater();
    private volatile GridCoverage2D buffer = null;
    
    public StatefullMapLayerJ2D(J2DCanvas canvas, StatefullMapItemJ2D parent, MapLayer item) {
        super(canvas, parent, item);
    }

    @Override
    public void paint(RenderingContext2D renderingContext) {
        super.paint(renderingContext);
        
        if(!item.isVisible()) return;
        
        
        final Envelope env = renderingContext.getCanvasObjectiveBounds();
        final Envelope env2d = renderingContext.getCanvasObjectiveBounds2D();
        final Dimension rect = renderingContext.getCanvasDisplayBounds().getSize();
        updater.updateRequest(env, env2d, rect);
        
        GridCoverage2D coverage = this.buffer;        
        if(coverage == null) return;
                
        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();
        
        final RenderedImage img = coverage.getRenderedImage();
        final AffineTransform2D trs = (AffineTransform2D) coverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
                
        final Graphics2D g = renderingContext.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)item.getOpacity()));
        g.drawRenderedImage(img, trs);
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
            updater.update();
        }
    }
    
    
    
    private class Updater implements Runnable{
        
        private boolean submitted = false;
        private Envelope env;
        private Envelope env2d;
        private Dimension dim;
        
        private void updateRequest(Envelope env, Envelope env2d, Dimension dim){
            boolean mustUpdate = false;
            synchronized(Updater.this){
                if(this.env == null || !this.env.equals(env) ||
                   this.env2d == null || !this.env2d.equals(env2d) ||
                   this.dim == null || !this.dim.equals(dim)){
                    mustUpdate = true;
                }
                this.env = env;
                this.env2d = env2d;
                this.dim = dim;
            }
            if(mustUpdate) update();
        }
        
        private void update(){
            if(submitted){
                //already going to be updated
                return;
            }
            updater.submitted = true;
            getExecutor().submit(this);
        }
        
        @Override
        public void run() {
            submitted = false;
            try{
                final MapContext context = MapBuilder.createContext();
                context.items().add(item);

                final Envelope env;
                final Envelope env2d;
                final Dimension dim;
                synchronized(Updater.this){
                    env = this.env;
                    env2d = this.env2d;
                    dim = this.dim;
                }
                if(env == null) return;
                
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
        }
        
    }
    
}
