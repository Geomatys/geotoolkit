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

import javax.swing.tree.DefaultMutableTreeNode;

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
 * @module pending
 */
public class StyleModelTest {

    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static double DELTA = 0d;
    
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

        final StyleTreeModel<MutableRule> model = new StyleTreeModel<MutableRule>(rule);
        final DefaultMutableTreeNode root = model.getRoot();

        assertEquals(rule, root.getUserObject());
        assertEquals(0, root.getChildCount());

        final Symbolizer symbol1 = SF.lineSymbolizer();
        final Symbolizer symbol2 = SF.polygonSymbolizer();

        rule.symbolizers().add(symbol1);
        assertEquals(1, root.getChildCount());
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        rule.symbolizers().add(symbol2);
        assertEquals(2, root.getChildCount());
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(symbol2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        
        rule.symbolizers().remove(symbol1);
        assertEquals(1, root.getChildCount());
        assertEquals(symbol2, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        rule.symbolizers().remove(symbol2);
        assertEquals(0, root.getChildCount());


        //try modifying the properties -----------------------------------------
        rule.symbolizers().add(symbol1);
        rule.symbolizers().add(symbol2);
        assertEquals(2, root.getChildCount());
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(symbol2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        rule.setElseFilter(true);
        assertEquals(2, root.getChildCount());
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(symbol2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        rule.setName("hello");
        assertEquals(2, root.getChildCount());
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(symbol2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        
    }

    @Test
    public void ftsTest(){
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle();

        final StyleTreeModel<MutableFeatureTypeStyle> model = new StyleTreeModel<MutableFeatureTypeStyle>(fts);
        final DefaultMutableTreeNode root = model.getRoot();

        assertEquals(fts, root.getUserObject());
        assertEquals(0, root.getChildCount());

        final MutableRule rule1 = SF.rule();
        final MutableRule rule2 = SF.rule();

        fts.rules().add(rule1);
        assertEquals(1, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        fts.rules().add(rule2);
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        fts.rules().remove(rule1);
        assertEquals(1, root.getChildCount());
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        fts.rules().remove(rule2);
        assertEquals(0, root.getChildCount());

        //try modifying the properties -----------------------------------------
        fts.rules().add(rule1);
        fts.rules().add(rule2);
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        fts.setOnlineResource(null);
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        fts.setName("hello");
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        fts.semanticTypeIdentifiers().clear();
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        fts.semanticTypeIdentifiers().add(SemanticType.LINE);
        assertEquals(2, root.getChildCount());
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(rule2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );


    }

    @Test
    public void styleTest(){
        final MutableStyle style = SF.style();

        final StyleTreeModel<MutableStyle> model = new StyleTreeModel<MutableStyle>(style);
        final DefaultMutableTreeNode root = model.getRoot();

        assertEquals(style, root.getUserObject());
        assertEquals(0, root.getChildCount());

        final MutableFeatureTypeStyle fts1 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts2 = SF.featureTypeStyle();

        style.featureTypeStyles().add(fts1);
        assertEquals(1, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        style.featureTypeStyles().add(fts2);
        assertEquals(2, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        style.featureTypeStyles().remove(fts1);
        assertEquals(1, root.getChildCount());
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        style.featureTypeStyles().remove(fts2);
        assertEquals(0, root.getChildCount());

        //try modifying the properties -----------------------------------------
        style.featureTypeStyles().add(fts1);
        style.featureTypeStyles().add(fts2);
        assertEquals(2, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        style.setDefault(true);
        assertEquals(2, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        style.setName("hello");
        assertEquals(2, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

    }

    @Test
    public void deepTest(){
        final MutableStyle style = SF.style();

        final StyleTreeModel<MutableStyle> model = new StyleTreeModel<MutableStyle>(style);
        final DefaultMutableTreeNode root = model.getRoot();

        assertEquals(style, root.getUserObject());
        assertEquals(0, root.getChildCount());

        final MutableFeatureTypeStyle fts1 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts2 = SF.featureTypeStyle();
        final MutableFeatureTypeStyle fts3 = SF.featureTypeStyle();
        final MutableRule rule1 = SF.rule();
        final Symbolizer symbol1 = SF.lineSymbolizer();

        style.featureTypeStyles().add(fts1);
        assertEquals(1, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );

        style.featureTypeStyles().add(fts2);
        assertEquals(2, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );

        style.featureTypeStyles().add(fts3);
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );

        fts1.rules().add(rule1);
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        
        rule1.symbolizers().add(symbol1);
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildAt(0).getChildCount() );
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject() );


        fts2.semanticTypeIdentifiers().clear();
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildAt(0).getChildCount() );
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject() );
        
        fts2.semanticTypeIdentifiers().add(SemanticType.ANY);
        fts2.semanticTypeIdentifiers().add(SemanticType.POINT);
        fts2.semanticTypeIdentifiers().add(SemanticType.LINE);
        fts2.semanticTypeIdentifiers().add(SemanticType.POLYGON);
        fts2.semanticTypeIdentifiers().add(SemanticType.TEXT);
        fts2.semanticTypeIdentifiers().add(SemanticType.RASTER);
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildAt(0).getChildCount() );
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject() );

        fts2.setDescription(SF.description("tadam", "plopplop"));
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildAt(0).getChildCount() );
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject() );

        fts2.setName("the name");
        assertEquals(3, root.getChildCount());
        assertEquals(fts1, ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject() );
        assertEquals(fts2, ((DefaultMutableTreeNode)root.getChildAt(1)).getUserObject() );
        assertEquals(fts3, ((DefaultMutableTreeNode)root.getChildAt(2)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildCount() );
        assertEquals(rule1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0)).getUserObject() );
        assertEquals(1, root.getChildAt(0).getChildAt(0).getChildCount() );
        assertEquals(symbol1, ((DefaultMutableTreeNode)root.getChildAt(0).getChildAt(0).getChildAt(0)).getUserObject() );

    }

}
