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

package org.geotoolkit.style.function;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.style.StyleConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import static org.junit.Assert.*;
import static java.awt.Color.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CategorizeTest {

    public CategorizeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void categorize(){
        final String attribut = "att_value";

        final FilterFactory ff = FactoryFinder.getFilterFactory(null);

        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("test");
        sftb.add(attribut, Double.class);
        final SimpleFeatureType sft = sftb.buildFeatureType();

        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        sfb.set(attribut, -5d);
        final Feature f1 = sfb.buildFeature("id1");
        sfb.set(attribut, 8d);
        final Feature f2 = sfb.buildFeature("id2");
        sfb.set(attribut, 30d);
        final Feature f3 = sfb.buildFeature("id3");


        final Expression Lookup = ff.property(attribut);
        final Map<Expression,Expression> values = new HashMap<Expression, Expression>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, ff.literal(GREEN));
        values.put(ff.literal(1d), ff.literal(RED));
        values.put(ff.literal(8d), ff.literal(YELLOW));
        values.put(ff.literal(15d), ff.literal(BLUE));

        Categorize categorize = new DefaultCategorize(Lookup, values, ThreshholdsBelongTo.PRECEDING, null);

        Color c = categorize.evaluate(f1,Color.class);
        assertEquals(c, GREEN);
        c = categorize.evaluate(f2,Color.class);
        assertEquals(c, YELLOW);
        c = categorize.evaluate(f3,Color.class);
        assertEquals(c, BLUE);

        categorize = new DefaultCategorize(Lookup, values, ThreshholdsBelongTo.SUCCEEDING, null);
        c = categorize.evaluate(f1,Color.class);
        assertEquals(c, GREEN);
        c = categorize.evaluate(f2,Color.class);
        assertEquals(c, RED);
        c = categorize.evaluate(f3,Color.class);
        assertEquals(c, BLUE);


        //test get lookup property
        Collection<String> requieredAttributs = new HashSet<String>();
        categorize.accept(ListingPropertyVisitor.VISITOR, requieredAttributs);

        assertEquals(requieredAttributs.size(), 1);
        assertEquals(requieredAttributs.iterator().next(), attribut);

    }

}