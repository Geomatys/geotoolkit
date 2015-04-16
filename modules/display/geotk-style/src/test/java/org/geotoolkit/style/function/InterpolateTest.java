/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.geotoolkit.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

import static org.junit.Assert.*;
import static java.awt.Color.*;
import org.geotoolkit.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InterpolateTest {

    public InterpolateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void interpolate(){
        final String attribut = "att_value";

        final FilterFactory ff = FactoryFinder.getFilterFactory(null);
        final MutableStyleFactory sf = new DefaultStyleFactory();

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.add(attribut, Double.class);
        final FeatureType sft = sftb.buildFeatureType();

        final FeatureBuilder sfb = new FeatureBuilder(sft);
        sfb.setPropertyValue(attribut, 0d);
        final Feature f1 = sfb.buildFeature("id1");
        sfb.setPropertyValue(attribut, 5d);
        final Feature f2 = sfb.buildFeature("id2");
        sfb.setPropertyValue(attribut, 10d);
        final Feature f3 = sfb.buildFeature("id3");
        sfb.setPropertyValue(attribut, 15d);
        final Feature f4 = sfb.buildFeature("id4");

        final Expression Lookup = ff.property(attribut);
        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();


        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,ff.literal(BLACK)));
        values.add(new DefaultInterpolationPoint(10d,ff.literal(RED)));
        values.add(new DefaultInterpolationPoint(20d,ff.literal(BLUE)));

        Interpolate interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        Color c = interpolate.evaluate(f1,Color.class);
        assertEquals(c, BLACK);
        c = interpolate.evaluate(f2,Color.class);
        assertEquals(c.getAlpha(), 255);
        assertEquals(c.getRed(), 127);
        assertEquals(c.getGreen(), 0);
        assertEquals(c.getBlue(), 0);
        c = interpolate.evaluate(f3,Color.class);
        assertEquals(c, RED);

        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,sf.literal(BLACK)));
        values.add(new DefaultInterpolationPoint(10d,sf.literal(RED)));
        values.add(new DefaultInterpolationPoint(20d,sf.literal(BLUE)));

        interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        c = interpolate.evaluate(f1,Color.class);
        assertEquals(c, BLACK);
        c = interpolate.evaluate(f2,Color.class);
        assertEquals(c.getAlpha(), 255);
        assertEquals(c.getRed(), 127);
        assertEquals(c.getGreen(), 0);
        assertEquals(c.getBlue(), 0);
        c = interpolate.evaluate(f3,Color.class);
        assertEquals(c, RED);


        //test number interpolation --------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,ff.literal(0d)));
        values.add(new DefaultInterpolationPoint(10d,ff.literal(100d)));
        values.add(new DefaultInterpolationPoint(20d,ff.literal(50d)));

        interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        Double d = interpolate.evaluate(f1,Double.class);
        assertEquals(d.doubleValue(), 0d, 0d);
        d = interpolate.evaluate(f2,Double.class);
        assertEquals(d.doubleValue(), 50d, 0d);
        d = interpolate.evaluate(f3,Double.class);
        assertEquals(d.doubleValue(), 100d, 0d);
        d = interpolate.evaluate(f4,Double.class);
        assertEquals(d.doubleValue(), 75d, 0d);

        //test get lookup property
        Collection<String> requieredAttributs = new HashSet<String>();
        interpolate.accept(ListingPropertyVisitor.VISITOR, requieredAttributs);

        assertEquals(requieredAttributs.size(), 1);
        assertEquals(requieredAttributs.iterator().next(), attribut);

    }

}
