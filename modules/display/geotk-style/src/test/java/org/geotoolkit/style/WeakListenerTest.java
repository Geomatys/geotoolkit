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
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.filter.DefaultFilterFactory2;
import org.opengis.feature.type.Name;

import org.opengis.filter.FilterFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WeakListenerTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = new DefaultFilterFactory2();

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
     * Test no memory leak in weak style listener
     */
    @Test
    public void testWeakStyleListener() {
        final AtomicInteger count = new AtomicInteger(0);

        final MutableStyle style = SF.style();
        StyleListener listener = new StyleListener() {
            @Override
            public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
                count.incrementAndGet();
            }
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fail("Should never had been called");
            }
            
        };

        style.addListener(new StyleListener.Weak(style, listener));

        style.featureTypeStyles().add(SF.featureTypeStyle());
        assertEquals(1, count.get());
        listener = null;
        System.gc();

        style.featureTypeStyles().add(SF.featureTypeStyle());
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }

    /**
     * Test no memory leak in weak fts listener
     */
    @Test
    public void testWeakFTSListener() {
        final AtomicInteger count = new AtomicInteger(0);

        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
        FeatureTypeStyleListener listener = new FeatureTypeStyleListener() {
            @Override
            public void ruleChange(CollectionChangeEvent<MutableRule> event) {
                count.incrementAndGet();
            }
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fail("Should never had been called");
            }
            @Override
            public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
                fail("Should never had been called");
            }
            @Override
            public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
                fail("Should never had been called");
            }

        };

        fts.addListener(new FeatureTypeStyleListener.Weak(fts, listener));

        fts.rules().add(SF.rule());
        assertEquals(1, count.get());
        listener = null;
        System.gc();

        fts.rules().add(SF.rule());
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }

    /**
     * Test no memory leak in weak rule listener
     */
    @Test
    public void testWeakRuleListener() {
        final AtomicInteger count = new AtomicInteger(0);

        final MutableRule fts = SF.rule();
        RuleListener listener = new RuleListener() {
            @Override
            public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
                count.incrementAndGet();
            }
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fail("Should never had been called");
            }
        };

        fts.addListener(new RuleListener.Weak(fts, listener));

        fts.symbolizers().add(SF.lineSymbolizer());
        assertEquals(1, count.get());
        listener = null;
        System.gc();

        fts.symbolizers().add(SF.pointSymbolizer());
        //listener should have desapear now, so the event should not have been send
        assertEquals(1, count.get());
    }

}
