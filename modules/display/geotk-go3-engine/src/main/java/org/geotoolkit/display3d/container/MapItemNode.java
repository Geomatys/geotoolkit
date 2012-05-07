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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final ItemListener.Weak listener = new ItemListener.Weak(this);
    
    //childs
    private final Map<MapItem, A3DGraphic> itemGraphics = new HashMap<MapItem, A3DGraphic>();    
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
    
    protected A3DGraphic parseChild(final MapItem child){

        final A3DGraphic graphic;
        if (child instanceof FeatureMapLayer){
            graphic = new FeatureLayerNode(canvas, (FeatureMapLayer)child);
        }else if (child instanceof CollectionMapLayer){
            graphic = new CollectionLayerNode(canvas, (CollectionMapLayer)child);
        }else if (child instanceof CoverageMapLayer){
            graphic = new CoverageLayerNode(canvas, (CoverageMapLayer)child);
        }else if(child instanceof MapLayer){
            graphic = new MapLayerNode(canvas, (MapLayer)child);
        }else{
            graphic = new MapItemNode(canvas, child);
        }

        getChildren().add(graphic);
        itemGraphics.put(child, graphic);
        return graphic;
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
                final A3DGraphic gra = itemGraphics.get(child);
                if(gra != null){
                    gra.dispose();
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
