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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullMapItemJ2D<T extends MapItem> extends GraphicJ2D implements ItemListener{

    private final ItemListener.Weak weakListener = new ItemListener.Weak(this);



    //childs
    private final Map<MapItem, GraphicJ2D> itemGraphics = new HashMap<>();
    protected final T item;

    public StatefullMapItemJ2D(final J2DCanvas canvas,final T item, boolean allowChildren){
        super(canvas, allowChildren);
        this.item = item;

        //build children nodes
        final List<MapItem> childs = item.items();
        for(int i=0,n=childs.size(); i<n; i++){
            final MapItem child = childs.get(i);
            final GraphicJ2D gj2d = parseChild(child);
            itemGraphics.put(child, gj2d);
            getChildren().add(gj2d);
        }

        //listen to mapitem changes
        weakListener.registerSource(item);
    }

    protected ThreadPoolExecutor getExecutor(){
        if(parent instanceof StatefullMapItemJ2D){
            return ((StatefullMapItemJ2D)parent).getExecutor();
        }else if(parent instanceof RootSceneNode){
            return ((RootSceneNode)parent).getExecutor();
        }else{
            throw new RuntimeException("Get not access executor if node is not linked to the scene root.");
        }
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
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }

    // create graphics ---------------------------------------------------------

    protected GraphicJ2D parseChild(final MapItem child){

        final StatefullMapItemJ2D g2d;
        if (child instanceof FeatureMapLayer){
            g2d = new StatefullFeatureMapLayerJ2D(getCanvas(), (FeatureMapLayer)child);
        }else if (child instanceof CoverageMapLayer){
            final CoverageMapLayer layer = (CoverageMapLayer) child;
            final CoverageReference ref = layer.getCoverageReference();
            if(ref != null && ref instanceof PyramidalCoverageReference){
                //pyramidal model, we can improve rendering
                g2d = new StatefullPyramidalCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)child);
            }else{
                //normal coverage
                g2d = new StatefullMapLayerJ2D(getCanvas(), (CoverageMapLayer)child, false);
            }
        }else if (child instanceof MapLayer){
            g2d = new StatefullMapLayerJ2D(getCanvas(), (MapLayer)child, false);
        }else{
            g2d = new StatefullMapItemJ2D(getCanvas(), child, true);
        }

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
                getLogger().log(Level.WARNING, "GraphicContextJ2D, paint method : strange, no graphic object affected to layer :{0}", child.getName());
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
            final NumberRange range = event.getRange();
            int index = (int) range.getMinDouble();
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gj2d = parseChild(child);
                getChildren().add(index,(SceneNode)gj2d);
                itemGraphics.put(child, gj2d);
                index++;
            }

            //TODO should call a repaint only on this graphic
            getCanvas().repaint();

        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(final MapItem child : event.getItems()){
                //remove the graphic
                final GraphicJ2D gra = itemGraphics.remove(child);
                if(gra != null){
                    getChildren().remove(gra);
                    gra.dispose();
                }
            }

            //TODO should call a repaint only on this graphic
            getCanvas().repaint();
        }

    }


}
