/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2011, Johann Sorel
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

package org.geotoolkit.gui.swing.style;

import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.style.SemanticType;
import org.opengis.style.Symbolizer;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel
 * @module
 */
public class StyleModelTest extends org.geotoolkit.test.TestBase {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final double DELTA = 0d;

    public StyleModelTest() {
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
    public void ruleTest(){
        final MutableRule rule = SF.rule();

        final StyleTreeModel model = new StyleTreeModel(rule);
        final Object root = model.getRoot();

        assertEquals(rule, root);
        assertEquals(0, model.getChildCount(root));

        final Symbolizer symbol1 = SF.lineSymbolizer();
        final Symbolizer symbol2 = SF.polygonSymbolizer();

        rule.symbolizers().add(symbol1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(symbol1, model.getChild(root, 0));

        rule.symbolizers().add(symbol2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(symbol1, model.getChild(root, 0));
        assertEquals(symbol2, model.getChild(root, 1));

        rule.symbolizers().remove(symbol1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(symbol2, model.getChild(root, 0));

        rule.symbolizers().remove(symbol2);
        assertEquals(0, model.getChildCount(root));


        //try modifying the properties -----------------------------------------
        rule.symbolizers().add(symbol1);
        rule.symbolizers().add(symbol2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(symbol1, model.getChild(root, 0));
        assertEquals(symbol2, model.getChild(root, 1));

        rule.setElseFilter(true);
        assertEquals(2, model.getChildCount(root));
        assertEquals(symbol1, model.getChild(root, 0));
        assertEquals(symbol2, model.getChild(root, 1));

        rule.setName("hello");
        assertEquals(2, model.getChildCount(root));
        assertEquals(symbol1, model.getChild(root, 0));
        assertEquals(symbol2, model.getChild(root, 1));

    }

    @Test
    public void ftsTest(){
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

        final StyleTreeModel model = new StyleTreeModel(fts);
        final Object root = model.getRoot();

        assertEquals(fts, root);
        assertEquals(0, model.getChildCount(root));

        final MutableRule rule1 = SF.rule();
        final MutableRule rule2 = SF.rule();

        fts.rules().add(rule1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));

        fts.rules().add(rule2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));

        fts.rules().remove(rule1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(rule2, model.getChild(root, 0));

        fts.rules().remove(rule2);
        assertEquals(0, model.getChildCount(root));

        //try modifying the properties -----------------------------------------
        fts.rules().add(rule1);
        fts.rules().add(rule2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));

        fts.setOnlineResource(null);
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));

        fts.setName("hello");
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));

        fts.semanticTypeIdentifiers().clear();
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));

        fts.semanticTypeIdentifiers().add(SemanticType.LINE);
        assertEquals(2, model.getChildCount(root));
        assertEquals(rule1, model.getChild(root, 0));
        assertEquals(rule2, model.getChild(root, 1));


    }

    @Test
    public void styleTest(){
        final MutableStyle style = SF.style();

        final StyleTreeModel model = new StyleTreeModel(style);
        final Object root = model.getRoot();

        assertEquals(style, root);
        assertEquals(0, model.getChildCount(root));

        final MutableFeatureTypeStyle fts1 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts2 = SF.featureTypeStyle();

        style.featureTypeStyles().add(fts1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));

        style.featureTypeStyles().add(fts2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));

        style.featureTypeStyles().remove(fts1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(fts2, model.getChild(root, 0));

        style.featureTypeStyles().remove(fts2);
        assertEquals(0, model.getChildCount(root));

        //try modifying the properties -----------------------------------------
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));

        style.setDefault(true);
        assertEquals(2, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));

        style.setName("hello");
        assertEquals(2, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));

    }

    @Test
    public void deepTest(){
        final MutableStyle style = SF.style();

        final StyleTreeModel model = new StyleTreeModel(style);
        final Object root = model.getRoot();

        assertEquals(style, root);
        assertEquals(0, model.getChildCount(root));

        final MutableFeatureTypeStyle fts1 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts2 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts3 = SF.featureTypeStyle();
        final MutableRule rule1 = SF.rule();
        final Symbolizer symbol1 = SF.lineSymbolizer();

        style.featureTypeStyles().add(fts1);
        assertEquals(1, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));

        style.featureTypeStyles().add(fts2);
        assertEquals(2, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));

        style.featureTypeStyles().add(fts3);
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));

        fts1.rules().add(rule1);
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));

        rule1.symbolizers().add(symbol1);
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));
        assertEquals(1, model.getChildCount(model.getChild(model.getChild(root, 0),0)) );
        assertEquals(symbol1, model.getChild(model.getChild(model.getChild(root, 0),0),0));


        fts2.semanticTypeIdentifiers().clear();
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));
        assertEquals(1, model.getChildCount(model.getChild(model.getChild(root, 0),0)) );
        assertEquals(symbol1, model.getChild(model.getChild(model.getChild(root, 0),0),0));

        fts2.semanticTypeIdentifiers().add(SemanticType.ANY);
        fts2.semanticTypeIdentifiers().add(SemanticType.POINT);
        fts2.semanticTypeIdentifiers().add(SemanticType.LINE);
        fts2.semanticTypeIdentifiers().add(SemanticType.POLYGON);
        fts2.semanticTypeIdentifiers().add(SemanticType.TEXT);
        fts2.semanticTypeIdentifiers().add(SemanticType.RASTER);
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));
        assertEquals(1, model.getChildCount(model.getChild(model.getChild(root, 0),0)) );
        assertEquals(symbol1, model.getChild(model.getChild(model.getChild(root, 0),0),0));

        fts2.setDescription(SF.description("tadam", "plopplop"));
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));
        assertEquals(1, model.getChildCount(model.getChild(model.getChild(root, 0),0)) );
        assertEquals(symbol1, model.getChild(model.getChild(model.getChild(root, 0),0),0));

        fts2.setName("the name");
        assertEquals(3, model.getChildCount(root));
        assertEquals(fts1, model.getChild(root, 0));
        assertEquals(fts2, model.getChild(root, 1));
        assertEquals(fts3, model.getChild(root, 2));
        assertEquals(1, model.getChildCount(model.getChild(root, 0)) );
        assertEquals(rule1, model.getChild(model.getChild(root, 0),0));
        assertEquals(1, model.getChildCount(model.getChild(model.getChild(root, 0),0)) );
        assertEquals(symbol1, model.getChild(model.getChild(model.getChild(root, 0),0),0));

    }

}
