/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.container.stateless;

import org.geotoolkit.display.canvas.VisitFilter;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display2d.canvas.GO2Hints;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.container.MultiThreadedRendering;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.CollectionChangeEvent;

import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.operation.TransformException;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatelessContextJ2D extends GraphicJ2D{

    private static final Logger LOGGER = Logger.getLogger(StatelessContextJ2D.class.getName());

    private final Map<MapLayer, GraphicJ2D> layerGraphics = new HashMap<MapLayer, GraphicJ2D>();
    
    private final ContextListener contextListener = new ContextListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
        }

        @Override
        public void layerChange(CollectionChangeEvent<MapLayer> event) {
            final int type = event.getType();

            if(CollectionChangeEvent.ITEM_ADDED == type){
                for(final MapLayer layer : event.getItems()){
                    parseLayer(layer, context.layers().indexOf(layer));
                }
                //change other layers indexes
                final List<MapLayer> layers = context.layers();
                for(int i=0,n=layers.size(); i<n; i++){
                    final MapLayer layer = layers.get(i);
                    final GraphicJ2D gra = layerGraphics.get(layer);
                    gra.setZOrderHint(i);
                }
            }else if(CollectionChangeEvent.ITEM_REMOVED == type){
                for(final MapLayer layer : event.getItems()){
                    final GraphicJ2D gra = layerGraphics.get(layer);
                    gra.dispose();
                    //remove the graphic
                    layerGraphics.remove(layer);
                }
                //change other layers indexes
                final List<MapLayer> layers = context.layers();
                for(int i=0,n=layers.size(); i<n; i++){
                    final MapLayer layer = layers.get(i);
                    final GraphicJ2D gra = layerGraphics.get(layer);
                    gra.setZOrderHint(i);
                }
            }else if(CollectionChangeEvent.ITEM_CHANGED == type){
            }
        }
    };

    private final MapContext context;

    public StatelessContextJ2D(final ReferencedCanvas2D canvas, final MapContext context) {
        super(canvas, context.getCoordinateReferenceSystem());

        if(context == null){
            throw new NullPointerException("Mapcontext can not be null");
        }

        this.context = context;

        try {
            setEnvelope(context.getBounds());
        } catch (TransformException ex) {
            Logger.getLogger(StatelessContextJ2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StatelessContextJ2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        parseContext(this.context);
        this.context.addContextListener(contextListener);
    }
   
    public MapContext getContext(){
        return context;
    }

    @Override
    public void dispose() {
        super.dispose();
        context.removeContextListener(contextListener);

        for(GraphicJ2D graphic : layerGraphics.values()){
            graphic.dispose();
        }
        layerGraphics.clear();
    }

    // create graphics ---------------------------------------------------------

    private void parseContext(final MapContext context){
        final List<MapLayer> layers = context.layers();
        for(int i=0,n=layers.size(); i<n; i++){
            final MapLayer layer = layers.get(i);
            parseLayer(layer,i);
        }

    }

    private GraphicJ2D parseLayer(final MapLayer layer,int index){

        if(layer instanceof DynamicMapLayer){
            final StatelessDynamicLayerJ2D g2d = new StatelessDynamicLayerJ2D(getCanvas(), (DynamicMapLayer)layer);
            g2d.setParent(this);
            g2d.setZOrderHint(index);
            layerGraphics.put(layer, g2d);
            return g2d;
        }else if (layer instanceof FeatureMapLayer){
            final StatelessFeatureLayerJ2D g2d = new StatelessFeatureLayerJ2D(getCanvas(), (FeatureMapLayer)layer);
            g2d.setParent(this);
            g2d.setZOrderHint(index);
            layerGraphics.put(layer, g2d);
            return g2d;
        }else if (layer instanceof CoverageMapLayer){
            final StatelessCoverageLayerJ2D g2d = new StatelessCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)layer);
            g2d.setParent(this);
            g2d.setZOrderHint(index);
            layerGraphics.put(layer, g2d);
            return g2d;
        }else{
            return null;
        }

    }

    // painting methods --------------------------------------------------------
    @Override
    public void paint(final RenderingContext2D renderingContext) {

        Boolean multithread = (Boolean) canvas.getRenderingHint(GO2Hints.KEY_MULTI_THREAD);
        if(multithread == null) multithread = Boolean.FALSE;
                
        if( GO2Hints.MULTI_THREAD_ON.equals(multithread) ){
            // Multithreading ------------------------------------------------------
            final MultiThreadedRendering renderingThreads = new MultiThreadedRendering(
                    getCanvas(),context, layerGraphics, renderingContext);
            renderingThreads.render();
            
        }else{
            // No Multithreading ---------------------------------------------------
            for(final MapLayer layer : context.layers()){
                if(renderingContext.getMonitor().stopRequested()) break;
                final GraphicJ2D gra = layerGraphics.get(layer);
                if(gra != null){
                    layerGraphics.get(layer).paint(renderingContext);
                }else{
                    LOGGER.log(Level.WARNING, "GrahicContextJ2D, paint method : strange, no graphic object affected to layer :" + layer.getName());
                }
                
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext rdcontext, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        List<MapLayer> layers = context.layers();
        for(int i=layers.size()-1; i>=0; i--){
            final MapLayer layer = layers.get(i);
            if(layer.isVisible()){
                final GraphicJ2D gra = layerGraphics.get(layer);
                graphics = gra.getGraphicAt(rdcontext,mask,filter,graphics);
            }
        }
        return graphics;
    }

}
