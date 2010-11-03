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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WeakListenerTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();

    public WeakListenerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test no memory leak in weak context listener
     */
    @Test
    public void testWeakContextListener() {
        final AtomicInteger countLayerChange = new AtomicInteger(0);
        final AtomicInteger countItemChange = new AtomicInteger(0);

        final MapContext context = MapBuilder.createContext();
        ContextListener listener = new ContextListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fail("Should never had been called");
            }

            @Override
            public void layerChange(CollectionChangeEvent<MapLayer> event) {
                countLayerChange.incrementAndGet();
            }

            @Override
            public void itemChange(CollectionChangeEvent<MapItem> event) {
                countItemChange.incrementAndGet();
            }
            
        };

        ContextListener.Weak weak = new ContextListener.Weak(listener);
        weak.registerSource(context);

        context.layers().add(MapBuilder.createEmptyMapLayer());
        assertEquals(1, countLayerChange.get());
        assertEquals(1, countItemChange.get());
        listener = null;
        pause();

        context.layers().add(MapBuilder.createEmptyMapLayer());
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, countLayerChange.get());
    }

    /**
     * Test no memory leak in weak layer listener
     */
    @Test
    public void testWeakLayerListener() {
        final AtomicInteger count = new AtomicInteger(0);

        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        LayerListener listener = new LayerListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                count.incrementAndGet();
            }

            @Override
            public void styleChange(MapLayer source, EventObject event) {
                fail("Should never had been called");
            }

            @Override
            public void itemChange(CollectionChangeEvent<MapItem> event) {
                fail("Should never had been called");
            }
        };

        LayerListener.Weak weak = new LayerListener.Weak(listener);
        weak.registerSource(layer);

        layer.setStyle(SF.style(SF.lineSymbolizer()));
        assertEquals(1, count.get());
        listener = null;
        pause();

        layer.setStyle(SF.style(SF.pointSymbolizer()));
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }

    private static void pause(){
        for(int i=0;i<4;i++){
            System.gc();
            System.gc();
            System.gc();
            System.gc();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(WeakListenerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
