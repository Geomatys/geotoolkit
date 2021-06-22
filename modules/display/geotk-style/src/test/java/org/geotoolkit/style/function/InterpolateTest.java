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
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import static org.junit.Assert.*;
import static java.awt.Color.*;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InterpolateTest extends org.geotoolkit.test.TestBase {

    public InterpolateTest() {
    }

    @Test
    public void interpolate(){
        final String attribut = "att_value";

        final FilterFactory ff = FilterUtilities.FF;
        final MutableStyleFactory sf = new DefaultStyleFactory();

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("test");
        sftb.addAttribute(Double.class).setName(attribut);
        final FeatureType sft = sftb.build();

        final Feature f1 = sft.newInstance();
        f1.setPropertyValue(attribut, 0d);
        final Feature f2 = sft.newInstance();
        f2.setPropertyValue(attribut, 5d);
        final Feature f3 = sft.newInstance();
        f3.setPropertyValue(attribut, 10d);
        final Feature f4 = sft.newInstance();
        f4.setPropertyValue(attribut, 15d);

        final Expression Lookup = ff.property(attribut);
        final List<InterpolationPoint> values = new ArrayList<>();


        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,ff.literal(BLACK)));
        values.add(new DefaultInterpolationPoint(10d,ff.literal(RED)));
        values.add(new DefaultInterpolationPoint(20d,ff.literal(BLUE)));

        Interpolate interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        Color c = (Color) interpolate.apply(f1);
        assertEquals(c, BLACK);
        c = (Color) interpolate.apply(f2);
        assertEquals(c.getAlpha(), 255);
        assertEquals(c.getRed(), 127);
        assertEquals(c.getGreen(), 0);
        assertEquals(c.getBlue(), 0);
        c = (Color) interpolate.apply(f3);
        assertEquals(c, RED);

        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,sf.literal(BLACK)));
        values.add(new DefaultInterpolationPoint(10d,sf.literal(RED)));
        values.add(new DefaultInterpolationPoint(20d,sf.literal(BLUE)));

        interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        c = (Color) interpolate.apply(f1);
        assertEquals(c, BLACK);
        c = (Color) interpolate.apply(f2);
        assertEquals(c.getAlpha(), 255);
        assertEquals(c.getRed(), 127);
        assertEquals(c.getGreen(), 0);
        assertEquals(c.getBlue(), 0);
        c = (Color) interpolate.apply(f3);
        assertEquals(c, RED);


        //test number interpolation --------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(0d,ff.literal(0d)));
        values.add(new DefaultInterpolationPoint(10d,ff.literal(100d)));
        values.add(new DefaultInterpolationPoint(20d,ff.literal(50d)));

        interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        Double d = (Double) interpolate.apply(f1);
        assertEquals(d.doubleValue(), 0d, 0d);
        d = (Double) interpolate.apply(f2);
        assertEquals(d.doubleValue(), 50d, 0d);
        d = (Double) interpolate.apply(f3);
        assertEquals(d.doubleValue(), 100d, 0d);
        d = (Double) interpolate.apply(f4);
        assertEquals(d.doubleValue(), 75d, 0d);

        //test get lookup property
        Collection<String> requieredAttributs = new HashSet<String>();
        ListingPropertyVisitor.VISITOR.visit(interpolate, requieredAttributs);

        assertEquals(requieredAttributs.size(), 1);
        assertEquals(requieredAttributs.iterator().next(), attribut);
    }
}
