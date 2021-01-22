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
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.style.StyleFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TreeMapContextTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = (MutableStyleFactory) DefaultFactories.forBuildin(StyleFactory.class);

    public TreeMapContextTest() {
    }

    /**
     * Check that flat context are correctly created.
     */
    @Test
    public void testFlatStructure(){

        MapContext context = MapBuilder.createContext();

        assertEquals(0, context.getComponents().size());

        MapLayer layer1 = MapBuilder.createEmptyMapLayer(); layer1.setIdentifier("layer 1");
        MapLayer layer2 = MapBuilder.createEmptyMapLayer(); layer2.setIdentifier("layer 2");
        MapLayer layer3 = MapBuilder.createEmptyMapLayer(); layer3.setIdentifier("layer 3");

        //add layers
        context.getComponents().add(layer1);
        assertEquals(1, context.getComponents().size());
        context.getComponents().add(layer2);
        context.getComponents().add(layer3);
        assertEquals(3, context.getComponents().size());
        assertEquals(layer1, context.getComponents().get(0));
        assertEquals(layer2, context.getComponents().get(1));
        assertEquals(layer3, context.getComponents().get(2));

        //remove a layer
        context.getComponents().remove(layer2);
        assertEquals(2, context.getComponents().size());
        assertEquals(layer1, context.getComponents().get(0));
        assertEquals(layer3, context.getComponents().get(1));

    }

    /**
     * Test property change events and items add events.
     */
    @Test
    public void testItemEvents(){

        MapContext item = MapBuilder.createItem();

        final RecordListener listener = new RecordListener();
        item.addItemListener(listener);

        //test a property event
        item.setIdentifier("test");
        assertEquals(0, listener.itemChangecount);
        assertEquals(1, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof PropertyChangeEvent);
        PropertyChangeEvent event = (PropertyChangeEvent) listener.lastEvent;
        assertEquals(MapItem.IDENTIFIER_PROPERTY, event.getPropertyName());
        assertEquals(null, event.getOldValue());
        assertEquals("test", event.getNewValue());
        listener.reset();

        //test an item add event
        MapItem child = MapBuilder.createItem();
        item.getComponents().add(child);
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
        child.setTitle("child0");
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
        assertEquals(MapItem.TITLE_PROPERTY, pevent.getPropertyName());
        assertEquals(null, pevent.getOldValue());
        assertEquals("child0", pevent.getNewValue());
        listener.reset();

        //test an item remove event
        item.getComponents().remove(child);
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
        context.getComponents().add(layer);
        assertEquals(1, listener.itemChangecount);
        assertEquals(1, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        listener.reset();

        //test an item event
        context.getComponents().add(MapBuilder.createItem());
        assertEquals(1, listener.itemChangecount);
        assertEquals(0, listener.layerChangecount);
        assertEquals(0, listener.propertyChangecount);
        listener.reset();

        //test a layer property change
        layer.setIdentifier("layer0");
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
        assertEquals(MapItem.IDENTIFIER_PROPERTY, pevent.getPropertyName());
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
