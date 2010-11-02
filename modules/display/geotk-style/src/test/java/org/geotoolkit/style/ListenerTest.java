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

import org.geotoolkit.util.collection.CollectionChangeEvent;
import java.beans.PropertyChangeEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.util.NumberRange;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
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
    private static final FilterFactory FF = new DefaultFilterFactory2();
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
    public void testRuleListener() throws URISyntaxException {
        final MutableRule rule = SF.rule();
        final List<PropertyChangeEvent> propEvents = new ArrayList<PropertyChangeEvent>();
        final List<CollectionChangeEvent<Symbolizer>> events = new ArrayList<CollectionChangeEvent<Symbolizer>>();
        NumberRange range;

        rule.addListener(new RuleListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                propEvents.add(event);
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


        //test property events
        rule.setName("currentName");
        rule.setDescription(SF.description("title", "abstract"));
        rule.setElseFilter(true);
        rule.setFilter(Filter.INCLUDE);
        rule.setOnlineResource(SF.onlineResource(new URI("http://test.com")));
        rule.setLegendGraphic(SF.graphicLegend(SF.graphic()));
        rule.setMinScaleDenominator(0);
        rule.setMaxScaleDenominator(10);
        propEvents.clear();

        //ensure that no events are fired when the new value is the same
        rule.setName("currentName");
        assertEquals(0, propEvents.size());
        rule.setDescription(SF.description("title", "abstract"));
        assertEquals(0, propEvents.size());
        rule.setElseFilter(true);
        assertEquals(0, propEvents.size());
        rule.setFilter(Filter.INCLUDE);
        assertEquals(0, propEvents.size());
        rule.setOnlineResource(SF.onlineResource(new URI("http://test.com")));
        assertEquals(0, propEvents.size());
        rule.setLegendGraphic(SF.graphicLegend(SF.graphic()));
        assertEquals(0, propEvents.size());
        rule.setMinScaleDenominator(0);
        assertEquals(0, propEvents.size());
        rule.setMaxScaleDenominator(10);
        assertEquals(0, propEvents.size());

        //ensure that event are correctly send

        propEvents.clear();
        rule.setName("newName");
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), "currentName");
        assertEquals(propEvents.get(0).getNewValue(), "newName");

        propEvents.clear();
        rule.setDescription(SF.description("newtitle", "newabstract"));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.description("title", "abstract"));
        assertEquals(propEvents.get(0).getNewValue(), SF.description("newtitle", "newabstract"));

        propEvents.clear();
        rule.setElseFilter(false);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), true);
        assertEquals(propEvents.get(0).getNewValue(), false);

        propEvents.clear();
        rule.setFilter(Filter.EXCLUDE);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), Filter.INCLUDE);
        assertEquals(propEvents.get(0).getNewValue(), Filter.EXCLUDE);

        propEvents.clear();
        rule.setOnlineResource(SF.onlineResource(new URI("http://test2.com")));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.onlineResource(new URI("http://test.com")));
        assertEquals(propEvents.get(0).getNewValue(), SF.onlineResource(new URI("http://test2.com")));

        propEvents.clear();
        rule.setLegendGraphic(null);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.graphicLegend(SF.graphic()));
        assertEquals(propEvents.get(0).getNewValue(), null);

        propEvents.clear();
        rule.setMinScaleDenominator(100d);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), 0d);
        assertEquals(propEvents.get(0).getNewValue(), 100d);

        propEvents.clear();
        rule.setMaxScaleDenominator(1000d);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), 10d);
        assertEquals(propEvents.get(0).getNewValue(), 1000d);

    }

    @Test
    public void testFTSListener() throws URISyntaxException {
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        final List<PropertyChangeEvent> propEvents = new ArrayList<PropertyChangeEvent>();
        final List<CollectionChangeEvent<MutableRule>> events = new ArrayList();
        NumberRange range;

        fts.addListener(new FeatureTypeStyleListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                propEvents.add(event);
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


        //test property events
        fts.setName("currentName");
        fts.setDescription(SF.description("title", "abstract"));
        fts.setOnlineResource(SF.onlineResource(new URI("http://test.com")));
        fts.setFeatureInstanceIDs(FF.id(Collections.singleton(new DefaultFeatureId("id1"))));
        propEvents.clear();

        //ensure that no events are fired when the new value is the same
        fts.setName("currentName");
        assertEquals(0, propEvents.size());
        fts.setDescription(SF.description("title", "abstract"));
        assertEquals(0, propEvents.size());
        fts.setOnlineResource(SF.onlineResource(new URI("http://test.com")));
        assertEquals(0, propEvents.size());
        fts.setFeatureInstanceIDs(FF.id(Collections.singleton(new DefaultFeatureId("id1"))));
        assertEquals(0, propEvents.size());

        //ensure that event are correctly send
        propEvents.clear();
        fts.setName("newName");
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), "currentName");
        assertEquals(propEvents.get(0).getNewValue(), "newName");

        propEvents.clear();
        fts.setDescription(SF.description("newtitle", "newabstract"));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.description("title", "abstract"));
        assertEquals(propEvents.get(0).getNewValue(), SF.description("newtitle", "newabstract"));

        propEvents.clear();
        fts.setOnlineResource(SF.onlineResource(new URI("http://test2.com")));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.onlineResource(new URI("http://test.com")));
        assertEquals(propEvents.get(0).getNewValue(), SF.onlineResource(new URI("http://test2.com")));

        propEvents.clear();
        fts.setFeatureInstanceIDs(FF.id(Collections.singleton(new DefaultFeatureId("id2"))));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), FF.id(Collections.singleton(new DefaultFeatureId("id1"))));
        assertEquals(propEvents.get(0).getNewValue(), FF.id(Collections.singleton(new DefaultFeatureId("id2"))));

    }

    @Test
    public void testStyleListener() {
        final MutableStyle style = SF.style();
        final List<PropertyChangeEvent> propEvents = new ArrayList<PropertyChangeEvent>();
        final List<CollectionChangeEvent<MutableFeatureTypeStyle>> events = new ArrayList();
        NumberRange range;

        style.addListener(new StyleListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                propEvents.add(event);
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


        //test property events
        style.setName("currentName");
        style.setDescription(SF.description("title", "abstract"));
        style.setDefault(true);
        style.setDefaultSpecification(SF.lineSymbolizer());
        propEvents.clear();

        //ensure that no events are fired when the new value is the same
        style.setName("currentName");
        assertEquals(0, propEvents.size());
        style.setDescription(SF.description("title", "abstract"));
        assertEquals(0, propEvents.size());
        style.setDefault(true);
        assertEquals(0, propEvents.size());
        style.setDefaultSpecification(SF.lineSymbolizer());
        assertEquals(0, propEvents.size());

        //ensure that event are correctly send
        propEvents.clear();
        style.setName("newName");
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), "currentName");
        assertEquals(propEvents.get(0).getNewValue(), "newName");

        propEvents.clear();
        style.setDescription(SF.description("newtitle", "newabstract"));
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.description("title", "abstract"));
        assertEquals(propEvents.get(0).getNewValue(), SF.description("newtitle", "newabstract"));

        propEvents.clear();
        style.setDefault(false);
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), true);
        assertEquals(propEvents.get(0).getNewValue(), false);

        propEvents.clear();
        style.setDefaultSpecification(SF.pointSymbolizer());
        assertEquals(1, propEvents.size());
        assertEquals(propEvents.get(0).getOldValue(), SF.lineSymbolizer());
        assertEquals(propEvents.get(0).getNewValue(), SF.pointSymbolizer());

    }


}
