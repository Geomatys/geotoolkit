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

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullMapItemJ2D<T extends MapItem> extends AbstractGraphicJ2D implements ItemListener{
    
    private final ItemListener.Weak weakListener = new ItemListener.Weak(this);

    /** Executor used to update graphics */
    private static final RejectedExecutionHandler LOCAL_REJECT_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy(); 
    private BlockingQueue queue;
    private ThreadPoolExecutor exec;
    
    
    //childs
    private final Map<MapItem, GraphicJ2D> itemGraphics = new HashMap<MapItem, GraphicJ2D>();
    protected final T item;

    public StatefullMapItemJ2D(final J2DCanvas canvas, final StatefullMapItemJ2D parent, final T item){
        super(canvas);
        super.setParent(parent);
        this.item = item;

        if(parent == null){
            queue = new ArrayBlockingQueue(100);
            exec = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 
                    1, TimeUnit.MINUTES, queue, LOCAL_REJECT_EXECUTION_HANDLER);
        }
        
        parseItem(this.item);
        weakListener.registerSource(item);
        
    }

    protected ThreadPoolExecutor getExecutor(){
        if(parent != null){
            return ((StatefullMapItemJ2D)parent).getExecutor();
        }
        return exec;
    }
    
    @Override
    public void setParent(Graphic parent) {
        throw new RuntimeException("Not allowed to modify parent in statefull mode.");
    }
    
    @Override
    public T getUserObject() {
        return item;
    }

    @Override
    public void dispose() {
        super.dispose();
        weakListener.dispose();

        for(GraphicJ2D graphic : itemGraphics.values()){
            graphic.dispose();
        }
        itemGraphics.clear();
        
        if (exec != null) {
           exec.shutdownNow();
           exec = null;
           queue = null;
        }
        
    }

    // create graphics ---------------------------------------------------------

    private void parseItem(final MapItem candidate){
        final List<MapItem> childs = candidate.items();
        for(int i=0,n=childs.size(); i<n; i++){
            final MapItem child = childs.get(i);
            final GraphicJ2D gj2d = parseChild(child, i);
            itemGraphics.put(child, gj2d);
        }

    }

    protected GraphicJ2D parseChild(final MapItem child, final int index){

        final StatefullMapItemJ2D g2d;
        if (child instanceof MapLayer){
            g2d = new StatefullMapLayerJ2D(getCanvas(), this, (MapLayer)child);
        }else{
            g2d = new StatefullMapItemJ2D(getCanvas(), this, child);
        }

        g2d.setZOrderHint(index);
        return g2d;
    }

    @Override
    public void paint(final RenderingContext2D renderingContext) {

        //we abort painting if the item is not visible.
        if (!item.isVisible()) return;
        
        for(final MapItem child : item.items()){
            if(renderingContext.getMonitor().stopRequested()) break;
            final GraphicJ2D gra = itemGraphics.get(child);
            if(gra != null){
                gra.paint(renderingContext);
            }else{
                getLogger().log(Level.WARNING, "GrahicContextJ2D, paint method : strange, no graphic object affected to layer :{0}", child.getName());
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        //we abort painting if the item is not visible.
        if (!item.isVisible()) return graphics;

        final List<MapItem> children = item.items();
        for(int i=children.size()-1; i>=0; i--){
            final MapItem child = children.get(i);
            final GraphicJ2D gra = itemGraphics.get(child);
            graphics = gra.getGraphicAt(rdcontext,mask,filter,graphics);
        }
        return graphics;
    }


    // item listener ----------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
    }

    @Override
    public void itemChange(final CollectionChangeEvent<MapItem> event) {
        final int type = event.getType();

        if(CollectionChangeEvent.ITEM_ADDED == type){
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gj2d = parseChild(child, item.items().indexOf(child));
                itemGraphics.put(child, gj2d);
            }
            //change other layers indexes
            final List<MapItem> children = item.items();
            for(int i=0,n=children.size(); i<n; i++){
                final MapItem layer = children.get(i);
                final GraphicJ2D gra = itemGraphics.get(layer);
                if(gra != null){
                    gra.setZOrderHint(i);
                }
            }
            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
            
        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gra = itemGraphics.get(child);
                if(gra != null){
                    gra.dispose();
                }
                //remove the graphic
                itemGraphics.remove(child);
            }
            //change other layers indexes
            final List<MapItem> children = item.items();
            for(int i=0,n=children.size(); i<n; i++){
                final MapItem child = children.get(i);
                final GraphicJ2D gra = itemGraphics.get(child);
                if(gra != null){
                    gra.setZOrderHint(i);
                }
            }
            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
        }

    }
    
}
