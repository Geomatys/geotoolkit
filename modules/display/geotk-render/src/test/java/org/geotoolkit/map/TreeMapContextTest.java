/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.map;

import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TreeMapContextTest {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(null);

    public TreeMapContextTest() {
    }

    /**
     * Check that flat context are correctly created.
     */
    @Test
    public void testFlatStructure(){

        MapContext context = MapBuilder.createContext();

        assertEquals(0, context.layers().size());

        MapLayer layer1 = MapBuilder.createEmptyMapLayer(); layer1.setName("layer 1");
        MapLayer layer2 = MapBuilder.createEmptyMapLayer(); layer2.setName("layer 2");
        MapLayer layer3 = MapBuilder.createEmptyMapLayer(); layer3.setName("layer 3");

        //add layers
        context.layers().add(layer1);
        assertEquals(1, context.layers().size());
        context.layers().add(layer2);
        context.layers().add(layer3);
        assertEquals(3, context.layers().size());
        assertEquals(layer1, context.layers().get(0));
        assertEquals(layer2, context.layers().get(1));
        assertEquals(layer3, context.layers().get(2));

        //remove a layer
        context.layers().remove(layer2);
        assertEquals(2, context.layers().size());
        assertEquals(layer1, context.layers().get(0));
        assertEquals(layer3, context.layers().get(1));

    }

    /**
     * Test property change events and items add events.
     */
    @Test
    public void testItemEvents(){

        MapItem item = MapBuilder.createItem();

        final RecordListener listener = new RecordListener();
        item.addItemListener(listener);

        //test a property event
        item.setName("test");
        assertEquals(0, listener.itemChangecount);
        assertEquals(1, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof PropertyChangeEvent);
        PropertyChangeEvent event = (PropertyChangeEvent) listener.lastEvent;
        assertEquals(MapItem.NAME_PROPERTY, event.getPropertyName());
        assertEquals(null, event.getOldValue());
        assertEquals("test", event.getNewValue());
        listener.reset();

        //test an item add event
        MapItem child = MapBuilder.createItem();
        item.items().add(child);
        assertEquals(1, listener.itemChangecount);
        assertEquals(0, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof CollectionChangeEvent);
        CollectionChangeEvent levent = (CollectionChangeEvent) listener.lastEvent;
        assertEquals(item,levent.getSource());
        assertEquals(CollectionChangeEvent.ITEM_ADDED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(child,levent.getItems().iterator().next());
        listener.reset();

        //test an item change event
        child.setDescription(SF.description("child0", "abstract child0"));
        assertEquals(1, listener.itemChangecount);
        assertEquals(0, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof CollectionChangeEvent);
        levent = (CollectionChangeEvent) listener.lastEvent;
        assertEquals(item,levent.getSource());
        assertEquals(CollectionChangeEvent.ITEM_CHANGED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(child,levent.getItems().iterator().next());

        assertTrue(levent.getChangeEvent() instanceof PropertyChangeEvent);
        PropertyChangeEvent pevent = (PropertyChangeEvent) levent.getChangeEvent();
        assertEquals(child,pevent.getSource());
        assertEquals(MapItem.DESCRIPTION_PROPERTY, pevent.getPropertyName());
        assertEquals(StyleConstants.DEFAULT_DESCRIPTION, pevent.getOldValue());
        assertEquals(SF.description("child0", "abstract child0"), pevent.getNewValue());
        listener.reset();

        //test an item remove event
        item.items().remove(child);
        assertEquals(1, listener.itemChangecount);
        assertEquals(0, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof CollectionChangeEvent);
        levent = (CollectionChangeEvent) listener.lastEvent;
        assertEquals(item,levent.getSource());
        assertEquals(CollectionChangeEvent.ITEM_REMOVED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(child,levent.getItems().iterator().next());
        listener.reset();

    }


    @Test
    public void testContextEvents(){

        MapContext context = MapBuilder.createContext();

        final RecordListener listener = new RecordListener();
        context.addContextListener(listener);

        //test a layer event
        MapLayer layer = MapBuilder.createEmptyMapLayer();
        context.layers().add(layer);
        assertEquals(1, listener.itemChangecount);
        assertEquals(1, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        listener.reset();

        //test an item event
        context.items().add(MapBuilder.createItem());
        assertEquals(1, listener.itemChangecount);
        assertEquals(0, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        listener.reset();

        //test a layer property change
        layer.setName("layer0");
        assertEquals(1, listener.itemChangecount);
        assertEquals(1, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof CollectionChangeEvent);
        CollectionChangeEvent levent = (CollectionChangeEvent) listener.lastEvent;
        assertEquals(context,levent.getSource());
        assertEquals(CollectionChangeEvent.ITEM_CHANGED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(layer,levent.getItems().iterator().next());

        assertTrue(levent.getChangeEvent() instanceof PropertyChangeEvent);
        PropertyChangeEvent pevent = (PropertyChangeEvent) levent.getChangeEvent();
        assertEquals(layer,pevent.getSource());
        assertEquals(MapItem.NAME_PROPERTY, pevent.getPropertyName());
        assertEquals(null, pevent.getOldValue());
        assertEquals("layer0", pevent.getNewValue());
        listener.reset();

        //test a layer style change
        MutableStyle style = SF.style(SF.pointSymbolizer());
        layer.setStyle(style);
        assertEquals(1, listener.itemChangecount);
        assertEquals(1, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof CollectionChangeEvent);
        levent = (CollectionChangeEvent) listener.lastEvent;
        assertEquals(context,levent.getSource());
        assertEquals(CollectionChangeEvent.ITEM_CHANGED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(layer,levent.getItems().iterator().next());

        assertTrue(levent.getChangeEvent() instanceof PropertyChangeEvent);
        pevent = (PropertyChangeEvent) levent.getChangeEvent();
        assertEquals(layer,pevent.getSource());
        assertEquals(MapLayer.STYLE_PROPERTY, pevent.getPropertyName());
        assertEquals(style, pevent.getNewValue());
        listener.reset();


    }

    /**
     * Check that tree like context are correctly created.
     */
    @Test
    public void testTreeStructure(){

        MapContext context = MapBuilder.createContext();
        assertEquals(0, context.items().size());
        assertEquals(0, context.layers().size());

        MapItem item1 = MapBuilder.createItem();    item1.setName("item 1");
        MapItem item11 = MapBuilder.createItem();   item11.setName("item 11");
        MapItem item12 = MapBuilder.createItem();   item12.setName("item 12");
        MapItem item2 = MapBuilder.createItem();    item2.setName("item 2");
        MapItem item3 = MapBuilder.createItem();    item3.setName("item 3");
        MapItem item31 = MapBuilder.createItem();   item31.setName("item 31");
        MapItem item32 = MapBuilder.createItem();   item32.setName("item 32");
        MapItem item321 = MapBuilder.createItem();  item321.setName("item 321");
        MapItem item33 = MapBuilder.createItem();   item33.setName("item 33");
        MapLayer layer1 = MapBuilder.createEmptyMapLayer(); layer1.setName("layer 1");
        MapLayer layer2 = MapBuilder.createEmptyMapLayer(); layer2.setName("layer 2");
        MapLayer layer3 = MapBuilder.createEmptyMapLayer(); layer3.setName("layer 3");

        assertEquals(0, context.layers().size());
        context.items().add(item1);
            item1.items().add(item11);
            item1.items().add(item12);
                item12.items().add(layer1);
        assertEquals(1, context.layers().size());
        context.items().add(item2);
        context.items().add(layer2);
        assertEquals(2, context.layers().size());
        context.items().add(item3);
            item3.items().add(item31);
            item3.items().add(item32);
                item32.items().add(item321);
                    item321.items().add(layer3);
        assertEquals(3, context.layers().size());
            item3.items().add(item33);

// DefaultMapContext[Description : Title= Abstract=]
//  ├─DefaultMapItem (item 1) [Description : Title= Abstract=]
//  │   ├─DefaultMapItem (item 11) [Description : Title= Abstract=]
//  │   └─DefaultMapItem (item 12) [Description : Title= Abstract=]
//  │       └─EmptyMapLayer[Description : Title= Abstract=]
//  ├─DefaultMapItem (item 2) [Description : Title= Abstract=]
//  ├─EmptyMapLayer[Description : Title= Abstract=]
//  └─DefaultMapItem (item 3) [Description : Title= Abstract=]
//      ├─DefaultMapItem (item 31) [Description : Title= Abstract=]
//      ├─DefaultMapItem (item 32) [Description : Title= Abstract=]
//      │   └─DefaultMapItem (item 321) [Description : Title= Abstract=]
//      │       └─EmptyMapLayer[Description : Title= Abstract=]
//      └─DefaultMapItem (item 33) [Description : Title= Abstract=]


        assertEquals(4, context.items().size());
        assertEquals(2, item1.items().size());
        assertEquals(0, item11.items().size());
        assertEquals(1, item12.items().size());
        assertEquals(0, item2.items().size());
        assertEquals(3, item3.items().size());
        assertEquals(0, item31.items().size());
        assertEquals(1, item32.items().size());
        assertEquals(1, item321.items().size());
        assertEquals(0, item33.items().size());

        //layers method from mapcontext must contain the layers
        assertEquals(3, context.layers().size());
        assertEquals(layer1, context.layers().get(0));
        assertEquals(layer2, context.layers().get(1));
        assertEquals(layer3, context.layers().get(2));

        //check operation on the list
        context.layers().remove(layer3);
        assertEquals(2, context.layers().size());
        assertEquals(0, item321.items().size());

    }

    private static class RecordListener implements ContextListener{

        private EventObject lastEvent = null;
        private int layerChangecount = 0;
        private int itemChangecount = 0;
        private int propertyChangecount = 0;

        public void reset(){
            lastEvent = null;
            layerChangecount = 0;
            itemChangecount = 0;
            propertyChangecount = 0;
        }

        @Override
        public void layerChange(final CollectionChangeEvent<MapLayer> event) {
            lastEvent = event;
            layerChangecount++;
        }

        @Override
        public void itemChange(final CollectionChangeEvent<MapItem> event) {
            lastEvent = event;
            itemChangecount++;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            lastEvent = event;
            propertyChangecount++;
        }

    }


}
