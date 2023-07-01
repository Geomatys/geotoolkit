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
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import org.apache.sis.internal.map.ListChangeEvent;
import org.apache.sis.portrayal.MapItem;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TreeMapContextTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public TreeMapContextTest() {
    }

    /**
     * Check that flat context are correctly created.
     */
    @Test
    public void testFlatStructure(){

        MapLayers context = MapBuilder.createContext();

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

        MapLayers item = MapBuilder.createItem();

        final RecordListener listener = new RecordListener();
        item.addPropertyChangeListener(MapLayers.IDENTIFIER_PROPERTY, listener);
        item.addPropertyChangeListener(MapLayers.COMPONENTS_PROPERTY, listener);

        //test a property event
        item.setIdentifier("test");
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
        assertEquals(1, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof ListChangeEvent);
        ListChangeEvent levent = (ListChangeEvent) listener.lastEvent;
        assertEquals(item,levent.getSource());
        assertEquals(ListChangeEvent.Type.ADDED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(child,levent.getItems().iterator().next());
        listener.reset();

        //test an item remove event
        item.getComponents().remove(child);
        assertEquals(1, listener.propertyChangecount);
        assertTrue(listener.lastEvent instanceof ListChangeEvent);
        levent = (ListChangeEvent) listener.lastEvent;
        assertEquals(item,levent.getSource());
        assertEquals(ListChangeEvent.Type.REMOVED,levent.getType());
        assertEquals(0,levent.getRange().getMinValue());
        assertEquals(0,levent.getRange().getMaxValue());
        assertEquals(1,levent.getItems().size());
        assertEquals(child,levent.getItems().iterator().next());
        listener.reset();
    }

    private static class RecordListener implements PropertyChangeListener {

        private EventObject lastEvent = null;
        private int propertyChangecount = 0;

        public void reset(){
            lastEvent = null;
            propertyChangecount = 0;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            lastEvent = event;
            propertyChangecount++;
        }
    }
}
