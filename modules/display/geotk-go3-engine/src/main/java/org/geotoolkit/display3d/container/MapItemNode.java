/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Johann Sorel
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
package org.geotoolkit.display3d.container;

import java.beans.PropertyChangeEvent;
import java.util.*;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.*;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class MapItemNode<T extends MapItem> extends A3DGraphic implements ItemListener{

    private static final DefaultMapLayerGraphicBuilder DEFAULT_BUILDER = new DefaultMapLayerGraphicBuilder();
    
    private final ItemListener.Weak listener = new ItemListener.Weak(this);
    
    //childs
    private final Map<MapItem, Collection<A3DGraphic>> itemGraphics = new HashMap<MapItem, Collection<A3DGraphic>>();    
    private final T mapitem;

    public MapItemNode(final A3DCanvas canvas, final T mapitem) {
        super(canvas);
        this.mapitem = mapitem;

        parseItem(mapitem);
        listener.registerSource(mapitem);
    }

    public T getItem() {
        return mapitem;
    }

    private void parseItem(final T candidate){
        final List<MapItem> childs = candidate.items();
        for(int i=0,n=childs.size(); i<n; i++){
            final MapItem child = childs.get(i);
            parseChild(child);
        }
    }
    
    protected void parseChild(final MapItem child){

        final Collection<A3DGraphic> graphics;
        if(child instanceof MapLayer){
            //check if the layer has its own graphic builder
            final MapLayer layer = (MapLayer) child;
            GraphicBuilder gb = layer.getGraphicBuilder(A3DGraphic.class);
            if(gb == null){
                gb = DEFAULT_BUILDER;
            }
            graphics = gb.createGraphics(layer, canvas);
            
        }else{
            graphics = Collections.singleton((A3DGraphic)new MapItemNode(canvas,child));
        }
        
        getChildren().addAll(graphics);
        itemGraphics.put(child, graphics);
    }
    
    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        final int type = event.getType();

        if(CollectionChangeEvent.ITEM_ADDED == type){
            for(final MapItem child : event.getItems()){
                parseChild(child);
            }
        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(final MapItem child : event.getItems()){
                final Collection<A3DGraphic> gra = itemGraphics.get(child);
                if(gra != null){
                    for(A3DGraphic g : gra){
                        g.dispose();
                    }
                }
                //remove the graphic
                itemGraphics.remove(child);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final String propName = event.getPropertyName();
        if(MapItem.VISIBILITY_PROPERTY.equals(propName)){
            setVisible(mapitem.isVisible());
        }
    }
    
}
