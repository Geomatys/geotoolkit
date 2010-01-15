/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.style.function;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
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
 * @author sorel
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

        final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("test");
        sftb.add(attribut, Double.class);
        final SimpleFeatureType sft = sftb.buildFeatureType();

        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);
        sfb.set(attribut, 0d);
        final Feature f1 = sfb.buildFeature("id1");
        sfb.set(attribut, 5d);
        final Feature f2 = sfb.buildFeature("id2");
        sfb.set(attribut, 10d);
        final Feature f3 = sfb.buildFeature("id3");
        sfb.set(attribut, 15d);
        final Feature f4 = sfb.buildFeature("id4");

        final Expression Lookup = ff.property(attribut);
        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();


        //test color interpolation ---------------------------------------------
        values.clear();
        values.add(new DefaultInterpolationPoint(ff.literal(BLACK),0d));
        values.add(new DefaultInterpolationPoint(ff.literal(RED),10d));
        values.add(new DefaultInterpolationPoint(ff.literal(BLUE),20d));

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
        values.add(new DefaultInterpolationPoint(sf.literal(BLACK),0d));
        values.add(new DefaultInterpolationPoint(sf.literal(RED),10d));
        values.add(new DefaultInterpolationPoint(sf.literal(BLUE),20d));

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
        values.add(new DefaultInterpolationPoint(ff.literal(0d),0d));
        values.add(new DefaultInterpolationPoint(ff.literal(100d),10d));
        values.add(new DefaultInterpolationPoint(ff.literal(50d),20d));

        interpolate = new DefaultInterpolate(Lookup, values, Method.COLOR, Mode.CUBIC, null);

        Double d = interpolate.evaluate(f1,Double.class);
        assertEquals(d.doubleValue(), 0d, 0d);
        d = interpolate.evaluate(f2,Double.class);
        assertEquals(d.doubleValue(), 50d, 0d);
        d = interpolate.evaluate(f3,Double.class);
        assertEquals(d.doubleValue(), 100d, 0d);
        d = interpolate.evaluate(f4,Double.class);
        assertEquals(d.doubleValue(), 75d, 0d);


    }

}