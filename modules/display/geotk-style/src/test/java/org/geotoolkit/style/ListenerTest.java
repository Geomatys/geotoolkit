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
package org.geotoolkit.style;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import org.geotoolkit.util.NumberRange;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.type.Name;
import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

/**
 * Test different style object events.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ListenerTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static double DELTA = 0d;

    public ListenerTest() {
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

    @Test
    public void testRuleListener() {
        final MutableRule rule = SF.rule();
        final List<CollectionChangeEvent<Symbolizer>> events = new ArrayList();
        NumberRange range;

        rule.addListener(new RuleListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                //todo add test on this event
            }
            @Override
            public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
                events.add(event);
            }
        });

        final Symbolizer symbol = SF.lineSymbolizer();

        //test add
        events.clear();
        rule.symbolizers().add(symbol);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_ADDED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(symbol, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        //test remove
        events.clear();
        rule.symbolizers().remove(symbol);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_REMOVED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(symbol, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

    }

    @Test
    public void testFTSListener() {
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        final List<CollectionChangeEvent<MutableRule>> events = new ArrayList();
        NumberRange range;

        fts.addListener(new FeatureTypeStyleListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                //todo add test on this event
            }
            @Override
            public void ruleChange(CollectionChangeEvent<MutableRule> event) {
                events.add(event);
            }
            @Override
            public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
                //todo add test on this event
            }
            @Override
            public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
                //todo add test on this event
            }
        });

        final MutableRule rule = SF.rule();

        //test add
        events.clear();
        fts.rules().add(rule);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_ADDED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(rule, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        //test remove
        events.clear();
        fts.rules().remove(rule);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_REMOVED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(rule, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        //test rule event forwarded
        final Symbolizer symbol = SF.lineSymbolizer();
        fts.rules().add(rule);
        events.clear();
        rule.symbolizers().add(symbol);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_CHANGED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(rule, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        EventObject subEvent = events.get(0).getChangeEvent();
        assertTrue(subEvent instanceof CollectionChangeEvent);
        CollectionChangeEvent sub = (CollectionChangeEvent) subEvent;
        assertEquals(CollectionChangeEvent.ITEM_ADDED, sub.getType());
        assertEquals(1, sub.getItems().size());
        assertEquals(symbol, sub.getItems().iterator().next());
        range = sub.getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);
    }

    @Test
    public void testStyleListener() {
        final MutableStyle style = SF.style();
        final List<CollectionChangeEvent<MutableFeatureTypeStyle>> events = new ArrayList();
        NumberRange range;

        style.addListener(new StyleListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
                events.add(event);
            }
        });

        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

        //test add
        events.clear();
        style.featureTypeStyles().add(fts);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_ADDED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(fts, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        //test remove
        events.clear();
        style.featureTypeStyles().remove(fts);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_REMOVED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(fts, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        //test rule event forwarded
        final MutableRule rule = SF.rule();
        style.featureTypeStyles().add(fts);
        events.clear();
        fts.rules().add(rule);
        assertEquals(1, events.size());
        assertEquals(CollectionChangeEvent.ITEM_CHANGED, events.get(0).getType());
        assertEquals(1, events.get(0).getItems().size());
        assertEquals(fts, events.get(0).getItems().iterator().next());
        range = events.get(0).getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);

        EventObject subEvent = events.get(0).getChangeEvent();
        assertTrue(subEvent instanceof CollectionChangeEvent);
        CollectionChangeEvent sub = (CollectionChangeEvent) subEvent;
        assertEquals(CollectionChangeEvent.ITEM_ADDED, sub.getType());
        assertEquals(1, sub.getItems().size());
        assertEquals(rule, sub.getItems().iterator().next());
        range = sub.getRange();
        assertEquals(0d, range.getMinimum(), DELTA);
        assertEquals(0d, range.getMaximum(), DELTA);
    }


}
